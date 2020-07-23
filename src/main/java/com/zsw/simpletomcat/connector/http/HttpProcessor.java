package com.zsw.simpletomcat.connector.http;

import com.zsw.simpletomcat.processor.ServletProcessor;
import com.zsw.simpletomcat.processor.StaticResourceProcessor;
import com.zsw.simpletomcat.util.RequestUtil;
import com.zsw.simpletomcat.util.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 用于将http请求封装成request和response并执行相应操作
 *
 * @author zsw
 * @date 2020/07/12 11:54
 */
public class HttpProcessor {

	HttpRequest httpRequest;
	HttpResponse httpResponse;
	HttpRequestLine requestLine = new HttpRequestLine();

	/**
	 * The string manager for this package.
	 */
	protected StringManager sm =
			StringManager.getManager("com.zsw.simpletomcat.connector.http");

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
			httpResponse.setRequest(httpRequest);

			httpResponse.setHeader("Server", "LOVE LFR Container");
			httpResponse.setDateHeader("Date",System.currentTimeMillis());

			// 解析请求
			parseRequest(input, output);
			parseHeaders(input);

			// 检查这个request请求的是 静态资源 or servlet
			if (httpRequest.getRequestURI().startsWith("/servlet/")) {
				ServletProcessor processor = new ServletProcessor();
				processor.process(httpRequest, httpResponse);
			} else {
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(httpRequest, httpResponse);
			}
			// close the socket
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
		while (true) {
			HttpHeader header = new HttpHeader();
			input.readHeader(header);
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}
			String name = new String(header.name,0,header.nameEnd);
			String value = new String(header.value,0,header.valueEnd);
			httpRequest.addHeader(name,value);
			if("cookie".equals(name)) {
				// parse cookies here
				Cookie[] cookies = RequestUtil.parseCookieHeader(value);
				for(int i = 0; i < cookies.length; i++) {
					if(cookies[i].getName().equals("jsessionid")) {
						if(!httpRequest.isRequestedSessionIdFromCookie()) {
							httpRequest.setRequestedSessionId(cookies[i].getValue());
							httpRequest.setRequestedSessionCookie(true);
							httpRequest.setRequestedSessionURL(false);
						}
					}
					httpRequest.addCookie(cookies[i]);
				}
			} else if("content-length".equals(name)) {
				int n = -1;
				try{
					n = Integer.parseInt(value);
				}catch (Exception e) {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
				}
				httpRequest.setContentLength(n);
			}
			else if("content-type".equals(name)) {
				httpRequest.setContentType(value);
			}
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

}
