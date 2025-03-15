package com.sentury.approvalflow.common.service.core;

import com.sentury.approvalflow.common.dto.*;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-08-04 16:40
 */
public interface IFlowService {

    /**
     * 创建流程模型
     *
     * @param createFlowDto
     * @return
     */
    R2 create(CreateFlowDto createFlowDto);

    /**
     * 发起流程
     *
     * @param processInstanceParamDto
     * @return
     */
    R2 start(ProcessInstanceParamDto processInstanceParamDto);

    /**
     * 清理所有的流程
     *
     * @param clearProcessParamDto 清理数据对象
     * @return 成功失败
     */
    R2 clearProcess(ClearProcessParamDto clearProcessParamDto);

    /**
     * 查询用户待办任务
     *
     * @param taskQueryParamDto
     * @return
     */
    R2<PageResultDto<TaskDto>> queryTodoTask(TaskQueryParamDto taskQueryParamDto);

    /**
     * 查询用户已办任务
     *
     * @param taskQueryParamDto
     * @return
     */
    R2<PageResultDto<TaskDto>> queryCompletedTask(TaskQueryParamDto taskQueryParamDto);

    /**
     * 获取我已办的流程实例
     *
     * @param processQueryParamDto
     * @return
     */
     R2<PageResultDto<ProcessInstanceDto>> queryCompletedProcessInstance(ProcessQueryParamDto processQueryParamDto);
}
