package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.ApproveNode;
import com.sentury.approvalflow.common.dto.flow.node.CopyNode;
import com.sentury.approvalflow.common.dto.flow.node.GatewayNode;
import com.sentury.approvalflow.common.dto.flow.node.RootNode;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.service.IProcessService;

import java.util.List;
import java.util.Map;

/**
 * 节点格式化显示工具
 */
public class NodeUtil {

    /**
     * 处理节点添加执行id和流程唯一id
     *
     * @param node
     * @param nodeId
     * @param executionId
     */
    public static void handleNodeAddExecutionId(Node node, String nodeId, String executionId
    ) {

        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node)) {
            return;
        }

        if ((!Convert.toBool(node.isRemarkedAtNodeShow(), false)) && (((StrUtil.contains(nodeId, node.getId()) && StrUtil.startWith(nodeId,
                ProcessInstanceConstant.VariableKey.START_NODE)) ||
                (StrUtil.equals(nodeId, node.getId()))) && StrUtil.isBlank(node.getExecutionId()))) {
            node.setExecutionId(executionId);
            return;
        }


        Integer type = node.getType();

        if (NodeTypeEnum.getByValue(type).getBranch()) {

            GatewayNode gatewayNode = (GatewayNode) node;

            //条件分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            if (CollUtil.isNotEmpty(branchs)) {
                for (Node branch : branchs) {
                    Node children = branch.getChildNode();
                    handleNodeAddExecutionId(children, nodeId, executionId);
                }
            }
        }

        handleNodeAddExecutionId(node.getChildNode(), nodeId, executionId);
    }

    /**
     * 根据nodeid 获取最终的nodeid列表
     *
     * @param nodeId 节点id
     * @return
     */
    public static List<String> getFinalNodeIdList(String nodeId) {
        if (StrUtil.startWith(nodeId, ProcessInstanceConstant.VariableKey.START_NODE)) {
            return CollUtil.newArrayList(ProcessInstanceConstant.VariableKey.START_NODE, StrUtil.format("{}_user_task",
                    ProcessInstanceConstant.VariableKey.START_NODE));

        }
        return CollUtil.newArrayList(nodeId);
    }


    /**
     * 处理发起人节点表单
     *
     * @param node 根节点
     */
    public static void handleStarterForm(RootNode node, List<FormItemVO> formItemVOList) {


        Map<String, String> formPerms = node.getFormPerms();
        handlerPerm(formItemVOList, formPerms, ProcessInstanceConstant.FormPermClass.EDIT);
    }

    private static void handlerPerm(List<FormItemVO> formItemVOList, Map<String,
            String> formPerms, String defaultPerm) {
        for (FormItemVO formItemVO : formItemVOList) {

            formPerms.putIfAbsent(formItemVO.getId(), defaultPerm);


        }
    }

    /**
     * 处理用户任务节点的表单--如果没设置上 默认是只读
     *
     * @param node 跟节点
     */
    public static void handleApproveForm(Node node, List<FormItemVO> formItemVOList) {
        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node)) {
            return;
        }

        Integer type = node.getType();
        if (type.intValue() == NodeTypeEnum.APPROVAL.getValue()) {
            //审批节点
            //当前节点的权限

            ApproveNode approveNode = (ApproveNode) node;

            Map<String, String> formPerms = approveNode.getFormPerms();

            handlerPerm(formItemVOList, formPerms, ProcessInstanceConstant.FormPermClass.READ);

        }


        if (NodeTypeEnum.getByValue(type).getBranch()) {

            GatewayNode gatewayNode= (GatewayNode) node;
            //分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {
                branch.setParentId(node.getId());
                Node children = branch.getChildNode();
                handleApproveForm(children, formItemVOList);
            }

        }

        handleApproveForm(node.getChildNode(), formItemVOList);

    }

    /**
     * 处理抄送节点的表单--如果没设置上 默认是只读
     *
     * @param node 跟节点
     */
    public static void handleCopyForm(Node node, List<FormItemVO> formItemVOList) {
        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node)) {
            return;
        }

        Integer type = node.getType();
        if (type.intValue() == NodeTypeEnum.CC.getValue()) {
            //审批节点
            //当前节点的权限
            CopyNode copyNode = (CopyNode) node;
            Map<String, String> formPerms = copyNode.getFormPerms();
            handlerPerm(formItemVOList, formPerms, ProcessInstanceConstant.FormPermClass.READ);

        }


        if (NodeTypeEnum.getByValue(type).getBranch()) {
            GatewayNode gatewayNode= (GatewayNode) node;

            //分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {
                branch.setParentId(node.getId());
                Node children = branch.getChildNode();
                handleCopyForm(children, formItemVOList);
            }

        }

        handleCopyForm(node.getChildNode(), formItemVOList);

    }



    /**
     * 如果下级不是节点 设置为null
     *
     * @param node
     */
    public static void handEmptyNode(Node node) {

        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node)) {
            return;
        }

        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node.getChildNode())) {
            node.setChildNode(null);
            return;
        }

        Integer type = node.getType();


        if (NodeTypeEnum.getByValue(type).getBranch()) {
            GatewayNode gatewayNode= (GatewayNode) node;

            //分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {
                branch.setParentId(node.getId());
                Node children = branch.getChildNode();
                handEmptyNode(children);
            }

        }

        handEmptyNode(node.getChildNode());
    }

    /**
     * 处理用户任务节点
     *
     * @param node 跟节点
     */
    public static void handleApprove(Node node) {
        if (!com.sentury.approvalflow.common.utils.NodeUtil.isNode(node)) {
            return;
        }

        Integer type = node.getType();


        if (NodeTypeEnum.getByValue(type).getBranch()) {
            GatewayNode gatewayNode= (GatewayNode) node;

            //分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {
                branch.setParentId(node.getId());
                Node children = branch.getChildNode();
                handleApprove(children);
            }

        }

        handleApprove(node.getChildNode());

    }


    public static String getLastNodeId(String flowId, String nodeId, String parentNodeId) {

        if (StrUtil.isNotBlank(parentNodeId)) {
            return parentNodeId;
        }

        IProcessService processService = SpringUtil.getBean(IProcessService.class);
        Process process = processService.getByFlowId(flowId);
        Node node = JsonUtil.parseObject(process.getProcess(), Node.class);

        Node parentNode = com.sentury.approvalflow.common.utils.NodeUtil.getParentNode(node, nodeId, false);
        if (parentNode == null) {
            return null;
        }
        return parentNode.getId();

    }

}
