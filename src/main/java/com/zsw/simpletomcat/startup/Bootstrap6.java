package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpConnector;
import com.zsw.simpletomcat.core.*;

/**
 * @author zsw
 * @date 2020/07/24 16:39
 */
public class Bootstrap6 {
	public static void main(String[] args) {
		HttpConnector connector = new HttpConnector();
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");
		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");

		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);

		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");
		LifecycleListener listener = new SimpleContextLifecycleListener();
		((Lifecycle) context).addLifecycleListener(listener);
		context.addMapper(mapper);
		Loader loader = new SimpleLoader();
		context.setLoader(loader);
		// context.addServletMapping(pattern, name);
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");
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
