package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.domain.enums.WorkTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 审批汇总
@Getter
@Setter
public class TaskSummaryVo {

    private WorkTypeEnum workType;

    private Integer taskCount;

    List<TaskItemVo> items;
}
