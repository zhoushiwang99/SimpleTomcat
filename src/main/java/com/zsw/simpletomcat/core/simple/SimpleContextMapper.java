package com.zsw.simpletomcat.core.simple;

import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Mapper;
import com.zsw.simpletomcat.Wrapper;
import com.zsw.simpletomcat.connector.http.HttpRequest;

/**
 * @author zsw
 * @date 2020/07/23 23:44
 */
public class SimpleContextMapper implements Mapper {

	private SimpleContext context = null;

	private String protocol;
	@Override
	public Container getContainer() {
		return (context);
	}

	@Override
	public void setContainer(Container container) {
		if (!(container instanceof SimpleContext)) {
			throw new IllegalArgumentException
					("Illegal type of container");
		}
		context = (SimpleContext) container;
	}

	@Override
	public String getProtocol() {
		return protocol;
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
