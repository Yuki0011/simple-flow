package com.sentury.approvalflow.core.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;

/**
 * @author zhj
 * @version 1.0
 * @description: 执行工具类
 * @date 2024/2/18 15:54
 */
public class ExecutionUtil {
    /**
     * 查询上级的执行id
     *
     * @param executionId 当前的执行id
     * @return 上级执行id
     */
    public static String getParentExecutionId(String executionId) {
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        if (execution == null) {
            return null;
        }
        String executionParentId = execution.getParentId();


        return executionParentId;
    }



}
