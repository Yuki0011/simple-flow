package com.sentury.approvalflow.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 结果类
 * 统一返回值
 *
 * @param <T> 数据对象
 */
@Data
public class R2<T> implements Serializable {
    /**
     * 链路id
     */
    private String traceId;

    /**
     * 是否请求成功
     */
    private boolean ok;
    /**
     * 结果码
     */
    private Integer code;
    /**
     * 提示消息
     */
    private String msg = "业务处理成功";
    /**
     * 数据
     */
    private T data;

    public static R2 success() {
        R2 r = new R2();
        r.setOk(true);
        return r;
    }

    public static <T> R2 success(T data) {
        R2 r = new R2();
        r.setOk(true);
        r.setData(data);
        return r;
    }

    public static <T> R2 fail(String msg) {
        R2 r = new R2();
        r.setOk(false);
        r.setMsg(msg);
        return r;
    }

    public static <T> R2 success(T data, String msg) {
        R2 r = new R2();
        r.setOk(true);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
