
package com.sentury.approvalflow.common.service.biz;

import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;

import java.util.List;
import java.util.Map;

/**
 * 远程调用的接口
 */
public interface IRemoteService {
    /**
     * 根据用户id查询角色id集合
     * @param userId
     * @return
     */
    R2<List<String>> loadRoleIdListByUserId(String userId);

    /**
     * 根据部门id获取部门列表
     * @param deptIdList
     * @return
     */
    R2<List<DeptDto>> queryDeptList(List<String> deptIdList);

    /**
     * 保存待办任务
     * @param messageDto
     * @return
     */
    R2 saveMessage(com.sentury.approvalflow.common.dto.third.MessageDto messageDto);

    /**
     * 根据角色id集合查询用户id集合
     *
     * @param roleIdList
     * @return
     */
    R2<List<String>> queryUserIdListByRoleIdList(List<String> roleIdList);

    /**
     * 根据用户id查询上级部门列表
     * @param userQueryDto
     * @return
     */
    R2<List<String>> queryUserIdListByRoleIdListAndDeptIdList(UserQueryDto userQueryDto);

    /**
     * 保存抄送
     * @param copyDto
     * @return
     */
    R2 saveCC(ProcessInstanceCopyDto copyDto);



    /**
     * 检查是否是所有的父级
     * @param checkParentDto
     * @return
     */
    R2<Boolean> checkIsAllParent(CheckParentDto checkParentDto);

    /**
     * 根据部门id集合查询用户id集合
     *
     * @param deptIdList
     * @return
     */
    R2<List<String>> queryUserIdListByDepIdList(List<String> deptIdList);

    /**
     * 检查是否是所有的子级
     * @param checkChildDto
     * @return
     */
    R2<Boolean> checkIsAllChild(CheckChildDto checkChildDto);

    /**
     * 获取用户的信息-包括扩展字段
     * @param userId
     * @return
     */
    R2<Map<String,Object>> queryUserAllInfo(String userId);






    /**
     * 查询上级部门
     * @param deptId
     * @return
     */
    R2<List<DeptDto>> queryParentDeptList(String deptId);




    /**
     * 批量获取部门的下级部门
     * @param deptIdList 部门id集合
     * @return
     */
    R2<Map<String,List<DeptDto>>> batchQueryChildDeptList(List<String> deptIdList);

    /**
     * 开始节点事件
     * @param recordParamDto
     * @return
     */
    R2 startNodeEvent(ProcessInstanceNodeRecordParamDto recordParamDto);


    /**
     * 流程创建了
     * @param processInstanceRecordParamDto
     * @return
     */
    R2 processStartEvent(ProcessInstanceRecordParamDto processInstanceRecordParamDto);



    /**
     * 完成节点事件
     * @param recordParamDto
     * @return
     */
    R2 endNodeEvent(ProcessInstanceNodeRecordParamDto recordParamDto);

    /**
     * 节点取消
     * @param recordParamDto
     * @return
     */
    R2 cancelNodeEvent(ProcessInstanceNodeRecordParamDto recordParamDto);

    /**
     * 开始设置执行人
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    R2 createTaskEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto);

    /**
     * 任务结束事件
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    R2 taskCompletedEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto);

    /**
     * 任务结束
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    R2 taskEndEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto);


    /**
     * 实例结束
     *
     * @param processInstanceParamDto
     * @return
     */
    R2 processEndEvent(ProcessInstanceParamDto processInstanceParamDto);


    /**
     * 查询流程管理员
     *
     * @param flowId
     * @return
     */
    R2<String> queryProcessAdmin(String flowId);


    /**
     * 查询流程数据
     * @param flowId
     * @return
     */
    R2<ProcessDto> queryProcess(String flowId);

    /**
     * 保存流程节点数据
     * @param processNodeDataDto
     * @return
     */
    R2 saveNodeData(ProcessNodeDataDto processNodeDataDto);

    /***
     * 获取节点数据
     * @param flowId
     * @param nodeId
     * @return
     */
    R2<String> getNodeData(String flowId, String nodeId);

    /**
     * 保存执行数据
     * @param processInstanceExecutionDto
     * @return
     */
    R2 saveExecution(ProcessInstanceExecutionDto processInstanceExecutionDto);
}
