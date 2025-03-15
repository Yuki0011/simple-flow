package com.sentury.approvalflow.core.node.impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.core.node.AssignUserStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 来自角色
 * @author Huijun Zhao
 * @description
 * @date 2023-07-07 13:42
 */
@Component
public class AssignUserRoleStrategyImpl implements InitializingBean, AssignUserStrategy {
    @Override
    public List<String> handle(Node node, String rootUserId, Map<String, Object> variables, String tenantId) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        List<String> assignList=new ArrayList<>();

        //角色

        List<NodeUser> nodeUserList = superUserNode.getNodeUserList();

        List<String> roleIdList = nodeUserList.stream().map(w -> w.getId()).collect(Collectors.toList());


        R2<List<String>> r = BizHttpUtil.queryUserIdListByRoleIdList(roleIdList,tenantId );

        List<String> data = r.getData();


        assignList.addAll(data);
        return assignList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.ROLE);

    }
}
