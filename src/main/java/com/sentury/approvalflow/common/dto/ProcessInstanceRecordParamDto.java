package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 流程记录
 * </p>
 *
 * @author Vincent
 * @since 2023-05-07
 */
@Schema(description = "<p> 流程记录 </p>")
@Getter
@Setter
public class ProcessInstanceRecordParamDto {
    /**
     * 发起人主部门id
     */

    @Schema(description = "发起人主部门id")
    private String mainDeptId;

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String userId;


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
     * 流程实例业务key
     */

    @Schema(description = "流程实例业务key")
    private String processInstanceBizKey;
    /**
     * 业务编码
     */

    @Schema(description = "业务编码")
    private String processInstanceBizCode;


    @Schema(hidden = true)
    private String parentProcessInstanceId;
    /**
     * 主流程节点执行id
     */

    @Schema(description = "主流程节点执行id")
    private String parentProcessNodeExecutionId;

    /**
     * 表单数据
     */

    @Schema(description = "表单数据")
    private String formData;

    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;
}
