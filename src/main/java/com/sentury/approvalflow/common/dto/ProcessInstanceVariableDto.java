package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程实例变量
 * @author zhj
 */
@Schema(description = "流程实例变量")
@Data
public class ProcessInstanceVariableDto {

    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;
    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;
    /**
     * 变量名称
     */

    @Schema(description = "变量名称")
    private String variableName;
    /**
     * 变量值
     */

    @Schema(description = "变量值")
    private Object variableValue;
}
