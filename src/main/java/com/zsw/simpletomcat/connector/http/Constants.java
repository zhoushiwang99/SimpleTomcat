package com.zsw.simpletomcat.connector.http;

import java.io.File;

/**
 * @author zsw
 * @date 2020/07/12 19:24
 */
public class Constants {
	public static final String WEB_ROOT =
			System.getProperty("user.dir") + File.separator + "webroot";
	public static final String Package = "com.zsw.simpletomcat.connector.http";
	public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
	public static final int PROCESSOR_IDLE = 0;
	public static final int PROCESSOR_ACTIVE = 1;
}
