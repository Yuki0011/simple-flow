package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.ProcessForm;
import com.sentury.approvalflow.mapper.ProcessFormMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProcessFormServiceImpl extends ServiceImpl<ProcessFormMapper, ProcessForm> implements IProcessFormService
        , IClearService {


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
                .eq(ProcessForm::getUniqueId, uniqueId)
                .eq(ProcessForm::getTenantId, tenantId)
                .remove();
    }
}
