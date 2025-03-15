package com.sentury.approvalflow.common.service.core;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskDto;
import com.sentury.approvalflow.common.dto.TaskParamDto;
import com.sentury.approvalflow.common.dto.VariableQueryParamDto;

import java.util.List;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-10-16 16:59
 */
public interface ITaskService {
    /**
     * 完成任务
     *
     * @param taskParamDto
     * @return
     */
    R2 complete(TaskParamDto taskParamDto);



    /**
     * 查询任务
     *
     * @param taskId
     * @param userId
     * @return
     */
    R2 queryTask(String taskId, String userId);

    /**
     * 查询任务评论
     *
     * @param paramDto
     * @return
     */
    R2 queryTaskComments(VariableQueryParamDto paramDto);


    /**
     * 查询任务的执行人
     * @param taskParamDto
     * @return
     */
    R2<List<TaskDto>> queryTaskAssignee(TaskParamDto taskParamDto);
}
