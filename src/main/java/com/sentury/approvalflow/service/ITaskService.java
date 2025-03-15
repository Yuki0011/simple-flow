package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskParamDto;

/**
 * 任务处理
 */
public interface ITaskService {


    /**
     * 完成任务
     *
     * @param taskParamDto
     * @return
     */
    R2 completeTask(TaskParamDto taskParamDto);


    /**
     * 获取任务信息
     * @param taskId 任务id
     * @return
     */
    R2 getTask(String taskId);

}
