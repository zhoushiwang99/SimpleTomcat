package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SimpleContext的基础阀
 *
 * @author zsw
 * @date 2020/07/23 22:49
 */
public class SimpleContextValve implements Valve, Contained {

	protected Container container;


	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws IOException, ServletException {
		// Validate the request and response object types
		if (request.getRequest() == null ||
				response.getResponse() == null) {
			return;     // NOTE - Not much else we can do generically
		}

		// Disallow any direct access to resources under WEB-INF or META-INF
		HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
		String contextPath = hreq.getContextPath();
		String requestURI = ((HttpRequest) request).getRequestURI();
		String relativeURI =
				requestURI.substring(contextPath.length()).toUpperCase();

		Context context = (Context) getContainer();
		// Select the Wrapper to be used for this Request
		Wrapper wrapper = null;
		try {
			wrapper = (Wrapper) context.map(request, true);
		} catch (IllegalArgumentException e) {
			badRequest(requestURI, (HttpServletResponse) response.getResponse());
			return;
		}
		if (wrapper == null) {
			notFound(requestURI, (HttpServletResponse) response.getResponse());
			return;
		}
		// Ask this Wrapper to process this Request
		request.setContext(context);
		wrapper.invoke(request, response);
	}

	private void badRequest(String requestURI, HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}

	private void notFound(String requestURI, HttpServletResponse response) {
		try {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
}
