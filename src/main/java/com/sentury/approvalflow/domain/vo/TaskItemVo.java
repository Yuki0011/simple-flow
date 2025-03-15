package com.sentury.approvalflow.domain.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskItemVo {
    // 流程名称
    private String processName;

    private Integer taskCount;

    private String processInstanceId;

}
