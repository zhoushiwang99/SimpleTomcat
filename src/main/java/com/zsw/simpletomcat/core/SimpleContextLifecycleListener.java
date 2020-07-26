package com.zsw.simpletomcat.core;

import com.zsw.simpletomcat.Lifecycle;
import com.zsw.simpletomcat.LifecycleEvent;
import com.zsw.simpletomcat.LifecycleListener;

/**
 * @author zsw
 * @date 2020/07/24 16:20
 */
public class SimpleContextLifecycleListener implements LifecycleListener {
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		Lifecycle lifecycle = event.getLifecycle();
		System.out.println("SimpleContextLifecycleListener's event " +
				event.getType().toString());
		// 容器启动
		if (Lifecycle.START_EVENT.equals(event.getType())) {
			System.out.println("Starting context.");
		}

		// 容器关闭
		else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
			System.out.println("Stopping context.");
		}
	}
}
