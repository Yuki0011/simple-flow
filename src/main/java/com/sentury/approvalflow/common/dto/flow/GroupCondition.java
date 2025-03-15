package com.sentury.approvalflow.common.dto.flow;

import lombok.Data;

import java.util.List;

/**
 * 组条件
 */
@Data
public class GroupCondition {

    private Boolean mode;

    private List<Condition> conditionList;



}
