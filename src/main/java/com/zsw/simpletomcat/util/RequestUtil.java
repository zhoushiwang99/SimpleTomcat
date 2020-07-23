package com.zsw.simpletomcat.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author zsw
 * @date 2020/07/20 22:07
 */
public class RequestUtil {
	public static Cookie[] parseCookieHeader(String header) {
		if (header == null || (header.length()) < 1) {
			return new Cookie[0];
		}
		ArrayList<Cookie> cookies = new ArrayList<>();
		while (header.length() > 0) {
			int semicolon = header.indexOf(';');
			if (semicolon < 0) {
				semicolon = header.length();
			}
			if (semicolon == 0) {
				break;
			}
			String token = header.substring(0, semicolon);
			if (semicolon < header.length()) {
				header = header.substring(semicolon + 1);
			} else {
				header = "";
			}
			try {
				int equals = token.indexOf('=');
				if (equals > 0) {
					String name = token.substring(0, equals).trim();
					String value = token.substring(equals + 1).trim();
					cookies.add(new Cookie(name, value));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return (Cookie[]) cookies.toArray();
	}

	/**
	 * 解析数据
	 *
	 * @param map      request中的parameterMap对象
	 * @param data     待解析的数据
	 * @param encoding 字符编码
	 */
	public static void parseParameters(Map<String, String[]> map, String data, String encoding) throws UnsupportedEncodingException {
		if (data != null && data.length() > 0) {
			byte[] bytes = null;

			try {
				if (encoding == null) {
					bytes = data.getBytes();
				} else {
					bytes = data.getBytes(encoding);
				}
			} catch (UnsupportedEncodingException var5) {
			}

			parseParameters(map, bytes, encoding);
		}

	}

	public static void parseParameters(Map<String, String[]> map, byte[] data, String encoding) throws UnsupportedEncodingException {
		if (data != null && data.length > 0) {
			int ix = 0;
			int ox = 0;
			String key = null;
			String value = null;

			while (ix < data.length) {
				byte c = data[ix++];
				switch ((char) c) {
					case '%':
						data[ox++] = (byte) ((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
						break;
					case '&':
						value = new String(data, 0, ox, encoding);
						if (key != null) {
							putMapEntry(map, key, value);
							key = null;
						}

						ox = 0;
						break;
					case '+':
						data[ox++] = 32;
						break;
					case '=':
						if (key == null) {
							key = new String(data, 0, ox, encoding);
							ox = 0;
						} else {
							data[ox++] = c;
						}
						break;
					default:
						data[ox++] = c;
				}
			}

			if (key != null) {
				value = new String(data, 0, ox, encoding);
				putMapEntry(map, key, value);
			}
		}

	}

	private static byte convertHexDigit(byte b) {
		if (b >= 48 && b <= 57) {
			return (byte) (b - 48);
		} else if (b >= 97 && b <= 102) {
			return (byte) (b - 97 + 10);
		} else {
			return b >= 65 && b <= 70 ? (byte) (b - 65 + 10) : 0;
		}
	}

	/**
	 * 将键值对放入map中
	 * 若键已存在，则获取旧数组，扩容后添加此值，再放回map中
	 *
	 * @param map   参数map
	 * @param name  键名称
	 * @param value 键值
	 */
	private static void putMapEntry(Map<String, String[]> map, String name, String value) {
		String[] newValues;
		String[] oldValues = map.get(name);
		if (oldValues == null) {
			newValues = new String[1];
			newValues[0] = value;
		} else {
			newValues = new String[oldValues.length + 1];
			System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
			newValues[oldValues.length] = value;
		}
		map.put(name, newValues);
	}

	/**
	 * url解码
	 *
	 * @param s        待解码字符串
	 * @param encoding 字符编码
	 * @return 解码后的字符串
	 */
	private static String urlDecode(String s, String encoding) {
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return urlDecode(s);
		}
	}

	private static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

}
