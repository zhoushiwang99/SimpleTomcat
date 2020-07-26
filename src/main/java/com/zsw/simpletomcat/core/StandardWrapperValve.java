package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.ValveContext;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.filter.FilterDef;
import com.zsw.simpletomcat.filter.FilterMap;
import com.zsw.simpletomcat.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础阀，创建过滤器链等
 * @author zsw
 * @date 2020/07/25 18:01
 */
public class StandardWrapperValve extends ValveBase {

	private int debug = 0;
	private FilterDef filterDef = null;


	/**
	 * The descriptive information related to this implementation.
	 */
	private static final String info =
			"org.apache.catalina.core.StandardWrapperValve/1.0";

	@Override
	public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws IOException, ServletException {
		StandardWrapper wrapper = (StandardWrapper) getContainer();
		HttpServletRequest hreq = request.getRequest();
		HttpServletResponse hres = response.getResponse();
		Servlet servlet = null;
		servlet = wrapper.allocate();
		ApplicationFilterChain filterChain = createFilterChain(request, servlet);
		if(servlet != null && filterChain != null) {
			filterChain.doFilter(hreq,hres);
		}
		filterChain.release();
		response.finishResponse();
	}

	private ApplicationFilterChain createFilterChain(HttpRequest request,
	                                                 Servlet servlet) {
		if (servlet == null) {
			return null;
		}
		ApplicationFilterChain filterChain =
				new ApplicationFilterChain();
		filterChain.setServlet(servlet);
		StandardWrapper wrapper = (StandardWrapper) getContainer();
		StandardContext context = (StandardContext) wrapper.getParent();
		FilterMap[] filterMaps = context.findFilterMaps();
		if ((filterMaps == null) || (filterMaps.length == 0)) {
			return filterChain;
		}
		String requestPath = null;
		HttpServletRequest hreq = request.getRequest();
		String contextPath = hreq.getContextPath();
		if (contextPath == null) {
			contextPath = "";
		}
		String requestURI = hreq.getRequestURI();
		if (requestURI.length() >= contextPath.length()) {
			requestPath = requestURI.substring(contextPath.length());
		}
		String servletName = wrapper.getName();
		int n = 0;
		for(int i = 0; i < filterMaps.length;i++) {
			if (!matchFiltersURL(filterMaps[i], requestPath)) {
				continue;
			}
			ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMaps[i].getFilterName());
			if(filterConfig == null) {
				continue;
			}
			filterChain.addFilter(filterConfig);
			n++;
		}

		for (int i = 0; i < filterMaps.length; i++) {
			if (!matchFiltersServlet(filterMaps[i], servletName)) {
				continue;
			}
			ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)
					context.findFilterConfig(filterMaps[i].getFilterName());
			if (filterConfig == null) {
				continue;
			}
			filterChain.addFilter(filterConfig);
			n++;
		}
		return filterChain;
	}
	private boolean matchFiltersURL(FilterMap filterMap,
	                                String requestPath) {

		if (requestPath == null) {
			return (false);
		}

		// Match on context relative request path
		String testPath = filterMap.getURLPattern();
		if (testPath == null) {
			return (false);
		}

		// Case 1 - Exact Match
		if (testPath.equals(requestPath)) {
			return (true);
		}

		// Case 2 - Path Match ("/.../*")
		if ("/*".equals(testPath)) {
			return (true);
		}
		if (testPath.endsWith("/*")) {
			String comparePath = requestPath;
			while (true) {
				if (testPath.equals(comparePath + "/*")) {
					return (true);
				}
				int slash = comparePath.lastIndexOf('/');
				if (slash < 0) {
					break;
				}
				comparePath = comparePath.substring(0, slash);
			}
			return (false);
		}

		if (testPath.startsWith("*.")) {
			int slash = requestPath.lastIndexOf('/');
			int period = requestPath.lastIndexOf('.');
			if ((slash >= 0) && (period > slash)) {
				return (testPath.equals("*." +
						requestPath.substring(period + 1)));
			}
		}

		return (false);

	}


	private boolean matchFiltersServlet(FilterMap filterMap,
	                                    String servletName) {

		if (servletName == null) {
			return (false);
		} else {
			return (servletName.equals(filterMap.getServletName()));
		}

	}
}
