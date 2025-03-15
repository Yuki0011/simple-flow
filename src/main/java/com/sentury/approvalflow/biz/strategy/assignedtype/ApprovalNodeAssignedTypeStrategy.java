package com.sentury.approvalflow.biz.strategy.assignedtype;

import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.biz.strategy.node.NodeStrategyFactory;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;

import java.util.List;
import java.util.Map;

/**
 * 审批人节点人员类型策略
 */
public interface ApprovalNodeAssignedTypeStrategy {

    /**
     * 策略注册方法
     *
     * @param key
     */
    default void afterPropertiesSet(Integer key) {
        ApprovalNodeAssignedTypeStrategyFactory.register(key, this);
    }

    /**
     * 处理评论显示
     * @param node
     * @param processInstanceId
     * @param nodeVo
     * @param nodeFormatUserVoList
     */
    void handle(Node node, String processInstanceId, NodeShowVo nodeVo, List<NodeFormatUserVo> nodeFormatUserVoList, Map<String, Object> paramMap);

    default NodeFormatUserVo buildUser(String userId){
       return NodeStrategyFactory.getStrategy(NodeTypeEnum.APPROVAL.getValue()).buildUser(userId);
    }
    default List<NodeFormatUserVo>  buildUser(List<NodeUser> nodeUserList){
       return NodeStrategyFactory.getStrategy(NodeTypeEnum.APPROVAL.getValue()).buildUser(nodeUserList);
    }
}
