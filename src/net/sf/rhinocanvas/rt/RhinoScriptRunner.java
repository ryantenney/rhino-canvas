/**
 * 
 */
package net.sf.rhinocanvas.rt;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;

class RhinoScriptRunner implements ContextAction, Runnable {

	
	private final RhinoRuntime runtime;
	private Object script;
	//private Context context;

	
	RhinoScriptRunner(RhinoRuntime runtime, Object script){
		this.runtime = runtime;
		this.script = script;
		//this.context = context;
	}
	

	public void run(){
		run(Context.enter());
	}
	
	
	public Object run(Context cx) {
		
		try{
		
		cx.putThreadLocal("runtime", runtime);
		
		if(script instanceof String){
			this.script = cx.compileString((String) script,
                this.runtime.currentUrl, 1, null);
		}
		
		
		 if(script instanceof Script){
		 
			 Object result = ((Script) script).exec(cx, runtime.scope);
	//		 Object result = Main.evaluateScript((Script) script, cx, Main.getGlobal());
//			 PrintStream ps = Main.getGlobal().getErr();
//			 
//			 if (result != Context.getUndefinedValue()) {
//	            try {
//	            	ps.println();
//	                ps.println(Context.toString(result));
//	                ps.print("js> ");
//	                ps.flush();
//	            } catch (RhinoException rex) {
//	                ToolErrorReporter.reportException(
//	                    cx.getErrorReporter(), rex);
//	            }
//	        }
//		 
			 return result;
		 }
		 else {
			 Function fn = (Function) script;
			 return fn.call(cx, fn, fn, new Object[0]);
		 }
		 
		}
		catch(Exception e){
			e.printStackTrace(runtime.writer);
			return e;
		}
	 }
}