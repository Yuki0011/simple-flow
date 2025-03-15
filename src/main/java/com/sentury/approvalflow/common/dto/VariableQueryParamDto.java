package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema
@Data
public class VariableQueryParamDto {
    /**
     * 任务id
     */

    @Schema(description = "任务id")
    private String taskId;
    /**
     * 表单key
     */

    @Schema(description = "表单key")
    private List<String> keyList;
    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;

}
