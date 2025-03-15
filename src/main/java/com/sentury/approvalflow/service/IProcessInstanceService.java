package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.ProcessInstanceParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;

/**
 * 流程实例进程
 */
public interface IProcessInstanceService  {

    /**
     * 启动流程
     *
     * @param processInstanceParamDto
     * @return
     */
    R2 startProcessInstance(ProcessInstanceParamDto processInstanceParamDto);

    R2 startProcessInstanceWithOutLogin(ProcessInstanceParamDto processInstanceParamDto,String userId);



    /**
     *  查询已办任务的流程实例
     * @param pageVO
     * @return
     */
    R2 queryMineDoneProcessInstance(ProcessDataQueryVO pageVO);

    /**
     * 流程结束
     *
     * @param processInstanceParamDto
     * @return
     */
    R2 processEndEvent(ProcessInstanceParamDto processInstanceParamDto);

    /**
     * 查询流程实例
     * @param pageDto
     * @return
     */
    R2 queryList(ProcessDataQueryVO pageDto);

    /**
     * 查询流程实例详情
     * @param processInstanceId
     * @return
     */
    R2 queryDetailByProcessInstanceId(String processInstanceId);



    /**
     * 查询处理中的任务
     * @param processInstanceId
     * @return
     */
    R2 queryTaskListInProgress(String processInstanceId);



    String processInformationJson(String flowId,String instanceId,String processName,String flag);

    boolean removeProcessInstance(String processInstanceId);
}
