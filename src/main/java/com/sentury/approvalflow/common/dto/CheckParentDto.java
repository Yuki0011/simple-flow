package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 检查是否是给定的父级
 *
 */
@Schema(description = "检查是否是给定的父级")
@Data
public class CheckParentDto {
    /**
     * 上级id
     */

    @Schema(description = "上级id")
    private String parentId;
    /**
     * 部门id集合
     */

    @Schema(description = "部门id集合")
    private List<String> deptIdList;

}
