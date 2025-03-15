package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema
@Data
public class ProcessDto {

    /**
     * 唯一性id
     */

    @Schema(description = "唯一性id")
    private String uniqueId;
    /**
     * 设置项
     */

    @Schema(description = "设置项")
    private String settings;
}
