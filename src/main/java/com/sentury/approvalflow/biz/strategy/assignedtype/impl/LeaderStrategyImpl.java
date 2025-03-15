package com.sentury.approvalflow.biz.strategy.assignedtype.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.biz.strategy.assignedtype.ApprovalNodeAssignedTypeStrategy;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhj
 */
@Component
@Slf4j
public class LeaderStrategyImpl implements ApprovalNodeAssignedTypeStrategy, InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.LEADER);
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

        //制定主管

        //指定主管审批
        //第几级主管审批
        Integer level = superUserNode.getDeptLeaderLevel();

        //去获取主管

        IRemoteService remoteService = SpringUtil.getBean(IRemoteService.class);

        R2<List<DeptDto>> r = remoteService.queryParentDeptList(MapUtil.getStr(paramMap,
                ProcessInstanceConstant.VariableKey.START_USER_MAIN_DEPTID_KEY));

        List<DeptDto> deptDtoList = r.getData();
        if (CollUtil.isNotEmpty(deptDtoList)) {
            if (deptDtoList.size() >= level) {
                DeptDto deptDto = deptDtoList.get(level - 1);

                List<String> leaderUserIdList = deptDto.getLeaderUserIdList();
                if(CollectionUtils.isNotEmpty(leaderUserIdList)) {
                    for (String s : leaderUserIdList) {
                        NodeFormatUserVo nodeFormatUserVo = buildUser(s);
                        nodeFormatUserVoList.add(nodeFormatUserVo);
                    }
                }


            }
        }
    }
}
