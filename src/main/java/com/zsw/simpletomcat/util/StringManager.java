package com.zsw.simpletomcat.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author zsw
 * @date 2020/07/12 19:23
 */
public class StringManager {

	private final ResourceBundle bundle;
	private final Locale locale;
	private static final Hashtable<String, StringManager> managers = new Hashtable<>();

	private StringManager(String packageName) {
		String bundleName = packageName + ".LocalStrings";
		ResourceBundle tempBundle = null;

		try {
			tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
		} catch (MissingResourceException var8) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl != null) {
				try {
					tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), cl);
				} catch (MissingResourceException var7) {
				}
			}
		}

		if (tempBundle != null) {
			this.locale = tempBundle.getLocale();
		} else {
			this.locale = null;
		}

		this.bundle = tempBundle;
	}

	public String getString(String key) {
		String str;
		if (key == null) {
			str = "key may not have a null value";
			throw new IllegalArgumentException(str);
		} else {
			str = null;

			try {
				str = this.bundle.getString(key);
			} catch (MissingResourceException var4) {
				str = null;
			}

			return str;
		}
	}

	public String getString(String key, Object... args) {
		String value = this.getString(key);
		if (value == null) {
			value = key;
		}

		MessageFormat mf = new MessageFormat(value);
		mf.setLocale(this.locale);
		return mf.format(args, new StringBuffer(), (FieldPosition)null).toString();
	}

	public static final synchronized StringManager getManager(String packageName) {
		StringManager mgr = (StringManager)managers.get(packageName);
		if (mgr == null) {
			mgr = new StringManager(packageName);
			managers.put(packageName, mgr);
		}

		return mgr;
	}

	public static final StringManager getManager(Class<?> clazz) {
		return getManager(clazz.getPackage().getName());
	}
}
