package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.Loader;
import com.zsw.simpletomcat.Pipeline;
import com.zsw.simpletomcat.Valve;
import com.zsw.simpletomcat.Wrapper;
import com.zsw.simpletomcat.connector.http.HttpConnector;
import com.zsw.simpletomcat.core.SimpleLoader;
import com.zsw.simpletomcat.core.simple.SimpleWrapper;
import com.zsw.simpletomcat.valves.ClinetIPLoggerValve;
import com.zsw.simpletomcat.valves.HeaderLoggerValve;

/**
 * @author zsw
 * @date 2020/07/23 18:56
 */
public class BootStrap1 {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		Wrapper wrapper = new SimpleWrapper();
		wrapper.setServletClass("com.zsw.simpletomcat.servlet.ModernServlet");

		Loader loader = new SimpleLoader();
		Valve valve1 = new HeaderLoggerValve();
		Valve valve2 = new ClinetIPLoggerValve();

		wrapper.setLoader(loader);

		((Pipeline)wrapper).addValve(valve1);
		((Pipeline)wrapper).addValve(valve2);

		connector.setContainer(wrapper);

		try{
			connector.initialize();
			connector.start();

			// 可以使用户在控制台按回车键来关闭程序
			System.in.read();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
