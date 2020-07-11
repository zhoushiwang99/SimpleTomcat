package com.zsw.simpletomcat.connector.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zsw
 * @date 2020/07/11 11:39
 */
public class Request {
	private InputStream input;
	private String uri;

	public Request(InputStream input) {
		this.input = input;
	}

	/**
	 * 用于解析HTTP请求的原始数据。
	 */
	public void parse() {
		StringBuffer request = new StringBuffer(2048);
		int i;
		byte[] buffer = new byte[2048];
		try {
			i = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		for (int j = 0; j < i; j++) {
			request.append((char) buffer[j]);
		}
		System.out.println(request.toString());
		uri = parseUri(request.toString());
	}

	/**
	 * 从请求行获取URI
	 *
	 * @param requestString
	 * @return
	 */
	private String parseUri(String requestString) {
		int index1, index2;
		index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1) {
				return requestString.substring(index1 + 1, index2);
			}
		}
		return null;
	}

	public String getUri() {
		return uri;
	}
}
