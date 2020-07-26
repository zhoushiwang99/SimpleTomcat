package com.zsw.simpletomcat.core.simple;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;



public class SimpleContainer implements Container {

  public static final String WEB_ROOT =
    System.getProperty("user.dir") + File.separator  + "webroot";

  public SimpleContainer() {
  }

  @Override
  public String getInfo() {
    return null;
  }

  @Override
  public Loader getLoader() {
    return null;
  }

  @Override
  public void setLoader(Loader loader) {
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
    return null;
  }

  @Override
  public ContainerListener[] findContainerListeners() {
    return null;
  }

  @Override
  public Mapper findMapper(String protocol) {
    return null;
  }

  @Override
  public Mapper[] findMappers() {
    return null;
  }

  @Override
  public void invoke(HttpRequest request, HttpResponse response)
    throws IOException, ServletException {

    String servletName = ( (HttpServletRequest) request).getRequestURI();
    servletName = servletName.substring(servletName.lastIndexOf("/") + 1);
    URLClassLoader loader = null;
    try {
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(WEB_ROOT);
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class myClass = null;
    try {
      myClass = loader.loadClass(servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      servlet = (Servlet) myClass.newInstance();
      servlet.service((HttpServletRequest) request, (HttpServletResponse) response);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }



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

  }

  @Override
  public void stop() throws LifecycleException {

  }
}