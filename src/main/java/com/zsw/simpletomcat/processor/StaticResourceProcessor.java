package com.zsw.simpletomcat.processor;

import com.zsw.simpletomcat.connector.http.HttpRequest;
import com.zsw.simpletomcat.connector.http.HttpResponse;

import java.io.IOException;

/**
 * 用户处理对静态资源的请求
 *
 * @author zsw
 * @date 2020/07/11 16:21
 */
public class StaticResourceProcessor {
	public void process(HttpRequest request, HttpResponse response) {
		try {
			response.sendStaticResource();
			response.finishResponse();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
