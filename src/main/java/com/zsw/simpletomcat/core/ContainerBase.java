package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.Constants;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.util.LifecycleSupport;
import com.zsw.simpletomcat.util.StringManager;

import javax.naming.directory.DirContext;
import javax.servlet.ServletException;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author zsw
 * @date 2020/07/25 18:00
 */
public abstract class ContainerBase implements Container, Lifecycle, Pipeline {
	protected final HashMap<String, Container> children = new HashMap<>();
	protected int debug = 0;
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	protected final ArrayList<ContainerListener> listeners = new ArrayList<>();
	protected Loader loader = null;
	protected Logger logger = null;
	protected Manager manager = null;
	protected final HashMap<String, Mapper> mappers = new HashMap<>();
	protected Mapper mapper = null;

	protected String mapperClass = null;

	protected Container parent = null;
	protected Pipeline pipeline = new StandardPipeline(this);
	protected static StringManager sm =
			StringManager.getManager(Constants.Package);
	protected boolean started = false;

	protected String name = null;

	public int getDebug() {
		return (this.debug);
	}

	public void setDebug(int debug) {
		this.debug = debug;
	}

	@Override
	public abstract String getInfo();

	@Override
	public Loader getLoader() {
		if (loader != null) {
			return (loader);
		}
		if (parent != null) {
			return (parent.getLoader());
		}
		return (null);
	}

	@Override
	public void setLoader(Loader loader) {
		Loader oldLoader = this.loader;
		this.loader = loader;
		// stop oldLoader
		if (started && oldLoader instanceof Lifecycle) {
			try {
				((Lifecycle) oldLoader).stop();
			} catch (LifecycleException e) {
				log("ContainerBase.setLoader: stop: ", e);
			}
		}

		// start the new loader
		if (loader != null) {
			loader.setContainer(this);
		}
		if (started && loader != null && loader instanceof Pipeline) {
			try {
				((Lifecycle) loader).start();
			} catch (LifecycleException e) {
				log("ContainerBase.setLoader: start: ", e);
			}
		}
	}

	@Override
	public Logger getLogger() {
		if (logger != null) {
			return (logger);
		}
		if (parent != null) {
			return (parent.getLogger());
		}
		return (null);
	}

	@Override
	public synchronized void setLogger(Logger logger) {
		Logger oldLogger = this.logger;
		if (oldLogger == logger) {
			return;
		}
		this.logger = logger;
		if (started && (oldLogger instanceof Lifecycle)) {
			try {
				((Lifecycle) oldLogger).stop();
			} catch (LifecycleException e) {
				log("ContainerBase.setLogger: stop: ", e);
			}
		}

		// Start the new component if necessary
		if (logger != null) {
			logger.setContainer(this);
		}
		if (started && (logger instanceof Lifecycle)) {
			try {
				((Lifecycle) logger).start();
			} catch (LifecycleException e) {
				log("ContainerBase.setLogger: start: ", e);
			}
		}
	}

	@Override
	public Manager getManager() {
		if (manager != null) {
			return (manager);
		}
		if (parent != null) {
			return (parent.getManager());
		}
		return (null);
	}

	@Override
	public Cluster getCluster() {
		return null;
	}

	@Override
	public void setCluster(Cluster cluster) {

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public void setParent(Container container) {
		this.parent = container;
	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {

	}


	public Pipeline getPipeline() {

		return (this.pipeline);

	}

	@Override
	public void setRealm(Realm realm) {

	}

	@Override
	public Realm getRealm() {
		return null;
	}

	@Override
	public DirContext getResources() {
		return null;
	}

	@Override
	public void setResources(DirContext resources) {

	}

	@Override
	public void addChild(Container child) {
		synchronized (children) {
			child.setParent(this);
			if (started) {
				try {
					((Lifecycle) child).start();
				} catch (LifecycleException e) {
					log("ContainerBase.addChild: start: ", e);
					throw new IllegalStateException
							("ContainerBase.addChild: start: " + e);
				}
			}
			children.put(child.getName(), child);
		}
	}

	@Override
	public void addContainerListener(ContainerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void addMapper(Mapper mapper) {
		synchronized (mappers) {
			mapper.setContainer(this);
			mapper.setContainer((Container) this);
			if (started && (mapper instanceof Lifecycle)) {
				try {
					((Lifecycle) mapper).start();
				} catch (LifecycleException e) {
					log("ContainerBase.addMapper: start: ", e);
					throw new IllegalStateException
							("ContainerBase.addMapper: start: " + e);
				}
			}
			mappers.put(mapper.getProtocol(), mapper);
			if (mappers.size() == 1) {
				this.mapper = mapper;
			} else {
				this.mapper = null;
			}
		}
	}

	@Override
	public Container findChild(String name) {
		if (name == null) {
			return (null);
		}
		synchronized (children) {
			return ((Container) children.get(name));
		}
	}

	@Override
	public Container[] findChildren() {

		synchronized (children) {
			Container[] results = new Container[children.size()];
			return ((Container[]) children.values().toArray(results));
		}

	}

	@Override
	public ContainerListener[] findContainerListeners() {

		synchronized (listeners) {
			ContainerListener[] results =
					new ContainerListener[listeners.size()];
			return ((ContainerListener[]) listeners.toArray(results));
		}

	}

	@Override
	public Mapper findMapper(String protocol) {
		if (mapper != null) {
			return (mapper);
		} else {
			synchronized (mappers) {
				return ((Mapper) mappers.get(protocol));
			}
		}
	}

	@Override
	public Mapper[] findMappers() {
		synchronized (mappers) {
			Mapper[] results = new Mapper[mappers.size()];
			return ((Mapper[]) mappers.values().toArray(results));
		}
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
		pipeline.invoke(request, response);
	}

	@Override
	public Container map(HttpRequest request, boolean update) {
		Mapper mapper = findMapper(request.getRequest().getProtocol());
		if (mapper == null) {
			return (null);
		}
		return (mapper.map(request, update));
	}


	@Override
	public void removeChild(Container child) {
		synchronized (children) {
			if (children.get(child.getName()) == null) {
				return;
			}
			children.remove(child.getName());
		}
		if (started) {
			try {
				((Lifecycle) child).stop();
			} catch (LifecycleException e) {
				log("ContainerBase.removeChild: stop: ", e);
			}
		}
		child.setParent(null);
	}

	@Override
	public void removeContainerListener(ContainerListener listener) {

		synchronized (listeners) {
			listeners.remove(listener);
		}

	}


	@Override
	public void removeMapper(Mapper mapper) {
		synchronized (mappers) {
			if (mappers.get(mapper.getProtocol()) == null) {
				return;
			}
			mappers.remove(mapper.getProtocol());
			if (started && (mapper instanceof Lifecycle)) {
				try {
					((Lifecycle) mapper).stop();
				} catch (LifecycleException e) {
					log("ContainerBase.removeMapper: stop: ", e);
					throw new IllegalStateException
							("ContainerBase.removeMapper: stop: " + e);
				}
			}
			if (mappers.size() != 1) {
				this.mapper = null;
			} else {
				Iterator values = mappers.values().iterator();
				this.mapper = (Mapper) values.next();
			}
		}

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

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
					(sm.getString("containerBase.alreadyStarted", logName()));
		}

		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);

		addDefaultMapper(this.mapperClass);
		started = true;

		// Start our subordinate components, if any
		if ((loader != null) && (loader instanceof Lifecycle)) {
			((Lifecycle) loader).start();
		}
		if ((logger != null) && (logger instanceof Lifecycle)) {
			((Lifecycle) logger).start();
		}
		if ((manager != null) && (manager instanceof Lifecycle)) {
			((Lifecycle) manager).start();
		}

		Mapper[] mappers = findMappers();
		for (Mapper value : mappers) {
			if (value instanceof Lifecycle) {
				((Lifecycle) value).start();
			}
		}

		Container[] children = findChildren();
		for (Container child : children) {
			if (child != null) {
				((Lifecycle) child).start();
			}
		}

		// Start the Valves in our pipeline (including the basic), if any
		if (pipeline instanceof Lifecycle) {
			((Lifecycle) pipeline).start();
		}
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}


	protected void addDefaultMapper(String mapperClass) {

		// Do we need a default Mapper?
		if (mapperClass == null) {
			return;
		}
		if (mappers.size() >= 1) {
			return;
		}

		try {
			Class clazz = Class.forName(mapperClass);
			Mapper mapper = (Mapper) clazz.newInstance();
			mapper.setProtocol("http");
			addMapper(mapper);
		} catch (Exception e) {
			log(sm.getString("containerBase.addDefaultMapper", mapperClass),
					e);
		}

	}

	@Override
	public void stop() throws LifecycleException {
		// Validate and update our current component state
		if (!started) {
			throw new LifecycleException
					(sm.getString("containerBase.notStarted", logName()));
		}

		lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;

		if (pipeline instanceof Lifecycle) {
			((Lifecycle) pipeline).stop();
		}

		// Stop our child containers, if any
		Container[] children = findChildren();
		for (Container child : children) {
			if (child != null) {
				((Lifecycle) child).stop();
			}
		}

		// Stop our Mappers, if any
		Mapper[] mappers = findMappers();
		for (int i = 0; i < mappers.length; i++) {
			if (mappers[(mappers.length-1)-i] instanceof Lifecycle) {
				((Lifecycle) mappers[(mappers.length-1)-i]).stop();
			}
		}

		if ((manager != null) && (manager instanceof Lifecycle)) {
			((Lifecycle) manager).stop();
		}
		if ((logger != null) && (logger instanceof Lifecycle)) {
			((Lifecycle) logger).stop();
		}
		if ((loader != null) && (loader instanceof Lifecycle)) {
			((Lifecycle) loader).stop();
		}
		lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
	}

	@Override
	public Valve getBasic() {
		return (pipeline.getBasic());
	}

	@Override
	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);
	}

	@Override
	public void addValve(Valve valve) {
		pipeline.addValve(valve);
	}

	@Override
	public Valve[] getValves() {
		return new Valve[0];
	}

	@Override
	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);
	}

	@Override
	public synchronized void setManager(Manager manager) {
		// Change components if necessary
		Manager oldManager = this.manager;
		if (oldManager == manager) {
			return;
		}
		this.manager = manager;

		// Stop the old component if necessary
		if (started && (oldManager instanceof Lifecycle)) {
			try {
				((Lifecycle) oldManager).stop();
			} catch (LifecycleException e) {
				log("ContainerBase.setManager: stop: ", e);
			}
		}

		// Start the new component if necessary
		if (manager != null) {
			manager.setContainer(this);
		}
		if (started && (manager instanceof Lifecycle)) {
			try {
				((Lifecycle) manager).start();
			} catch (LifecycleException e) {
				log("ContainerBase.setManager: start: ", e);
			}
		}
	}


	protected void log(String message, Throwable throwable) {

		Logger logger = getLogger();
		if (logger != null) {
			logger.log(logName() + ": " + message, throwable);
		} else {
			System.out.println(logName() + ": " + message + ": " + throwable);
			throwable.printStackTrace(System.out);
		}

	}

	protected void log(String message) {

		Logger logger = getLogger();
		if (logger != null) {
			logger.log(logName() + ": " + message);
		} else {
			System.out.println(logName() + ": " + message);
		}

	}


	protected String logName() {

		String className = this.getClass().getName();
		int period = className.lastIndexOf(".");
		if (period >= 0) {
			className = className.substring(period + 1);
		}
		return (className + "[" + getName() + "]");

	}
}
