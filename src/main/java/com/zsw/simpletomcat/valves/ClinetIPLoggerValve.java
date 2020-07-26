package com.zsw.simpletomcat.valves;

import com.zsw.simpletomcat.Contained;
import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Valve;
import com.zsw.simpletomcat.ValveContext;
import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * 此类表示的阀用来将客户端的IP地址输出到控制台上
 * @author zsw
 * @date 2020/07/23 18:47
 */
public class ClinetIPLoggerValve implements Valve, Contained {
	protected Container container;

	@Override
	public Container getContainer() {
		return this.container;
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
		// 先调用方法参数valveContext的下一个阀
		context.invokeNext(request,response);
		System.out.println("===================Client Ip Logger Valve========================");

		// 在输出Ip地址
		ServletRequest sreq = request.getRequest();
		System.out.println(sreq.getRemoteAddr());
		System.out.println("-------------------------------------");
	}
}
