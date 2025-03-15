package com.sentury.approvalflow.domain.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkTypeEnum {



    TODO("0","待办"),
    DONE("1","已办"),
    CONNECT("2", "抄送我"),
    SEND("3", "已发起");





    private final String code;
    private final String desc;


}
