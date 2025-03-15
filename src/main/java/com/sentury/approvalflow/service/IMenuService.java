package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.github.yulichang.base.MPJBaseService;
import com.sentury.approvalflow.domain.entity.Menu;
import com.sentury.approvalflow.domain.vo.MenuVO;

import java.util.Set;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-10
 */
public interface IMenuService extends MPJBaseService<Menu> {

    /**
     * 路由列表
     * @return
     */
    R2 routes();

    /**
     * 根据角色id集合查询权限集合
     * @param roleKeySet
     * @return
     */
    R2<Set<String>> listRolePerms(Set<String> roleKeySet);

    /**
     * 树形显示菜单
     * @return
     */
    R2 treeAll(String keywords, Integer status);

    /**
     * 创建菜单
     * @param menuVO
     * @return
     */
    R2 create(MenuVO menuVO);

    /**
     * 编辑菜单
     * @param menuVO
     * @return
     */
    R2 edit(MenuVO menuVO);

    /**
     * 删除菜单
     * @param menuVO
     * @return
     */
    R2 delete(MenuVO menuVO);
}
