package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.utils.HttpUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

public class WxCpHttpUtil {

    public static String getBaseUrl() {
        Environment environment = SpringUtil.getBean(Environment.class);
        String bizUrl = environment.getProperty("wxcp.url");
        return bizUrl;
    }


    public static String post(Object object, String url) {

        String baseUrl = getBaseUrl();


        return HttpUtil.post(object, url, baseUrl, null);
    }

    public static String get(String url) {

        String baseUrl = getBaseUrl();


        return HttpUtil.get(url, baseUrl, TenantUtil.get());

    }




}
