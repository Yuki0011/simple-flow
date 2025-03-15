package com.sentury.approvalflow.biz.strategy.node.impl;

import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.dto.flow.Node;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.strategy.node.NodeStrategy;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RootNodeStrategy implements NodeStrategy, InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(NodeTypeEnum.ROOT.getValue());
    }

    @Override
    public void handleNodeShow(Map<String, Object> paramMap, String nodeId, List<NodeFormatUserVo> nodeFormatUserVoList,
                               String processInstanceId, Node node, NodeShowVo nodeVo, List<String> selectUserNodeIdList) {
        //发起节点
        if (StrUtil.isBlank(processInstanceId)) {
            //NodeFormatUserVo nodeFormatUserVo = buildUser(LoginHelper.getUserId()+"");
            NodeFormatUserVo nodeFormatUserVo = buildUser(LoginHelper.getUserId()+"");

            nodeFormatUserVoList.addAll(CollUtil.newArrayList(nodeFormatUserVo));

        } else {


            buildApproveDesc(node, processInstanceId, nodeVo, nodeFormatUserVoList);

        }
    }
}
