package net.sf.rhinocanvas.rt;

import java.io.PrintWriter;


public interface ScriptRuntime {
	
	
	public Object exec(String expression);
	public void setSource(String url);
	public void stop();
	
	public void defineProperty(String name, Object value);
	public int getLineNumber();

	
	public void setOutput(PrintWriter writer);
}
