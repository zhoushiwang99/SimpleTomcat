package com.zsw.simpletomcat.servlet;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zsw
 * @date 2020/07/11 10:28
 */
public class PrimitiveServlet implements Servlet {

	public void init(ServletConfig config) throws ServletException {
		System.out.println("PrimitiveServlet init");
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		System.out.println("PrimitiveServlet service");
		PrintWriter out = res.getWriter();
		out.println("Hello. Roses are red.");
		out.print("Violets are blue");
	}

	public String getServletInfo() {
		return null;
	}

	public void destroy() {
		System.out.println("PrimitiveServlet destroy");
	}
}
