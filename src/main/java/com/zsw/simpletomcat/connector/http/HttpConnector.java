package com.zsw.simpletomcat.connector.http;

import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Lifecycle;
import com.zsw.simpletomcat.LifecycleException;
import com.zsw.simpletomcat.LifecycleListener;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

/**
 * 等待HTTP请求
 *
 * @author zsw
 * @date 2020/07/12 11:44
 */
public class HttpConnector implements Runnable, Lifecycle {
	boolean stopped;
	private String scheme = "http";
	private Container container;

	private Stack<HttpProcessor> processors = new Stack<>();
	private int minProcessors = 5;
	private int maxProcessors = 20;


	public String getScheme() {
		return scheme;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		while (!stopped) {
			// 获取socket连接
			Socket socket;
			try{
				socket = serverSocket.accept();
			}catch (Exception e){
				continue;
			}
			HttpProcessor processor = new HttpProcessor(this);
			processor.process(socket);
		}
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {

	}

	@Override
	public void start() {
		System.out.println("LOVE LFR Container[info]: HttpConnector start");
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void stop() throws LifecycleException {
		System.out.println("LOVE LFR Container[info]: HttpConnector stop");
	}


	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public void initialize() {
		int curProcessors = processors.size();
		while(curProcessors < minProcessors) {
			if(maxProcessors > 0 && curProcessors >= maxProcessors) {
				break;
			}
			HttpProcessor processor = new HttpProcessor(this);
			recycleProcessor(processor);
			curProcessors ++;
		}
	}

	public HttpProcessor createProcessor() {
		if(!processors.empty()) {
			HttpProcessor processor = processors.pop();
			return processor;
		}
		return null;
	}

	public void recycleProcessor(HttpProcessor processor) {
		processors.push(processor);
	}


}
