package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.constants.ApproveResultEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.ProcessInstanceAssignUserRecordParamDto;
import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.dto.flow.Nobody;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.ApproveNode;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.APPROVE_NODE_RESULT;

/**
 * 实体删除
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class EntityDeletedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {

        //流程开始了
        Object entity = null;
        if (event instanceof org.flowable.common.engine.impl.event.FlowableEntityEventImpl) {
            org.flowable.common.engine.impl.event.FlowableEntityEventImpl f = (org.flowable.common.engine.impl.event.FlowableEntityEventImpl) event;
            entity = f.getEntity();


        } else if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl f = (FlowableEntityEventImpl) event;
            entity = f.getEntity();
        }



        if (entity != null) {
            log.info("实例删除了：{}", entity.getClass().getCanonicalName());
            if (entity instanceof ExecutionEntityImpl) {
                ExecutionEntityImpl e = (ExecutionEntityImpl) entity;
                List<ExecutionEntityImpl> executions = e.getExecutions();
                log.info("实体删除了 exeid：{} 是否是多实例根节点:{} 子执行实例数量:{}", e.getId(), e.isMultiInstanceRoot(),
                        e.getExecutions().size());
                if (e.isMultiInstanceRoot() && executions.isEmpty()) {
                    //多实例任务节点  并且没有子执行实例  说明是没有设置执行人
                    String activityId = e.getActivityId();
                    String activityName = e.getActivityName();
                    String flowId = e.getProcessDefinitionKey();
                    String tenantId = e.getTenantId();
                    String processInstanceId = e.getProcessInstanceId();
                    String executionId = e.getId();

                    log.info("节点删除了 没有执行人:{}", e.getId());

                    Node node = NodeDataStoreFactory.getInstance().getNode(flowId, activityId, tenantId);
                    if (node != null) {
                        Nobody nobody = null;
                        if(node instanceof ApproveNode){
                            ApproveNode approveNode= (ApproveNode) node;
                            nobody=approveNode.getNobody();
                        }


                        String handler = nobody.getHandler();

                        if (StrUtil.equals(handler, ProcessInstanceConstant.USER_TASK_NOBODY_HANDLER_TO_PASS)) {
                            //直接通过
                            e.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                                    ApproveResultEnum.PASS.getValue());

                        }
                        if (StrUtil.equals(handler, ProcessInstanceConstant.USER_TASK_NOBODY_HANDLER_TO_REFUSE)) {
                            //自动拒绝
                            e.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                                    ApproveResultEnum.REFUSE.getValue());
                        }
                        //通知节点结束了
                        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
                        Map<String, Object> variables = runtimeService.getVariables(executionId);
                        ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();
                        processInstanceNodeRecordParamDto.setFlowId(flowId);
                        processInstanceNodeRecordParamDto.setExecutionId(executionId);
                        processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);
                        processInstanceNodeRecordParamDto.setData(JsonUtil.toJSONString(variables));
                        processInstanceNodeRecordParamDto.setNodeId(activityId);
                        processInstanceNodeRecordParamDto.setNodeName(activityName);
                        processInstanceNodeRecordParamDto.setTenantId(tenantId);
                        BizHttpUtil.endNodeEvent(processInstanceNodeRecordParamDto);
                    }
                }
            }
        }


        if (entity != null && entity instanceof TaskEntityImpl) {

            TaskService taskService = SpringUtil.getBean(TaskService.class);

            //任务完成
            FlowableEntityEvent flowableEntityEvent = (FlowableEntityEvent) event;
            TaskEntityImpl task = (TaskEntityImpl) flowableEntityEvent.getEntity();
            String assignee = task.getAssignee();

            String tenantId = task.getTenantId();

            //nodeid
            String nodeId = task.getTaskDefinitionKey();

            //实例id
            String processInstanceId = task.getProcessInstanceId();

            String processDefinitionId = task.getProcessDefinitionId();
            //流程id
            String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);
            ProcessInstanceAssignUserRecordParamDto processInstanceAssignUserRecordParamDto = new ProcessInstanceAssignUserRecordParamDto();
            processInstanceAssignUserRecordParamDto.setFlowId(flowId);
            processInstanceAssignUserRecordParamDto.setProcessInstanceId(processInstanceId);
            //   processNodeRecordAssignUserParamDto.setParentExecutionId();
            processInstanceAssignUserRecordParamDto.setData(JsonUtil.toJSONString(taskService.getVariables(task.getId())));
            processInstanceAssignUserRecordParamDto.setLocalData(JsonUtil.toJSONString(taskService.getVariablesLocal(task.getId())));
            processInstanceAssignUserRecordParamDto.setNodeId(nodeId);
            processInstanceAssignUserRecordParamDto.setUserId((assignee));
            processInstanceAssignUserRecordParamDto.setTaskId(task.getId());
            processInstanceAssignUserRecordParamDto.setNodeName(task.getName());
            processInstanceAssignUserRecordParamDto.setFlowUniqueId(FlowableUtils.getFlowUniqueId(nodeId, flowId,
                    processInstanceId, tenantId));
            String taskType = task.getVariableLocal(ProcessInstanceConstant.VariableKey.TASK_TYPE, String.class);
            //RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
            processInstanceAssignUserRecordParamDto.setTaskType(taskType);


            processInstanceAssignUserRecordParamDto.setExecutionId(task.getExecutionId());
            processInstanceAssignUserRecordParamDto.setTenantId(tenantId);

            BizHttpUtil.taskEndEvent(processInstanceAssignUserRecordParamDto);

            log.info("实体删除了：{}", JsonUtil.toJSONString(processInstanceAssignUserRecordParamDto));

        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.ENTITY_DELETED.toString());

    }
}
