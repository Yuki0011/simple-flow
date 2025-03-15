package com.sentury.approvalflow.core.node.impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.sentury.approvalflow.core.node.AssignUserStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 指定主管
 * @author Huijun Zhao
 * @description
 * @date 2023-07-07 13:42
 */
@Component
public class AssignUserLeaderStrategyImpl implements InitializingBean, AssignUserStrategy {
    @Override
    public List<String> handle(Node node, String rootUserId, Map<String, Object> variables, String tenantId) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        List<String> userIdList=new ArrayList<>();

        //指定主管审批
        //第几级主管审批
        Integer level = superUserNode.getDeptLeaderLevel();

        //去获取主管

        R2<List<DeptDto>> r =
                BizHttpUtil.queryParentDeptList(MapUtil.getStr(variables,ProcessInstanceConstant.VariableKey.START_USER_MAIN_DEPTID_KEY),
                        tenantId);

        List<DeptDto> deptDtoList = r.getData();
        if (CollUtil.isNotEmpty(deptDtoList)) {
            if (deptDtoList.size() >= level) {
                DeptDto deptDto = deptDtoList.get(level - 1);

                List<String> leaderUserIdList = deptDto.getLeaderUserIdList();

                if(CollUtil.isNotEmpty(leaderUserIdList)){
                    userIdList.addAll(leaderUserIdList);
                }


            }
        }

        return userIdList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.LEADER);

    }
}
