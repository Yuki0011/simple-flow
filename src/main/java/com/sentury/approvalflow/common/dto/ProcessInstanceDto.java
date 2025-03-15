package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 流程实例对象
 */
@Schema(description = "流程实例对象")
@Data
public class ProcessInstanceDto {
    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 流程名称
     */

    @Schema(description = "流程名称")
    private String processName;
    /**
     * 发起人id
     */

    @Schema(description = "发起人id")
    private String startUserId;
    /**
     * 发起人名称
     */

    @Schema(description = "发起人名称")
    private String startUserName;
    /**
     * 群组名称
     */

    @Schema(description = "群组名称")
    private String groupName;
    /**
     * 发起时间
     */

    @Schema(description = "发起时间")
    private Date startTime;
    /**
     * 结束时间
     */

    @Schema(description = "结束时间")
    private Date endTime;
    /**
     * 流程结果
     */

    @Schema(description = "流程结果")
    private Integer processInstanceResult;
    /**
     * 流程状态
     */

    @Schema(description = "流程状态")
    private Integer processInstanceStatus;

}
