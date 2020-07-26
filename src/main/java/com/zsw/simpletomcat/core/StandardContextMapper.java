package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Mapper;
import com.zsw.simpletomcat.Wrapper;
import com.zsw.simpletomcat.connector.http.Constants;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.util.StringManager;

/**
 * @author zsw
 * @date 2020/07/26 11:36
 */
public class StandardContextMapper implements Mapper {

	private StandardContext context = null;

	private String protocol = null;

	private static final StringManager sm =
			StringManager.getManager(Constants.Package);

	@Override
	public Container getContainer() {
		return context;
	}

	@Override
	public void setContainer(Container container) {
		if (!(container instanceof StandardContext)) {
			throw new IllegalArgumentException
					(sm.getString("httpContextMapper.container"));
		}
		context = (StandardContext) container;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public Container map(HttpRequest request, boolean update) {
		String contextPath =
				request.getRequest().getContextPath();
		String requestURI = request.getRequestURI();
		String relativeURI = requestURI.substring(contextPath.length());
		Wrapper wrapper = null;

		String servletName = context.findServletMapping(relativeURI);
		if (servletName != null) {
			wrapper = (Wrapper) context.findChild(servletName);
		}
		return wrapper;
	}
}
