package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.dto.ProcessInstanceParamDto;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.APPROVE_RESULT;
import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.REDIS_KEY_OF_FLOW_UNIQUE_ID;

/**
 * 流程取消了
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class ProcessCancelEventListener implements EventListenerStrategy, InitializingBean {

    @Resource
    private RedisTemplate redisTemplate;
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {

        //流程开完成
        org.flowable.engine.delegate.event.impl.FlowableProcessCancelledEventImpl e = (org.flowable.engine.delegate.event.impl.FlowableProcessCancelledEventImpl) event;
        DelegateExecution execution = e.getExecution();
        String tenantId = execution.getTenantId();

        String processInstanceId = e.getProcessInstanceId();

        String processDefinitionId = e.getProcessDefinitionId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);

        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);


        //结果
        Integer finalResult = execution.getVariable(StrUtil.format("{}_{}", flowId, APPROVE_RESULT), Integer.class);


        ProcessInstanceParamDto processInstanceParamDto = new ProcessInstanceParamDto();
        processInstanceParamDto.setProcessInstanceId(processInstanceId);
        processInstanceParamDto.setCancel(true);
        processInstanceParamDto.setResult(finalResult);
        processInstanceParamDto.setFlowId(flowId);
        processInstanceParamDto.setParamMap(variables);
        processInstanceParamDto.setTenantId(tenantId);
        BizHttpUtil.processEndEvent(processInstanceParamDto);


        //删除flowuniqueid
        redisTemplate.delete(StrUtil.format(REDIS_KEY_OF_FLOW_UNIQUE_ID,processInstanceId));
        //删除执行人
        redisTemplate.delete(StrUtil.format("resolveAssignee_{}",processInstanceId));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.PROCESS_CANCELLED.toString());

    }
}
