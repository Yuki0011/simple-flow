package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.utils.HttpUtil;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

public class DingTalkHttpUtil {

    public static String getBaseUrl() {
        Environment environment = SpringUtil.getBean(Environment.class);
        String bizUrl = environment.getProperty("dingtalk.url");
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

    /**
     * 小程序--根据code获取用户id
     *
     * @param authCode
     * @return
     */
    public static R2<String> getUserIdByCodeAtMiniApp(String authCode) {
        String s = get("/user/getUserIdByCodeAtMiniApp?authCode=" + authCode);
        return JsonUtil.parseObject(s, new JsonUtil.TypeReference<R2<String>>() {
        });
    }

}
