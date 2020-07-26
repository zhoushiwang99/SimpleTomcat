package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.Constants;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.util.LifecycleSupport;
import com.zsw.simpletomcat.util.StringManager;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zsw
 * @date 2020/07/25 18:02
 */
public class StandardPipeline implements Pipeline, Contained, Lifecycle {
	protected Valve basic = null;
	protected Container container = null;
	protected int debug = 0;
	protected String info = "org.apache.catalina.core.StandardPipeline/1.0";
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	protected static StringManager sm =
			StringManager.getManager(Constants.Package);
	protected boolean started = false;
	protected ArrayList<Valve> valves = new ArrayList<>();

	public StandardPipeline(Container container) {
		this.container = container;
	}

	public StandardPipeline() {
		this(null);
	}


	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public void start() throws LifecycleException {
		if (started) {
			throw new LifecycleException
					(sm.getString("standardPipeline.alreadyStarted"));
		}
		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
		started = true;
		for (Valve valve : valves) {
			if (valve instanceof Lifecycle) {
				((Lifecycle) valve).start();
			}
		}
		if ((basic != null) && (basic instanceof Lifecycle)) {
			((Lifecycle) basic).start();
		}
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);

	}

	@Override
	public void stop() throws LifecycleException {
		if (!started) {
			throw new LifecycleException
					(sm.getString("standardPipeline.notStarted"));
		}
		lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;
		if ((basic != null) && (basic instanceof Lifecycle)) {
			((Lifecycle) basic).stop();
		}
		for (Valve valve : valves) {
			if (valve instanceof Lifecycle) {
				((Lifecycle) valve).stop();
			}
		}
		lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
	}

	@Override
	public Valve getBasic() {
		return basic;
	}

	@Override
	public void setBasic(Valve valve) {
		Valve oldBasic = this.basic;
		if (oldBasic == valve) {
			return;
		}
		if (oldBasic != null) {
			if (started && (oldBasic instanceof Lifecycle)) {
				try {
					((Lifecycle) oldBasic).stop();
				} catch (LifecycleException e) {
					log("StandardPipeline.setBasic: stop", e);
				}
			}
			if (oldBasic instanceof Contained) {
				try {
					((Contained) oldBasic).setContainer(null);
				} catch (Throwable t) {
					;
				}
			}
		}
		if (valve == null) {
			return;
		}
		if (valve instanceof Contained) {
			((Contained) valve).setContainer(this.container);
		}
		if (valve instanceof Lifecycle) {
			try {
				((Lifecycle) valve).start();
			} catch (LifecycleException e) {
				log("StandardPipeline.setBasic: start", e);
				return;
			}
		}
		this.basic = valve;

	}

	@Override
	public void addValve(Valve valve) {
		if (valve instanceof Contained) {
			((Contained) valve).setContainer(this.container);
		}

		// Start the new component if necessary
		if (started && (valve instanceof Lifecycle)) {
			try {
				((Lifecycle) valve).start();
			} catch (LifecycleException e) {
				log("StandardPipeline.addValve: start: ", e);
			}
		}
		synchronized (valves) {
			valves.add(valve);
		}
	}

	@Override
	public Valve[] getValves() {
		return valves.toArray(new Valve[1]);
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
		(new StandardPipelineValveContext()).invokeNext(request, response);
	}

	@Override
	public void removeValve(Valve valve) {
		valves.remove(valve);
	}

	protected void log(String message, Throwable throwable) {

		Logger logger = null;
		if (container != null) {
			logger = container.getLogger();
		}
		if (logger != null) {
			logger.log("StandardPipeline[" + container.getName() + "]: " +
					message, throwable);
		} else {
			System.out.println("StandardPipeline[" + container.getName() +
					"]: " + message);
			throwable.printStackTrace(System.out);
		}

	}
	protected class StandardPipelineValveContext
			implements ValveContext {
		protected int stage = 0;

		@Override
		public String getInfo() {
			return info;
		}

		@Override
		public void invokeNext(HttpRequest request, HttpResponse response) throws IOException, ServletException {
			int subscript = stage;
			stage = stage + 1;
			if (subscript < valves.size()) {
				valves.get(subscript).invoke(request, response, this);
			} else if ((subscript == valves.size()) && (basic != null)) {
				basic.invoke(request, response, this);
			} else {
				throw new ServletException
						(sm.getString("standardPipeline.noValve"));
			}
		}


	}
}
