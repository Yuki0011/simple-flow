package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.RoleDto;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
public interface IRoleService extends IService<Role> {

    /**
     * 根据用户id获取角色key集合
     * @param userId
     * @return
     */
    R2<Set<String>> queryRoleKeyByUserId(String userId);





    /**
     * 创建角色
     *
     * @param roleDto
     * @return
     */
    R2 create(RoleDto roleDto);

    /**
     * 修改角色
     *
     * @param roleDto
     * @return
     */
    R2 edit(RoleDto roleDto);

    /**
     * 删除角色
     *
     * @param roleDto
     * @return
     */
    R2 delete(RoleDto roleDto);

    /**
     * 获取角色的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合(包括按钮权限ID)
     */
    R2<List<Long>> getRoleMenuIds(String roleId);

    /**
     * 修改角色的资源权限
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    R2 updateRoleMenus(String roleId, List<Long> menuIds);
}
