package com.zsw.simpletomcat.connector.http;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 等待HTTP请求
 *
 * @author zsw
 * @date 2020/07/12 11:44
 */
public class HttpConnector implements Runnable {
	boolean stopped;
	private String scheme = "http";

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stopped) {
			// 获取socket连接
			Socket socket;
			try{
				socket = serverSocket.accept();
			}catch (Exception e){
				continue;
			}
			HttpProcessor processor = new HttpProcessor();
			processor.process(socket);
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
}
