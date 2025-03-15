package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程节点记录
 */
@Schema(description = "流程节点记录")
@Data
public class ProcessInstanceNodeRecordParamDto {


    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;

    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
    private String processInstanceId;

    /**
     * 表单数据
     */

    @Schema(description = "表单数据")
    private String data;
    /**
     * 跳转标识
     */

    @Schema(description = "跳转标识")
    private String flowUniqueId;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;

    /**
     * 上级节点id
     */

    @Schema(description = "上级节点id")
    private String parentNodeId;


    /**
     * 节点类型
     */

    @Schema(description = "节点类型")
    private Integer nodeType;

    /**
     * 节点名字
     */

    @Schema(description = "节点名字")
    private String nodeName;
    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;
    /**
     * 子级执行id
     */

    @Schema(description = "子级执行id")
    private List<String> childExecutionId;



    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;

}
