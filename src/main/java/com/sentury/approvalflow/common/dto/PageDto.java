package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页对象
 */
@Schema(description = "分页对象")
@Data
public class PageDto {

    /**
     * 页码
     */

    @Schema(description = "页码")
    private Integer pageNum;
    /**
     * 每页的数量
     */

    @Schema(description = "每页的数量")
    private Integer pageSize;

    private String processName;

}
