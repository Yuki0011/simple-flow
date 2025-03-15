package com.sentury.approvalflow.core.servicetask;

import com.sentury.approvalflow.common.dto.ProcessInstanceCopyDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.CopyNode;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.collection.CollUtil;
import com.sentury.approvalflow.core.node.AssignUserStrategyFactory;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 抄送任务处理器--java服务任务
 */
public class CopyServiceTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {

        ExecutionEntityImpl entity = (ExecutionEntityImpl) execution;
        String nodeId = entity.getActivityId();
        String flowId = entity.getProcessDefinitionKey();

        String tenantId = execution.getTenantId();
        Node node = NodeDataStoreFactory.getInstance().getNode(flowId, nodeId, tenantId);

//发起人
        String rootUserId = FlowableUtils.getStartUserId(execution);

        if (node != null) {

            Map<String, Object> variables = execution.getVariables();

            CopyNode copyNode= (CopyNode) node;

            Integer assignedType = copyNode.getAssignedType();

            List<String> userIdList = AssignUserStrategyFactory.getStrategy(assignedType).handle(node, rootUserId,
                    variables, tenantId);
            if(CollUtil.isNotEmpty(userIdList)){
                //发送抄送任务
                ProcessInstanceCopyDto processInstanceCopyDto = new ProcessInstanceCopyDto();
                processInstanceCopyDto.setNodeTime(new Date());
                processInstanceCopyDto.setStartUserId(rootUserId);
                processInstanceCopyDto.setFlowId(flowId);
                processInstanceCopyDto.setProcessInstanceId(execution.getProcessInstanceId());
                processInstanceCopyDto.setNodeId(nodeId);
                processInstanceCopyDto.setNodeName(node.getNodeName());
                processInstanceCopyDto.setFormData(JsonUtil.toJSONString(variables));
                processInstanceCopyDto.setUserIdList((userIdList));
                processInstanceCopyDto.setTenantId(tenantId);
                BizHttpUtil.saveCC(processInstanceCopyDto);
            }



        }


    }
}
