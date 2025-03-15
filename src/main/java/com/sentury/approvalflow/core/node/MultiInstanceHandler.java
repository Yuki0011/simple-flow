package com.sentury.approvalflow.core.node;

import com.sentury.approvalflow.common.constants.ApproveResultEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Nobody;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.flow.node.ApproveNode;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserRootNode;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.APPROVE_NODE_RESULT;
import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.APPROVE_RESULT;

/**
 * 多实例任务处理
 */
@Component("multiInstanceHandler")
@Slf4j
public class MultiInstanceHandler {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 审批人节点
     * 处理执行人
     *
     * @param execution
     * @return
     */
    public List<String> resolveAssignee(DelegateExecution execution) {
        String executionId = execution.getId();
        ExecutionEntityImpl entity = (ExecutionEntityImpl) execution;


        String flowId = entity.getProcessDefinitionKey();
        String nodeId = entity.getActivityId();
        String processInstanceId = entity.getProcessInstanceId();

        ExecutionEntityImpl parent = entity.getParent();

        String redisKey = StrUtil.format("resolveAssignee_{}", execution.getProcessInstanceId());
        if (parent.isMultiInstanceRoot()) {
            //判断在redis中缓存
            HashOperations hashOperations = redisTemplate.opsForHash();
            Object o = hashOperations.get(redisKey, parent.getId());
            log.info("上级id：{} redis存储的执行人：{}", parent.getId(), o);
            if (o != null) {
                return JsonUtil.toList(String.class, o);
            }
        }


        //执行人集合
        Set<String> assignSet = new LinkedHashSet<>();

        IDataStoreHandler nodeDataStoreHandler = NodeDataStoreFactory.getInstance();


        String tenantId = execution.getTenantId();

        //发起人
        String rootUserId = FlowableUtils.getStartUserId(execution);

        //节点数据
        Node node = nodeDataStoreHandler.getNode(flowId, nodeId, tenantId);
        if (node != null) {

            SuperUserNode superUserNode = (SuperUserNode) node;

            Map<String, Object> variables = execution.getVariables();

            Integer assignedType = superUserNode.getAssignedType();

            List<String> userIdList = AssignUserStrategyFactory.getStrategy(assignedType).handle(node, rootUserId,
                    variables, tenantId);



            assignSet.addAll(userIdList);

        } else {
            //默认值
            log.error("设置执行人没有找到节点");
        }

        if (CollUtil.isEmpty(assignSet)) {
            log.info("没有找到执行人 开始设置默认执行人");
            List<String> list = handleEmptyAssign(entity, node, flowId, tenantId);
            log.info("设置默认执行人：{}", JsonUtil.toJSONString(list));
            if (CollUtil.isEmpty(list)) {
//                assignSet.add(ProcessInstanceConstant.DEFAULT_EMPTY_ASSIGN);
            }
        }

        log.info("设置执行人 assignSet={}", assignSet);
        if (entity.isMultiInstanceRoot()) {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put(redisKey, executionId, JsonUtil.toJSONString(assignSet));
        }

        return new ArrayList<>(assignSet);

    }

    private List<String> handleEmptyAssign(ExecutionEntityImpl execution, Node node, String flowId, String tenantId) {

        Nobody nobody = null;

        if (node instanceof ApproveNode) {
            ApproveNode approveNode = (ApproveNode) node;
            nobody = approveNode.getNobody();
        }


        String handler = nobody.getHandler();

        if (StrUtil.equals(handler, ProcessInstanceConstant.USER_TASK_NOBODY_HANDLER_TO_ADMIN)) {
            //指派给管理员

            R2<String> longR = BizHttpUtil.queryProcessAdmin(flowId, tenantId);

            String adminId = longR.getData();


            return CollUtil.newArrayList(adminId);
        }

        if (StrUtil.equals(handler, ProcessInstanceConstant.USER_TASK_NOBODY_HANDLER_TO_USER)) {
            //指定用户

            NodeUser nodeUser = nobody.getAssignedUser().get(0);

            return CollUtil.newArrayList(nodeUser.getId());

        }

        log.info("执行id：{}  是否是根节点执行：{}", execution.getId(), execution.isMultiInstanceRoot());

        return new ArrayList<>();
    }



    /**
     * 处理发起人用户任务
     * 默认情况下自动完成
     *
     * @param execution
     * @return
     */
    public List<String> resolveStarAssignee(DelegateExecution execution) {

        List<String> assignList = new ArrayList<>();

        String startUserId = FlowableUtils.getStartUserId(execution);


        assignList.add(startUserId);

        return assignList;

    }


    /**
     * 会签或者或签完成条件检查
     *
     * @param execution
     */
    public boolean completionCondition(DelegateExecution execution) {


        ExecutionEntityImpl entity = (ExecutionEntityImpl) execution;


        log.info("开始检查会签或者或签完成条件检查 {}是否是多实例根节点:{}", entity.getId(), entity.isMultiInstanceRoot());


        String flowId = entity.getProcessDefinitionKey();

        UserTask flowNode = (UserTask) FlowableUtils.getFlowNode(execution.getProcessInstanceId(), entity.getActivityId());
        String nodeId = FlowableUtils.getNodeIdFromExtension(flowNode);


        Node node = NodeDataStoreFactory.getInstance().getNode(flowId, nodeId, execution.getTenantId());
        SuperUserRootNode superUserRootNode = (SuperUserRootNode) node;
        Integer multipleMode = superUserRootNode.getMultipleMode();



        //实例总数
        int nrOfInstances = (int) execution.getVariable("nrOfInstances");
        //完成的实例数
        int nrOfCompletedInstances = (int) execution.getVariable("nrOfCompletedInstances");

        int okNum = 0;
        int failNum = 0;
        List<? extends DelegateExecution> executionList = execution.getExecutions();


        for (DelegateExecution delegateExecution : executionList) {
            Boolean variableLocal = delegateExecution.getVariableLocal(APPROVE_RESULT, Boolean.class);
            if (variableLocal != null && variableLocal) {
                okNum++;
            }
            if (variableLocal != null && !variableLocal) {
                failNum++;
            }
        }



        if (
                multipleMode.intValue() == ProcessInstanceConstant.MULTIPLE_MODE_ONE
        ) {
            //或签
            if (okNum > 0) {
                entity.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                        ApproveResultEnum.PASS.getValue());

                return true;
            }
            if (nrOfCompletedInstances == nrOfInstances) {
                entity.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                        ApproveResultEnum.REFUSE.getValue());

                return true;
            }

            return false;
        }
        if (
                multipleMode.intValue() == ProcessInstanceConstant.MULTIPLE_MODE_ALL_SORT|| multipleMode.intValue() == ProcessInstanceConstant.MULTIPLE_MODE_ALL_SAME
        ) {
            //顺签
            if (failNum > 0) {
                entity.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                        ApproveResultEnum.REFUSE.getValue());

                return true;
            }
            if (nrOfCompletedInstances == nrOfInstances) {
                entity.setVariable(StrUtil.format("{}_{}", node.getId(), APPROVE_NODE_RESULT),
                        ApproveResultEnum.PASS.getValue());

                return true;
            }

            return false;
        }




        return false;
    }

}
