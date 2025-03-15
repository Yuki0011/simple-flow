package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableActivityEventImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 实例节点结束了
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class ActivityCompletedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);

        //节点完成执行

        FlowableActivityEventImpl flowableActivityEvent = (FlowableActivityEventImpl) event;
        DelegateExecution execution = flowableActivityEvent.getExecution();

        String activityId = flowableActivityEvent.getActivityId();
        String activityName = flowableActivityEvent.getActivityName();
        log.info("实例完成2 节点id：{} 名字:{}", activityId, activityName);
        String executionId = flowableActivityEvent.getExecutionId();
        Map<String, Object> variables = runtimeService.getVariables(executionId);

        String processInstanceId = flowableActivityEvent.getProcessInstanceId();

        String processDefinitionId = flowableActivityEvent.getProcessDefinitionId();
        String tenantId = execution.getTenantId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);

        ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();
        processInstanceNodeRecordParamDto.setFlowId(flowId);
        processInstanceNodeRecordParamDto.setExecutionId(executionId);
        processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);
        processInstanceNodeRecordParamDto.setData(JsonUtil.toJSONString(variables));
        processInstanceNodeRecordParamDto.setNodeId(activityId);
//            processNodeRecordParamDto.setNodeType(nodeDto.getType());
        processInstanceNodeRecordParamDto.setNodeName(activityName);
        processInstanceNodeRecordParamDto.setTenantId(tenantId);
        BizHttpUtil.endNodeEvent(processInstanceNodeRecordParamDto);

        StaticLog.info("节点结束了： {} {}  {}",flowId,executionId,activityName);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.ACTIVITY_COMPLETED.toString());

    }
}
