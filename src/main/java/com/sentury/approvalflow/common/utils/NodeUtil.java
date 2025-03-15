package com.sentury.approvalflow.common.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.EndNode;
import com.sentury.approvalflow.common.dto.flow.node.GatewayNode;

import java.util.ArrayList;
import java.util.List;

public class NodeUtil {



    /**
     * 一直到最上层的节点
     *
     * @param node
     * @param nodeId
     * @return
     */
    public static List<Node> getParentNodeUntilRoot(Node node, String nodeId) {

        List<Node> nodeList = new ArrayList<>();


        while (true) {
            Node parentNode = getParentNode(node, nodeId, true);

            if (isNode(parentNode)) {
                nodeList.add(parentNode);
                nodeId = parentNode.getId();
            } else {
                break;
            }
        }


        return nodeList;
    }

    /**
     * 获取父级节点
     *
     * @param node
     * @param nodeId
     * @param containEmptyNode
     * @return
     */
    public static Node getParentNode(Node node, String nodeId, boolean containEmptyNode) {

        if (!isNode(node)) {
            return null;
        }


        Node childNode = node.getChildNode();

        if (isNode(childNode) && StrUtil.equals(childNode.getId(), nodeId)) {
            return node;
        }

        Integer type = node.getType();

        if (NodeTypeEnum.getByValue(type).getBranch()) {

            GatewayNode gatewayNode = (GatewayNode) node;

            //条件分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {

                if (containEmptyNode) {
                    if (isNode(branch) && StrUtil.equals(branch.getId(), nodeId)) {
                        return node;
                    }
                    Node parentNode = getParentNode(branch, nodeId, containEmptyNode);
                    if (parentNode != null) {
                        return parentNode;
                    }
                    continue;
                }

                Node children = branch.getChildNode();

                if (isNode(children) && StrUtil.equals(children.getId(), nodeId)) {
                    return node;
                }
                Node parentNode = getParentNode(children, nodeId, containEmptyNode);
                if (parentNode != null) {
                    return parentNode;
                }

            }
        }

        return getParentNode(childNode, nodeId, containEmptyNode);
    }

    public static Node getNode(Node node, String nodeId, boolean containEmptyNode) {

        if (!isNode(node)) {
            return null;
        }


        if (StrUtil.equals(node.getId(), nodeId)) {
            return node;
        }
        Node childNode = node.getChildNode();

        Integer type = node.getType();

        if (NodeTypeEnum.getByValue(type).getBranch()) {

            GatewayNode gatewayNode = (GatewayNode) node;

            //条件分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {

                if (containEmptyNode) {

                    Node parentNode = getNode(branch, nodeId, containEmptyNode);
                    if (parentNode != null) {
                        return parentNode;
                    }
                    continue;
                }

                Node children = branch.getChildNode();

                Node parentNode = getNode(children, nodeId, containEmptyNode);
                if (parentNode != null) {
                    return parentNode;
                }

            }
        }

        return getNode(childNode, nodeId, containEmptyNode);
    }


    /**
     * 添加结束节点
     *
     * @param node
     */
    public static void addEndNode(Node node) {

        Node children = node.getChildNode();
        if (isNode(children)) {
            addEndNode(children);
        } else {
            Node end = new EndNode();
            end.setId(ProcessInstanceConstant.VariableKey.END);
            end.setType(NodeTypeEnum.END.getValue());
            end.setNodeName("结束节点");
            end.setParentId(node.getId());
            node.setChildNode(end);
        }

    }


    public static boolean isNode(Node childNode) {
        if (childNode != null && StrUtil.isNotBlank(childNode.getId())) {
            return true;
        }
        return false;
    }


    /**
     * 处理父级id
     *
     * @param node
     */
    public static void handleParentId(Node node, String parentId) {
        if (!isNode(node)) {
            return;
        }
        node.setParentId(parentId);
        node.setTempId(StrUtil.format("{}|{}", node.getId(), IdUtil.fastSimpleUUID()));
        Integer type = node.getType();


        if (NodeTypeEnum.getByValue(type).getBranch()) {
            GatewayNode gatewayNode = (GatewayNode) node;

            //分支
            List<Node> branchs = gatewayNode.getConditionNodes();
            for (Node branch : branchs) {
                branch.setParentId(node.getId());
                Node children = branch.getChildNode();
                handleParentId(children, branch.getId());
            }

        }

        handleParentId(node.getChildNode(), node.getId());

    }



}
