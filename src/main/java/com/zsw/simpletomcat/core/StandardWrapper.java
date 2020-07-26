package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;

import javax.naming.directory.DirContext;
import javax.servlet.*;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zsw
 * @date 2020/07/25 17:56
 */
public class StandardWrapper extends ContainerBase implements ServletConfig, Wrapper {

	public StandardWrapper() {
		super();
		pipeline.setBasic(new StandardWrapperValve());
	}

	/**
	 * 是否一次只处理一个请求（单线程）
	 */
	protected volatile boolean singleThreadModel = false;

	/**
	 * 表示非单线程访问的Servlet实例
	 */
	private volatile Servlet instance;

	/**
	 * 活动的Servlet实例数
	 */
	protected final AtomicInteger countAllocated = new AtomicInteger(0);

	/**
	 * 保存参数
	 */
	protected final HashMap<String, String> parameters = new HashMap<>();

	protected String servletClass;

	/**
	 * 保存单线程Servlet的池子
	 */
	private Stack<Servlet> instancePool = null;

	/**
	 * STM Servlet池中的实例数
	 */
	protected int nInstances = 0;

	public int getMaxInstances() {
		return maxInstances;
	}

	public void setMaxInstances(int maxInstances) {
		this.maxInstances = maxInstances;
	}

	/**
	 * STM Serlvet的最大数量
	 */
	private int maxInstances = 20;

	private static final String info =
			"com.zsw.simpletomcat.core.StandardWrapper/1.0";

	/**
	 * The facade associated with this wrapper.
	 */
	private StandardWrapperFacade facade =
			new StandardWrapperFacade(this);

	private int loadOnStartup = -1;

	private String jspFile = null;


	@Override
	public void load() throws ServletException {

	}

	/**
	 * 分配一个servlet的可用实例，若是singleThreadModel模式，则返回一个没被任何线程使用的实例
	 * 若不是则返回同一个实例instance
	 *
	 * @return
	 * @throws ServletException
	 */
	@Override
	public Servlet allocate() throws ServletException {
		if (!singleThreadModel) {
			if (instance == null) {
				synchronized (this) {
					if (instance == null) {
						try {
							instance = loadServlet();
						} catch (ServletException e) {
							throw e;
						} catch (Throwable e) {
							throw new ServletException
									(sm.getString("standardWrapper.allocate"), e);
						}
					}
				}
			}
			// 由于loadServlet()中有此变量的相关操作，所以还需要判断
			// 若singleThreadModel为false，则分配的实例在此返回
			if (!singleThreadModel) {
				countAllocated.incrementAndGet();
				return instance;
			}
		}
		// 从对象池返回一个Servlet
		synchronized (instancePool) {
			while (countAllocated.get() >= nInstances) {
				if (nInstances < maxInstances) {
					try {
						instancePool.push(loadServlet());
						nInstances++;
					} catch (Exception e) {
						throw new ServletException
								(sm.getString("standardWrapper.allocate"), e);
					}
				} else {
					try {
						instancePool.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		countAllocated.incrementAndGet();
		return instancePool.pop();
	}

	public synchronized Servlet loadServlet() throws ServletException {
		if (!singleThreadModel && (instance != null)) {
			return instance;
		}
		Servlet servlet = null;
		if (servletClass == null) {
			throw new ServletException(sm.getString("standardWrapper.notClass", getName()));
		}
		Loader loader = getLoader();
		if (loader == null) {
			throw new ServletException
					(sm.getString("standardWrapper.missingLoader", getName()));
		}
		ClassLoader classLoader = loader.getClassLoader();
		try {
			servlet = (Servlet) classLoader.loadClass(servletClass).getDeclaredConstructor().newInstance();
		} catch (ClassCastException e) {
			throw new ServletException(sm.getString("standardWrapper.notServlet", servletClass), e);
		} catch (Exception e) {
			throw new ServletException(sm.getString("standardWrapper.instantiate", servletClass), e);
		}
		// 初始化Servlet
		servlet.init(facade);
//		if (loadOnStartup > 0 && jspFile != null) {
//			HttpRequest request = new HttpRequest(null);
//			HttpResponse response = new HttpResponse(null);
//			request.setSer
//		}
		if (servlet instanceof SingleThreadModel) {
			if (instancePool == null) {
				instancePool = new Stack<>();
			}
			singleThreadModel = true;
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
		return jspFile;
	}

	@Override
	public void setJspFile(String jspFile) {
		this.jspFile = jspFile;
	}

	@Override
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	@Override
	public void setLoadOnStartup(int value) {
		this.loadOnStartup = value;
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
		return servletClass;
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
		synchronized (parameters) {
			parameters.put(name, value);
		}
	}

	@Override
	public void addSecurityReference(String name, String link) {

	}

	@Override
	public void deallocate(Servlet servlet) throws ServletException {
		if (!singleThreadModel) {
			countAllocated.decrementAndGet();
			return;
		}

		synchronized (instancePool) {
			countAllocated.decrementAndGet();
			instancePool.push(servlet);
			instancePool.notify();
		}

	}

	@Override
	public String findInitParameter(String name) {
		synchronized (parameters) {
			return parameters.get(name);
		}
	}

	@Override
	public String[] findInitParameters() {
		synchronized (parameters) {
			String[] results = new String[parameters.size()];
			return ((String[]) parameters.keySet().toArray(results));
		}
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
		synchronized (parameters) {
			parameters.remove(name);
		}
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
		return info;
	}

	@Override
	public void setParent(Container container) {
		if(container != null && ! (container instanceof Context)){
			throw new IllegalArgumentException(sm.getString("standardWrapper.notContext"));
		}
		super.setParent(container);
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

	/**
	 * wrapper容器不能有子容器
	 */
	@Override
	public void addChild(Container child) {
		throw new IllegalStateException(sm.getString("standardWrapper.notChild"));
	}

	@Override
	public void addContainerListener(ContainerListener listener) {

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
	public void removeChild(Container child) {

	}

	@Override
	public void removeContainerListener(ContainerListener listener) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public String getServletName() {
		return getName();
	}

	@Override
	public ServletContext getServletContext() {
		if (parent == null) {
			return null;
		} else if (!(parent instanceof Context)) {
			return null;
		}
		return ((Context) parent).getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		return findInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		synchronized (parameters) {
			return Collections.enumeration(parameters.keySet());
		}
	}

	@Override
	public void stop() throws LifecycleException {
		try {
			unload();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		super.stop();
	}
}
