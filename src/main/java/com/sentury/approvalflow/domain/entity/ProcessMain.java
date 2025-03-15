package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sentury.approvalflow.domain.constants.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 流程主表
 */

@Schema(description = "流程主表")
@Getter
@Setter
@Accessors(chain = true)
public class ProcessMain extends BaseEntityForWorkFlow {





    /**
     * 表单名称
     */

    @Schema(description = "表单名称")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "表单名称不能为空")
    @TableField("`name`")
    private String name;

    /**
     * 图标配置
     */

    @Schema(description = "图标配置")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "表单头像不能为空")
    @TableField("`logo`")
    private String logo;



    /**
     * 分组ID
     */

    @Schema(description = "分组ID")
    @TableField("`group_id`")
    private Long groupId;





    @Schema(hidden = true)
    @TableField("`sort`")
    private Integer sort;



    /**
     * 范围描述显示
     */

    @Schema(description = "范围描述显示")
    @TableField("`range_show`")
    private String rangeShow;

    /**
     * 唯一性id
     */

    @Schema(description = "唯一性id")
    @TableField("`unique_id`")
    private String uniqueId;


    @Schema(description = "表单名称")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "表单名称不能为空")
    @TableField("`name_second`")
    private String nameSecond;

}
