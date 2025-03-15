package com.sentury.approvalflow.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.http.useragent.UserAgent;

public class ThreadLocalUtil {

    private static ThreadLocal<Dict> threadLocal = new ThreadLocal<>();

    public static void clear(){
        threadLocal.remove();
    }

    public static void putUserAgent(UserAgent userAgent) {
        Dict dict = threadLocal.get();
        if (dict == null) {
            dict = new Dict();
        }
        dict.set("userAgent", userAgent);
        threadLocal.set(dict);
    }


    public static void putUserAgentStr(String userAgent) {
        Dict dict = threadLocal.get();
        if (dict == null) {
            dict = new Dict();
        }
        dict.set("userAgentStr", userAgent);
        threadLocal.set(dict);
    }



    public static UserAgent getUserAgent() {
        Dict dict = threadLocal.get();
        if (dict == null) {
            return null;
        }
        Object o = dict.get("userAgent");
        if (o == null) {
            return null;
        }
        return (UserAgent) o;
    }

    public static String getUserAgentStr() {
        Dict dict = threadLocal.get();
        if (dict == null) {
            return null;
        }
        Object o = dict.get("userAgentStr");
        if (o == null) {
            return null;
        }
        return Convert.toStr(o);
    }

}
