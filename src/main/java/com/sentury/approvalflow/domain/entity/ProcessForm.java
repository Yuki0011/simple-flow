package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Schema
@Getter
@Setter
@Accessors(chain = true)
public class ProcessForm extends BaseEntityForWorkFlow {


    /**
     * 流程唯一id
     */

    @Schema(description = "流程唯一id")
    @TableField("`unique_id`")
    private String uniqueId;
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    @TableField("`flow_id`")
    private String flowId;

    /**
     * 表单名称
     */

    @Schema(description = "表单名称")
    @TableField("`form_name`")
    private String formName;


    /**
     * 表单id
     */

    @Schema(description = "表单id")
    @TableField("`form_id`")
    private String formId;


    /**
     * 表单类型
     */

    @Schema(description = "表单类型")
    @TableField("`form_type`")
    private String formType;

    /**
     * 表单属性
     */

    @Schema(description = "表单属性")
    @TableField("`props`")
    private String props;



}
