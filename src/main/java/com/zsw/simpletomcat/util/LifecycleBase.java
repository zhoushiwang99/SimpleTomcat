package com.zsw.simpletomcat.util;

import com.zsw.simpletomcat.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zsw
 * @date 2020/07/25 19:43
 */
public abstract class LifecycleBase implements Lifecycle{
	private static final StringManager sm = StringManager.getManager(LifecycleBase.class);

	private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();

	private volatile LifecycleState state = LifecycleState.NEW;


	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		listeners.add(listener);
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		return listeners.toArray(new LifecycleListener[0]);
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 子类通过调用此方法生产生命周期事件
	 *
	 * @param type 事件类型
	 * @param data 处理事件的数据
	 */
	protected void fireLifecycleEvent(String type, Object data) {
		LifecycleEvent event = new LifecycleEvent(this, type, data);
		for (LifecycleListener listener : listeners) {
			listener.lifecycleEvent(event);
		}
	}


	protected abstract void initInternal() throws LifecycleException;

	@Override
	public final synchronized void start() throws LifecycleException {
		if (LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state)
				|| LifecycleState.STARTED.equals(state)) {
			return;
		}
		if (LifecycleState.NEW.equals(state)) {

		} else if (LifecycleState.FAILED.equals(state)) {
			stop();
		}

		try {
			setStateInternal(LifecycleState.STARTING_PREP, null, false);
			startInternal();
			setStateInternal(LifecycleState.STARTED, null, false);
		} catch (Exception e) {
			handleException(e, "lifecycleBase.startFail", toString());
		}

	}

	protected abstract void startInternal() throws LifecycleException;

	@Override
	public final synchronized void stop() throws LifecycleException {
		if (LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state)
				|| LifecycleState.STOPPED.equals(state)) {
			return;
		}

		if (LifecycleState.NEW.equals(state)) {
			state = LifecycleState.STOPPED;
			return;
		}

		try {
			setStateInternal(LifecycleState.STOPPING_PREP, null, false);
			stopInternal();
			setStateInternal(LifecycleState.STOPPED, null, false);
		} catch (Exception e) {
			handleException(e, "lifecycleBase.stopFail", toString());
		}

	}

	protected abstract void stopInternal() throws LifecycleException;


	protected abstract void destroyInternal() throws LifecycleException;



	protected synchronized void setState(LifecycleState state) {
		setStateInternal(state, null, true);
	}

	protected synchronized void setState(LifecycleState state, Object data) {
		setStateInternal(state, data, true);
	}

	private synchronized void setStateInternal(LifecycleState state, Object data, boolean check) {

		if (check) {
			// 检查操作
		}

		this.state = state;
		String event = state.getLifecycleEvent();
		if (event != null) {
			fireLifecycleEvent(event, data);
		}
	}

	private void handleException(Exception e, String key, Object... args) throws LifecycleException {
		setStateInternal(LifecycleState.FAILED, null, false);
		if (!(e instanceof LifecycleException)) {
			throw new LifecycleException(sm.getString(key, args), e);
		}
		throw (LifecycleException) e;
	}

}
