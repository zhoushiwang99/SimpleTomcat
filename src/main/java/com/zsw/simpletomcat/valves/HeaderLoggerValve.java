package com.zsw.simpletomcat.valves;

import com.zsw.simpletomcat.Contained;
import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Valve;
import com.zsw.simpletomcat.ValveContext;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author zsw
 * @date 2020/07/23 18:51
 */
public class HeaderLoggerValve implements Valve, Contained {

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
	public void invoke(HttpRequest request, HttpResponse response, ValveContext context) throws IOException, ServletException {
		context.invokeNext(request, response);
		System.out.println("===================Header Logger Valve========================");
		HttpServletRequest sreq = request.getRequest();
		if(sreq != null){
			HttpServletRequest hreq = sreq;
			Enumeration<String> headerNames = hreq.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement().toString();
				String headerValue = hreq.getHeader(headerName);
				System.out.println(headerName + ":" + headerValue);
			}
		}else{
			System.out.println("Not an HTTP Request");
		}
		System.out.println("---------------------------------------");
	}
}
