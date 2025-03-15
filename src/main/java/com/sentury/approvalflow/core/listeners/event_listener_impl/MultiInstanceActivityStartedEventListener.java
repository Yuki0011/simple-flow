package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableMultiInstanceActivityEventImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 多实例节点开始了
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class MultiInstanceActivityStartedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {



        FlowableMultiInstanceActivityEventImpl flowableActivityEvent = (FlowableMultiInstanceActivityEventImpl) event;
        DelegateExecution execution = flowableActivityEvent.getExecution();
        String tenantId = execution.getTenantId();
        String activityId = flowableActivityEvent.getActivityId();
        String activityName = flowableActivityEvent.getActivityName();

        String processInstanceId = flowableActivityEvent.getProcessInstanceId();

        String processDefinitionId = flowableActivityEvent.getProcessDefinitionId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);




        saveStartEventContent(flowId, processInstanceId, activityId, activityName,
                flowableActivityEvent.getExecutionId(), tenantId, execution.getParentId());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.MULTI_INSTANCE_ACTIVITY_STARTED.toString());

    }
}
