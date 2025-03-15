package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 检查是否是给定的子级
 *
 */
@Schema(description = "检查是否是给定的子级")
@Data
public class CheckChildDto {
    /**
     * 子级id
     */

    @Schema(description = "子级id")
    private String childId;
    /**
     * 部门id集合
     */

    @Schema(description = "部门id集合")
    private List<String> deptIdList;

}
