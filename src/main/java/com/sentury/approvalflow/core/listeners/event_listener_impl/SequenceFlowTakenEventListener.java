package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.NODE_JUMP_KEY;

/**
 * 分支执行
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class SequenceFlowTakenEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {

        FlowableSequenceFlowTakenEventImpl e = (FlowableSequenceFlowTakenEventImpl) event;

        DelegateExecution execution = e.getExecution();

        String tenantId = execution.getTenantId();
        String executionId = e.getExecutionId();
        String activityId = e.getId();
        String processInstanceId = e.getProcessInstanceId();
        String processDefinitionId = e.getProcessDefinitionId();
        String flowId = FlowableUtils.getFlowId(processDefinitionId, tenantId);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);

        Node node = NodeDataStoreFactory.getInstance().getNode(flowId, activityId, tenantId);
        if (node == null) {
            return;
        }


        Map<String, Object> processVariables = runtimeService.getVariables(executionId);

        {
            ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();
            processInstanceNodeRecordParamDto.setFlowId(flowId);
            processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);
//            processNodeRecordParamDto.setChildExecutionId(childExecutionIdList);
            processInstanceNodeRecordParamDto.setData(JsonUtil.toJSONString(processVariables));
            processInstanceNodeRecordParamDto.setNodeId(activityId);
//        processInstanceNodeRecordParamDto.setFromNodeId();
            processInstanceNodeRecordParamDto.setParentNodeId(MapUtil.getStr(processVariables, StrUtil.format(NODE_JUMP_KEY, activityId)));
            processInstanceNodeRecordParamDto.setFlowUniqueId(FlowableUtils.getFlowUniqueId(activityId,flowId,
                    processInstanceId, tenantId));


            processInstanceNodeRecordParamDto.setNodeType((node.getType()));

            processInstanceNodeRecordParamDto.setNodeName(node.getNodeName());
            processInstanceNodeRecordParamDto.setExecutionId(executionId);
            processInstanceNodeRecordParamDto.setTenantId(tenantId);
            BizHttpUtil.startNodeEvent(processInstanceNodeRecordParamDto);
        }
        {

            ProcessInstanceNodeRecordParamDto processInstanceNodeRecordParamDto = new ProcessInstanceNodeRecordParamDto();
            processInstanceNodeRecordParamDto.setFlowId(flowId);
            processInstanceNodeRecordParamDto.setExecutionId(executionId);
            processInstanceNodeRecordParamDto.setProcessInstanceId(processInstanceId);
            processInstanceNodeRecordParamDto.setData(JsonUtil.toJSONString(processVariables));
            processInstanceNodeRecordParamDto.setNodeId(activityId);
//            processNodeRecordParamDto.setNodeType(nodeDto.getType());
            processInstanceNodeRecordParamDto.setNodeName(node.getNodeName());
            processInstanceNodeRecordParamDto.setTenantId(tenantId);
            BizHttpUtil.endNodeEvent(processInstanceNodeRecordParamDto);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.SEQUENCEFLOW_TAKEN.toString());

    }
}
