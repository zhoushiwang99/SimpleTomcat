package com.zsw.simpletomcat.connector.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * @author zsw
 * @date 2020/07/12 10:24
 */
public class HttpResponseFacade implements HttpServletResponse {
	private HttpResponse httpResponse;

	public HttpResponseFacade(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	@Override
	public String getCharacterEncoding() {
		return httpResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return httpResponse.getContentType();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return httpResponse.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return httpResponse.getWriter();
	}

	@Override
	public void setCharacterEncoding(String charset) {
		httpResponse.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len) {
		httpResponse.setContentLength(len);
	}

	@Override
	public void setContentLengthLong(long len) {
		httpResponse.setContentLengthLong(len);
	}

	@Override
	public void setContentType(String type) {
		httpResponse.setContentType(type);
	}

	@Override
	public void setBufferSize(int size) {
		httpResponse.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return httpResponse.getBufferSize();
	}

	@Override
	public void flushBuffer() throws IOException {
		httpResponse.flushBuffer();
	}

	@Override
	public void resetBuffer() {
		httpResponse.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return httpResponse.isCommitted();
	}

	@Override
	public void reset() {
		httpResponse.reset();
	}

	@Override
	public void setLocale(Locale loc) {
		httpResponse.setLocale(loc);
	}

	@Override
	public Locale getLocale() {
		return httpResponse.getLocale();
	}

	@Override
	public void addCookie(Cookie cookie) {
		httpResponse.addCookie(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return httpResponse.containsHeader(name);
	}

	@Override
	public String encodeURL(String url) {
		return httpResponse.encodeURL(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return httpResponse.encodeRedirectURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		return httpResponse.encodeUrl(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return httpResponse.encodeRedirectUrl(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpResponse.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException {
		httpResponse.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		httpResponse.sendRedirect(location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		httpResponse.setDateHeader(name, date);
	}

	@Override
	public void addDateHeader(String name, long date) {
		httpResponse.addDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		httpResponse.setHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		httpResponse.addHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		httpResponse.setIntHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		httpResponse.addIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc) {
		httpResponse.setStatus(sc);
	}

	@Override
	public void setStatus(int sc, String sm) {
		httpResponse.setStatus(sc, sm);
	}

	@Override
	public int getStatus() {
		return httpResponse.getStatus();
	}

	@Override
	public String getHeader(String name) {
		return httpResponse.getHeader(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return httpResponse.getHeaders(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return httpResponse.getHeaderNames();
	}
}
