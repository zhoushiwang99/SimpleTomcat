package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * 载入相关的Servlet类
 * @author zsw
 * @date 2020/07/23 18:09
 */
public class SimpleLoader implements Loader, Lifecycle {
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	private ClassLoader classLoader;
	private Container container;

	public SimpleLoader() {
		try{
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(WEB_ROOT);
			String repository = (new URL("file",null,classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null,repository,streamHandler);
			classLoader = new URLClassLoader(urls);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
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
	public boolean getDelegate() {
		return false;
	}

	@Override
	public void setDelegate(boolean delegate) {

	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public boolean getReloadable() {
		return false;
	}

	@Override
	public void setReloadable(boolean reloadable) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void addRepository(String repository) {

	}

	@Override
	public String[] findRepositories() {
		return new String[0];
	}

	@Override
	public boolean modified() {
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public void start() throws LifecycleException {
		System.out.println("Starting SimpleLoader");
	}

	@Override
	public void stop() throws LifecycleException {

	}
}
