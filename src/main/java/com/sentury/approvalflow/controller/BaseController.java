package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.domain.vo.*;
import com.sentury.approvalflow.service.IBaseService;
import com.sentury.approvalflow.common.dto.IndexPageStatistics;
import com.sentury.approvalflow.common.dto.R2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基础的控制器接口
 */
@Tag(name = "基础的控制器接口", description = "基础的控制器接口")
@RestController
@RequestMapping(value = "base")
public class BaseController {

    @Resource
    private IBaseService baseService;

    /**
     * 修改前端版本号
     *
     * @param webVersionVO
     * @return
     */
    @Operation(summary = "修改前端版本号", description = "修改前端版本号")
    @PostMapping("setWebVersion")
    public R2 setWebVersion(@RequestBody WebVersionVO webVersionVO) {
        return baseService.setWebVersion(webVersionVO);
    }

    /**
     * 获取当前系统版本号
     *
     * @return
     */
    @Operation(summary = "获取当前系统版本号", description = "获取当前系统版本号")
    @GetMapping("getWebVersion")
    public R2 getWebVersion() {
        return baseService.getWebVersion();
    }


    /**
     * 首页数据
     *
     * @return
     */
    @Operation(summary = "首页数据", description = "首页数据")
    @GetMapping("index")
    public R2<IndexPageStatistics> index() {
        return baseService.index();
    }


    /**
     * 获取所有地区数据
     *
     * @return
     */
    @Operation(summary = "获取所有地区数据", description = "获取所有地区数据")
    @GetMapping("areaList")
    public R2 areaList() {
        return baseService.areaList();
    }

    /**
     * 同步数据--主要是真的第三方接口，比如钉钉
     *
     * @return
     */
    @Operation(summary = "同步数据--主要是真的第三方接口，比如钉钉", description = "同步数据--主要是真的第三方接口，比如钉钉")
    @PostMapping("loadRemoteData")
    public R2 loadRemoteData() {
        return baseService.loadRemoteData();
    }

    /**
     * 格式化流程显示
     *
     * @param nodeFormatParamVo
     * @return
     */
    @Operation(summary = "格式化流程显示", description = "格式化流程显示")
    @PostMapping("formatStartNodeShow")
    public R2<NodeFormatResultVo> formatStartNodeShow(@RequestBody NodeFormatParamVo nodeFormatParamVo) {
        return baseService.formatStartNodeShow(nodeFormatParamVo);
    }

    /**
     * 查询头部显示数据
     *
     * @param nodeFormatParamVo
     * @return
     */
    @Operation(summary = "查询头部显示数据", description = "查询头部显示数据")
    @PostMapping("queryHeaderShow")
    public R2<TaskHeaderShowResultVO> queryHeaderShow(@RequestBody QueryFormListParamVo nodeFormatParamVo) {
        return baseService.queryHeaderShow(nodeFormatParamVo);
    }

    /**
     * 获取任务操作列表
     *
     * @param taskId 任务id
     * @return
     */
    @Operation(summary = "获取任务操作列表", description = "获取任务操作列表")
    @GetMapping("queryTaskOperData")
    public R2<TaskOperDataResultVO> queryTaskOperData(String taskId) {
        return baseService.queryTaskOperData(taskId);
    }


}
