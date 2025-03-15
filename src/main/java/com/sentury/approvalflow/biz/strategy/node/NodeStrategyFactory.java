package com.sentury.approvalflow.biz.strategy.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NodeStrategyFactory {

    private static final Map<Integer, NodeStrategy> STRATEGY_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 提供获取策略的方法
     *
     * @param key
     * @return
     */
    public static NodeStrategy getStrategy(int key) {
        NodeStrategy sendService = STRATEGY_CONCURRENT_HASH_MAP.get(key);
        return sendService;
    }

    /**
     * 在Bean属性初始化后执行该方法
     *
     * @param key                  批次类型枚举
     * @param nodeConditionHandler 表达式处理接口
     */
    public static void register(Integer key, NodeStrategy nodeConditionHandler) {
        STRATEGY_CONCURRENT_HASH_MAP.put(key, nodeConditionHandler);
    }




}
