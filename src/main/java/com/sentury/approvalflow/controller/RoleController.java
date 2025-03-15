package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.service.IRoleBizService;
import com.sentury.approvalflow.service.IRoleService;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.third.RoleDto;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色
 */
@RestController
@RequestMapping(value = {"role"})
public class RoleController {


    @Resource
    private IRoleService roleService;

    @Resource
    private IRoleBizService roleBizService;

    /**
     * 创建角色
     * @param role
     * @return
     */
    @PostMapping("create")
    public Object create(@RequestBody RoleDto role){
        return roleService.create(role);
    }
    /**
     * 修改角色
     * @param roleDto
     * @return
     */
    @PutMapping("edit")
    public Object edit(@RequestBody RoleDto roleDto){
        return roleService.edit(roleDto);
    }


    /**
     * 删除角色
     *
     * @param role
     * @return
     */
    @SneakyThrows
    @DeleteMapping("delete")
    public Object delete(@RequestBody RoleDto role) {

        return roleService.delete(role);

    }
    /**
     * 获取角色的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合(包括按钮权限ID)
     */
    @GetMapping("getRoleMenuIds")
    R2<List<Long>> getRoleMenuIds(String roleId){
        return roleService.getRoleMenuIds(roleId);
    }

    /**
     * 修改角色权限
     * @param roleId
     * @param menuIds
     * @return
     */
    @PutMapping("/{roleId}/menus")
    public R2 updateRoleMenus(
            @PathVariable String roleId,
            @RequestBody List<Long> menuIds
    ) {
      return roleService.updateRoleMenus(roleId,menuIds);

    }
    /**
     * 获取角色详细信息
     * @param user
     * @return
     */
    @GetMapping("getDetail")
    public Object getDetail(long id){
        return roleService.getById(id);
    }




    /**
     * 查询所有角色
     * @param pageDto
     * @return
     */
    @GetMapping("queryAll")
    public R2 queryAll(){
        return roleBizService.queryAll();
    }

    /**
     * 保存角色用户
     * @param pageDto
     * @return
     */
    @PostMapping("saveUserList")
    public Object saveUserList(@RequestBody List<NodeUser> nodeUserDtoList, String  id){
        return roleBizService.saveUserList(nodeUserDtoList,id);
    }


}
