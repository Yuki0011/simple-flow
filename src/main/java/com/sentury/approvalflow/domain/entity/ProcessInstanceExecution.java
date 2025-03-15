package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 流程实例执行id表
 * </p>
 *
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProcessInstanceExecution extends BaseEntityForWorkFlow {
    /**
     * 执行id
     */
    @TableField("`execution_id`")
    private String executionId;
    /**
     * 上级的执行id
     */
    @TableField("`parent_execution_id`")
    private String parentIdExecutionId;
    /**
     * 流程id
     */
    @TableField("`flow_id`")
    private String flowId;

    /**
     * 实例id
     */
    @TableField("`process_instance_id`")
    private String processInstanceId;

    /**
     * 节点id
     */
    @TableField("`node_id`")
    private String nodeId;


}
