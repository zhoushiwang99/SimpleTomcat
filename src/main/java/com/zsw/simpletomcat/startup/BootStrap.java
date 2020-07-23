package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.connector.http.HttpConnector;

/**
 * 启动类
 *
 * @author zsw
 * @date 2020/07/12 11:43
 */
public class BootStrap {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		connector.start();
	}
}
