package com.sentury.approvalflow.core.node.impl;

import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.core.node.AssignUserStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 连续多级主管
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-07-07 13:42
 */
@Component
public class AssignUserLeaderTopStrategyImpl implements InitializingBean, AssignUserStrategy {
    @Override
    public List<String> handle(Node node, String rootUserId, Map<String, Object> variables, String tenantId) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        List<String> userIdList = new ArrayList<>();


        //去获取主管

//        R<List<DeptDto>> r = BizHttpUtil.queryParentDepListByUserId(rootUserId, tenantId);


        R2<List<DeptDto>> r =
                BizHttpUtil.queryParentDeptList(MapUtil.getStr(variables,ProcessInstanceConstant.VariableKey.START_USER_MAIN_DEPTID_KEY),
                        tenantId);

        List<DeptDto> deptDtoList = r.getData();

        //上级主管依次审批

        //第几级主管审批截止
        Integer level = superUserNode.getDeptLeaderLevel();


        if (CollUtil.isNotEmpty(deptDtoList)) {
            int index = 1;
            for (DeptDto deptDto : deptDtoList) {
                if (level != null && level < index) {
                    break;
                }
                List<String> leaderUserIdList = deptDto.getLeaderUserIdList();
                if (CollUtil.isNotEmpty(leaderUserIdList)) {
                    for (String s : leaderUserIdList) {
                        if (StrUtil.isNotBlank(s) && !userIdList.contains(s)) {
                            userIdList.add((s));
                        }
                    }
                }

                index++;
            }
        }


        return userIdList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.LEADER_TOP);

    }
}
