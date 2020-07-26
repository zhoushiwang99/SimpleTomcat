package com.zsw.simpletomcat.session;

import com.zsw.simpletomcat.Lifecycle;
import com.zsw.simpletomcat.LifecycleException;
import com.zsw.simpletomcat.LifecycleListener;
import com.zsw.simpletomcat.Session;
import com.zsw.simpletomcat.util.LifecycleSupport;

import java.io.IOException;

/**
 * Manager接口的标准实现，将Session对存于内存中
 *
 * @author zsw
 * @date 2020/07/24 18:42
 */
public class StandardManager extends ManagerBase implements Lifecycle, Runnable {
	/**
	 * 检查过期session之间（秒）的时间间隔
	 */
	private int checkInterval = 60;

	/**
	 * 存活session的最大数量，-1表示没有限制
	 */
	private int maxActiveSessions = -1;

	/**
	 * session存储在介质中的文件名
	 */
	private String pathname = "SESSIONS.ser";

	/**
	 * 是否已启动
	 */
	private boolean started = false;

	/**
	 * 后台线程.
	 */
	private Thread thread = null;


	/**
	 * 后台线程是否已完成
	 */
	private boolean threadDone = false;

	/**
	 * 后台线程名
	 */
	private String threadName = "StandardManager";

	protected LifecycleSupport lifecycle = new LifecycleSupport(this);


	// 日志级别
	protected int debug = 0;

	/**
	 * 检查过期键之间的时间间隔
	 *
	 * @return
	 */
	public int getCheckInterval() {
		return (this.checkInterval);
	}

	public void setCheckInterval(int second) {
		this.checkInterval = second;
	}

	/**
	 * 最大存活session数量
	 *
	 * @return
	 */
	public int getMaxActiveSessions() {

		return (this.maxActiveSessions);

	}

	public void setMaxActiveSessions(int max) {
		this.maxActiveSessions = max;
	}

	public String getPathname() {

		return (this.pathname);

	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	@Override
	public Session createSession() {
		// session存活数量超出限制则抛异常
		if ((maxActiveSessions >= 0) &&
				(sessions.size() >= maxActiveSessions)) {
			throw new IllegalStateException
					(sm.getString("standardManager.createSession.ise 存活的session数量超出限制"));
		}
		Session session = super.createSession();
		sessions.put(session.getId(),session);
		return session;
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public void start() throws LifecycleException {
		if (started) {
			throw new LifecycleException
					(sm.getString("standardManager.alreadyStarted"));
		}
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		started = true;
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		threadStart();
	}

	private void threadStart() {
		if (thread != null) {
			return;
		}
		threadDone = false;
		threadName = "StandardManager[" + container.getName() + "]";
		thread = new Thread(this, threadName);
		thread.setDaemon(true);
		thread.setContextClassLoader(container.getLoader().getClassLoader());
		thread.start();
	}

	@Override
	public void stop() throws LifecycleException {
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;
		// Stop the background reaper thread
		threadStop();
		try {
			// 序列化保存session
			unload();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 将所有存活的session设置为过期
		Session[] sessions = findSessions();
		for (int i = 0; i < sessions.length; i++) {
			StandardSession session = (StandardSession) sessions[i];
			if (!session.isValid()) {
				continue;
			}
			try {
				session.expire(true);
			} catch (Throwable t) {
				;
			}
		}
	}

	private void threadStop() {
		if (thread == null) {
			return;
		}

		threadDone = true;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			;
		}

		thread = null;
	}


	/**
	 * 删除无效或已过期的session
	 */
	private void processExpires() {
		long timeNow = System.currentTimeMillis();
		Session sessions[] = findSessions();

		for (int i = 0; i < sessions.length; i++) {
			StandardSession session = (StandardSession) sessions[i];
			if (!session.isValid()) {
				continue;
			}
			// 获取session超时时间
			int maxInactiveInterval = session.getMaxInactiveInterval();
			if (maxInactiveInterval < 0) {
				continue;
			}
			int timeIdle = // Truncate, do not round up
					(int) ((timeNow - session.getLastAccessedTime()) / 1000L);
			if (timeIdle >= maxInactiveInterval) {
				try {
					session.expire(true);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 * 过期session删除线程的休眠时间
	 */
	private void threadSleep() {

		try {
			Thread.sleep(checkInterval * 1000L);
		} catch (InterruptedException e) {
			;
		}

	}

	@Override
	public void run() {
		while (!threadDone) {
			threadSleep();
			processExpires();
		}
	}

	@Override
	public void load() throws ClassNotFoundException, IOException {

	}

	@Override
	public void unload() throws IOException {

	}
}
