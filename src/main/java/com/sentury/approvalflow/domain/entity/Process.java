package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sentury.approvalflow.domain.constants.ValidGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * <p>
 *
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */

@Schema(description = "<p> </p>")
@Getter
@Setter
@Accessors(chain = true)
public class Process  extends BaseEntityForWorkFlow {



    /**
     * 表单ID
     */

    @Schema(description = "表单ID")
    @TableField("`flow_id`")
    private String flowId;

    /**
     * 表单名称
     */

    @Schema(description = "表单名称")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "表单名称不能为空")
    @TableField("`name`")
    private String name;

    @TableField("`name_second`")
    private String nameSecond;

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

    /**
     * 表单设置内容
     */

    @Schema(description = "表单设置内容")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "请设置表单")
    @TableField("`form_items`")
    private String formItems;
    /**
     * pc表单内容
     */
    @Schema(description = "pc表单内容")
    @TableField("`form_items_pc`")
    private String formItemsPc;

    /**
     * 流程设置内容
     */

    @Schema(description = "流程设置内容")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "请设置流程")
    @TableField("`process`")
    private String process;

    /**
     * 备注
     */

    @Schema(description = "备注")
    @TableField("`remark`")
    private String remark;


    @Schema(hidden = true)
    @TableField("`sort`")
    private Integer sort;

    /**
     * 0 正常 1=隐藏
     */

    @Schema(description = "0 正常 1=隐藏")
    @TableField("`is_hidden`")
    private Boolean hidden;



    /**
     * 0 正常 1=停用
     */

    @Schema(description = "0 正常 1=停用")
    @TableField("`is_stop`")
    private Boolean stop;

    /**
     * 流程管理员
     */

    @Schema(description = "流程管理员")
    @TableField("`admin_id`")
    private String adminId;


    /**
     * 管理员
     */

    @Schema(description = "管理员")
    @NotBlank(groups = {ValidGroup.Crud.Create.class},message = "管理员不能为空")
    @TableField("`admin`")
    private String admin;

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


    /**
     * 版本
     */

    @Schema(description = "版本")
    @TableField("`version`")
    private Integer version;



}
