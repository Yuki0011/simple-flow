package com.sentury.approvalflow.core.listeners;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

/**
 * 审批节点
 */
@Slf4j
public class ApprovalCreateListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        TaskService taskService = SpringUtil.getBean(TaskService.class);


        String assignee = delegateTask.getAssignee();
        String name = delegateTask.getName();
        log.info("审批人创建了 任务{}-执行人:{}", name, assignee);
        String processInstanceId = delegateTask.getProcessInstanceId();
        TaskEntityImpl taskEntity = (TaskEntityImpl) delegateTask;


        String tenantId = taskEntity.getTenantId();

        String nodeId = taskEntity.getTaskDefinitionKey();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        //流程id
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);

        //设置流程唯一id
        String taskId = taskEntity.getId();
        taskService.setVariableLocal(taskId, ProcessInstanceConstant.VariableKey.FLOW_UNIQUE_ID,
                FlowableUtils.getFlowUniqueId(nodeId, flowId, processInstanceId, delegateTask.getTenantId())
        );



    }
}
