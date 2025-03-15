package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.third.DeptDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 选择组织对象数据
 * @author Huijun Zhao
 * @description
 * @date 2023-12-06 10:20
 */

@Schema(description = "选择组织对象数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgSelectShowVo {
    /**
     * 头部标题显示
     */

    @Schema(description = "头部标题显示")
    private List<DeptDto> titleDepartments;
    /**
     * 角色集合
     */

    @Schema(description = "角色集合")
    private List<OrgDataVo> roleList;
    /**
     * 子级部门
     */

    @Schema(description = "子级部门")
    private List<OrgDataVo> childDepartments;
    /**
     * 人员集合
     */

    @Schema(description = "人员集合")
    private List<OrgDataVo> employees;
}
