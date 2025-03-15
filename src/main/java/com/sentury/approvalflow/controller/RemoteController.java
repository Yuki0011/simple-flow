package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.MessageDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 远程请求控制器
 */
@Tag(name = "远程请求控制器", description = "远程请求控制器")
@RestController
@RequestMapping("remote")
public class RemoteController {
    @Resource
    private IRemoteService remoteService;


    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Operation(summary = "根据用户id查询角色id集合", description = "根据用户id查询角色id集合")
    @GetMapping("loadRoleIdListByUserId")
    public R2<List<String>> loadRoleIdListByUserId(String userId) {
        return remoteService.loadRoleIdListByUserId(userId);
    }

    /**
     * 根据部门id获取部门列表
     *
     * @param deptIdList
     * @return
     */
    @Operation(summary = "根据部门id获取部门列表", description = "根据部门id获取部门列表")
    @PostMapping("queryDeptList")
    public R2<List<DeptDto>> queryDeptList(@RequestBody List<String> deptIdList) {
        return remoteService.queryDeptList(deptIdList);
    }

    /**
     * 保存待办任务
     *
     * @param messageDto
     * @return
     */
    @Operation(summary = "保存待办任务", description = "保存待办任务")
    @PostMapping("saveMessage")
    public R2 saveMessage(@RequestBody MessageDto messageDto) {
        return remoteService.saveMessage(messageDto);
    }

    /**
     * 根据角色id集合查询用户id集合
     *
     * @param roleIdList
     * @return
     */
    @Operation(summary = "根据角色id集合查询用户id集合", description = "根据角色id集合查询用户id集合")
    @PostMapping("queryUserIdListByRoleIdList")
    public R2<List<String>> queryUserIdListByRoleIdList(@RequestBody List<String> roleIdList) {
        return remoteService.queryUserIdListByRoleIdList(roleIdList);
    }

    /**
     * 根据角色id集合和部门id集合查询人员id集合
     *
     * @param userQueryDto 查询对象  只关注roleIdList和deptIdList即可
     * @return
     */
    @Operation(summary = "根据角色id集合和部门id集合查询人员id集合", description = "根据角色id集合和部门id集合查询人员id集合")
    @PostMapping("queryUserIdListByRoleIdListAndDeptIdList")
    public R2<List<String>> queryUserIdListByRoleIdListAndDeptIdList(@RequestBody UserQueryDto userQueryDto) {
        return remoteService.queryUserIdListByRoleIdListAndDeptIdList(userQueryDto);
    }

    /**
     * 保存抄送
     *
     * @param copyDto
     * @return
     */
    @Operation(summary = "保存抄送", description = "保存抄送")
    @PostMapping("saveCC")
    public R2 saveCC(@RequestBody ProcessInstanceCopyDto copyDto) {
        return remoteService.saveCC(copyDto);
    }

    /**
     * 检查是否是所有的父级
     *
     * @param checkParentDto
     * @return
     */
    @Operation(summary = "检查是否是所有的父级", description = "检查是否是所有的父级")
    @PostMapping("checkIsAllParent")
    public R2<Boolean> checkIsAllParent(@RequestBody CheckParentDto checkParentDto) {
        return remoteService.checkIsAllParent(checkParentDto);
    }


    /**
     * 根据部门id集合查询用户id集合
     *
     * @param deptIdList
     * @return
     */
    @Operation(summary = "根据部门id集合查询用户id集合", description = "根据部门id集合查询用户id集合")
    @PostMapping("queryUserIdListByDepIdList")
    public R2<List<String>> queryUserIdListByDepIdList(@RequestBody List<String> deptIdList) {
        return remoteService.queryUserIdListByDepIdList(deptIdList);
    }

    /**
     * 检查是否是所有的子级
     *
     * @param checkChildDto
     * @return
     */
    @Operation(summary = "检查是否是所有的子级", description = "检查是否是所有的子级")
    @PostMapping("checkIsAllChild")
    public R2<Boolean> checkIsAllChild(@RequestBody CheckChildDto checkChildDto) {
        return remoteService.checkIsAllChild(checkChildDto);
    }

    /**
     * 获取用户的信息-包括扩展字段
     *
     * @param userId
     * @return
     */
    @Operation(summary = "获取用户的信息-包括扩展字段", description = "获取用户的信息-包括扩展字段")
    @GetMapping("queryUserAllInfo")
    public R2<Map<String, Object>> queryUserAllInfo(String userId) {
        return remoteService.queryUserAllInfo(userId);
    }


    /**
     * 查询上级部门
     *
     * @param deptId
     * @return
     */
    @Operation(summary = "查询上级部门", description = "查询上级部门")
    @SneakyThrows
    @GetMapping("queryParentDeptList")
    public R2<List<DeptDto>> queryParentDeptList(String deptId) {

        return remoteService.queryParentDeptList(deptId);
    }


    /**
     * 批量获取部门的下级部门
     *
     * @param deptIdList 部门id集合
     * @return
     */
    @Operation(summary = "批量获取部门的下级部门", description = "批量获取部门的下级部门")
    @PostMapping("batchQueryChildDeptList")
    public R2<Map<String, List<DeptDto>>> batchQueryChildDeptList(@RequestBody List<String> deptIdList) {
        return remoteService.batchQueryChildDeptList(deptIdList);
    }

    /**
     * 开始节点事件
     *
     * @param recordParamDto
     * @return
     */
    @Operation(summary = "开始节点事件", description = "开始节点事件")
    @PostMapping("startNodeEvent")
    public R2 startNodeEvent(@RequestBody ProcessInstanceNodeRecordParamDto recordParamDto) {
        return remoteService.startNodeEvent(recordParamDto);
    }


    /**
     * 流程创建了
     *
     * @param processInstanceRecordParamDto
     * @return
     */
    @Operation(summary = "流程创建了", description = "流程创建了")
    @PostMapping("processStartEvent")
    public R2 processStartEvent(@RequestBody ProcessInstanceRecordParamDto processInstanceRecordParamDto) {
        return remoteService.processStartEvent(processInstanceRecordParamDto);
    }

    /**
     * 结束节点事件
     *
     * @param recordParamDto
     * @return
     */
    @Operation(summary = "结束节点事件", description = "结束节点事件")
    @PostMapping("endNodeEvent")
    public R2 endNodeEvent(@RequestBody ProcessInstanceNodeRecordParamDto recordParamDto) {
        return remoteService.endNodeEvent(recordParamDto);
    }

    /**
     * 取消节点事件
     *
     * @param recordParamDto
     * @return
     */
    @Operation(summary = "取消节点事件", description = "取消节点事件")
    @PostMapping("cancelNodeEvent")
    public R2 cancelNodeEvent(@RequestBody ProcessInstanceNodeRecordParamDto recordParamDto) {
        return remoteService.cancelNodeEvent(recordParamDto);
    }

    /**
     * 开始设置执行人
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    @Operation(summary = "开始设置执行人", description = "开始设置执行人")
    @PostMapping("createTaskEvent")
    public R2 createTaskEvent(@RequestBody ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {
        return remoteService.createTaskEvent(processInstanceAssignUserRecordParamDto);
    }

    /**
     * 任务结束事件
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    @Operation(summary = "任务结束事件", description = "任务结束事件")
    @PostMapping("taskCompletedEvent")
    public R2 taskCompletedEvent(@RequestBody ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {
        return remoteService.taskCompletedEvent(processInstanceAssignUserRecordParamDto);
    }

    /**
     * 任务结束
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    @Operation(summary = "任务结束", description = "任务结束")
    @PostMapping("taskEndEvent")
    public R2 taskEndEvent(@RequestBody ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {
        return remoteService.taskEndEvent(processInstanceAssignUserRecordParamDto);
    }


    /**
     * 实例结束
     *
     * @param processInstanceParamDto
     * @return
     */
    @Operation(summary = "实例结束", description = "实例结束")
    @PostMapping("processEndEvent")
    public R2 processEndEvent(@RequestBody ProcessInstanceParamDto processInstanceParamDto) {
        return remoteService.processEndEvent(processInstanceParamDto);
    }



    /**
     * 查询流程管理员
     *
     * @param flowId
     * @return
     */
    @Operation(summary = "查询流程管理员", description = "查询流程管理员")
    @GetMapping("queryProcessAdmin")
    public R2<String> queryProcessAdmin(String flowId) {
        return remoteService.queryProcessAdmin(flowId);
    }



    /**
     * 查询流程数据
     *
     * @param flowId
     * @return
     */
    @Operation(summary = "查询流程数据", description = "查询流程数据")
    @GetMapping("queryProcess")
    public R2<ProcessDto> queryProcess(String flowId) {
        return remoteService.queryProcess(flowId);
    }

    /**
     * 保存节点数据
     *
     * @param processNodeDataDto
     * @return
     */
    @Operation(summary = "保存节点数据", description = "保存节点数据")
    @PostMapping("saveNodeData")
    public R2 saveNodeData(@RequestBody ProcessNodeDataDto processNodeDataDto) {
        return remoteService.saveNodeData(processNodeDataDto);
    }

    /**
     * 保存执行数据
     * @param processInstanceExecutionDto
     * @return
     */
    @Operation(summary = "保存执行数据", description = "保存执行数据")
    @PostMapping("saveExecution")
    public R2 saveExecution(@RequestBody ProcessInstanceExecutionDto processInstanceExecutionDto) {
        return remoteService.saveExecution(processInstanceExecutionDto);
    }

    /**
     * 获取节点数据
     *
     * @param flowId 流程id
     * @param nodeId 节点id
     * @return
     */
    @Operation(summary = "获取节点数据", description = "获取节点数据")
    @GetMapping("getNodeData")
    public R2<String> getNodeData(String flowId, String nodeId) {
        return remoteService.getNodeData(flowId, nodeId);
    }
}
