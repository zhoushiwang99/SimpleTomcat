package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpConnector;
import com.zsw.simpletomcat.core.SimpleContext;
import com.zsw.simpletomcat.core.SimpleContextMapper;
import com.zsw.simpletomcat.core.SimpleLoader;
import com.zsw.simpletomcat.core.SimpleWrapper;
import com.zsw.simpletomcat.valves.ClinetIPLoggerValve;
import com.zsw.simpletomcat.valves.HeaderLoggerValve;

/**
 * @author zsw
 * @date 2020/07/24 12:11
 */
public class BootStrap2 {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();

		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("com.zsw.simpletomcat.servlet.PrimitiveServlet");
		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("com.zsw.simpletomcat.servlet.ModernServlet");

		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);

		Valve valve1 = new HeaderLoggerValve();
		Valve valve2 = new ClinetIPLoggerValve();

		((Pipeline)context).addValve(valve1);
		((Pipeline)context).addValve(valve2);

		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");

		context.addMapper(mapper);
		Loader loader = new SimpleLoader();

		context.setLoader(loader);
		context.addServletMapping("/Primitive","Primitive");
		context.addServletMapping("/Modern","Modern");

		connector.setContainer(context);

		try{
			connector.initialize();;
			connector.start();
			System.in.read();
		}catch (Exception e) {
			e.printStackTrace();;
		}
	}

}
