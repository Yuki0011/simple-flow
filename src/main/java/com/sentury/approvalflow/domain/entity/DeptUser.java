package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 部门-用户表
 *
 * @author Vincent
 * @since 2023-12-27
 */
@Getter
@Setter
@Accessors(chain = true)
public class DeptUser extends BaseEntityForWorkFlow {


    /**
     * 部门id
     */
    @TableField("`dept_id`")
    private String  deptId;


    /**
     * 主管user_id
     */
    @TableField("`user_id`")
    private String userId;

}
