package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema
@Data
public class ProcessNodeDataDto {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 数据
     */

    @Schema(description = "数据")
    private String data;
    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;

}
