package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.domain.entity.ProcessGroup;
import com.sentury.approvalflow.service.IProcessGroupService;
import com.sentury.approvalflow.common.dto.R2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程组接口
 */
@Tag(name = "流程组接口", description = "流程组接口")
@RestController
@RequestMapping(value = {"processGroup"})
public class ProcessGroupController {

    @Resource
    private IProcessGroupService processGroupService;

    /**
     * 组列表
     *
     * @return
     */
    @Operation(summary = "组列表", description = "组列表")
    @GetMapping("list")
    public R2<List<ProcessGroup>> queryList() {
        return processGroupService.queryList();
    }

    /**
     * 新增流程分组
     *
     * @param processGroup 分组名
     * @return 添加结果
     */
    @Operation(summary = "新增流程分组", description = "新增流程分组")
    @PostMapping("create")
    public R2 create(@RequestBody ProcessGroup processGroup) {
        return processGroupService.create(processGroup);
    }
    /**
     * 上移排序
     *
     * @param processGroup 分组名
     * @return 添加结果
     */
    @Operation(summary = "上移排序", description = "上移排序")
    @PostMapping("topSort")
    public R2 topSort(@RequestBody ProcessGroup processGroup) {
        return processGroupService.topSort(processGroup);
    }
    /**
     * 下移排序
     *
     * @param processGroup 分组名
     * @return 添加结果
     */
    @Operation(summary = "下移排序", description = "下移排序")
    @PostMapping("bottomSort")
    public R2 bottomSort(@RequestBody ProcessGroup processGroup) {
        return processGroupService.bottomSort(processGroup);
    }

    /**
     * 修改流程分组
     *
     * @param processGroup 分组名
     * @return 添加结果
     */
    @Operation(summary = "修改流程分组", description = "修改流程分组")
    @PostMapping("edit")
    public R2 edit(@RequestBody ProcessGroup processGroup) {
        return processGroupService.edit(processGroup);
    }


    /**
     *  删除分组
     * @param id
     * @return
     */
    @Parameter(name = "id", description = "", in = ParameterIn.PATH, required = true)
    @Operation(summary = "删除分组", description = "删除分组")
    @DeleteMapping("delete/{id}")
    public R2 delete(@PathVariable long id){
        return processGroupService.delete(id);
    }
}
