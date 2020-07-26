package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.Context;
import com.zsw.simpletomcat.filter.FilterDef;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zsw
 * @date 2020/07/25 20:06
 */
public class ApplicationFilterConfig implements FilterConfig {
	private Context context;
	private FilterDef filterDef;
	private Filter filter;

	public ApplicationFilterConfig(Context context, FilterDef filterDef) {
		this.context = context;
		this.filterDef = filterDef;
	}

	@Override
	public String getFilterName() {
		return filterDef.getFilterName();
	}

	@Override
	public ServletContext getServletContext() {
		return this.context.getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		Map<String, String> map = filterDef.getParameterMap();
		if(map == null) {
			return null;
		}
		return map.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		Map<String,String> map = filterDef.getParameterMap();
		if (map == null) {
			return Collections.enumeration((new ArrayList<String>()));
		} else {
			return Collections.enumeration(map.keySet());
		}
	}

	public void release() {

		if (this.filter != null) {
			filter.destroy();
		}
		this.filter = null;

	}


	Filter getFilter() throws ClassNotFoundException, NoSuchMethodException, ServletException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (this.filter != null) {
			return (this.filter);
		}
		String filterClass = filterDef.getFilterClass();
		ClassLoader classLoader = context.getLoader().getClassLoader();

		Class clazz = classLoader.loadClass(filterClass);
		this.filter = (Filter) clazz.getDeclaredConstructor().newInstance();
		filter.init(this);
		return (this.filter);
	}
}
