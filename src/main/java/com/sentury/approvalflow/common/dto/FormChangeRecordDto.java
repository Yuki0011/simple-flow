package com.sentury.approvalflow.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhj
 * @version 1.0
 * @description: 表单变化记录对象
 * @date 2024/2/19 16:12
 */
@Data
public class FormChangeRecordDto implements Serializable {
    /**
     * 表单id
     */
    private String formId;
    /**
     * 改变人的id
     */
    private String userId;
    /**
     * 改变人的名字
     */
    private String userName;
    /**
     * 表单值
     */
    private Object value;
    /**
     * 旧的表单值
     */
    private Object oldValue;
    /**
     * 改变的时间
     */
    private String date;
}
