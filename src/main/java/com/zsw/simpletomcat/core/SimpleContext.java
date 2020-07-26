package com.zsw.simpletomcat.core;


import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.filter.FilterDef;
import com.zsw.simpletomcat.filter.FilterMap;
import com.zsw.simpletomcat.util.LifecycleSupport;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;


public class SimpleContext implements Context, Pipeline, Lifecycle {

	private Valve valve;
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	private boolean started;
	protected Logger logger;

	public SimpleContext() {
		pipeline.setBasic(new SimpleContextValve());
	}

	protected final HashMap<String, Container> children = new HashMap<>();
	protected Loader loader = null;
	protected SimplePipeline pipeline = new SimplePipeline(this);

	/**
	 * url 和 servlet 之间的映射
	 */
	protected final HashMap<String, String> servletMappings = new HashMap<>();

	protected Mapper mapper = null;
	protected final HashMap<String, Mapper> mappers = new HashMap<>();
	private Container parent = null;

	protected Manager manager;

	@Override
	public Object[] getApplicationListeners() {
		return null;
	}

	@Override
	public void setApplicationListeners(Object listeners[]) {
	}

	@Override
	public boolean getAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean flag) {
	}

	@Override
	public void addFilterMap(FilterMap filterMap) {

	}

	@Override
	public void addFilterDef(FilterDef filterDef) {

	}

	@Override
	public void removeFilterDef(FilterDef filterDef) {

	}

	@Override
	public void removeFilterMap(FilterMap filterMap) {

	}


	@Override
	public boolean getConfigured() {
		return false;
	}

	@Override
	public void setConfigured(boolean configured) {
	}

	@Override
	public boolean getCookies() {
		return false;
	}

	@Override
	public void setCookies(boolean cookies) {
	}

	@Override
	public boolean getCrossContext() {
		return false;
	}

	@Override
	public void setCrossContext(boolean crossContext) {
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public void setDisplayName(String displayName) {
	}

	@Override
	public boolean getDistributable() {
		return false;
	}

	@Override
	public void setDistributable(boolean distributable) {
	}

	@Override
	public String getDocBase() {
		return null;
	}

	@Override
	public void setDocBase(String docBase) {
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public void setPath(String path) {
	}

	@Override
	public String getPublicId() {
		return null;
	}

	@Override
	public void setPublicId(String publicId) {
	}

	@Override
	public boolean getReloadable() {
		return false;
	}

	@Override
	public void setReloadable(boolean reloadable) {
	}

	@Override
	public boolean getOverride() {
		return false;
	}

	@Override
	public void setOverride(boolean override) {
	}

	@Override
	public boolean getPrivileged() {
		return false;
	}

	@Override
	public void setPrivileged(boolean privileged) {
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public int getSessionTimeout() {
		return 0;
	}

	@Override
	public void setSessionTimeout(int timeout) {
	}

	@Override
	public String getWrapperClass() {
		return null;
	}

	@Override
	public void setWrapperClass(String wrapperClass) {
	}

	@Override
	public void addApplicationListener(String listener) {
	}


	@Override
	public void addInstanceListener(String listener) {
	}


	@Override
	public void addMimeMapping(String extension, String mimeType) {
	}

	@Override
	public void addParameter(String name, String value) {
	}


	@Override
	public void addResourceEnvRef(String name, String type) {
	}


	@Override
	public void addRoleMapping(String role, String link) {
	}

	@Override
	public void addSecurityRole(String role) {
	}

	/**
	 * 添加一个url和wrapper实例的名称对
	 *
	 * @param pattern URL pattern to be mapped
	 * @param name    Name of the corresponding servlet to execute
	 */
	@Override
	public void addServletMapping(String pattern, String name) {
		synchronized (servletMappings) {
			servletMappings.put(pattern, name);
		}
	}

	@Override
	public void addTaglib(String uri, String location) {
	}

	@Override
	public void addWelcomeFile(String name) {
	}

	@Override
	public void addWrapperLifecycle(String listener) {
	}

	@Override
	public void addWrapperListener(String listener) {
	}

	@Override
	public Wrapper createWrapper() {
		return null;
	}

	@Override
	public String[] findApplicationListeners() {
		return null;
	}


	@Override
	public String[] findInstanceListeners() {
		return null;
	}


	@Override
	public String findMimeMapping(String extension) {
		return null;
	}

	@Override
	public String[] findMimeMappings() {
		return null;
	}

	@Override
	public String findParameter(String name) {
		return null;
	}

	@Override
	public String[] findParameters() {
		return null;
	}


	@Override
	public String findResourceEnvRef(String name) {
		return null;
	}

	@Override
	public String[] findResourceEnvRefs() {
		return null;
	}


	@Override
	public String findRoleMapping(String role) {
		return null;
	}

	@Override
	public boolean findSecurityRole(String role) {
		return false;
	}

	@Override
	public String[] findSecurityRoles() {
		return null;
	}

	/**
	 * 通过url查找对应的Wrapper
	 *
	 * @param pattern Pattern for which a mapping is requested
	 * @return
	 */
	@Override
	public String findServletMapping(String pattern) {
		synchronized (servletMappings) {
			return ((String) servletMappings.get(pattern));
		}
	}

	@Override
	public String[] findServletMappings() {
		return null;
	}

	@Override
	public String findStatusPage(int status) {
		return null;
	}

	@Override
	public int[] findStatusPages() {
		return null;
	}

	@Override
	public String findTaglib(String uri) {
		return null;
	}

	@Override
	public String[] findTaglibs() {
		return null;
	}

	@Override
	public boolean findWelcomeFile(String name) {
		return false;
	}

	@Override
	public String[] findWelcomeFiles() {
		return null;
	}

	@Override
	public String[] findWrapperLifecycles() {
		return null;
	}

	@Override
	public String[] findWrapperListeners() {
		return null;
	}

	@Override
	public void reload() {
	}

	@Override
	public void removeApplicationListener(String listener) {
	}

	@Override
	public void removeApplicationParameter(String name) {
	}

	@Override
	public void removeEjb(String name) {
	}

	@Override
	public void removeEnvironment(String name) {
	}


	@Override
	public void removeInstanceListener(String listener) {
	}

	@Override
	public void removeLocalEjb(String name) {
	}

	@Override
	public void removeMimeMapping(String extension) {
	}

	@Override
	public void removeParameter(String name) {
	}

	@Override
	public void removeResource(String name) {
	}

	@Override
	public void removeResourceEnvRef(String name) {
	}

	@Override
	public void removeResourceLink(String name) {
	}

	@Override
	public void removeRoleMapping(String role) {
	}

	@Override
	public void removeSecurityRole(String role) {
	}

	@Override
	public void removeServletMapping(String pattern) {
	}

	@Override
	public void removeTaglib(String uri) {
	}

	@Override
	public void removeWelcomeFile(String name) {
	}

	@Override
	public void removeWrapperLifecycle(String listener) {
	}

	@Override
	public void removeWrapperListener(String listener) {
	}


	//methods of the Container interface
	@Override
	public String getInfo() {
		return null;
	}

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
		this.loader = loader;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public Manager getManager() {
		return manager;
	}

	@Override
	public void setManager(Manager manager) {
		this.manager = manager;
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
		return null;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public Container getParent() {
		return null;
	}

	@Override
	public void setParent(Container container) {
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
		child.setParent((Container) this);
		children.put(child.getName(), child);
	}

	@Override
	public void addContainerListener(ContainerListener listener) {
	}

	/**
	 * 添加一个映射器
	 *
	 * @param mapper The corresponding Mapper implementation
	 */
	@Override
	public void addMapper(Mapper mapper) {

		mapper.setContainer((Container) this);
		this.mapper = mapper;
		synchronized (mappers) {
			if (mappers.get(mapper.getProtocol()) != null) {
				throw new IllegalArgumentException("addMapper:  Protocol '" +
						mapper.getProtocol() + "' is not unique");
			}
			mapper.setContainer((Container) this);      // May throw IAE
			mappers.put(mapper.getProtocol(), mapper);
			if (mappers.size() == 1) {
				this.mapper = mapper;
			} else {
				this.mapper = null;
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	@Override
	public Container findChild(String name) {
		if (name == null) {
			return (null);
		}
		synchronized (children) {       // Required by post-start changes
			return ((Container) children.get(name));
		}
	}

	@Override
	public Container[] findChildren() {
		synchronized (children) {
			Container results[] = new Container[children.size()];
			return ((Container[]) children.values().toArray(results));
		}
	}

	@Override
	public ContainerListener[] findContainerListeners() {
		return null;
	}

	/**
	 * 找到正确的映射器
	 *
	 * @param protocol Protocol for which to find a Mapper
	 * @return
	 */
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

	public Mapper[] findMappers() {
		return null;
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response)
			throws IOException, ServletException {
		pipeline.invoke(request, response);
	}

	/**
	 * 返回负责处理当前请求的Wrapper实例
	 *
	 * @param request Request being processed
	 * @param update  Update the Request to reflect the mapping selection?
	 * @return
	 */
	@Override
	public Container map(HttpRequest request, boolean update) {
		Mapper mapper = findMapper(request.getRequest().getProtocol());
		if (mapper == null) {
			return null;
		}

		return mapper.map(request, update);
	}

	public void removeChild(Container child) {
	}

	public void removeContainerListener(ContainerListener listener) {
	}

	public void removeMapper(Mapper mapper) {
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
	}

	// method implementations of Pipeline
	@Override
	public Valve getBasic() {
		return pipeline.getBasic();
	}

	@Override
	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);
	}

	@Override
	public synchronized void addValve(Valve valve) {
		this.valve = valve;
		pipeline.addValve(valve);
	}

	@Override
	public Valve[] getValves() {
		return pipeline.getValves();
	}

	@Override
	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public synchronized void start() throws LifecycleException {
		Logger logger = getLogger();
		if (started) {
			throw new LifecycleException("SimpleContext has already started");
		}

		// 触发 BEFORE_START_EVENT 事件，监听该事件的所有监听器会收到通知
		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);

		// 表示组件已经启动了
		started = true;
		try {
		    // 启动loader
			if ((loader != null) && (loader instanceof Lifecycle)) {
				((Lifecycle) loader).start();
			}

			// 启动子容器
			Container children[] = findChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof Lifecycle) {
					((Lifecycle) children[i]).start();
				}
			}

			// 启动pipeline
			if (pipeline instanceof Lifecycle) {
				((Lifecycle) pipeline).start();
			}
			// 触发START_EVENT事件
			lifecycle.fireLifecycleEvent(START_EVENT, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 触发AFTER_START_EVENT
		lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
	}

	@Override
	public void stop() throws LifecycleException {
		if (!started) {
          throw new LifecycleException("SimpleContext has not been started");
        }

		lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;
		try {
			// 关闭与它关联的所有组件及SimpleContext的子容器
			if (pipeline instanceof Lifecycle) {
				((Lifecycle) pipeline).stop();
			}

			// Stop our child containers, if any
			Container children[] = findChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof Lifecycle) {
                  ((Lifecycle) children[i]).stop();
                }
			}
			if ((loader != null) && (loader instanceof Lifecycle)) {
				((Lifecycle) loader).stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 触发AFTER_STOP_EVENT事件
		lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
	}
}