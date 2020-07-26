package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.util.LifecycleSupport;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * 表示一个Servlet
 *
 * @author zsw
 * @date 2020/07/23 18:18
 */
public class SimpleWrapper implements Wrapper,Pipeline,Lifecycle {
	/**
	 * 载入servlet类使用的类加载器
	 */
	private Loader loader;

	/**
	 * 表示Wrapper实例的父容器
	 */
	protected Container parent = null;

	private final SimplePipeline pipeline = new SimplePipeline(this);

	private String name;

	private Servlet instance;

	private String servletClass;

	protected boolean started = false;

	protected LifecycleSupport lifecycle = new LifecycleSupport(this);

	public SimpleWrapper() {
		pipeline.setBasic(new SimpleWrapperValve());
	}

	@Override
	public void load() throws ServletException {

	}

	@Override
	public Servlet allocate() throws ServletException {
		// Load and initialize our instance if necessary
		if (instance==null) {
			try {
				instance = loadServlet();
			}
			catch (ServletException e) {
				throw e;
			}
			catch (Throwable e) {
				throw new ServletException("Cannot allocate a servlet instance", e);
			}
		}
		return instance;
	}

	private Servlet loadServlet() throws ServletException {
		if (instance!=null) {
			return instance;
		}

		Servlet servlet = null;
		String actualClass = servletClass;
		if (actualClass == null) {
			throw new ServletException("servlet class has not been specified");
		}

		Loader loader = getLoader();
		// Acquire an instance of the class loader to be used
		if (loader==null) {
			throw new ServletException("No loader.");
		}
		ClassLoader classLoader = loader.getClassLoader();

		// Load the specified servlet class from the appropriate class loader
		Class classClass = null;
		try {
			if (classLoader!=null) {
				classClass = classLoader.loadClass(actualClass);
			}
		}
		catch (ClassNotFoundException e) {
			throw new ServletException("Servlet class not found");
		}
		// Instantiate and initialize an instance of the servlet class itself
		try {
			servlet = (Servlet) classClass.newInstance();
		}
		catch (Throwable e) {
			throw new ServletException("Failed to instantiate servlet");
		}

		// Call the initialization method of this servlet
		try {
			servlet.init(null);
		}
		catch (Throwable f) {
			throw new ServletException("Failed initialize servlet.");
		}
		return servlet;
	}



	@Override
	public long getAvailable() {
		return 0;
	}

	@Override
	public void setAvailable(long available) {

	}

	@Override
	public String getJspFile() {
		return null;
	}

	@Override
	public void setJspFile(String jspFile) {

	}

	@Override
	public int getLoadOnStartup() {
		return 0;
	}

	@Override
	public void setLoadOnStartup(int value) {

	}

	@Override
	public String getRunAs() {
		return null;
	}

	@Override
	public void setRunAs(String runAs) {

	}

	@Override
	public String getServletClass() {
		return this.servletClass;
	}

	@Override
	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	@Override
	public boolean isUnavailable() {
		return false;
	}

	@Override
	public void addInitParameter(String name, String value) {

	}

	@Override
	public void addSecurityReference(String name, String link) {

	}

	@Override
	public void deallocate(Servlet servlet) throws ServletException {

	}

	@Override
	public String findInitParameter(String name) {
		return null;
	}

	@Override
	public String[] findInitParameters() {
		return new String[0];
	}

	@Override
	public String findSecurityReference(String name) {
		return null;
	}

	@Override
	public String[] findSecurityReferences() {
		return new String[0];
	}

	@Override
	public void removeInitParameter(String name) {

	}

	@Override
	public void removeSecurityReference(String name) {

	}

	@Override
	public void unavailable(UnavailableException unavailable) {

	}

	@Override
	public void unload() throws ServletException {

	}

	@Override
	public String getInfo() {
		return null;
	}

	/**
	 * 若Wrapper实例已关联一个类加载器，则直接返回该加载器
	 * 否则返回父容器的加载器或null
	 *
	 * @return
	 */
	@Override
	public Loader getLoader() {
		if (loader != null) {
			return loader;
		}
		if (parent != null) {
			return parent.getLoader();
		}
		return null;
	}

	@Override
	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	@Override
	public Logger getLogger() {
		return null;
	}

	@Override
	public void setLogger(Logger logger) {

	}

	@Override
	public Manager getManager() {
		return null;
	}

	@Override
	public void setManager(Manager manager) {

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
		parent = container;
	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {

	}

	@Override
	public Realm getRealm() {
		return null;
	}

	@Override
	public void setRealm(Realm realm) {

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

	}

	@Override
	public void addContainerListener(ContainerListener listener) {

	}

	@Override
	public void addMapper(Mapper mapper) {

	}


	@Override
	public Container findChild(String name) {
		return null;
	}

	@Override
	public Container[] findChildren() {
		return new Container[0];
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		return new ContainerListener[0];
	}

	@Override
	public Mapper findMapper(String protocol) {
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		return new Mapper[0];
	}

	@Override
	public Valve getBasic() {
		return pipeline.getBasic();
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
		return pipeline.getValves();
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
		pipeline.invoke(request, response);
	}

	@Override
	public void removeValve(Valve valve) {

	}

	@Override
	public Container map(HttpRequest request, boolean update) {
		return null;
	}

	@Override
	public void removeChild(Container child) {

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {

	}

	@Override
	public void removeMapper(Mapper mapper) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

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
		System.out.println("Starting Wrapper " + name);
		if (started) {
			throw new LifecycleException("Wrapper already started");
		}

		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
		started = true;

		if ((loader != null) && (loader instanceof Lifecycle)) {
			((Lifecycle) loader).start();
		}

		// Start the Valves in our pipeline (including the basic), if any
		if (pipeline instanceof Lifecycle) {
			((Lifecycle) pipeline).start();
		}

		lifecycle.fireLifecycleEvent(START_EVENT, null);
		lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}

	@Override
	public void stop() throws LifecycleException {
		System.out.println("Stopping wrapper " + name);
		// Shut down our servlet instance (if it has been initialized)
		try {
			// 调用 Servlet的destory方法
			instance.destroy();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		instance = null;
		if (!started) {
			throw new LifecycleException("Wrapper " + name + " not started");
		}

		lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);

		// 标识为未启动
		started = false;

		if (pipeline instanceof Lifecycle) {
			((Lifecycle) pipeline).stop();
		}


		if ((loader != null) && (loader instanceof Lifecycle)) {
			((Lifecycle) loader).stop();
		}

		lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
	}
}
