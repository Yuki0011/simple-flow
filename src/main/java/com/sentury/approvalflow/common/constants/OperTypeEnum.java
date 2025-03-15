package com.sentury.approvalflow.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * 操作类型
 */
@Getter
public enum OperTypeEnum {

    START( "开始","start"),

    PASS( "同意","pass"),
    REFUSE( "拒绝","refuse"),
    CANCEL( "取消","cancel"),

    ;

    public static OperTypeEnum getByValue(String value){
        return Arrays.stream(OperTypeEnum.values()).filter(w->w.getValue().equals(value)).findAny().orElse(null);
    }


    OperTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private String value;


}
