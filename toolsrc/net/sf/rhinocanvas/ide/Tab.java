/*
 * Created on 28/10/2006 by Stefan Haustein
 */
package net.sf.rhinocanvas.ide;

import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;

import net.sf.rhinocanvas.js.Image;
import net.sf.rhinocanvas.rt.RhinoRuntime;

import org.ujac.ui.editor.TextArea;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;



public class Tab extends JSplitPane {

	
	class ConsoleHandler implements Runnable{
		BufferedReader reader;
		PrintWriter writer;
		
		ConsoleHandler(ConsoleTextArea console){
			writer = new PrintWriter(console.getOut());
			reader = new BufferedReader(new InputStreamReader(console.getIn()));
		}

		public void run() {
			
			while(true){
				try {
					writer.write("\n> ");
				
					writer.flush();
					String cmd = reader.readLine();
					writer.write(""+runtime.exec(cmd));
				} catch (Exception e) {
					
						e.printStackTrace(writer);
					
				}
			}
		}
	}
	
	RhinoRuntime runtime = new RhinoRuntime();
	ConsoleTextArea console = new ConsoleTextArea(null);
	String title;
	File file;
	TextArea editor  = new TextArea();
	boolean changed;
	static int unnamedCount = 0;
	//JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(editor), new JScrollPane(console));
	
	boolean consoleFocussed;
	IDE ide;
	
	
	Tab(IDE ide, File file){
		super(JSplitPane.VERTICAL_SPLIT);
		this.ide = ide;
		this.file = file;
		
		setTopComponent(new JScrollPane(editor));
		setBottomComponent(new JScrollPane(console));
		
		editor.setDocument(new JsDocument());
		editor.setLineHighlightEnabled(true);

		if(ide.tabs.size() == 0){
			setDividerLocation(Integer.parseInt(ide.properties.getProperty("divider", ""+ide.getHeight()*3/2)));

		}
		else {
			setDividerLocation(((Tab) ide.tabPane.getSelectedComponent()).getDividerLocation()); 
		}
		
		
		if(file == null){
			unnamedCount ++;
			title = "unnamed"+(unnamedCount > 1 ? " "+unnamedCount : "")+".js";
		}
		else{
			title = file.getName();
			
			try{
				FileReader r = new FileReader(file);
				StringWriter sw = new StringWriter();
				char[] buf = new char[16384];
				
				while(true){
					int count = r.read(buf);
					if(count <= 0) break;
					sw.write(buf, 0, count);
				}
				
				editor.setText(sw.toString());
			}
			catch(IOException e){
				throw new RuntimeException(e);
			}
			
		}
		
		 console.addFocusListener(new FocusListener(){

				

				public void focusGained(FocusEvent arg0) {
					consoleFocussed = true;
				}

				public void focusLost(FocusEvent arg0) {
					consoleFocussed = false;
				}
	        	
	        });
		 
		 
		 runtime.defineProperty("document", new net.sf.rhinocanvas.js.Frame(title, new Image(320, 240)));
		 
		 runtime.setOutput(new PrintWriter(console.getOut()));
		new Thread(new ConsoleHandler(console)).start();
	}

	
	public void actionCopy(){
		if(consoleFocussed){
			console.copy();
		}
		else {
			editor.copy();
		}
	}
	
	public void actionPaste(){
		System.out.println("Paste");
		if(consoleFocussed){
			console.paste();
		}
		else {
			editor.paste();
		}
	}

	
	public void actionCut(){
		if(consoleFocussed){
			console.cut();
		}
		else {
			editor.cut();
		}
	}

	public void actionRun(){
	//	runNumber++;

		if(file != null){
				runtime.setSource(file.toURI().toString());
		}
		PrintWriter pw = new PrintWriter(console.getOut());
		try{
			runtime.stop(); // inc run number
			pw.write(runtime.exec(editor.getText())+"\n>");
		}
		catch(Exception e){
			e.printStackTrace(pw);
		}
		pw.flush();
	}
	
	public boolean actionSave(){
	
		if(file == null){
			return actionSaveAs();
		}

		try{
			Writer w = new FileWriter(file);
			w.write(editor.getText());
			w.close();
			
			int index = ide.tabPane.getSelectedIndex();
			String title = ide.tabPane.getTitleAt(index);
			if(title.endsWith("*")){
				ide.tabPane.setTitleAt(index, title.substring(0, title.length()-1));
			}
			changed = false;
			
			
			ide.addLRU(file.getAbsolutePath());
			
			return true;
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public boolean actionSaveAs(){
		ide.fileChooser.setDialogTitle("Save file");
        int returnVal = ide.fileChooser.showSaveDialog(this);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
        	return false;
        }
        	
        File file = ide.fileChooser.getSelectedFile();
            
        this.file = file;
        title = file.getName();
        ide.tabPane.setTitleAt(ide.tabPane.getSelectedIndex(), title);
        actionSave();
        
        return true;
	}


	public boolean actionSavePDF(){
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileFilter(new FileFilter() {

			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
			}

			public String getDescription() {
				return "PDF file";
			}
			
		});

		fileChooser.setDialogTitle("Save PDF");
        int returnVal = fileChooser.showSaveDialog(this);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
        	return false;
        }
        	
        File file = fileChooser.getSelectedFile();
            
        this.file = file;

		try {
			float SCALE = 1.0f;
			Rectangle SIZE = new Rectangle(612f, 792f);

			FileOutputStream fos = new FileOutputStream(file);

			Document document = new Document(SIZE);
			PdfWriter w = PdfWriter.getInstance(document, fos);

			document.open();

			document.addTitle("Canvas-to-PDF Render");
			document.addSubject("Using rhino-canvas to iText");
			document.addKeywords("Java, PDF, iText");

			PdfContentByte cb = w.getDirectContent();

			cb.saveState();
			cb.concatCTM(AffineTransform.getScaleInstance(SCALE, SCALE));

			Graphics2D g = cb.createGraphics(SIZE.getWidth() / SCALE, SIZE.getHeight() / SCALE);

			runtime.defineProperty("document", new net.sf.rhinocanvas.js.Frame(g));

			runtime.stop(); // inc run number
			runtime.exec(editor.getText());

			g.dispose();

			cb.restoreState();

			document.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

        
        return true;
	}
	
	
	
	
	public void actionFind(){	
		editor.showFindDialog();
	}

	public void actionGoto(){	
		editor.showGotoLineDialog();
	}

	
	public void actionTerminate(){
		runtime.stop();
	}

	
	
}
