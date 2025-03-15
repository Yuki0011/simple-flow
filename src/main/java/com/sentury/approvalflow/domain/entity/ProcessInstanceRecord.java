package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 流程记录
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */

@Schema(description = "<p> 流程记录 </p>")
@Getter
@Setter
@Accessors(chain = true)
public class ProcessInstanceRecord  extends BaseEntityForWorkFlow {



    /**
     * 流程名字
     */

    @Schema(description = "流程名字")
    @TableField("`name`")
    private String name;

    /**
     * 发起人的主部门id
     */

    @Schema(description = "发起人的主部门id")
    @TableField("`main_dept_id`")
    private String mainDeptId;

    /**
     * 头像
     */

    @Schema(description = "头像")
    @TableField("`logo`")
    private String logo;

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    @TableField("`user_id`")
    private String userId;


    /**
     * 流程id
     */

    @Schema(description = "流程id")
    @TableField("`flow_id`")
    private String flowId;

    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
    @TableField("`process_instance_id`")
    private String processInstanceId;

    /**
     * 流程实例业务key
     */

    @Schema(description = "流程实例业务key")
    @TableField("`process_instance_biz_key`")
    private String processInstanceBizKey;

    /**
     * 流程实例业务编码
     */

    @Schema(description = "流程实例业务编码")
    @TableField("`process_instance_biz_code`")
    private String processInstanceBizCode;

    /**
     * 表单数据
     */

    @Schema(description = "表单数据")
    @TableField("`form_data`")
    private String formData;

    /**
     * 组id
     */

    @Schema(description = "组id")
    @TableField("`group_id`")
    private Long groupId;

    /**
     * 组名称
     */

    @Schema(description = "组名称")
    @TableField("`group_name`")
    private String groupName;

    /**
     * 状态
     */

    @Schema(description = "状态")
    @TableField("`status`")
    private Integer status;

    /**
     * 结束时间
     */

    @Schema(description = "结束时间")
    @TableField("`end_time`")
    private Date endTime;

    /**
     * 上级流程实例id
     */

    @Schema(description = "上级流程实例id")
    @TableField("`parent_process_instance_id`")
    private String parentProcessInstanceId;
    /**
     * 流程节点内容
     */

    @Schema(description = "流程节点内容")
    @TableField("`process`")
    private String process;
    /**
     * 结果
     */

    @Schema(description = "结果")
    @TableField("`result`")
    private Integer result;
    /**
     * 主流程节点执行id
     */
    @Schema(description = "主流程节点执行id")
    @TableField("`parent_process_node_execution_id`")
    private String parentProcessNodeExecutionId;

    @TableField(exist = false)
    private String rootUserCode;

}
