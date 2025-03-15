package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import com.sentury.approvalflow.common.utils.HttpUtil;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.extra.spring.SpringUtil;

import java.util.Map;

public class ApiHttpUtil {

    public static String getBaseUrl() {
       return SpringUtil.getProperty("api.http.baseUrl");
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
     * 查询当前存在的任务变量
     * 全部都是
     *
     * 如果任务完成了 返回错误码
     * @param taskId
     * @param keyList
     * @return
     */
    public static R2<Map<String,Object>> queryCurrentTaskVariables(UserQueryDto userQueryDto) {


        String post = post(userQueryDto, "userList");
        return JsonUtil.parseObject(post,new JsonUtil.TypeReference<R2<Map<String,Object>>>(){});

    }



}
