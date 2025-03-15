package com.sentury.approvalflow.core.listeners;

import com.sentury.approvalflow.common.dto.ProcessInstanceExecutionDto;
import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.NODE_JUMP_KEY;
import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.REDIS_KEY_OF_FLOW_UNIQUE_ID;

/**
 * 节点单个条件处理器
 */
public interface EventListenerStrategy {

    /**
     * 策略注册方法
     *
     * @param key
     */
    default void afterPropertiesSet(String key) {
        EventListenerStrategyFactory.register(key, this);
    }


    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    void handle(FlowableEvent event);


    default void saveStartEventContent(String flowId, String processInstanceId,
                                       String activityId, String activityName, String executionId,String tenantId,
                                       String parentExecutionId) {



        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);



        Map<String, Object> processVariables = runtimeService.getVariables(executionId);


        Node node = NodeDataStoreFactory.getInstance().getNode(flowId, activityId, tenantId);
        ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();
        processInstanceNodeRecordParamDto.setFlowId(flowId);
        processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);
        processInstanceNodeRecordParamDto.setData(JsonUtil.toJSONString(processVariables));
        processInstanceNodeRecordParamDto.setNodeId(activityId);
        String parentNodeId = MapUtil.getStr(processVariables, StrUtil.format(NODE_JUMP_KEY, activityId));
        processInstanceNodeRecordParamDto.setParentNodeId(parentNodeId);
        RedisTemplate redisTemplate = SpringUtil.getBean("redisTemplate",RedisTemplate.class);
        if (StrUtil.isNotBlank(parentNodeId)) {
            //跳转过来的
            String s = IdUtil.fastSimpleUUID();
            ZSetOperations zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add(StrUtil.format(REDIS_KEY_OF_FLOW_UNIQUE_ID,processInstanceId),StrUtil.format(
                    "{}||||{}",activityId,s),
                    System.currentTimeMillis());
            processInstanceNodeRecordParamDto.setFlowUniqueId(s);
        } else {

            String flowUniqueId = FlowableUtils.getFlowUniqueId(activityId, flowId,
                    processInstanceId, tenantId);

            processInstanceNodeRecordParamDto.setFlowUniqueId(flowUniqueId);

        }


        if (node != null) {

            processInstanceNodeRecordParamDto.setNodeType((node.getType()));

        }
        processInstanceNodeRecordParamDto.setNodeName(activityName);
        processInstanceNodeRecordParamDto.setExecutionId(executionId);
        processInstanceNodeRecordParamDto.setTenantId(tenantId);
        BizHttpUtil.startNodeEvent(processInstanceNodeRecordParamDto);

        //保存执行信息
        ProcessInstanceExecutionDto processInstanceExecutionDto = new ProcessInstanceExecutionDto();
        processInstanceExecutionDto.setExecutionId(executionId);
        processInstanceExecutionDto.setParentIdExecutionId(parentExecutionId);
        processInstanceExecutionDto.setProcessInstanceId(processInstanceId);
        processInstanceExecutionDto.setNodeId(activityId);
        processInstanceExecutionDto.setTenantId(tenantId);
        processInstanceExecutionDto.setFlowId(flowId);
        BizHttpUtil.saveExecution(processInstanceExecutionDto);
        StaticLog.info("节点开始了： {} {}  {} ",flowId,executionId,activityName);


        //清除变量
        runtimeService.removeVariable(executionId, StrUtil.format(NODE_JUMP_KEY, activityId));
    }

}
