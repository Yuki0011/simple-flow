package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色和菜单关联表
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */
@Getter
@Setter
@Accessors(chain = true)
public class RoleMenu  extends BaseEntityForWorkFlow {

    /**
     * 角色ID
     */
    @TableField("`role_id`")
    private String roleId;

    /**
     * 菜单ID
     */
    @TableField("`menu_id`")
    private Long menuId;

}
