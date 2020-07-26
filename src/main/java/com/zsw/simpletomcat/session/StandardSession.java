package com.zsw.simpletomcat.session;

import com.zsw.simpletomcat.Context;
import com.zsw.simpletomcat.Manager;
import com.zsw.simpletomcat.Session;
import com.zsw.simpletomcat.util.Enumerator;
import com.zsw.simpletomcat.util.StringManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zsw
 * @date 2020/07/24 19:04
 */
public class StandardSession implements HttpSession, Session {
	protected static final StringManager sm = StringManager.getManager(StandardSession.class);
	protected Manager manager;
	protected String id;

	// 创建session的时间戳
	protected long creationTime = 0L;
	protected long thisAccessedTime = 0L;
	protected long lastAccessedTime = 0L;

	/**
	 * 表示Session永不超时
	 */
	protected int maxInactiveInterval = -1;


	// 在判断session是否过期时会用到
	protected volatile boolean expiring = false;
	protected boolean isNew = false;
	protected boolean isValid = false;

	protected AtomicInteger accessCount;
	protected StandardSessionFacade facade;
	protected final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

	/**
	 * The HTTP session context associated with this session.
	 */
	private static HttpSessionContext sessionContext = null;

	public StandardSession(Manager manager) {
		this.manager = manager;
		accessCount = new AtomicInteger();
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public void setAuthType(String authType) {

	}

	@Override
	public void setCreationTime(long time) {
		this.creationTime = time;
		this.thisAccessedTime = time;
		this.lastAccessedTime = time;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Manager getManager() {
		return manager;
	}

	@Override
	public void setManager(Manager manager) {
		this.manager = manager;
	}

	@Override
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}


	@Override
	public HttpSession getSession() {
		if (facade == null) {
			facade = new StandardSessionFacade(this);
		}
		return facade;
	}

	@Override
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public boolean isValid() {
		return this.isValid;
	}

	@Override
	public long getIdleTimeInternal() {
		long timeNow = System.currentTimeMillis();
		return timeNow - lastAccessedTime;
	}

	@Override
	public void access() {
		this.lastAccessedTime = this.thisAccessedTime;
		this.thisAccessedTime = System.currentTimeMillis();
	}

	@Override
	public void expire(boolean notify) {
		if (!isValid || expiring) {
			return;
		}
		synchronized (this) {
			expiring = true;
			setValid(false);
			manager.remove(this);
			// 这里不做任何通知操作，清空属性集
			attributes.clear();
		}
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}


	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	@Override
	public ServletContext getServletContext() {
		if (manager == null) {
			return null;
		}
		Context context = (Context) manager.getContainer();
		if (context == null) {
			return null;
		}
		return context.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		if (sessionContext == null) {
			sessionContext = new StandardSessionContext();
		}
		return sessionContext;
	}

	@Override
	public Object getAttribute(String name) {
		if (!isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.getAttribute.ise"));
		}
		synchronized (attributes) {
			return attributes.get(name);
		}

	}

	@Override
	public Enumeration<String> getAttributeNames() {
		if (!isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.getAttributeNames.ise"));
		}

		synchronized (attributes) {
			return (new Enumerator(attributes.keySet()));
		}
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}


	@Override
	public String[] getValueNames() {
		if (!isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.getValueNames.ise"));
		}
		synchronized (attributes) {
			return attributes.keySet().toArray(new String[0]);
		}
	}


	@Override
	public void invalidate() {
		if (!isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.invalidate.ise"));
		}

		expire(true);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		if (!expiring && !isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.removeAttribute.ise"));
		}
		attributes.remove(name);

	}


	@Override
	public void setAttribute(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException
					(sm.getString("standardSession.setAttribute.namenull"));
		}
		if (value == null) {
			removeAttribute(name);
			return;
		}
		if (!isValid) {
			throw new IllegalStateException
					(sm.getString("standardSession.setAttribute.ise"));
		}
		attributes.put(name,value);
	}




	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}


	@Override
	public boolean isNew() {
		return true;
	}
}

final class StandardSessionContext implements HttpSessionContext {


	private HashMap dummy = new HashMap();

	/**
	 * Return the session identifiers of all sessions defined
	 * within this context.
	 *
	 * @deprecated As of Java Servlet API 2.1 with no replacement.
	 * This method must return an empty <code>Enumeration</code>
	 * and will be removed in a future version of the API.
	 */
	public Enumeration getIds() {

		return (new Enumerator(dummy));

	}


	/**
	 * Return the <code>HttpSession</code> associated with the
	 * specified session identifier.
	 *
	 * @param id Session identifier for which to look up a session
	 * @deprecated As of Java Servlet API 2.1 with no replacement.
	 * This method must return null and will be removed in a
	 * future version of the API.
	 */
	public HttpSession getSession(String id) {

		return (null);

	}


}

