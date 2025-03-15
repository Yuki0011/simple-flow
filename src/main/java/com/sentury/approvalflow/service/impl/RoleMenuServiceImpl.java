package com.sentury.approvalflow.service.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.sentury.approvalflow.domain.entity.RoleMenu;
import com.sentury.approvalflow.mapper.RoleMenuMapper;
import com.sentury.approvalflow.service.IRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-10
 */
@Service
public class RoleMenuServiceImpl extends MPJBaseServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

}
