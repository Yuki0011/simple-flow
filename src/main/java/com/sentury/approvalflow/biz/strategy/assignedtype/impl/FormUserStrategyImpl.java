package com.sentury.approvalflow.biz.strategy.assignedtype.impl;

import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.biz.strategy.assignedtype.ApprovalNodeAssignedTypeStrategy;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhj
 */
@Component
@Slf4j
public class FormUserStrategyImpl implements ApprovalNodeAssignedTypeStrategy, InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.FORM_USER);
    }

    /**
     * 处理评论显示
     *
     * @param node
     * @param processInstanceId
     * @param nodeVo
     * @param nodeFormatUserVoList
     * @param paramMap
     */
    @Override
    public void handle(Node node, String processInstanceId, NodeShowVo nodeVo,
                       List<NodeFormatUserVo> nodeFormatUserVoList, Map<String, Object> paramMap ) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        //表单人员
        String formUser = superUserNode.getFormUserId();

        Object o = paramMap.get(formUser);
        if (o != null) {
            String jsonString = JsonUtil.toJSONString(o);
            if (StrUtil.isNotBlank(jsonString)) {
                List<NodeUser> nodeUserDtoList = JsonUtil.parseArray(jsonString, NodeUser.class);
                List<String> userIdList =
                        nodeUserDtoList.stream().map(w -> (w.getId())).collect(Collectors.toList());
                for (String aLong : userIdList) {
                    nodeFormatUserVoList.add(buildUser(aLong));
                }
            }
        }


    }
}
