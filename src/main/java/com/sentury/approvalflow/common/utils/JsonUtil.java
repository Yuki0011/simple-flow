package com.sentury.approvalflow.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {


    public static List<?> toList(Object object) {
        String jsonString = toJSONString(object);
        if (StrUtil.isBlank(jsonString)) {
            return null;
        }


        return parseArray(jsonString, Object.class);
    }

    public static <T> List<T> toList(Class<T> tClass, Object object) {
        return parseArray(toJSONString(object), tClass);
    }

    @SneakyThrows
    public static <T> List<T> parseArray(String json, Class<T> tClass) {
        ObjectMapper objectMapper =buildObjectWrapper();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, tClass);

        List list = objectMapper.readValue(json, javaType);
        return list;

    }

    @SneakyThrows
    public static List<Map<Object,Object>> parseArrayOfMap(String json) {

        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Map<Object,Object>>>() {
        });
    }

    @SneakyThrows
    public static List<Object> parseOriArray(String json) {
        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Object>>() {
        });

    }

    @SneakyThrows
    public static Map<Object,Object> parseObject(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<Object,Object>>() {
        });
    }

    @SneakyThrows
    public static <T> T parseObject(String json, Class<T> tClass) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.readValue(json,tClass);
    }

    private static ObjectMapper buildObjectWrapper(){
        ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
        return objectMapper;
    }

    @SneakyThrows
    public static <T> T parseObject(String json, TypeReference<T> tTypeReference) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.readValue(json, tTypeReference);

    }

    /**
     * json字符串转对象
     *
     * @param json json字符串
     * @return 对象
     */
    public static Object toObject(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }


        String trim = StrUtil.trim(json);
        if (trim.startsWith("[") && trim.endsWith("]")) {
            return parseOriArray(trim);
        } else if (trim.startsWith("{") && trim.endsWith("}")) {
            return parseObject(trim);
        } else {
            return Convert.toStr(trim);
        }
    }

    @SneakyThrows
    public static String toJSONString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return Convert.toStr(obj);
        }
        ObjectMapper objectMapper =buildObjectWrapper();

        return objectMapper.writeValueAsString(obj);

    }

    public static class TypeReference<T> extends com.fasterxml.jackson.core.type.TypeReference<T> {

    }
}
