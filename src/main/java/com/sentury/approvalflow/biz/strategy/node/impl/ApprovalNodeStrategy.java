package com.sentury.approvalflow.biz.strategy.node.impl;

import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.biz.strategy.node.NodeStrategy;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ApprovalNodeStrategy implements NodeStrategy, InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(NodeTypeEnum.APPROVAL.getValue());
    }

    @Override
    public void handleNodeShow(Map<String, Object> paramMap, String nodeId, List<NodeFormatUserVo> nodeFormatUserVoList,
                               String processInstanceId, Node node, NodeShowVo nodeVo, List<String> selectUserNodeIdList) {
                handleUserNode(node,nodeVo,selectUserNodeIdList,paramMap,processInstanceId,
                        NodeTypeEnum.APPROVAL.getValue(),nodeFormatUserVoList);
    }
}
