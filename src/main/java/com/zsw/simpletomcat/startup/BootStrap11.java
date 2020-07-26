package com.zsw.simpletomcat.startup;

import com.zsw.simpletomcat.*;
import com.zsw.simpletomcat.connector.http.HttpConnector;
import com.zsw.simpletomcat.core.*;
import com.zsw.simpletomcat.filter.FilterDef;
import com.zsw.simpletomcat.filter.FilterMap;
import com.zsw.simpletomcat.session.StandardManager;

/**
 * @author zsw
 * @date 2020/07/25 23:15
 */
public class BootStrap11 {
	public static void main(String[] args) {
		System.setProperty("catalina.base", System.getProperty("user.dir"));



		HttpConnector connector = new HttpConnector();
		Context context = new StandardContext();
		Wrapper wrapper1 = new StandardWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("com.zsw.simpletomcat.servlet.PrimitiveServlet");

		Wrapper wrapper2 = new StandardWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("com.zsw.simpletomcat.servlet.ModernServlet");

		Wrapper wrapper3 = new StandardWrapper();
		wrapper3.setName("Hello");
		wrapper3.setServletClass("com.zsw.simpletomcat.servlet.HelloServlet");

		Wrapper wrapper4 = new StandardWrapper();
		wrapper4.setName("Session");
		wrapper4.setServletClass("com.zsw.simpletomcat.servlet.SessionServlet");


		context.addChild(wrapper1);
		context.addChild(wrapper2);
		context.addChild(wrapper3);
		context.addChild(wrapper4);

		context.addServletMapping("/primitive", wrapper1.getName());
		context.addServletMapping("/modern", wrapper2.getName());
		context.addServletMapping("/hello", wrapper3.getName());
		context.addServletMapping("/myApp/Session", wrapper4.getName());

		LifecycleListener listener = new SimpleContextLifecycleListener();
		((Lifecycle) context).addLifecycleListener(listener);

		FilterMap filterMap = new FilterMap();
		filterMap.setFilterName("hello");
		filterMap.setURLPattern("/hello");

		FilterMap filterMap1 = new FilterMap();
		filterMap1.setFilterName("hello");
		filterMap1.setURLPattern("/primitive");
		context.addFilterMap(filterMap);

		FilterDef filterDef = new FilterDef();
		filterDef.setFilterName("hello");
		filterDef.setFilterClass("com.zsw.simpletomcat.filter.HelloFilter");

		context.addFilterDef(filterDef);
		Manager manager = new StandardManager();
		context.setManager(manager);

		Loader loader = new SimpleLoader();
		context.setLoader(loader);

		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");

		connector.setContainer(context);
		try {
			connector.initialize();
			((Lifecycle) connector).start();
			((Lifecycle) context).start();

			System.out.println("============>>>>             <<<<============");
			System.out.println("============>>>>             <<<<============");
			System.out.println("============>>>>Simple Tomcat<<<<============");
			System.out.println("============>>>>             <<<<============");
			System.out.println("============>>>>             <<<<============");

			// make the application wait until we press a key.
			System.in.read();
			((Lifecycle) context).stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
