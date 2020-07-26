package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author zsw
 * @date 2020/07/23 18:17
 */
public class SimplePipeline implements Pipeline,Lifecycle {

	public SimplePipeline(Container container) {
		setContainer(container);
	}

	// The basic Valve (if any) associated with this Pipeline.
	protected Valve basic = null;

	// The Container with which this Pipeline is associated.
	protected Container container = null;

	// the array of Valves
	protected Valve valves[] = new Valve[0];

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public Valve getBasic() {
		return basic;
	}

	@Override
	public void setBasic(Valve valve) {
		this.basic = valve;
		((Contained) valve).setContainer(container);
	}

	@Override
	public void addValve(Valve valve) {
		if (valve instanceof Contained) {
			((Contained) valve).setContainer(this.container);
		}

		synchronized (valves) {
			Valve results[] = new Valve[valves.length + 1];
			System.arraycopy(valves, 0, results, 0, valves.length);
			results[valves.length] = valve;
			valves = results;
		}
	}

	@Override
	public Valve[] getValves() {
		return valves;
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
		// Invoke the first Valve in this pipeline for this request
		(new SimplePipelineValveContext()).invokeNext(request, response);
	}

	@Override
	public void removeValve(Valve valve) {
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return new LifecycleListener[0];
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public void start() throws LifecycleException {

	}

	@Override
	public void stop() throws LifecycleException {

	}

	// this class is copied from org.apache.catalina.core.StandardPipeline class's
	// StandardPipelineValveContext inner class.
	protected class SimplePipelineValveContext implements ValveContext {

		protected int stage = 0;

		@Override
		public String getInfo() {
			return null;
		}

		@Override
		public void invokeNext(HttpRequest request, HttpResponse response) throws IOException, ServletException {
			int subscript = stage;
			stage = stage + 1;
			// Invoke the requested Valve for the current request thread
			if (subscript < valves.length) {
				valves[subscript].invoke(request, response, this);
			} else if ((subscript == valves.length) && (basic != null)) {
				basic.invoke(request, response, this);
			} else {
				throw new ServletException("No valve");
			}
		}
	} // end of inner class
}
