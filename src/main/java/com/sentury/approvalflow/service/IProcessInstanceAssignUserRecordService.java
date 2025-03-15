package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.ProcessInstanceAssignUserRecordParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.github.yulichang.base.MPJBaseService;
import com.sentury.approvalflow.domain.entity.ProcessInstanceAssignUserRecord;

/**
 * <p>
 * 流程节点记录-执行人 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-10
 */
public interface IProcessInstanceAssignUserRecordService extends MPJBaseService<ProcessInstanceAssignUserRecord> {
    /**
     * 设置执行人
     * @param processInstanceAssignUserRecordParamDto
     * @return
     */
    R2 createTaskEvent(ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto);

    /**
     * 任务完成通知
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




}
