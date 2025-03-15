package com.sentury.approvalflow.domain.vo;



import lombok.Data;

/**
 * 对象
 */

@Data
public class UserFieldParamVO {

    /**
     * 字段名
     */

    private String name;


    /**
     * 字段类型
     */

    private String type;

    /**
     * 是否必填
     */

    private Boolean required;

    /**
     * 配置json字符串
     */

    private String props;
    /**
     * 字段唯一key
     */

    private String key;
}
