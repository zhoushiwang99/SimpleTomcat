package com.zsw.simpletomcat.connector;

import com.zsw.simpletomcat.connector.http.Request;
import com.zsw.simpletomcat.connector.http.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zsw
 * @date 2020/07/11 11:54
 */
public class HttpServer {
	/**
	 * 放置静态资源文件（HTML、CSS、JS等）的文件夹位置
	 */
	public static final String WEB_ROOT =
			System.getProperty("user.dir") + File.separator + "webroot";

	/**
	 * shutdown command
	 */
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	/**
	 * 是否接受shutdown命令
	 */
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
				Request request = new Request(input);
				request.parse();

				// create Response object
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();

				// close the socket
				socket.close();

				// check if the previous uri is a shutdown command
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
