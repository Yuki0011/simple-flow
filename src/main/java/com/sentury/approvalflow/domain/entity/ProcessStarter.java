package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 流程发起人
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProcessStarter  extends BaseEntityForWorkFlow {


    /**
     * 用户id或者部门id
     */
    @TableField("`type_id`")
    private String typeId;

    /**
     *  类型 user dept
     */
    @TableField("`type`")
    private String type;

    /**
     * 流程表id
     */
    @TableField("`process_id`")
    private Long processId;

    /**
     * 流程id
     */
    @TableField("`flow_id`")
    private String flowId;

    /**
     * 数据
     */
    @TableField("`data`")
    private String data;

    /**
     * 是否包含下级部门
     */
    @TableField("contain_children_dept")
    private Boolean containChildrenDept;


}
