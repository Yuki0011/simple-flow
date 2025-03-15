package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.RoleDto;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sentury.approvalflow.domain.entity.Menu;
import com.sentury.approvalflow.domain.entity.Role;
import com.sentury.approvalflow.domain.entity.RoleMenu;
import com.sentury.approvalflow.domain.entity.UserRole;
import com.sentury.approvalflow.mapper.RoleMapper;
import com.sentury.approvalflow.service.IRoleMenuService;
import com.sentury.approvalflow.service.IRoleService;
import com.sentury.approvalflow.service.IUserRoleService;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IRoleMenuService roleMenuService;

    /**
     * 根据用户id获取角色key集合
     *
     * @param userId
     * @return
     */
    @Override
    public R2<Set<String>> queryRoleKeyByUserId(String userId) {
        List<UserRole> userRoleList = userRoleService.queryListByUserId(userId).getData();
        if (CollUtil.isEmpty(userRoleList)) {
            return R2.success(CollUtil.newHashSet());
        }
        Set<String> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        return R2.success(roleIdSet);

    }







    /**
     * 创建角色
     *
     * @param roleDto
     * @return
     */
    @Transactional
    @Override
    public R2 create(RoleDto roleDto) {
        String key = roleDto.getId();
        Long count = this.lambdaQuery().eq(Role::getKey, key).count();
        if (count > 0) {
            return R2.fail("角色编码已存在");
        }
        String name = roleDto.getName();
        count = this.lambdaQuery().eq(Role::getName, name).count();
        if (count > 0) {
            return R2.fail("角色名字已存在");
        }

        Role role = new Role();
        role.setName(name);
        role.setKey(key);
        role.setStatus(roleDto.getStatus());
        role.setUserId(Long.valueOf(LoginHelper.getUserId()+""));
        this.save(role);
        return R2.success();
    }

    /**
     * 修改角色
     *
     * @param roleDto
     * @return
     */
    @Override
    public R2 edit(RoleDto roleDto) {
        String tenantId = TenantUtil.get();
        String key = roleDto.getId();
        Long count = this.lambdaQuery()
                .eq(Role::getKey, key)
                .count();
        if (count == 0) {
            return R2.fail("角色不存在");
        }
        String name = roleDto.getName();
        count = this.lambdaQuery().ne(Role::getKey, key)
                .eq(Role::getName, name)
                .count();
        if (count > 0) {
            return R2.fail("角色名字已存在");
        }

        this.lambdaUpdate()
                .set(Role::getName, roleDto.getName())
                .set(Role::getStatus, roleDto.getStatus())
                .set(Role::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Role::getKey, key)
                .update(new Role());

        return R2.success();
    }

    /**
     * 删除角色
     *
     * @param roleDto
     * @return
     */
    @Transactional
    @Override
    public R2 delete(RoleDto roleDto) {
        this.lambdaUpdate().eq(Role::getKey, roleDto.getId()).remove();
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, roleDto.getId());
        userRoleService.remove(queryWrapper);
        return R2.success();
    }

    /**
     * 获取角色的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合(包括按钮权限ID)
     */
    @Override
    public R2<List<Long>> getRoleMenuIds(String roleId) {

        MPJLambdaWrapper<RoleMenu> lambdaWrapper = new MPJLambdaWrapper<>();
        lambdaWrapper.select(RoleMenu::getMenuId)
                .innerJoin(Menu.class, Menu::getId, RoleMenu::getMenuId)
                .eq(RoleMenu::getRoleId, roleId);
        List<Long> longList = roleMenuService.selectJoinList(Long.class, lambdaWrapper);

        return R2.success(longList);
    }

    /**
     * 修改角色的资源权限
     *
     * @param roleId
     * @param menuIds
     * @return
     */
    @Override
    public R2 updateRoleMenus(String roleId, List<Long> menuIds) {
        // 删除角色菜单
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        // 新增角色菜单
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<RoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        RoleMenu roleMenu = new RoleMenu();
                        roleMenu.setMenuId(menuId);
                        roleMenu.setRoleId(roleId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenus);
        }
        return R2.success();
    }
}
