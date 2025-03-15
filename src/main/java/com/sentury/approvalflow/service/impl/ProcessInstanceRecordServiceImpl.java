package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.mapper.ProcessInstanceRecordMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 流程记录 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-05-07
 */
@Service
public class ProcessInstanceRecordServiceImpl extends ServiceImpl<ProcessInstanceRecordMapper, ProcessInstanceRecord> implements IProcessInstanceRecordService, IClearService {
    /**
     * 根据流程实例id查询流程记录
     *
     * @param processInstanceId 流程实例id
     * @return 流程实例
     */
    @Override
    public ProcessInstanceRecord getByProcessInstanceId(String processInstanceId) {
        return this.lambdaQuery().eq(ProcessInstanceRecord::getProcessInstanceId,processInstanceId).one();
    }

    /**
     * 清理数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId      租户id
     */
    @Override
    public void clearProcess(String uniqueId, List<String> flowIdList, List<Long> processIdList, String tenantId) {




        this.lambdaUpdate()
                .in(ProcessInstanceRecord::getFlowId, flowIdList)
                .eq(ProcessInstanceRecord::getTenantId, tenantId)
                .remove();
    }
}
