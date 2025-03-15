package com.sentury.approvalflow.common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sentury.approvalflow.common.dto.flow.node.EmptyNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema
@Data
public class CheckConditionMatchDto {
    /**
     * 节点条件
     */

    @Schema(description = "节点条件")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,property = "type",defaultImpl = EmptyNode.class)
    private EmptyNode node;
    /**
     * 参数
     */

    @Schema(description = "参数")
    private Map<String,Object> paramMap;

}
