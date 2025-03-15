package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户任务查询参数
 */
@Schema(description = "用户任务查询参数")
@Data
public class ProcessQueryParamDto extends PageDto {
    /**
     * 任务执行人
     */

    @Schema(description = "任务执行人")
    private String assign;
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private List<String> flowIdList;
}
