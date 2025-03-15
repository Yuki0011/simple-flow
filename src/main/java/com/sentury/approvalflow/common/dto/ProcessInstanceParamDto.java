package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例参数对象
 */
@Schema(description = "流程实例参数对象")
@Data
public class ProcessInstanceParamDto {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 流程主id
     */

    @Schema(description = "流程主id")
    private String uniqueId;
    /**
     * 业务key
     */

    @Schema(description = "业务key")
    private String bizKey;
    /**
     * 参数集合
     */

    @Schema(description = "参数集合")
    private Map<String,Object> paramMap=new HashMap<>();
    /**
     * 发起人
     */

    @Schema(description = "发起人")
    private String startUserId;
    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;
    /**
     * 是否撤销
     */

    @Schema(description = "是否撤销")
    private Boolean cancel;
    /**
     * 结果
     */

    @Schema(description = "结果")
    private Integer result;
    /**
     * 原因
     */

    @Schema(description = "原因")
    private String reason;
    /**
     * 流程实例id集合
     */

    @Schema(description = "流程实例id集合")
    private List<String> processInstanceIdList;
    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;
}
