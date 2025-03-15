package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.service.IMenuService;
import com.sentury.approvalflow.domain.vo.MenuVO;
import com.sentury.approvalflow.common.dto.R2;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author Vincent
 * @since 2023-06-10
 */
@RestController
@RequestMapping(value = {"menu","api/menu"})
public class MenuController {

    @Resource
    private IMenuService menuService;


    /**
     * 路由列表
     *
     * @return
     */
    @GetMapping("routes")
    public R2 routes() {
        return menuService.routes();
    }
    /**
     * 菜单树形列表
     *
     * @return
     */
    @GetMapping("treeAll")
    public R2 treeAll(String keywords, Integer status) {
        return menuService.treeAll(keywords, status);
    }
    /**
     * 创建菜单
     * @param menuVO
     * @return
     */
    @PostMapping("create")
    public R2 create(@RequestBody MenuVO menuVO){
        return menuService.create(menuVO);
    }
    /**
     * 编辑菜单
     * @param menuVO
     * @return
     */
    @PutMapping("edit")
    public R2 edit(@RequestBody MenuVO menuVO){
        return menuService.edit(menuVO);
    }
    /**
     * 删除菜单
     * @param menuVO
     * @return
     */
    @DeleteMapping("delete")
    public R2 delete(@RequestBody MenuVO menuVO){
        return menuService.delete(menuVO);
    }
}
