package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.filter.FilterDef;
import com.zsw.simpletomcat.filter.FilterMap;
import com.zsw.simpletomcat.session.StandardManager;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author zsw
 * @date 2020/07/26 10:32
 */
public class StandardContext extends ContainerBase implements Context {
	private boolean available = false;
	private String docBase = null;
	private HashMap<String, ApplicationFilterConfig> filterConfigs = new HashMap<>();
	private final HashMap<String, FilterDef> filterDefs = new HashMap<>();
	private FilterMap[] filterMaps = new FilterMap[0];
	private static final String info =
			"com.zsw.simpletomcat.core.StandardContext/1.0";
	private final HashMap<String, String> servletMappings = new HashMap<>();
	private int sessionTimeout = 30;
	private boolean paused = false;
	private String mapperClass =
			"com.zsw.simpletomcat.core.StandardContextMapper";

	public StandardContext() {
		pipeline.setBasic(new StandardContextValve());
	}

	@Override
	public void addFilterDef(FilterDef filterDef) {
		synchronized (filterDefs) {
			filterDefs.put(filterDef.getFilterName(), filterDef);
		}
	}

	@Override
	public void removeFilterDef(FilterDef filterDef) {
		synchronized (filterDefs) {
			filterDefs.remove(filterDef.getFilterName());
		}
	}

	@Override
	public void removeFilterMap(FilterMap filterMap) {
		synchronized (filterMaps) {
			// Make sure this filter mapping is currently present
			int n = -1;
			for (int i = 0; i < filterMaps.length; i++) {
				if (filterMaps[i] == filterMap) {
					n = i;
					break;
				}
			}
			if (n < 0)
				return;

			// Remove the specified filter mapping
			FilterMap results[] = new FilterMap[filterMaps.length - 1];
			System.arraycopy(filterMaps, 0, results, 0, n);
			System.arraycopy(filterMaps, n + 1, results, n,
					(filterMaps.length - 1) - n);
			filterMaps = results;

		}
	}

	public FilterMap[] findFilterMaps() {

		return filterMaps;

	}

	@Override
	public void addFilterMap(FilterMap filterMap) {
		FilterMap[] newFilterMaps = new FilterMap[filterMaps.length + 1];
		System.arraycopy(filterMaps, 0, newFilterMaps, 0, filterMaps.length);
		newFilterMaps[filterMaps.length] = filterMap;
		filterMaps = newFilterMaps;
	}

	public FilterConfig findFilterConfig(String name) {

		synchronized (filterConfigs) {
			return ((FilterConfig) filterConfigs.get(name));
		}

	}

	private FilterDef findFilterDef(String filterName) {
		synchronized (filterDefs) {
			return ((FilterDef) filterDefs.get(filterName));
		}
	}

	private boolean validateURLPattern(String urlPattern) {

		if (urlPattern == null) {
			return (false);
		}
		if (urlPattern.startsWith("*.")) {
			return urlPattern.indexOf('/') < 0;
		}
		return urlPattern.startsWith("/");

	}


	public boolean filterStart() {

		if (debug >= 1)
			log("Starting filters");

		// Instantiate and record a FilterConfig for each defined filter
		boolean ok = true;
		synchronized (filterConfigs) {
			filterConfigs.clear();
			Iterator names = filterDefs.keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				if (debug >= 1)
					log(" Starting filter '" + name + "'");
				ApplicationFilterConfig filterConfig = null;
				try {
					filterConfig = new ApplicationFilterConfig
							(this, (FilterDef) filterDefs.get(name));
					filterConfigs.put(name, filterConfig);
				} catch (Throwable t) {
					log(sm.getString("standardContext.filterStart", name), t);
					ok = false;
				}
			}
		}

		return (ok);

	}

	@Override
	public void start() throws LifecycleException {
		if (started) {
			throw new LifecycleException
					(sm.getString("containerBase.alreadyStarted", logName()));
		}

		if (debug >= 1)
			log("Starting");
		lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
		if (debug >= 1)
			log("Processing start(), current available=" + getAvailable());
		setAvailable(false);
		setConfigured(false);
		boolean ok = true;
		if (getLoader() == null) {
			setLoader(new SimpleLoader());
		}
		if (getManager() == null) {
			setManager(new StandardManager());
		}
		if (ok) {
			addDefaultMapper(this.mapperClass);
			started = true;
			if ((loader != null) && (loader instanceof Lifecycle)) {
				((Lifecycle) loader).start();
			}
			if ((logger != null) && (logger instanceof Lifecycle)) {
				((Lifecycle) logger).start();
			}
			// Start our Mappers, if any
			Mapper[] mappers = findMappers();
			for (int i = 0; i < mappers.length; i++) {
				if (mappers[i] instanceof Lifecycle) {
					((Lifecycle) mappers[i]).start();
				}
			}
			Container[] children = findChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] != null) {
					((Lifecycle) children[i]).start();
				}
			}
			if (pipeline instanceof Lifecycle) {
				((Lifecycle) pipeline).start();
			}
			filterStart();
			lifecycle.fireLifecycleEvent(START_EVENT, null);

			if ((manager != null) && (manager instanceof Lifecycle)) {
				((Lifecycle) manager).start();
			}

		}
	}

	@Override
	public void stop() throws LifecycleException {
		filterStop();
	}

	public boolean filterStop() {

		if (debug >= 1)
			log("Stopping filters");

		// Release all Filter and FilterConfig instances
		synchronized (filterConfigs) {
			Iterator names = filterConfigs.keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				if (debug >= 1)
					log(" Stopping filter '" + name + "'");
				ApplicationFilterConfig filterConfig =
						(ApplicationFilterConfig) filterConfigs.get(name);
				filterConfig.release();
			}
			filterConfigs.clear();
		}
		return (true);

	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
		while (getPaused()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.invoke(request, response);
	}

	private boolean getPaused() {
		return (this.paused);
	}

	@Override
	public Object[] getApplicationListeners() {
		return new Object[0];
	}

	@Override
	public void setApplicationListeners(Object[] listeners) {

	}

	@Override
	public boolean getAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {

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

	@Override
	public void addServletMapping(String pattern, String name) {
		if (findChild(name) == null) {
			throw new IllegalArgumentException
					(sm.getString("standardContext.servletMap.name", name));
		}
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
		return new String[0];
	}

	@Override
	public String[] findInstanceListeners() {
		return new String[0];
	}

	@Override
	public String findMimeMapping(String extension) {
		return null;
	}

	@Override
	public String[] findMimeMappings() {
		return new String[0];
	}

	@Override
	public String findParameter(String name) {
		return null;
	}

	@Override
	public String[] findParameters() {
		return new String[0];
	}

	@Override
	public String findResourceEnvRef(String name) {
		return null;
	}

	@Override
	public String[] findResourceEnvRefs() {
		return new String[0];
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
		return new String[0];
	}

	@Override
	public String findServletMapping(String pattern) {
		synchronized (servletMappings) {
			return servletMappings.get(pattern);
		}
	}

	@Override
	public String[] findServletMappings() {
		synchronized (servletMappings) {
			String[] results = new String[servletMappings.size()];
			return servletMappings.keySet().toArray(results);
		}
	}

	@Override
	public String findStatusPage(int status) {
		return null;
	}

	@Override
	public int[] findStatusPages() {
		return new int[0];
	}

	@Override
	public String findTaglib(String uri) {
		return null;
	}

	@Override
	public String[] findTaglibs() {
		return new String[0];
	}

	@Override
	public boolean findWelcomeFile(String name) {
		return false;
	}

	@Override
	public String[] findWelcomeFiles() {
		return new String[0];
	}

	@Override
	public String[] findWrapperLifecycles() {
		return new String[0];
	}

	@Override
	public String[] findWrapperListeners() {
		return new String[0];
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
}
