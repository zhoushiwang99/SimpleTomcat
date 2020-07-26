package com.zsw.simpletomcat.connector.http;

import com.zsw.simpletomcat.util.RequestUtil;
import com.zsw.simpletomcat.util.StringManager;
import com.zsw.simpletomcat.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于将http请求封装成request和response并执行相应操作
 *
 * @author zsw
 * @date 2020/07/12 11:54
 */
public class HttpProcessor implements Runnable {

	HttpRequest httpRequest;
	HttpResponse httpResponse;
	HttpRequestLine requestLine = new HttpRequestLine();


	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	/**
	 * 表明此处理器可获得socket
	 */
	private boolean available = false;

	/**
	 * 表明是否有错误发生
	 */
	private boolean ok = true;

	/**
	 * 表明是否应该调用response的finishResponse()
	 */
	private boolean finishResponse = true;

	/**
	 * 用于唤醒其他处理器，当此处理器退出时用于唤醒其他处理器
	 */
	private static final Object threadSync = new Object();


	/**
	 * 与httpProcessor关联的连接器
	 */
	private HttpConnector connector;

	/**
	 * 表明此处理器是否停止
	 */
	private boolean stopped;

	/**
	 * The string manager for this package.
	 */
	protected StringManager sm =
			StringManager.getManager("com.zsw.simpletomcat.connector.http");

	private Socket socket;

	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;
		try {
			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();

			// 创建 HttpRequest
			httpRequest = new HttpRequest(input);
			// 创建 HttpResponse
			httpResponse = new HttpResponse(output);

			httpRequest.setResponse(httpResponse);
			httpResponse.setRequest(httpRequest);


			httpResponse.setHeader("Server", "LOVE LFR Container");
			httpResponse.setDateHeader("Date", System.currentTimeMillis());

			// 解析请求
			parseRequest(input, output);
			parseHeaders(input);

			this.connector.getContainer().invoke(httpRequest, httpResponse);

//			// 检查这个request请求的是 静态资源 or servlet
//			if (httpRequest.getRequestURI().startsWith("/servlet/")) {
//				ServletProcessor processor = new ServletProcessor();
//				processor.process(httpRequest, httpResponse);
//			} else {
//				StaticResourceProcessor processor = new StaticResourceProcessor();
//				processor.process(httpRequest, httpResponse);
//			}
//			// close the socket

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * HTTP协议格式可访问以下链接
	 * <href>https://gitee.com/simple-one/CloudImage/raw/master/img/HTTP协议格式.png</href>
	 *
	 * @param input
	 * @throws IOException
	 * @throws ServletException
	 */
	private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
		StringBuilder sb = new StringBuilder();

		// 读取请求头，包括了post中的表单数据
		int i;
		while ((i = input.read()) > -1 && input.available() > 0) {
			sb.append((char) i);
		}
		sb.append((char) i);

		// 将读取的请求头字符串通过换行符分割成单行
		Queue<String> headers = Stream.of(sb.toString().split("\r\n"))
				.collect(Collectors.toCollection(LinkedList::new));

		while (!headers.isEmpty()) {
			String header = headers.poll();
			if (StringUtil.isEmpty(header)) {// 读到空行则说明请求头读取完毕
				break;
			}
			String[] keyValue = header.split(": ");
			try {
				httpRequest.addHeader(keyValue[0], keyValue[1]);
			} catch (Exception e) {
				throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
			}
		}

		// 读到空行后还有数据则是post请求的表单数据
		if (!headers.isEmpty()) {
			httpRequest.setPostString(headers.poll());
		}
		// 处理部分请求头(content-length content-type cookie)
		String contentLength = httpRequest.getHeader("content-length");
		if (contentLength != null) {
			try {
				httpRequest.setContentLength(Integer.parseInt(contentLength));
			} catch (Exception e) {
				throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
			}
		}
		httpRequest.setContentType(httpRequest.getHeader("content-type"));
		httpRequest.setCharacterEncoding(RequestUtil.parseCharacterEncoding(httpRequest.getContentType()));

		Cookie[] cookies = RequestUtil.parseCookieHeader(httpRequest.getHeader("cookie"));
		Optional.ofNullable(cookies).ifPresent(cookies2 ->
				Stream.of(cookies2).forEach(cookie -> httpRequest.addCookie(cookie))
		);

		if (!httpRequest.isRequestedSessionIdFromCookie() && cookies != null) {
			Stream.of(cookies)
					.filter(cookie -> "jsessionid".equals(cookie.getName().toLowerCase()))
					.findAny()
					.ifPresent(cookie -> {
						httpRequest.setRequestedSessionId(cookie.getValue());
						httpRequest.setRequestedSessionCookie(true);
						httpRequest.setRequestedSessionURL(false);
					});
		}
	}

	private void parseRequest(SocketInputStream input, OutputStream output) throws IOException, ServletException {
		input.readRequestLine(requestLine);
		String method = new String(requestLine.method, 0, requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
		if (method.length() < 1) {
			throw new ServletException("Missing Http request method");
		} else if (requestLine.uriEnd < 1) {
			throw new ServletException("Missing Http request URI");
		}
		int question = requestLine.indexOf("?");
		if (question >= 0) {
			httpRequest.setQueryString(new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			httpRequest.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}
		if (!uri.startsWith("/")) {
			int pos = uri.indexOf("://");
			if (pos != -1) {
				pos = uri.indexOf("/", pos + 3);
				if (pos == -1) {
					uri = "";
				} else {
					uri = uri.substring(pos);
				}
			}
		}
		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(";");
			if (semicolon2 >= 0) {
				httpRequest.setRequestedSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);
			} else {
				httpRequest.setRequestedSessionId(rest);
				rest = "";
			}
			httpRequest.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			httpRequest.setRequestedSessionId(null);
			httpRequest.setRequestedSessionURL(false);
		}

		String normalizedUri = normalize(uri);
		httpRequest.setMethod(method);
		httpRequest.setProtocol(protocol);
		if (normalizedUri != null) {
			httpRequest.setRequestURI(normalizedUri);
		} else {
			httpRequest.setRequestURI(uri);
		}
		if (normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "`");
		}
	}

	/**
	 * Return a context-relative path, beginning with a "/", that represents
	 * the canonical version of the specified path after ".." and "." elements
	 * are resolved out.  If the specified path attempts to go outside the
	 * boundaries of the current context (i.e. too many ".." path elements
	 * are present), return <code>null</code> instead.
	 *
	 * @param path Path to be normalized
	 */
	protected String normalize(String path) {
		if (path == null) {
			return null;
		}
		// Create a place for the normalized path
		String normalized = path;

		// 在开头将'/％7E'和'/％7e'规范化为 '/〜'
		if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e")) {
			normalized = "/~" + normalized.substring(4);
		}

		// 停止编码 '%', '/', '.', '\',这是特殊保留的
		if ((normalized.indexOf("%25") >= 0)
				|| (normalized.indexOf("%2F") >= 0)
				|| (normalized.indexOf("%2E") >= 0)
				|| (normalized.indexOf("%5C") >= 0)
				|| (normalized.indexOf("%2f") >= 0)
				|| (normalized.indexOf("%2e") >= 0)
				|| (normalized.indexOf("%5c") >= 0)) {
			return null;
		}

		if ("/.".equals(normalized)) {
			return "/";
		}

		// 规范化反斜杠
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');

		// 如果uri不是以斜杠开头则添加一个斜杠
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// 将双斜杠替换为单斜杠
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) +
					normalized.substring(index + 1);
		}

		// 将 "/./"替换为单斜杠
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) +
					normalized.substring(index + 2);
		}

		// 将 "/../"替换为单斜杠
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0) {
				break;
			}
			if (index == 0) {
				return (null);  // Trying to go outside our context
			}
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) +
					normalized.substring(index + 3);
		}

		// 将"/..." 出现三点或更多判断为非法请求
		// 在一些windows平台上这会浏览目录树
		if (normalized.indexOf("/...") >= 0)
			return (null);

		// Return the normalized path that we have completed
		return (normalized);

	}


	@Override
	public void run() {
		while (!stopped) {
			Socket socket = await();
			if (socket == null) {
				continue;
			}
			try {
				process(socket);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 执行完毕后放回处理器池
			connector.recycleProcessor(this);
		}
		synchronized (threadSync) {
			threadSync.notifyAll();
		}
	}

	/**
	 * 等待socket
	 *
	 * @return 返回此对象的socket
	 */
	private synchronized Socket await() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Socket socket = this.socket;
		available = false;
		notifyAll();
		return socket;
	}

	/**
	 * 从外部调用，用于分配socket，同时唤醒run()中的await()中的wait()，使方法继续执行下去
	 * HttpConnector调用此方法，用于将socket分配给处理器进行处理，被唤醒后将传递的socket设置为成员变量
	 * 同时将available设为true，使await()被唤醒后可以跳出循环
	 *
	 * @param socket
	 */
	synchronized void assign(Socket socket) {
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.socket = socket;
		available = true;
		notifyAll();
	}
}
