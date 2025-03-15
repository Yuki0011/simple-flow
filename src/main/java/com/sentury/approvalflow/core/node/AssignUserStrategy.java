package com.sentury.approvalflow.core.node;

import com.sentury.approvalflow.common.dto.flow.Node;

import java.util.List;
import java.util.Map;

/**
 * 指定用户策略处理器
 */
public interface AssignUserStrategy {

    /**
     * 策略注册方法
     *
     * @param key
     */
    default void afterPropertiesSet(Integer key) {
        AssignUserStrategyFactory.register(key, this);
    }

    /**
     * 抽象方法 处理表达式
     *
     * @param node
     * @param rootUserId
     * @param variables
     * @param tenantId
     */
    List<String> handle(Node node, String rootUserId, Map<String,Object> variables,String tenantId);


}
