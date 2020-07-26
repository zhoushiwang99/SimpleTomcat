package com.zsw.simpletomcat.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author zsw
 * @date 2020/07/26 11:49
 */
public class HelloFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("-------------HelloFilter init-------------");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		response.getWriter().write("helloFilter\n");
		System.out.println("-------------HelloFilter doFilter-------------");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		System.out.println("-------------HelloFilter destory-------------");

	}
}
