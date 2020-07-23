package com.zsw.simpletomcat.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zsw
 * @date 2020/07/22 15:23
 */
public class ParameterMap<K,V> extends HashMap<K,V> {
	public ParameterMap() {
		super();
	}

	public ParameterMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ParameterMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	public ParameterMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * ParameterMap的锁定状态
	 */
	private boolean locked = false;

	/**
	 * 返回锁定状态
	 * @return
	 */
	public boolean isLocked() {
		return this.locked;
	}

	/**
	 * 设置锁定状态
	 * @param locked
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * com.zsw.simpletomcat下的StringManager对象
	 */
	private static final StringManager sm = StringManager.getManager("com.zsw.simpletomcat.util");

	/**
	 * 清除Map中的映射
	 */
	@Override
	public void clear() {
		if(locked) {
			throw new IllegalStateException(sm.getString("parameterMap.locked"));
		}
		super.clear();
	}

	/**
	 * 将 参数名/参数值 放入map中，若参数名已存在，则会将原来的值替换掉并返回原来的值
	 * @param key 参数名
	 * @param value 参数值
	 * @return 旧的参数值
	 */
	@Override
	public V put(K key, V value) {
		if(locked) {
			throw  new IllegalStateException(sm.getString("parameterMap.locked"));
		}
		return super.put(key, value);
	}

	/**
	 * 将map中的映射添加到parameterMap中
	 * @param map 待添加的map
	 */
	@Override
	public void putAll(Map map) {
		if(locked) {
			throw  new IllegalStateException(sm.getString("parameterMap.locked"));
		}
		super.putAll(map);
	}

	/**
	 * 移除键值对
	 * @param key 移除的键
	 * @return 移除的值
	 */
	@Override
	public V remove(Object key) {
		if(locked) {
			throw  new IllegalStateException(sm.getString("parameterMap.locked"));
		}
		return super.remove(key);
	}
}
