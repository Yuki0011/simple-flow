package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.ProcessMain;
import com.sentury.approvalflow.mapper.ProcessMainMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProcessMainServiceImpl extends ServiceImpl<ProcessMainMapper, ProcessMain> implements IProcessMainService, IClearService {
    /**
     * 清理数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId
     */
    @Override
    public void clearProcess(String uniqueId, List<String> flowIdList, List<Long> processIdList, String tenantId) {
        this.lambdaUpdate()
                .eq(ProcessMain::getUniqueId, uniqueId)
                .eq(ProcessMain::getTenantId, tenantId)
                .remove();
    }
}
