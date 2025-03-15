package com.sentury.approvalflow.core.listeners;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

/**
 * 发起人用户任务节点
 */
@Slf4j
public class StarterUserTaskCreateListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        log.debug(delegateTask.getClass().getCanonicalName());
        TaskService taskService = SpringUtil.getBean(TaskService.class);

        String assignee = delegateTask.getAssignee();
        String name = delegateTask.getName();
        log.debug("任务{}-执行人:{}", name, assignee);
        String processInstanceId = delegateTask.getProcessInstanceId();
        TaskEntityImpl taskEntity = (TaskEntityImpl) delegateTask;
        String nodeId = taskEntity.getTaskDefinitionKey();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        //流程id
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);

        if (StrUtil.isBlank(assignee)) {

            runtimeService.setVariableLocal(taskEntity.getExecutionId(),
                    ProcessInstanceConstant.VariableKey.AUTO_COMPLETE_TASK, true);
            taskService.complete(taskEntity.getId());


        }

    }
}
