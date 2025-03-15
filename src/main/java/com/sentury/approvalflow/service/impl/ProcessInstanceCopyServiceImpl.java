package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.ProcessInstanceCopy;
import com.sentury.approvalflow.mapper.ProcessInstanceCopyMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessInstanceCopyService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 流程抄送数据 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-05-20
 */
@Service
public class ProcessInstanceCopyServiceImpl extends ServiceImpl<ProcessInstanceCopyMapper, ProcessInstanceCopy> implements IProcessInstanceCopyService, IClearService {


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
                .in(ProcessInstanceCopy::getFlowId, flowIdList)
                .eq(ProcessInstanceCopy::getTenantId, tenantId)
                .remove();
    }
}
