package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.ProcessInstanceNodeRecord;

/**
 * <p>
 * 流程节点记录 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-10
 */
public interface IProcessInstanceNodeRecordService extends IService<ProcessInstanceNodeRecord> {
    /**
     * 节点开始
     * @param processInstanceNodeRecordParamDto
     * @return
     */
    R2 startNodeEvent(ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto);

    /**
     * 节点结束
     * @param processInstanceNodeRecordParamDto
     * @return
     */
    R2 endNodeEvent(ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto);

    /**
     * 节点取消
     * @param processInstanceNodeRecordParamDto
     * @return
     */
    R2 cancelNodeEvent(ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto);
}
