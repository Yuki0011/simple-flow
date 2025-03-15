package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.domain.entity.Dept;
import com.sentury.approvalflow.service.IDeptService;
import com.sentury.approvalflow.service.IOrgService;
import com.sentury.approvalflow.domain.vo.DeptVO;
import com.sentury.approvalflow.common.dto.R2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 部门接口控制器
 */
@RestController
@RequestMapping(value = {"dept"})
public class DeptController {

    @Autowired
    private IDeptService deptService;

    @Resource
    private IOrgService orgService;








    /**
     * 创建部门
     * @param deptVO 部门对象
     * @return
     */
    @PostMapping("create")
    public R2 create(@RequestBody DeptVO deptVO){
        return deptService.create(deptVO);
    }

    /**
     * 修改部门
     * @param deptVO 部门对象
     * @return
     */
    @PutMapping("update")
    public R2 update(@RequestBody DeptVO deptVO){
        return deptService.updateDept(deptVO);
    }


    /**
     * 创建部门
     *
     * @param dept 部门对象
     * @return
     */
    @DeleteMapping("delete")
    public R2 delete(@RequestBody Dept dept){
        return orgService.delete(dept);
    }


}
