package com.sentury.approvalflow.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.HttpSetting;
import com.sentury.approvalflow.common.dto.flow.HttpSettingData;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.DEFAULT_TENANT_ID;

@Slf4j
public class HttpUtil {


    //private static TLogHutoolhttpInterceptor tLogHutoolhttpInterceptor = new TLogHutoolhttpInterceptor();
    /**
     * 请求超时时间
     */
    public static final int TIME_OUT = 60000;


    public static String post(Object object, String url, String baseUrl,String tenantId) {


        return post(object, url, baseUrl, tenantId,null);

    }


    public static String post(Object object, String url, String baseUrl,String tenantId, Map<String, String> headerParamMap) {


        return post(StrUtil.format("{}{}", baseUrl, url), headerParamMap, object, tenantId);

    }


    public static String post(String url, Map<String, String> headerParamMap, Object bodyMap,String tenantId) {
        log.info("请求地址：{}  请求头：{} 请求体：{}", url, JsonUtil.toJSONString(headerParamMap), JsonUtil.toJSONString(bodyMap));
        if (headerParamMap == null) {
            headerParamMap = new HashMap<>();
        }
        String result = null;
        try {
            String value = TenantUtil.get();
            if(StrUtil.isNotBlank(tenantId)){
                value=tenantId;
            }

            if(StrUtil.isBlank(value)||StrUtil.equals(value,DEFAULT_TENANT_ID)){
                log.warn("未找到租户id:{}  url:{}",value,url);
            }
            HttpResponse httpResponse = HttpRequest.post(url)
                    //头信息，多个头信息多次调用此方法即可
                    .header(Header.USER_AGENT,
                            ThreadLocalUtil.getUserAgentStr()+" "+ ProcessInstanceConstant.VariableKey.SYS_CODE)
                    //设置租户id
                    .header(ProcessInstanceConstant.VariableKey.HTTP_HEADER_TENANT_ID_KEY, value)
                    .headerMap(headerParamMap, true)
                    .body(JsonUtil.toJSONString(bodyMap))
                    //超时，毫秒
                    .timeout(TIME_OUT)
                    //.addInterceptor(tLogHutoolhttpInterceptor)
                    .execute();
            int status = httpResponse.getStatus();

            result = httpResponse.body();
            log.info("  返回值:{}  状态码：{}", result, status);
            if (HttpStatus.HTTP_OK != status) {
                return null;
            }

        } catch (Exception e) {
            log.error("POST异常", e);
        }
        return result;
    }


    public static String get(String url, String baseUrl,String tenantId) {

        log.info("get请求地址：{} {}", url, baseUrl);

        String value = TenantUtil.get();
        if(StrUtil.isNotBlank(tenantId)){
            value=tenantId;
        }

        if(StrUtil.isBlank(value)||StrUtil.equals(value,DEFAULT_TENANT_ID)){
            log.warn("未找到租户id:{}  url:{}",value,url);
        }

        return HttpRequest
                .get(StrUtil.format("{}{}", baseUrl, url))
                .header(ProcessInstanceConstant.VariableKey.HTTP_HEADER_TENANT_ID_KEY, value)
                .timeout(TIME_OUT)
              //  .addInterceptor(tLogHutoolhttpInterceptor)
            .execute().body();


    }

    public static void checkResult(String json){
        if(StrUtil.isBlank(json)){
            throw new RuntimeException("请求异常");
        }
        R2 r = JsonUtil.parseObject(json, R2.class);
        checkResult(r);
    }
    public static void checkResult(R2 r){

        if(!r.isOk()){
            throw new RuntimeException(r.getMsg());
        }
    }

    /**
     * 流程扩展请求
     *
     * @param httpSetting
     * @param paramMap
     * @param flowId
     * @param processInstanceId
     * @param messageNotifyId
     * @param cancel
     * @param extraMap
     * @param tenantId
     * @return
     */
    public static String flowExtenstionHttpRequest(HttpSetting httpSetting, Map<String, Object> paramMap, String flowId,
                                                   String processInstanceId, String messageNotifyId, Boolean cancel,
                                                   Map<String, Object> extraMap, String tenantId) {
        Map<String, String> headerParamMap = new HashMap<>();
        {
            List<HttpSettingData> headerSetting = httpSetting.getHeader();
            for (HttpSettingData httpSettingData : headerSetting) {
                String field = httpSettingData.getField();
                if (StrUtil.isBlank(field)) {
                    continue;
                }
                if (httpSettingData.getValueMode()) {
                    headerParamMap.put(field, httpSettingData.getValue());
                } else {
                    Object object = paramMap.get(httpSettingData.getValue());
                    headerParamMap.put(field, object == null ? null : (object instanceof String ? Convert.toStr(object) : JsonUtil.toJSONString(object)));
                }
            }

        }
        Map<String, Object> bodyMap = new HashMap<>();
        {
            //存入默认值
            bodyMap.put("flowId", flowId);
            bodyMap.put("processInstanceId", processInstanceId);
            bodyMap.put("messageNotifyId", messageNotifyId);
            bodyMap.put("isCancel", cancel);
            if (extraMap != null) {
                bodyMap.putAll(extraMap);
            }
            List<HttpSettingData> bodySetting = httpSetting.getBody();
            for (HttpSettingData httpSettingData : bodySetting) {
                String field = httpSettingData.getField();
                if (StrUtil.isBlank(field)) {
                    continue;
                }
                if (httpSettingData.getValueMode()) {
                    bodyMap.put(field, httpSettingData.getValue());
                } else {
                    Object object = paramMap.get(httpSettingData.getValue());
                    bodyMap.put(field, object == null ? null : (object instanceof String ?
                            Convert.toStr(object) : JsonUtil.toJSONString(object)));
                }
            }

        }
        log.info("url：{} 请求头：{} 请求体：{} ", httpSetting.getUrl(), JsonUtil.toJSONString(headerParamMap), JsonUtil.toJSONString(bodyMap));

        return HttpUtil.post(httpSetting.getUrl(), headerParamMap, bodyMap, tenantId);

    }
}
