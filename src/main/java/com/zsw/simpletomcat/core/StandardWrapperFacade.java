package com.zsw.simpletomcat.core;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * @author zsw
 * @date 2020/07/25 18:24
 */
public class StandardWrapperFacade implements ServletConfig {

	private ServletConfig config = null;

	public StandardWrapperFacade(StandardWrapper config) {
		this.config = (ServletConfig) config;
	}

	@Override
	public String getServletName() {
		return config.getServletName();
	}

	@Override
	public ServletContext getServletContext() {

		return config.getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		return config.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return config.getInitParameterNames();
	}
}
