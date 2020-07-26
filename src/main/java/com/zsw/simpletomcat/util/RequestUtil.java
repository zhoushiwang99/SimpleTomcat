package com.zsw.simpletomcat.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author zsw
 * @date 2020/07/20 22:07
 */
public class RequestUtil {

	/**
	 * 解析contentType中的字符集
	 *
	 * @param contentType a content type header
	 */
	public static String parseCharacterEncoding(String contentType) {

		if (contentType == null) {
			return (null);
		}
		int start = contentType.indexOf("charset=");
		if (start < 0) {
			return (null);
		}
		String encoding = contentType.substring(start + 8);
		int end = encoding.indexOf(';');
		if (end >= 0) {
			encoding = encoding.substring(0, end);
		}
		encoding = encoding.trim();
		if ((encoding.length() > 2) && (encoding.startsWith("\""))
				&& (encoding.endsWith("\""))) {
			encoding = encoding.substring(1, encoding.length() - 1);
		}
		return (encoding.trim());

	}


	/**
	 * 将cookie头解析为数组
	 *
	 * @param header http cookie请求头的值
	 * @return 解析完成后的cookie数组
	 */
	public static Cookie[] parseCookieHeader(String header) {
		if (StringUtil.isEmpty(header)) {
			return null;
		}
		// cookie头中根据"; "分割每个cookie
		return Stream.of(header.split("; "))
				.map(cookie -> {
					String[] keyValue = cookie.split("=");
					return new Cookie(urlDecode(keyValue[0]), urlDecode(keyValue[1]));
				}).toArray(Cookie[]::new);
	}

	/**
	 * 解析数据
	 *
	 * @param map      request中的parameterMap对象
	 * @param data     待解析的数据
	 * @param encoding 字符编码
	 */
	public static void parseParameters(Map<String, String[]> map, String data, String encoding) throws UnsupportedEncodingException {
		if(data == null || "".equals(data.trim()) || data.length() <= 0 ) {
			return;
		}
		byte[] bytes = null;

		try {
			if (encoding == null) {
				bytes = data.getBytes();
			} else {
				bytes = data.getBytes(encoding);
			}
		} catch (UnsupportedEncodingException var5) {
			var5.printStackTrace();
		}
		parseParameters(map, bytes, encoding);
	}

	public static void parseParameters(Map<String, String[]> map, byte[] data, String encoding) throws UnsupportedEncodingException {
		if (data != null && data.length > 0) {
			int ix = 0;
			int ox = 0;
			String key = null;
			String value = null;
			// 需要处理的几种特殊字符
			/*
			网页中的表单使用POST方法提交时，数据内容的类型是 application/x-www-form-urlencoded，这种类型会：
　　          1.字符"a"-"z"，"A"-"Z"，"0"-"9"，"."，"-"，"*"，和"_" 都不会被编码;
　　          2.将空格转换为加号 (+) ;
　　          3.将非文本内容转换成"%xy"的形式,xy是两位16进制的数值;
　　          4.在每个 name=value 对之间放置 & 符号。
			*/
			while (ix < data.length) {
				byte c = data[ix++];
				switch ((char) c) {
					case '%':
						data[ox++] = (byte) ((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
						break;
					case '&':
						// 生成value
						value = new String(data, 0, ox, encoding);
						if (key != null) {
							putMapEntry(map, key, value);
							key = null;
						}
						ox = 0;
						break;
					case '+':
						data[ox++] = 32;  // 32 代表空格
						break;
					case '=':
						if (key == null) {
							// 生成key
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
	public static String urlDecode(String s, String encoding) {
		try {
			return URLDecoder.decode(s, encoding);
		} catch (UnsupportedEncodingException e) {
			return urlDecode(s);
		}
	}

	public static String urlDecode(String s) {
		System.out.println(s + s.length());
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
