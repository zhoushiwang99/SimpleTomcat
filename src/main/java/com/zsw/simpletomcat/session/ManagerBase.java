package com.zsw.simpletomcat.session;

import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.Manager;
import com.zsw.simpletomcat.Session;
import com.zsw.simpletomcat.util.StringManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zsw
 * @date 2020/07/24 18:08
 */
public abstract class ManagerBase implements Manager {

	protected static final StringManager sm = StringManager.getManager(ManagerBase.class);
	protected Container container;

	// 存放session，键为id
	protected Map<String,Session> sessions = new ConcurrentHashMap<>();

	// 计创建过的session数
	protected long sessionCounter = 0;

	// 过期的session数
	protected AtomicLong expiredSessions = new AtomicLong();

	// 用来记录最大活跃的session数
	protected int maxActive = 0;

	// 最大接收的session数
	protected int maxActiveSessions = 100;

	// 当创建的session满了并拒绝创建时，此计数器增加
	protected int rejectedSessions = 0;

	protected int maxInactiveInterval = 60;

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public void add(Session session) {
		sessions.put(session.getId(),session);
		/*int size = getActiveSessions();
		if(size > maxActive) {
			maxActive = size;
		}*/
	}

	@Override
	public void remove(Session session) {
		if(session.getId() != null) {
			sessions.remove(session.getId());
		}
	}

	@Override
	public Session createSession() {
		if(maxActiveSessions >= 0 && getActiveSessions() >= maxActiveSessions) {
			rejectedSessions++;
			throw new IllegalStateException(sm.getString("managerBase.createSession.ise"));
		}
		Session session = new StandardSession(this);
		session.setId(generateSessionId());
		session.setNew(true);
		session.setValid(true);
		session.setCreationTime(System.currentTimeMillis());
		// 过期时间为30分钟
		session.setMaxInactiveInterval(60 * 30);
		sessionCounter++;
		return session;
	}

	protected String generateSessionId() {
		String result = null;

		do {
			result = UUID.randomUUID().toString();
		} while (sessions.containsKey(result));

		return result;
	}
	@Override
	public Session findSession(String id) {
		return sessions.get(id);
	}

	@Override
	public Session[] findSessions() {
		return sessions.values().toArray(new Session[0]);
	}

	@Override
	public int getActiveSessions() {
		return sessions.size();
	}

	@Override
	public int getMaxInactiveInterval() {
		return this.maxInactiveInterval;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}
}
