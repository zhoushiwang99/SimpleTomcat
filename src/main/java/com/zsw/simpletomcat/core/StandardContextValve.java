package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.Context;
import com.zsw.simpletomcat.ValveContext;
import com.zsw.simpletomcat.Wrapper;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.processor.StaticResourceProcessor;
import com.zsw.simpletomcat.util.StringManager;
import com.zsw.simpletomcat.valves.ValveBase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zsw
 * @date 2020/07/26 10:52
 */
public class StandardContextValve extends ValveBase {
	private static final String info =
			"com.zsw.simpletomcat.core.StandardContextValve/1.0";
	private static final StringManager sm =
			StringManager.getManager("org.apache.catalina.core");

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws IOException, ServletException {
		if (request.getRequest() == null || response.getResponse() == null) {
			return;
		}
		HttpServletRequest hreq = request.getRequest();
		HttpServletResponse hres = response.getResponse();
		String requestURI = request.getRequestURI();

		Context context = (Context) getContainer();

		Wrapper wrapper = null;

		wrapper = (Wrapper) context.map(request, true);

		if (wrapper == null) {
			// 找不到请求的servlet资源，请求静态资源
			new StaticResourceProcessor().process(request, response);
			return;
		}
//		response.setContext(context);
		request.setContext(context);
		wrapper.invoke(request, response);
	}
}
