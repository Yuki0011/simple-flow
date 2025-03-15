package com.sentury.approvalflow.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NodeFormatUserVoStatusEnum {

    WKS(0,"未开始"),
    JXZ(1,"进行中"),
    YJS(2,"已结束"),
    YCX(3,"已撤销"),
    ;

    private int code;

    private String name;


    public static NodeFormatUserVoStatusEnum get(int code){
        NodeFormatUserVoStatusEnum nodeFormatUserVoStatusEnum = Arrays.stream(NodeFormatUserVoStatusEnum.values()).filter(w -> w.getCode() == code).findAny().get();
        return nodeFormatUserVoStatusEnum;
    }

}
