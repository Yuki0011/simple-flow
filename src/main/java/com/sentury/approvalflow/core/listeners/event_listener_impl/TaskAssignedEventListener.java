package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.constants.MessageTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.ProcessInstanceAssignUserRecordParamDto;
import com.sentury.approvalflow.common.dto.ProcessInstanceExecutionDto;
import com.sentury.approvalflow.common.dto.third.MessageDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.ExecutionUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务被设置执行人了
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class TaskAssignedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {


        //任务被指派了人员
        FlowableEntityEvent flowableEntityEvent = (FlowableEntityEvent) event;


        TaskEntityImpl task = (TaskEntityImpl) flowableEntityEvent.getEntity();




        //执行人id
        String assignee = task.getAssignee();

        String tenantId = task.getTenantId();
        String executionId = task.getExecutionId();

        //查询父级执行id
        String parentExecutionId = ExecutionUtil.getParentExecutionId(executionId);
        //nodeid
        String nodeId = task.getTaskDefinitionKey();

        //实例id
        String processInstanceId = task.getProcessInstanceId();

        String processDefinitionId = task.getProcessDefinitionId();
        //流程id
        String flowId = FlowableUtils.getFlowId(processDefinitionId, task.getTenantId());
        ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto = new ProcessInstanceAssignUserRecordParamDto();
        processInstanceAssignUserRecordParamDto.setFlowId(flowId);
        processInstanceAssignUserRecordParamDto.setProcessInstanceId(processInstanceId);

        processInstanceAssignUserRecordParamDto.setNodeId(nodeId);
        processInstanceAssignUserRecordParamDto.setUserId((assignee));
        processInstanceAssignUserRecordParamDto.setTaskId(task.getId());
        processInstanceAssignUserRecordParamDto.setNodeName(task.getName());


        processInstanceAssignUserRecordParamDto.setFlowUniqueId(FlowableUtils.getFlowUniqueId(nodeId, flowId,
                processInstanceId, task.getTenantId()));


        String taskType = task.getVariableLocal(ProcessInstanceConstant.VariableKey.TASK_TYPE, String.class);

        processInstanceAssignUserRecordParamDto.setTaskType(taskType);
        processInstanceAssignUserRecordParamDto.setExecutionId(executionId);
        processInstanceAssignUserRecordParamDto.setTenantId(tenantId);
        processInstanceAssignUserRecordParamDto.setParentExecutionId(parentExecutionId);


        BizHttpUtil.createTaskEvent(processInstanceAssignUserRecordParamDto);

        //保存执行信息
        ProcessInstanceExecutionDto processInstanceExecutionDto = new ProcessInstanceExecutionDto();
        processInstanceExecutionDto.setExecutionId(executionId);
        processInstanceExecutionDto.setParentIdExecutionId(parentExecutionId);
        processInstanceExecutionDto.setProcessInstanceId(processInstanceId);
        processInstanceExecutionDto.setNodeId(nodeId);
        processInstanceExecutionDto.setTenantId(tenantId);
        processInstanceExecutionDto.setFlowId(flowId);
        BizHttpUtil.saveExecution(processInstanceExecutionDto);

        //待办消息

        //任务被创建了


        Map<String, Object> variables = task.getVariables();


        String taskId = task.getId();
        MessageDto messageDto = MessageDto.builder()
                .userId(assignee)
                .flowId(flowId)
                .processInstanceId(processInstanceId)


                .bizUniqueId(taskId)
                .tenantId(tenantId)
                .param(JsonUtil.toJSONString(variables))

                .type(MessageTypeEnum.TODO_TASK.getType())
                .readed(false).build();
        BizHttpUtil.saveMessage(messageDto);



    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.TASK_ASSIGNED.toString());

    }
}
