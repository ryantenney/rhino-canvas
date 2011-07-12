/**
 * 
 */
package net.sf.rhinocanvas.rt;

import java.awt.EventQueue;

import org.mozilla.javascript.Context;

class RhinoScheduler implements Runnable {

	private final RhinoRuntime runtime;

	RhinoScriptRunner runner;
	int time;
	int run;
	boolean loop;

	RhinoScheduler(RhinoRuntime runtime, Object command, int time, boolean loop) {
		this.runtime = runtime;
		this.time = time;
		this.run = this.runtime.runNumber;
		this.loop = loop;

		runner = new RhinoScriptRunner(runtime, command);
	}

	public void run() {
		do {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// Auto-generated catch block
				throw new RuntimeException(e);
			}

			if (run != this.runtime.runNumber) {
				break;
			}

			if (EventQueue.isDispatchThread()) {
				runner.run(Context.enter());
			} else {
				try {
					EventQueue.invokeAndWait(runner);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		} while (loop);
	}

}