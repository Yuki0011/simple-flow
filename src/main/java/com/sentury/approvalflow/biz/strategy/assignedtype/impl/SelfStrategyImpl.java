package com.sentury.approvalflow.biz.strategy.assignedtype.impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.Node;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import com.sentury.approvalflow.biz.strategy.assignedtype.ApprovalNodeAssignedTypeStrategy;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhj
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SelfStrategyImpl implements ApprovalNodeAssignedTypeStrategy, InitializingBean {

    private final IProcessInstanceRecordService processInstanceRecordService;


    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(ProcessInstanceConstant.AssignedTypeClass.SELF);
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
        //发起人自己
        if(StrUtil.isNotBlank(processInstanceId)){
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);
            nodeFormatUserVoList.addAll(CollUtil.newArrayList(buildUser(processInstanceRecord.getUserId())));
        }else{
         //   nodeFormatUserVoList.addAll(CollUtil.newArrayList(buildUser(LoginHelper.getUserId()+"")));
            nodeFormatUserVoList.addAll(CollUtil.newArrayList(buildUser(LoginHelper.getUserId()+"")));
        }
    }
}
