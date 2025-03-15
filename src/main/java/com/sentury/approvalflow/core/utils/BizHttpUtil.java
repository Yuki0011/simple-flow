package com.sentury.approvalflow.core.utils;

import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.MessageDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import com.sentury.approvalflow.common.utils.HttpUtil;
import cn.hutool.extra.spring.SpringUtil;

import java.util.List;
import java.util.Map;

public class BizHttpUtil {








    /**
     * 节点开始事件
     *
     * @param nodeRecordParamDto
     */
    public static void startNodeEvent(ProcessInstanceNodeRecordParamDto nodeRecordParamDto) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.startNodeEvent(nodeRecordParamDto);
        HttpUtil.checkResult(r);
    }


    /**
     * 节点结束事件
     *
     * @param nodeRecordParamDto
     */
    public static void endNodeEvent(ProcessInstanceNodeRecordParamDto nodeRecordParamDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.endNodeEvent(nodeRecordParamDto);
        HttpUtil.checkResult(r);
    }

    /**
     * 节点取消事件
     *
     * @param nodeRecordParamDto
     */
    public static void cancelNodeEvent(ProcessInstanceNodeRecordParamDto nodeRecordParamDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.cancelNodeEvent(nodeRecordParamDto);
        HttpUtil.checkResult(r);
    }

    /**
     * 查询部门列表
     *
     * @param deptIdList
     * @param tenantId
     */
    public static List<DeptDto> queryDeptList(List<String> deptIdList, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2<List<DeptDto>> listR = remoteService.queryDeptList(deptIdList);
        return listR.getData();
    }




    /**
     * 保存消息
     *
     * @param messageDto
     */
    public static void saveMessage(MessageDto messageDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.saveMessage(messageDto);
        HttpUtil.checkResult(r);
    }

    /**
     * 流程结束事件
     *
     * @param processInstanceParamDto
     */
    public static void processEndEvent(ProcessInstanceParamDto processInstanceParamDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.processEndEvent(processInstanceParamDto);
        HttpUtil.checkResult(r);
    }


    /**
     * 流程开始事件
     *
     * @param processInstanceParamDto
     */
    public static void processStartEvent(ProcessInstanceRecordParamDto processInstanceParamDto) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.processStartEvent(processInstanceParamDto);
        HttpUtil.checkResult(r);
    }

    /**
     * 根据角色id集合查询用户id集合
     *
     * @param roleIdList
     * @param tenantId
     */
    public static R2<List<String>> queryUserIdListByRoleIdList(List<String> roleIdList, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        return remoteService.queryUserIdListByRoleIdList(roleIdList);
    }

    /**
     * 根据角色id集合和部门id集合查询用户id集合
     *
     * @param roleIdList 角色id集合
     * @param deptIdList 部门id集合
     * @param tenantId
     */
    public static R2<List<String>> queryUserIdListByRoleIdListAndDeptIdList(List<String> roleIdList,
                                                                            List<String> deptIdList,
                                                                            String tenantId) {

        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setDeptIdList(deptIdList);
        userQueryDto.setRoleIdList(roleIdList);

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        return remoteService.queryUserIdListByRoleIdListAndDeptIdList(userQueryDto);
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @param tenantId
     */
    public static List<String> loadRoleIdListByUserId(String userId, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        return remoteService.loadRoleIdListByUserId(userId).getData();

    }

    /**
     * 根据部门id集合查询所有的用户id集合
     *
     * @param deptIdList
     * @param tenantId
     */
    public static R2<List<String>> queryUserIdListByDepIdList(List<String> deptIdList, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        return remoteService.queryUserIdListByDepIdList(deptIdList);

    }

    /**
     * 查询上级部门
     *
     * @param deptId
     * @param tenantId
     */
    public static R2<List<DeptDto>> queryParentDeptList(String deptId, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        return remoteService.queryParentDeptList(deptId);

    }

    /**
     * 批量查询子级部门
     *
     * @param deptIdList
     * @param tenantId
     */
    public static R2<Map<String, List<DeptDto>>> batchQueryChildDeptList(List<String> deptIdList, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        return remoteService.batchQueryChildDeptList(deptIdList);


    }


    /**
     * 查询流程管理员
     *
     * @param flowId   流程id
     * @param tenantId
     */
    public static R2<String> queryProcessAdmin(String flowId, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);


        return remoteService.queryProcessAdmin(flowId);

    }



    /**
     * 查询流程
     *
     * @param flowId   流程id
     * @param tenantId
     */
    public static R2<ProcessDto> queryProcess(String flowId, String tenantId) {

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);


        return remoteService.queryProcess(flowId);

    }

    /**
     * 查询节点数据
     *
     * @param flowId
     * @param nodeId
     * @param tenantId
     * @return
     */
    public static R2<String> queryNodeOriData(String flowId, String nodeId, String tenantId) {


        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        return remoteService.getNodeData(flowId, nodeId);
    }

    /**
     * 查询所有的用户信息
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public static R2<Map<String, Object>> queryUserInfo(String userId, String tenantId) {


        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);


        return remoteService.queryUserAllInfo(userId);
    }


    /**
     * 节点开始指派用户了
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    public static void createTaskEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.createTaskEvent(processInstanceAssignUserRecordParamDto);
        HttpUtil.checkResult(r);
    }


    /**
     * 任务完成事件
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    public static void taskCompletedEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {



        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.taskCompletedEvent(processInstanceAssignUserRecordParamDto);
        HttpUtil.checkResult(r);
    }

    /**
     * 任务结束事件
     *
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    public static void taskEndEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto) {


        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.taskEndEvent(processInstanceAssignUserRecordParamDto);
        HttpUtil.checkResult(r);
    }


    /**
     * 保存抄送数据
     *
     * @param processCopyDto
     * @return
     */
    public static void saveCC(ProcessInstanceCopyDto processCopyDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.saveCC(processCopyDto);
        HttpUtil.checkResult(r);

    }

    /**
     * 保存节点原始数据
     *
     * @param processNodeDataDto
     * @return
     */
    public static void saveNodeOriData(ProcessNodeDataDto processNodeDataDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.saveNodeData(processNodeDataDto);
        HttpUtil.checkResult(r);
    }


    /**
     * 保存执行信息
     *
     * @param processInstanceExecutionDto
     * @return
     */
    public static void saveExecution(ProcessInstanceExecutionDto processInstanceExecutionDto) {
        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);
        R2 r = remoteService.saveExecution(processInstanceExecutionDto);
        HttpUtil.checkResult(r);
    }


}
