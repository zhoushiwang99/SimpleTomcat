package com.zsw.simpletomcat.processor;

import com.zsw.simpletomcat.connector.http.Request;
import com.zsw.simpletomcat.connector.http.Response;

import java.io.IOException;

/**
 * 用户处理对静态资源的请求
 *
 * @author zsw
 * @date 2020/07/11 16:21
 */
public class StaticResourceProcessor {
	public void process(Request request, Response response) {
		try {
			response.sendStaticResource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
