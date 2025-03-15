package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.ProcessInstanceAssignUserRecordParamDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务完成
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class TaskCompletedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {

        TaskService taskService = SpringUtil.getBean(TaskService.class);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);

        //任务完成
        FlowableEntityEvent flowableEntityEvent = (FlowableEntityEvent) event;
        TaskEntityImpl task = (TaskEntityImpl) flowableEntityEvent.getEntity();
        //执行人id
        String assignee = task.getAssignee();

        //nodeid
        String nodeId = task.getTaskDefinitionKey();

        //实例id
        String processInstanceId = task.getProcessInstanceId();

        String processDefinitionId = task.getProcessDefinitionId();
        //流程id
        String tenantId = task.getTenantId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);
        ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto = new ProcessInstanceAssignUserRecordParamDto();
        processInstanceAssignUserRecordParamDto.setFlowId(flowId);
        processInstanceAssignUserRecordParamDto.setProcessInstanceId(processInstanceId);
        //   processNodeRecordAssignUserParamDto.setParentExecutionId();
        Map<String, Object> variables = taskService.getVariables(task.getId());
        processInstanceAssignUserRecordParamDto.setData(JsonUtil.toJSONString(variables));
        processInstanceAssignUserRecordParamDto.setLocalData(JsonUtil.toJSONString(taskService.getVariablesLocal(task.getId())));
        processInstanceAssignUserRecordParamDto.setNodeId(nodeId);
        processInstanceAssignUserRecordParamDto.setUserId((assignee));
        processInstanceAssignUserRecordParamDto.setTaskId(task.getId());
        processInstanceAssignUserRecordParamDto.setNodeName(task.getName());


        processInstanceAssignUserRecordParamDto.setFlowUniqueId(FlowableUtils.getFlowUniqueId(nodeId, flowId,
                processInstanceId, tenantId));

        String taskType = task.getVariableLocal(ProcessInstanceConstant.VariableKey.TASK_TYPE, String.class);
        processInstanceAssignUserRecordParamDto.setTaskType(taskType);


        processInstanceAssignUserRecordParamDto.setExecutionId(task.getExecutionId());
        processInstanceAssignUserRecordParamDto.setTenantId(tenantId);

        //审批结果
        //true同意  false拒绝


        //判断下 如果是自动完成的 不处理
        Object autoCompleteObj = runtimeService.getVariableLocal(task.getExecutionId(),
                ProcessInstanceConstant.VariableKey.AUTO_COMPLETE_TASK);
        processInstanceAssignUserRecordParamDto.setAuto(Convert.toBool(autoCompleteObj, false));


        BizHttpUtil.taskCompletedEvent(processInstanceAssignUserRecordParamDto);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.TASK_COMPLETED.toString());

    }
}
