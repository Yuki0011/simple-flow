package com.sentury.approvalflow.service;

import java.util.List;

public interface IClearService {
    /**
     * 清理流程数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId 租户id
     */
    void clearProcess(String uniqueId, List<String>flowIdList, List<Long> processIdList, String tenantId);

}
