package com.sentury.approvalflow.core.utils;


import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.NodeUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.REDIS_KEY_OF_FLOW_UNIQUE_ID;
import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.STARTER_USER;

@Slf4j
public class FlowableUtils {

    public static Map<String, List<ExtensionElement>> generateFlowNodeIdExtensionMap(String nodeId) {
        Map<String, List<ExtensionElement>> extensionElements = new HashMap<>();
        ExtensionElement extensionElement = generateFlowNodeIdExtension(nodeId);
        extensionElements.put(ProcessInstanceConstant.VariableKey.SYS_CODE, CollUtil.newArrayList(extensionElement));
        return extensionElements;
    }


    public static ExtensionElement generateFlowNodeIdExtension(String nodeId) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setElementText(nodeId);
        extensionElement.setName("nodeId");
        extensionElement.setNamespacePrefix("flowable");
        extensionElement.setNamespace("nodeId");
        return extensionElement;
    }


    public static String getNodeIdFromExtension(FlowElement flowElement) {

        Map<String, List<ExtensionElement>> extensionElements = flowElement.getExtensionElements();

        return extensionElements.get("nodeId").get(0).getElementText();
    }


    /**
     * 获取节点数据
     *
     * @param processInstanceId
     * @param nodeId
     * @return
     */
    public static FlowNode getFlowNode(String processInstanceId, String nodeId) {

        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);

        String definitionld = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();        //获取bpm（模型）对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionld);
        //传节点定义key获取当前节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(nodeId);
        return flowNode;
    }

    /**
     * 获取发起人用户id
     *
     * @param execution 执行实例
     * @return
     */
    public static String getStartUserId(DelegateExecution execution) {
        Object rootUserObj = execution.getVariable(STARTER_USER);
        NodeUser rootUser = JsonUtil.parseArray(JsonUtil.toJSONString(rootUserObj), NodeUser.class).get(0);
        return rootUser.getId();
    }

    /**
     * 通过流程定义id 获取流程id
     *
     * @param processDefinitionId 流程定义id
     * @param tenantId            租户id
     * @return
     */
    public static String getFlowId(String processDefinitionId, String tenantId) {
        RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);


        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .processDefinitionTenantId(tenantId)
                .singleResult();


        return processDefinition.getKey();
    }


    /**
     * 获取流程实例唯一id
     *
     * @param nodeId
     * @param flowId
     * @param processInstanceId
     * @param tenantId
     * @return
     */
    public static String getFlowUniqueId(String nodeId, String flowId, String processInstanceId, String tenantId) {

        String redisKey = StrUtil.format(REDIS_KEY_OF_FLOW_UNIQUE_ID, processInstanceId);

        RedisTemplate redisTemplate = SpringUtil.getBean("redisTemplate", RedisTemplate.class);

        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().rangeByScoreWithScores(redisKey,
                Long.MIN_VALUE,
                Long.MAX_VALUE);


        List<SortFlowUniqueId> f = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> t : set) {
            Double score = t.getScore();
            String value = t.getValue();
            if (StrUtil.startWith(value, nodeId) || StrUtil.startWith(value, ProcessInstanceConstant.VariableKey.START_NODE)) {

                SortFlowUniqueId sortFlowUniqueId = new SortFlowUniqueId(nodeId, StrUtil.subAfter(value, "||||", true),
                        score);
                f.add(sortFlowUniqueId);

            }

        }


        Node rootNode = NodeDataStoreFactory.getInstance().getNode(flowId,
                ProcessInstanceConstant.VariableKey.START_NODE, tenantId);
        List<Node> parentNodeUntilRoot = NodeUtil.getParentNodeUntilRoot(rootNode, nodeId);
        for (Node n : parentNodeUntilRoot) {
            for (ZSetOperations.TypedTuple<String> t : set) {
                Double score = t.getScore();
                String value = t.getValue();
                if (StrUtil.startWith(value, n.getId())) {
                    SortFlowUniqueId sortFlowUniqueId = new SortFlowUniqueId(n.getId(), StrUtil.subAfter(value, "||||", true),

                            score);
                    f.add(sortFlowUniqueId);
                    break;
                }
            }

        }
        CollUtil.sort(f, new Comparator<SortFlowUniqueId>() {
            @Override
            public int compare(SortFlowUniqueId o1, SortFlowUniqueId o2) {
                return o2.getScore().compareTo(o1.getScore());
            }
        });
        return f.get(0).getFlowUniqueId();
    }

    @AllArgsConstructor
    @Data
    private static class SortFlowUniqueId {
        private String nodeId;

        private String flowUniqueId;

        private Double score;
    }

}
