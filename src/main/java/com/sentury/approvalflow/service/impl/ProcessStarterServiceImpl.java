package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.ProcessStarter;
import com.sentury.approvalflow.mapper.ProcessStarterMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessStarterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 流程发起人 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-05-30
 */
@Service
public class ProcessStarterServiceImpl extends ServiceImpl<ProcessStarterMapper, ProcessStarter> implements IProcessStarterService, IClearService {

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
                .in(ProcessStarter::getProcessId, processIdList)
                .eq(ProcessStarter::getTenantId, tenantId)
                .remove();
    }
}
