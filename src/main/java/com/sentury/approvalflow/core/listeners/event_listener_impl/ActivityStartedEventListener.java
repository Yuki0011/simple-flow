package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableActivityEventImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 实例节点开始了
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class ActivityStartedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {
        //节点开始执行
        FlowableActivityEventImpl flowableActivityEvent = (FlowableActivityEventImpl) event;
        String activityId = flowableActivityEvent.getActivityId();
        String activityName = flowableActivityEvent.getActivityName();
        DelegateExecution execution = flowableActivityEvent.getExecution();
        String tenantId = execution.getTenantId();

        String executionId = flowableActivityEvent.getExecutionId();
        log.info("节点开始  节点id：{} 名字:{} executionId:{}", activityId, activityName, executionId);


        String processInstanceId = flowableActivityEvent.getProcessInstanceId();

        String processDefinitionId = flowableActivityEvent.getProcessDefinitionId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);

        DelegateExecution parent = execution.getParent();
        if (parent != null && parent.isMultiInstanceRoot()) {
            //上级是多实例
        } else {
            saveStartEventContent(flowId, processInstanceId, activityId, activityName, execution.getId(), tenantId,
                    execution.getParentId());
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.ACTIVITY_STARTED.toString());

    }
}
