package com.sentury.approvalflow.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ProcessInstanceNodeRecordStatusEnum {

    WKS(0,"未开始"),
    JXZ(1,"进行中"),
    YJS(2,"已结束"),
    YCX(3,"已撤销"),
    ;

    private int code;

    private String name;


    public static ProcessInstanceNodeRecordStatusEnum get(int code){
       return Arrays.stream(ProcessInstanceNodeRecordStatusEnum.values()).filter(w->w.getCode()==code).findAny().get();
    }

}
