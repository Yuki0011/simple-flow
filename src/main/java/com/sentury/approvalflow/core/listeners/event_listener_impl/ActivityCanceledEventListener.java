package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 节点取消了
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class ActivityCanceledEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {
        //任务取消了
        org.flowable.engine.delegate.event.impl.FlowableActivityCancelledEventImpl activityCancelledEvent = (org.flowable.engine.delegate.event.impl.FlowableActivityCancelledEventImpl) event;
        String activityId = activityCancelledEvent.getActivityId();
        String activityName = activityCancelledEvent.getActivityName();

        String executionId = activityCancelledEvent.getExecutionId();
        String processInstanceId = activityCancelledEvent.getProcessInstanceId();

        log.info("取消的节点：{}   {} {}  {}", activityId, activityName, processInstanceId, executionId);
        DelegateExecution execution = activityCancelledEvent.getExecution();



        {
            //节点取消了
            ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();

            processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);


            processInstanceNodeRecordParamDto.setExecutionId(executionId);
            processInstanceNodeRecordParamDto.setNodeId(activityId);
            processInstanceNodeRecordParamDto.setNodeName(activityName);

            processInstanceNodeRecordParamDto.setTenantId(execution.getTenantId());
            BizHttpUtil.cancelNodeEvent(processInstanceNodeRecordParamDto);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.ACTIVITY_CANCELLED.toString());

    }
}
