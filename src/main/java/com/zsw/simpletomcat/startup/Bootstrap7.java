package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpConnector;
import com.zsw.simpletomcat.core.*;
import com.zsw.simpletomcat.logger.FileLogger;

/**
 * @author zsw
 * @date 2020/07/24 16:39
 */
public class Bootstrap7 {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");
		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");
		Loader loader = new SimpleLoader();

		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);

		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");
		LifecycleListener listener = new SimpleContextLifecycleListener();
		((Lifecycle) context).addLifecycleListener(listener);
		context.addMapper(mapper);
		context.setLoader(loader);
		// context.addServletMapping(pattern, name);
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");

		// ------ add logger --------
		System.setProperty("catalina.base", System.getProperty("user.dir"));
		FileLogger logger = new FileLogger();
		logger.setPrefix("FileLog_");
		logger.setSuffix(".txt");
		logger.setTimestamp(true);
		logger.setDirectory("webroot");
		context.setLogger(logger);

		//---------------------------

		connector.setContainer(context);
		try {
			connector.initialize();
			((Lifecycle) connector).start();
			((Lifecycle) context).start();

			// make the application wait until we press a key.
			System.in.read();
			((Lifecycle) context).stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
