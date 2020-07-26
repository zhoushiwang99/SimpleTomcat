package com.zsw.simpletomcat.util;

/**
 * 字符串工具类
 */
public class StringUtil {

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

}
