package com.zsw.simpletomcat.servlet;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zsw
 * @date 2020/07/11 10:28
 */
public class PrimitiveServlet implements Servlet {

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("PrimitiveServlet init");
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		System.out.println("=======PrimitiveServlet service method=======");
		PrintWriter writer = res.getWriter();
		writer.println("Hello PrimitiveServlet");
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
		System.out.println("PrimitiveServlet destroy");
	}
}
