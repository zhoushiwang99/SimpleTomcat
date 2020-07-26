package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一个基础阀，专门处理对SimpleWrapper类的请求。
 *
 * @author zsw
 * @date 2020/07/23 18:24
 */
public class SimpleWrapperValve implements Valve, Contained {

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
		SimpleWrapper wrapper = (SimpleWrapper) getContainer();
		System.out.println("===================Base Valve========================");
		HttpServletRequest sreq = request.getRequest();
		HttpServletResponse sres = response.getResponse();
		Servlet servlet = null;
		HttpServletRequest hreq = null;
		HttpServletResponse hres = null;
		if (sreq != null) {
			hreq = sreq;
		}
		if (sres != null) {
			hres = sres;
		}
		Context context = (Context) wrapper.getParent();
		request.setContext(context);
		try {
			servlet = wrapper.allocate();
			if (hreq != null && hres != null) {
				servlet.service(hreq, hres);
			} else {
				servlet.service(sreq, sres);
			}
			response.finishResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
