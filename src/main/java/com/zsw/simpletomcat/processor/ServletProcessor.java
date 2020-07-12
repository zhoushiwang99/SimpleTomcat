package com.zsw.simpletomcat.processor;

import com.zsw.simpletomcat.config.Constants;
import com.zsw.simpletomcat.connector.http.Request;
import com.zsw.simpletomcat.connector.http.RequestFacade;
import com.zsw.simpletomcat.connector.http.Response;
import com.zsw.simpletomcat.connector.http.ResponseFacade;
import com.zsw.simpletomcat.enums.HttpStatusEnum;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * @author zsw
 * @date 2020/07/11 16:23
 */
public class ServletProcessor {
	/**
	 * 请求格式: /servlet/servletName，servletName是请求资源的类名
	 *
	 * @param request
	 * @param response
	 */
	public void process(Request request, Response response) {
		String uri = request.getUri();
		// 从 URI 中获取 servlet 类名
		String servletName = "com.zsw.simpletomcat.servlet." + uri.substring(uri.lastIndexOf("/") + 1);

		//  创建一个类加载器。URLClassLoader可以加载任意路径下的类
		URLClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT);
			System.out.println(classPath.getCanonicalPath());
			String repository =
					(new URL("file", null, classPath.getCanonicalPath() +
							File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Class myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Servlet servlet = null;
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		try {
			servlet = (Servlet) myClass.getConstructor().newInstance();
			responseFacade.getWriter().println(new String(Response.responseToByte(HttpStatusEnum.OK)));
			servlet.service(requestFacade,responseFacade);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
