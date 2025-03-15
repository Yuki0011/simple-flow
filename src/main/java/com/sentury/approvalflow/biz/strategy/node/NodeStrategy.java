package com.sentury.approvalflow.biz.strategy.node;

import com.sentury.approvalflow.common.constants.ApproveDescTypeEnum;
import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import com.sentury.approvalflow.common.constants.NodeUserTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.SimpleApproveDescDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import com.sentury.approvalflow.common.utils.DateUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.constants.NodeFormatUserVoStatusEnum;
import com.sentury.approvalflow.domain.entity.ProcessInstanceAssignUserRecord;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.service.IProcessInstanceAssignUserRecordService;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import com.sentury.approvalflow.biz.strategy.assignedtype.ApprovalNodeAssignedTypeStrategy;
import com.sentury.approvalflow.biz.strategy.assignedtype.ApprovalNodeAssignedTypeStrategyFactory;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.biz.utils.NodeUtil;
import com.sentury.approvalflow.domain.vo.ProcessFormatNodeApproveDescVo;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 节点策略
 */
public interface NodeStrategy {

    /**
     * 策略注册方法
     *
     * @param key
     */
    default void afterPropertiesSet(Integer key) {
        NodeStrategyFactory.register(key, this);
    }

    void handleNodeShow(Map<String, Object> paramMap, String nodeId, List<NodeFormatUserVo> nodeFormatUserVoList,
                        String processInstanceId, Node node, NodeShowVo nodeVo, List<String> selectUserNodeIdList);

    /**
     * 处理用户相关节点
     *
     * @param node
     * @param nodeShowVo
     * @param selectUserNodeIdList
     * @param paramMap
     * @param processInstanceId
     * @param type
     * @param nodeFormatUserVoList
     */
    default void handleUserNode(Node node, NodeShowVo nodeShowVo, List<String> selectUserNodeIdList, Map<String, Object> paramMap,
                                String processInstanceId, int type, List<NodeFormatUserVo> nodeFormatUserVoList) {

        SuperUserNode superUserNode= (SuperUserNode) node;

        Integer assignedType = superUserNode.getAssignedType();
        nodeShowVo.setSelectUser(false);
        {

            //是否需要选择用户
            boolean selectUser = selectUserNodeIdList.contains(node.getId());
            nodeShowVo.setSelectUser(selectUser);
            if (selectUser) {
                nodeShowVo.setMultiple(superUserNode.getMultiple());
            }
        }


        // 用户列表
        if (StrUtil.isAllNotBlank(processInstanceId, node.getExecutionId()) && type != NodeTypeEnum.CC.getValue().intValue()) {

            if (type == NodeTypeEnum.APPROVAL.getValue().intValue()) {
                buildApproveDesc(node, processInstanceId, nodeShowVo, nodeFormatUserVoList);

            }


        } else {
            ApprovalNodeAssignedTypeStrategy strategy = ApprovalNodeAssignedTypeStrategyFactory.getStrategy(assignedType);
            if (strategy != null) {
                strategy.handle(node, processInstanceId, nodeShowVo, nodeFormatUserVoList, paramMap);
            }
        }

    }

    /**
     * 构建用户
     *
     * @param nodeUserList 选项值
     * @return
     */
    default List<NodeFormatUserVo> buildUser(List<NodeUser> nodeUserList) {
        List<NodeFormatUserVo> nodeFormatUserVoList = new ArrayList<>();
        //用户id
        List<String> userIdList = nodeUserList.stream().filter(w -> StrUtil.equals(w.getType(),
                NodeUserTypeEnum.USER.getKey())).map(NodeUser::getId).collect(Collectors.toList());
        //部门id
        List<String> deptIdList = nodeUserList.stream().filter(w -> StrUtil.equals(w.getType(),
                NodeUserTypeEnum.DEPT.getKey())).map(NodeUser::getId).collect(Collectors.toList());

        IRemoteService iRemoteService = SpringUtil.getBean(IRemoteService.class);

        if (CollUtil.isNotEmpty(deptIdList)) {

            Set<String> deptIdSet = new LinkedHashSet<>(deptIdList);

            List<String> queryChildrenDeptIdList=new ArrayList<>();

            for (NodeUser nodeUser : nodeUserList) {
                //找出来包含子级的
                if (StrUtil.equals(nodeUser.getType(), NodeUserTypeEnum.DEPT.getKey()) && nodeUser.getContainChildrenDept()) {
                    queryChildrenDeptIdList.add(nodeUser.getId());
                }
            }

            Map<String, List<DeptDto>> childrenDeptMap =
                    iRemoteService.batchQueryChildDeptList(queryChildrenDeptIdList).getData();
            for (Map.Entry<String, List<DeptDto>> entry : childrenDeptMap.entrySet()) {
                Set<String> collect = entry.getValue().stream().map(DeptDto::getId).collect(Collectors.toSet());
                deptIdSet.addAll(collect);
            }


            deptIdList.clear();
            deptIdList.addAll(deptIdSet);


            //查询用户id
            List<String> data =
                    iRemoteService.queryUserIdListByDepIdList(deptIdList.stream().map(String::valueOf).collect(Collectors.toList())).getData();

            if (CollUtil.isNotEmpty(data)) {
                for (String datum : data) {
                    if (!userIdList.contains((datum))) {
                        userIdList.add((datum));
                    }
                }
            }
        }
        {
            for (String aLong : userIdList) {
                nodeFormatUserVoList.add(buildUser(aLong));
            }
        }
        return nodeFormatUserVoList;
    }



    /**
     * 构建审批评论内容
     *
     * @param node
     * @param processInstanceId
     * @param nodeVo
     * @param nodeFormatUserVoList
     * @return
     */
    default void buildApproveDesc(Node node, String processInstanceId, NodeShowVo nodeVo,
                                  List<NodeFormatUserVo> nodeFormatUserVoList) {


        IProcessInstanceAssignUserRecordService processNodeRecordAssignUserService = SpringUtil.getBean(IProcessInstanceAssignUserRecordService.class);
        List<ProcessInstanceAssignUserRecord> processInstanceAssignUserRecordList = processNodeRecordAssignUserService
                .lambdaQuery()
                .in(ProcessInstanceAssignUserRecord::getNodeId, NodeUtil.getFinalNodeIdList(node.getId()))
                .in(ProcessInstanceAssignUserRecord::getParentExecutionId, node.getExecutionId())
                .eq(ProcessInstanceAssignUserRecord::getProcessInstanceId, processInstanceId)
                .orderByAsc(ProcessInstanceAssignUserRecord::getCreateTime)
                .list();

        //处理用户评论
        if (CollUtil.isNotEmpty(processInstanceAssignUserRecordList)) {
            Set<String> taskIdList = processInstanceAssignUserRecordList.stream().map(w -> w.getTaskId()).collect(Collectors.toSet());

            List<ProcessFormatNodeApproveDescVo> descList = new ArrayList();

            for (String taskId : taskIdList) {
                List<SimpleApproveDescDto> simpleApproveDescDtoList = CoreHttpUtil.queryTaskComments(taskId).getData();
                if (simpleApproveDescDtoList == null) {
                    simpleApproveDescDtoList = new ArrayList<>();
                }

                for (SimpleApproveDescDto simpleApproveDescDto : simpleApproveDescDtoList) {
                    NodeFormatUserVo nodeFormatUserVo = buildUser(simpleApproveDescDto.getUserId());
                    ProcessFormatNodeApproveDescVo descVo = ProcessFormatNodeApproveDescVo.builder()
                            .user(nodeFormatUserVo)
                            .desc(simpleApproveDescDto.getMessage())
                            .descType(simpleApproveDescDto.getType())
                            .sys(simpleApproveDescDto.getSys())
                            .descTypeStr(ApproveDescTypeEnum.get(simpleApproveDescDto.getType()).getName())
                            .showTimeStr(DateUtil.dateShow(simpleApproveDescDto.getDate()))
                            .date(simpleApproveDescDto.getDate())
                            .approveImageList(simpleApproveDescDto.getApproveImageList())
                            .approveFileList(simpleApproveDescDto.getApproveFileList())
                            .signUrlList(simpleApproveDescDto.getSignUrlList())
                            .build();

                    descList.add(descVo);
                }

            }


            CollUtil.sort(descList, new Comparator<ProcessFormatNodeApproveDescVo>() {
                @Override
                public int compare(ProcessFormatNodeApproveDescVo t0,
                                   ProcessFormatNodeApproveDescVo t1) {
                    long time0 = t0.getDate().getTime();
                    long time1 = t1.getDate().getTime();
                    return time0 > time1 ? 1 : -1;
                }
            });

            nodeVo.setApproveDescList(descList);

        }


        Set<String> userIdSet = processInstanceAssignUserRecordList.stream().map(w -> w.getUserId()).collect(Collectors.toSet());
        if (CollUtil.isEmpty(userIdSet) && node.getId().equals(ProcessInstanceConstant.VariableKey.START_NODE)) {

            IProcessInstanceRecordService processInstanceRecordService = SpringUtil.getBean(IProcessInstanceRecordService.class);
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.lambdaQuery().eq(ProcessInstanceRecord::getProcessInstanceId, processInstanceId).one();
            String userId = processInstanceRecord.getUserId();


            NodeFormatUserVo nodeFormatUserVo = buildUser((userId));
            nodeFormatUserVo.setShowTime(processInstanceRecord.getCreateTime());
            nodeFormatUserVo.setShowTimeStr(DateUtil.dateShow(processInstanceRecord.getCreateTime()));
            nodeFormatUserVo.setStatus(NodeFormatUserVoStatusEnum.YJS.getCode());
//            userVo.setOperType(w.getTaskType());

            nodeFormatUserVoList.add(nodeFormatUserVo);
        }

        for (String userId : userIdSet) {

            List<ProcessInstanceAssignUserRecord> list =
                    processInstanceAssignUserRecordList.stream().filter(k -> StrUtil.equals(k.getUserId(), userId))
                            .collect(Collectors.toList());
            ProcessInstanceAssignUserRecord w = list.get(list.size() - 1);


            NodeFormatUserVo nodeFormatUserVo = buildUser((userId));
            if (nodeFormatUserVo == null) {
                continue;
            }
            nodeFormatUserVo.setShowTime(w.getEndTime());
            nodeFormatUserVo.setShowTimeStr(DateUtil.dateShow(w.getEndTime()));
            nodeFormatUserVo.setStatus(w.getStatus());
            nodeFormatUserVo.setOperType(w.getTaskType());


            nodeFormatUserVoList.add(nodeFormatUserVo);
        }
    }

    /**
     * 根据用户id
     *
     * @param userId
     * @return
     */
    default NodeFormatUserVo buildUser(String userId) {
        if (StrUtil.isBlank(userId)) {
            return null;
        }

        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);

        if (user == null) {
            return null;
        }

        NodeFormatUserVo nodeUserDto = NodeFormatUserVo.builder().id(userId).name(user.getName())
                .avatar(user.getAvatarUrl())
                .build();
        return nodeUserDto;
    }
}
