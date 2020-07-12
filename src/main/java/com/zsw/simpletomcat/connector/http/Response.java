package com.zsw.simpletomcat.connector.http;

import com.zsw.simpletomcat.connector.HttpServer;
import com.zsw.simpletomcat.constant.HttpVersionConstant;
import com.zsw.simpletomcat.enums.HttpStatusEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

/**
 * @author zsw
 * @date 2020/07/11 11:51
 */
public class Response implements ServletResponse {
	private static final int BUFFER_SIZE = 1024;
	Request request;
	OutputStream output;
	PrintWriter writer;
	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * 发送一个静态资源到浏览器
	 * @throws IOException
	 */
	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			File file = new File(HttpServer.WEB_ROOT, request.getUri());
			if (file.exists()) {
				fis = new FileInputStream(file);
				int ch = fis.read(bytes, 0, BUFFER_SIZE);
				output.write(responseToByte(HttpStatusEnum.OK));
				while (ch != -1) {
					output.write(bytes, 0, ch);
					ch = fis.read(bytes, 0, BUFFER_SIZE);
				}
			} else {
				// 文件不存在
				String errorMessage = "HTTP/1.1 File Not Found\r\n" +
						"Content-type: text/html\r\n" +
						"Content-Length: 23\r\n" +
						"\r\n" +
						"<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}
	/**
	 * 将请求行 请求头转换为byte数组
	 * @param status
	 * @return
	 */
	public static byte[] responseToByte(HttpStatusEnum status) {
		return new StringBuilder().append(HttpVersionConstant.HTTP_1_1).append(" ")
				.append(status.getStatus()).append(" ")
				.append(status.getDesc()).append("\r\n\r\n")
				.toString().getBytes();
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// 开启autoFlush，调用 println() 将会刷新输出，但是调用 print() 方法不会
		writer = new PrintWriter(output,true);
		return writer;
	}

	@Override
	public void setCharacterEncoding(String charset) {

	}

	@Override
	public void setContentLength(int len) {

	}

	@Override
	public void setContentLengthLong(long len) {

	}

	@Override
	public void setContentType(String type) {

	}

	@Override
	public void setBufferSize(int size) {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setLocale(Locale loc) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}
}
