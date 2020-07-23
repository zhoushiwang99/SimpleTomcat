/*
package com.zsw.simpletomcat.connector;

import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;
import com.zsw.simpletomcat.processor.ServletProcessor;
import com.zsw.simpletomcat.processor.StaticResourceProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

*/
/**
 * @author zsw
 * @date 2020/07/11 11:54
 *//*

public class HttpServer {
	*/
/**
	 * 放置静态资源文件（HTML、CSS、JS等）的文件夹位置
	 *//*

	public static final String WEB_ROOT =
			System.getProperty("user.dir") + File.separator + "webroot";

	*/
/**
	 * shutdown command
	 *//*

	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	*/
/**
	 * 标识Tomcat是否关闭
	 *//*

	private boolean shutdown = false;

	public static void main(String[] args) {
		System.out.println(WEB_ROOT);
		HttpServer server = new HttpServer();
		server.await();
	}

	private void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();

				// create Request object and parse
				HttpRequest httpRequest = new HttpRequest(input);
				httpRequest.parse();

				// create Response object
				HttpResponse httpResponse = new HttpResponse(output);
				httpResponse.setHttpRequest(httpRequest);

				// 检查这个request请求的是 静态资源 or servlet
				if(httpRequest.getUri().startsWith("/servlet/")) {
					ServletProcessor processor = new ServletProcessor();
					processor.process(httpRequest, httpResponse);
				}else{
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(httpRequest, httpResponse);
				}

				// close the socket
				socket.close();

				// check if the previous uri is a shutdown command
				shutdown = httpRequest.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
*/
