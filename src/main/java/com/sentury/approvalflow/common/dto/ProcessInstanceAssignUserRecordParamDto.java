package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程节点记录-执行人
 */
@Schema(description = "流程节点记录-执行人")
@Data
public class ProcessInstanceAssignUserRecordParamDto {


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
     * 流程本地数据
     */

    @Schema(description = "流程本地数据")
    private String localData;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String userId;
    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;
    /**
     * 节点执行id
     */

    @Schema(description = "节点执行id")
    private String parentExecutionId;

    /**
     * 任务id
     */

    @Schema(description = "任务id")
    private String taskId;
    /**
     * 节点名称
     */

    @Schema(description = "节点名称")
    private String nodeName;
    /**
     * 流程唯一id
     */

    @Schema(description = "流程唯一id")
    private String flowUniqueId;
    /**
     * 任务类型
     */

    @Schema(description = "任务类型")
    private String taskType;

    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 是否是自动完成
     */

    @Schema(description = "是否是自动完成")
    private Boolean auto;
}
