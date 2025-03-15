package com.sentury.approvalflow.core.node.impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.core.node.AssignUserStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 来自表单部门
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-07-31 11:30
 */
@Component
public class AssignUserFormDeptStrategyImpl implements InitializingBean, AssignUserStrategy {
    @Override
    public List<String> handle(Node node, String rootUserId, Map<String, Object> variables, String tenantId) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        Set<String> assignList = new HashSet<>();
        //表单值

        Object variable = variables.get(superUserNode.getFormUserId());

        String deptUserType = superUserNode.getDeptUserType();

        if (variable == null) {

        } else if (StrUtil.isBlankIfStr(variable)) {

        } else {
            //是否包含子级部门
            Boolean containChildrenDept = superUserNode.getContainChildrenDept();
            if (containChildrenDept == null) {
                containChildrenDept = false;
            }

            String jsonString = JsonUtil.toJSONString(variable);
            List<NodeUser> nodeUserDtoList = JsonUtil.parseArray(jsonString, NodeUser.class);

            List<String> deptIdList = nodeUserDtoList.stream().map(w -> String.valueOf(w.getId())).collect(Collectors.toList());
//部门id
            Set<String> deptIdSet = new LinkedHashSet<>(deptIdList);


            if (containChildrenDept) {
                //包含子级
                Map<String, List<DeptDto>> childrenDeptMap = BizHttpUtil.batchQueryChildDeptList(deptIdList, tenantId).getData();


                for (Map.Entry<String, List<DeptDto>> entry : childrenDeptMap.entrySet()) {
                    Set<String> collect = entry.getValue().stream().map(DeptDto::getId).collect(Collectors.toSet());
                    deptIdSet.addAll(collect);
                }
            }

            deptIdList.clear();
            deptIdList.addAll(deptIdSet);

            if (CollUtil.isNotEmpty(deptIdList)) {

                if (ProcessInstanceConstant.AssignedTypeFormDeptUserTypeClass.ALL_USER.equals(deptUserType)) {
                    //人员
                    R2<List<String>> r = BizHttpUtil.queryUserIdListByDepIdList(deptIdList, tenantId);

                    List<String> data = r.getData();
                    if (CollUtil.isNotEmpty(data)) {
                        assignList.addAll(data);
                    }
                } else if (ProcessInstanceConstant.AssignedTypeFormDeptUserTypeClass.LEADER.equals(deptUserType)) {
                    //主管
                    List<DeptDto> deptDtoList = BizHttpUtil.queryDeptList(deptIdList, tenantId);
                    for (DeptDto deptDto : deptDtoList) {
                        assignList.addAll(deptDto.getLeaderUserIdList());
                    }
                } else if (ProcessInstanceConstant.AssignedTypeFormDeptUserTypeClass.ROLE.equals(deptUserType)) {
                    //部门下的角色

                    List<NodeUser> roleList = superUserNode.getRoleList();
                    List<String> roleIdList =
                            roleList.stream().map(w -> (w.getId())).collect(Collectors.toList());

                    R2<List<String>> r = BizHttpUtil.queryUserIdListByRoleIdListAndDeptIdList(roleIdList, deptIdList, tenantId);

                    List<String> userIdList = r.getData();

                    assignList.addAll(userIdList);

                }


            }

        }
        return new ArrayList<>(assignList);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.FORM_DEPT);

    }
}
