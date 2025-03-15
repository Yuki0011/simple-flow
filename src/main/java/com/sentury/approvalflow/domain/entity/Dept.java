package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */
@Getter
@Setter
@Accessors(chain = true)
public class Dept  extends BaseEntityForWorkFlow {

    /**
     * 部门名
     */
    @TableField("`name`")
    private String name;

    /**
     * 上级部门id
     */
    @TableField("`parent_id`")
    private Long parentId;





    @TableField("`status`")
    private Integer status;
    @TableField("`sort`")
    private Integer sort;
}
