package com.sentury.approvalflow.common.dto.third;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptDto {
    /**
     * 部门id 不能为空
     */

    @Schema(description = "部门id 不能为空")
    private String id;
    /**
     * 部门名字 不能为空
     */

    @Schema(description = "部门名字 不能为空")
    private String name;
    /**
     * 部门上级id 不能为空 若为顶级 则是0
     */

    @Schema(description = "部门上级id 不能为空 若为顶级 则是0")
    private String parentId;
    /**
     * 部门主管的userId 可以为空
     */

    @Schema(description = "部门主管的userId 可以为空")
    private List<String> leaderUserIdList;
    /**
     * 部门状态 0 禁用 1启用
     */

    @Schema(description = "部门状态 0 禁用 1启用")
    private Integer status;
    /**
     * 排序
     */

    @Schema(description = "排序")
    private Integer sort;

}
