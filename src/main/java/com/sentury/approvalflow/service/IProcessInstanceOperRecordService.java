package com.sentury.approvalflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskParamDto;
import com.sentury.approvalflow.domain.entity.ProcessInstanceOperRecord;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-11-03 17:45
 */
public interface IProcessInstanceOperRecordService extends IService<ProcessInstanceOperRecord> {

    /**
     * 保存记录
     *
     * @param userId
     * @param taskParamDto
     * @param operType
     * @param desc
     * @return
     */
    R2 saveRecord(String userId, TaskParamDto taskParamDto, String operType, String desc);

    /**
     * 撤销流程
     * @param userId
     * @param processInstanceId
     * @return
     */
    R2 saveCancelProcessRecord(String userId, String processInstanceId);

    /**
     * 发起流程
     *
     * @param userId
     * @param processInstanceId
     * @param flowId
     * @return
     */
    R2 saveStartProcessRecord(String userId, String processInstanceId, String flowId);

}
