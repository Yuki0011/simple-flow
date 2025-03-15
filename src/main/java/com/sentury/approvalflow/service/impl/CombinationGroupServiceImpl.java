package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.sentury.approvalflow.common.constants.NodeUserTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserRootNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.constants.ProcessInstanceRecordStatusEnum;
import com.sentury.approvalflow.domain.entity.*;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.enums.WorkTypeEnum;
import com.sentury.approvalflow.domain.vo.*;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.dromara.system.api.model.LoginUser;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CombinationGroupServiceImpl implements ICombinationGroupService {
    @Resource
    private IProcessGroupService processGroupService;

    @Resource
    private IUserService userService;

    @Resource
    private IDeptUserService deptUserService;
    @Resource
    private IProcessMainService processMainService;

    @Resource
    private IProcessService processService;

    @Resource
    private IProcessStarterService processStarterService;

    @Resource
    private IRemoteService remoteService;


    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;


    @Resource
    private IProcessNodeDataService processNodeDataService;
    @Resource
    private IProcessInstanceCopyService processCopyService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @DubboReference
    private RemoteUserService remoteUserService;

    /**
     * 根据部门id集合和角色id集合 获取用户列表
     *
     * @param deptIdList 部门id集合
     * @param roleIdList 角色key集合
     * @return
     */
    @Override
    public R2<List<String>> queryUserListByDeptIdListAndRoleIdList(List<String> deptIdList, List<String> roleIdList) {

        MPJLambdaWrapper<User> wrapper = JoinWrappers.lambda(User.class).distinct().select(User::getId);
        if (CollUtil.isNotEmpty(deptIdList)) {
            wrapper.innerJoin(DeptUser.class, DeptUser::getUserId, User::getId).in(DeptUser::getDeptId, deptIdList);
        }
        if (CollUtil.isNotEmpty(roleIdList)) {
            wrapper.innerJoin(UserRole.class, UserRole::getUserId, User::getId).in(UserRole::getRoleId, roleIdList);
        }
        List<String> userIdList = userService.selectJoinList(String.class, wrapper);
        return R2.success(userIdList);
    }

    /**
     * 查询表单组包含流程
     *
     * @param hidden
     * @return 表单组数据
     */
    @Override
    public R2<List<FormGroupVo>> listGroupWithProcess(Boolean hidden) {

        List<FormGroupVo> formGroupVos = new LinkedList<>();

        List<ProcessGroup> processGroupList = processGroupService.lambdaQuery().eq(ProcessGroup::getTenantId, TenantUtil.get()).orderByDesc(ProcessGroup::getSort).list();

        processGroupList.forEach(group -> {
            FormGroupVo formGroupVo = FormGroupVo.builder().id((group.getId())).name(group.getGroupName()).items(new LinkedList<>()).build();
            formGroupVos.add(formGroupVo);

            List<Process> processList = processService.lambdaQuery().eq(Process::getGroupId, group.getId()).eq(Process::getTenantId, TenantUtil.get()).eq(hidden != null, Process::getHidden, hidden).eq(Process::getStop, false).orderByAsc(Process::getSort).orderByDesc(Process::getCreateTime).list();

            processList.forEach(process -> {


                formGroupVo.getItems().add(FormGroupVo.FlowVo.builder().flowId(process.getFlowId()).rangeShow(process.getRangeShow()).name(process.getName()).logo(process.getLogo()).remark(process.getRemark()).stop(process.getStop()).uniqueId(process.getUniqueId()).updated(process.getUpdateTime()).build());
            });
        });
        return R2.success(formGroupVos);
    }

    /**
     * 查询流程组和主流程
     *
     * @return
     */
    @Override
    public R2<List<FormGroupVo>> listGroupWithProcessMain() {

        List<FormGroupVo> formGroupVos = new LinkedList<>();

        List<ProcessGroup> processGroupList = processGroupService.lambdaQuery()

            .eq(ProcessGroup::getTenantId, TenantUtil.get()).orderByDesc(ProcessGroup::getSort).list();

        processGroupList.forEach(group -> {
            FormGroupVo formGroupVo = FormGroupVo.builder().id((group.getId())).name(group.getGroupName()).items(new LinkedList<>()).build();
            formGroupVos.add(formGroupVo);

            List<ProcessMain> processMainList = processMainService.lambdaQuery().eq(ProcessMain::getTenantId, TenantUtil.get()).eq(ProcessMain::getGroupId, group.getId()).eq(ProcessMain::getTenantId, TenantUtil.get()).orderByAsc(ProcessMain::getSort).orderByDesc(ProcessMain::getCreateTime).list();

            processMainList.forEach(processMain -> {

                List<Process> processList = processService.lambdaQuery()

                    .eq(Process::getUniqueId, processMain.getUniqueId()).eq(Process::getTenantId, TenantUtil.get()).eq(Process::getHidden, false).orderByDesc(Process::getUpdateTime).list();
                Process process = null;
                {
                    process = processList.stream().filter(w -> !w.getStop()).findAny().orElse(null);
                    if (process == null) {
                        process = processList.get(0);
                    }
                }

                Boolean reportEnable = false;


                formGroupVo.getItems().add(FormGroupVo.FlowVo.builder().flowId(process.getFlowId()).uniqueId(process.getUniqueId()).rangeShow(processMain.getRangeShow()).name(processMain.getName()).nameSecond(processMain.getNameSecond()).remark(process.getRemark()).logo(processMain.getLogo()).reportEnable(reportEnable).stop(process.getStop()).uniqueId(processMain.getUniqueId()).updated(processMain.getUpdateTime()).build());
            });
        });
        return R2.success(formGroupVos);
    }

    /**
     * 搜索流程
     *
     * @param word 关键词
     * @return
     */
    @Override
    public R2<List<FormGroupVo.FlowVo>> searchFlowList(String word) {

        List<ProcessMain> processMainList = processMainService.lambdaQuery().eq(ProcessMain::getTenantId, TenantUtil.get()).like(ProcessMain::getName, word).orderByAsc(ProcessMain::getSort).orderByDesc(ProcessMain::getCreateTime).list();

        List<FormGroupVo.FlowVo> flowVoList = new ArrayList<>();


        for (ProcessMain processMain : processMainList) {

            List<Process> processList = processService.lambdaQuery()

                .eq(Process::getUniqueId, processMain.getUniqueId()).eq(Process::getTenantId, TenantUtil.get()).eq(Process::getHidden, false).orderByDesc(Process::getUpdateTime).list();

            Process process = null;
            {
                process = processList.stream().filter(w -> !w.getStop()).findAny().orElse(null);
                if (process == null) {
                    process = processList.get(0);
                }
            }


            FormGroupVo.FlowVo flowVo = FormGroupVo.FlowVo.builder().flowId(process.getFlowId()).uniqueId(process.getUniqueId()).rangeShow(processMain.getRangeShow()).name(processMain.getName()).nameSecond(processMain.getNameSecond()).remark(process.getRemark()).logo(processMain.getLogo()).reportEnable(false).stop(process.getStop()).uniqueId(processMain.getUniqueId()).updated(processMain.getUpdateTime()).build();

            flowVoList.add(flowVo);
        }

        return R2.success(flowVoList);
    }

    /**
     * 查询所有我可以发起的表单组
     *
     * @return
     */
    @Override
    public R2<List<FormGroupVo>> listCurrentUserStartGroup() {

        // 旧版
        //String userId = LoginHelper.getUserId()+"";
        //UserDto user = ApiStrategyFactory.getStrategy().getUser(userId+"");
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId = loginUser.getUserId();
        UserDto user = new UserDto();
        user.setId(userId + "");
        user.setName(loginUser.getUsername());
        user.setDeptIdList(List.of(loginUser.getDeptId() + ""));

        List<FormGroupVo> formGroupVos = new LinkedList<>();

        List<ProcessGroup> processGroupList = processGroupService.lambdaQuery().eq(ProcessGroup::getTenantId, TenantUtil.get()).orderByDesc(ProcessGroup::getSort).list();

        processGroupList.forEach(group -> {
            FormGroupVo formGroupVo = FormGroupVo.builder().id((group.getId())).name(group.getGroupName()).items(new LinkedList<>()).build();
            formGroupVos.add(formGroupVo);

            List<Process> processList = processService.lambdaQuery().eq(Process::getTenantId, TenantUtil.get()).eq(Process::getGroupId, group.getId()).eq(Process::getHidden, false).eq(Process::getStop, false).orderByAsc(Process::getSort).list();

            Map<Long, Boolean> existMap = new HashMap<>();

            if (!processList.isEmpty()) {
                List<Long> idList = processList.stream().map(w -> w.getId()).collect(Collectors.toList());
                //查询发起人集合
                List<ProcessStarter> processStarterList = processStarterService.lambdaQuery().eq(ProcessStarter::getTenantId, TenantUtil.get()).in(ProcessStarter::getProcessId, idList).list();
                Map<Long, List<ProcessStarter>> groupmap = processStarterList.stream().collect(Collectors.groupingBy(ProcessStarter::getProcessId));

                for (Process process : processList) {
                    List<ProcessStarter> processStarters = groupmap.get(process.getId());
                    if (processStarters == null) {
                        existMap.put(process.getId(), true);
                        continue;
                    }
                    boolean match = processStarters.stream().anyMatch(w -> w.getTypeId().equals(userId) && w.getType().equals(NodeUserTypeEnum.USER.getKey()));
                    if (match) {
                        existMap.put(process.getId(), true);
                        continue;
                    }
                    Set<String> deptIdSet = new HashSet<>();

                    //需要查询包含子级的部门id
                    List<String> queryChildrenDeptIdList = new ArrayList<>();
                    for (ProcessStarter processStarter : processStarters) {
                        if (processStarter.getType().equals(NodeUserTypeEnum.DEPT.getKey())) {
                            deptIdSet.add(processStarter.getTypeId());
                            //包含下级
                            if (processStarter.getContainChildrenDept()) {
                                queryChildrenDeptIdList.add(processStarter.getTypeId());

                            }
                        }
                    }
                    Map<String, List<DeptDto>> childrenDeptMap = remoteService.batchQueryChildDeptList(queryChildrenDeptIdList).getData();
                    for (Map.Entry<String, List<DeptDto>> entry : childrenDeptMap.entrySet()) {
                        Set<String> collect = entry.getValue().stream().map(DeptDto::getId).collect(Collectors.toSet());
                        deptIdSet.addAll(collect);
                    }
                    //判断部门交集
                    List<String> userDeptIdList = user.getDeptIdList();
                    existMap.put(process.getId(), !CollUtil.intersection(userDeptIdList, deptIdSet).isEmpty());
                }
            }
            processList.forEach(process -> {
                if (!existMap.get(process.getId())) {
                    return;
                }
                formGroupVo.getItems().add(FormGroupVo.FlowVo.builder().flowId(process.getFlowId()).uniqueId(process.getUniqueId()).name(process.getName()).nameSecond(process.getNameSecond()).logo(process.getLogo()).remark(process.getRemark()).stop(process.getStop()).updated(process.getUpdateTime()).build());
            });
        });
        return R2.success(formGroupVos);
    }

    /**
     * 删除主流程
     *
     * @param uniqueId
     * @return
     */
    @Transactional
    @Override
    public R2 deleteProcessMain(String uniqueId) {
        processMainService.lambdaUpdate().eq(ProcessMain::getUniqueId, uniqueId).eq(ProcessMain::getTenantId, TenantUtil.get()).remove();
        processService.lambdaUpdate().set(Process::getHidden, true).eq(Process::getHidden, false).eq(Process::getUniqueId, uniqueId).eq(Process::getTenantId, TenantUtil.get()).update(new Process());
        return R2.success();
    }

    /**
     * 清理所有流程
     *
     * @param uniqueId 流程唯一id
     * @return
     */
    @Transactional
    @Override
    public R2 clear(String uniqueId) {

        String tenantId = TenantUtil.get();
        List<Process> processList = processService.lambdaQuery().eq(Process::getUniqueId, uniqueId).eq(Process::getTenantId, tenantId).list();
        if (CollUtil.isEmpty(processList)) {
            return R2.success();
        }
        List<String> flowIdList = processList.stream().map(Process::getFlowId).collect(Collectors.toList());
        List<Long> processIdList = processList.stream().map(BaseEntityForWorkFlow::getId).collect(Collectors.toList());


        Map<String, IClearService> serviceMap = SpringUtil.getBeansOfType(IClearService.class);
        for (Map.Entry<String, IClearService> entry : serviceMap.entrySet()) {
            entry.getValue().clearProcess(uniqueId, flowIdList, processIdList, tenantId);
        }

        String userId = LoginHelper.getUserId() + "";
        UserDto userDto = ApiStrategyFactory.getStrategy().getUser(userId);


        //清理flowable
        ClearProcessParamDto clearProcessParamDto = new ClearProcessParamDto();
        clearProcessParamDto.setFlowIdList(flowIdList);
        clearProcessParamDto.setUserId(userId);
        clearProcessParamDto.setUserName(userDto.getName());


        R2 r = CoreHttpUtil.clearProcess(clearProcessParamDto);
        if (!r.isOk()) {
            throw new RuntimeException("清理流程失败");
        }

        return R2.success();
    }

    @Override
    public R2 queryTotalTaskList() {
        String assign = LoginHelper.getUserId() + "";
        List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().select(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();
        Map<String, String> instanceWithName = processInstanceRecordList.stream().collect(Collectors.toMap(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName));
        // 待办
        TaskQuery taskQuery = taskService.createTaskQuery().taskTenantId(TenantUtil.get());
        List<Task> tasks = taskQuery.taskAssignee(assign).orderByTaskCreateTime().desc().list();
        List<TaskSummaryVo> results = new ArrayList<>();
        List<String> processNames = tasks.stream().map(e -> instanceWithName.get(e.getProcessInstanceId())).filter(Objects::nonNull).toList();
        handleData(results, WorkTypeEnum.TODO, processNames.size(), processNames);
        // 已办
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().taskTenantId(TenantUtil.get());
        List<HistoricTaskInstance> historyList = historicTaskInstanceQuery.taskAssignee(assign).taskWithoutDeleteReason().finished().orderByHistoricTaskInstanceEndTime().desc().list();
        List<String> processNames2 = historyList.stream().map(e -> instanceWithName.get(e.getProcessInstanceId())).filter(Objects::nonNull).toList();
        handleData(results, WorkTypeEnum.DONE, processNames2.size(), processNames2);
        // 抄送我
        List<ProcessInstanceCopy> connectList = processCopyService.lambdaQuery().eq(ProcessInstanceCopy::getUserId, assign).eq(ProcessInstanceCopy::getTenantId, TenantUtil.get()).list();
        List<String> processNames3 = connectList.stream().map(e -> instanceWithName.get(e.getProcessInstanceId())).filter(Objects::nonNull).toList();
        handleData(results, WorkTypeEnum.CONNECT, processNames3.size(), processNames3);
        // 已发起
        List<ProcessInstanceRecord> sendList = processInstanceRecordService.lambdaQuery().eq(ProcessInstanceRecord::getUserId, assign).eq(ProcessInstanceRecord::getTenantId, TenantUtil.get()).orderByDesc(ProcessInstanceRecord::getCreateTime).list();
        List<String> processNames4 = sendList.stream().map(e -> instanceWithName.get(e.getProcessInstanceId())).filter(Objects::nonNull).toList();
        handleData(results, WorkTypeEnum.SEND, processNames4.size(), processNames4);
        return R2.success(results);
    }

    private void handleData(List<TaskSummaryVo> results, WorkTypeEnum typeEnum, Integer count, List<String> processNames) {
        TaskSummaryVo summaryVo = new TaskSummaryVo();
        summaryVo.setWorkType(typeEnum);
        summaryVo.setTaskCount(count);
        Map<String, List<String>> nameGroup = processNames.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(e -> e));
        List<TaskItemVo> items = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : nameGroup.entrySet()) {
            TaskItemVo itemVo = new TaskItemVo();
            itemVo.setProcessName(e.getKey());
            itemVo.setTaskCount(e.getValue().size());
            items.add(itemVo);
        }
        summaryVo.setItems(items);
        results.add(summaryVo);
    }

    /**
     * 查询当前登录用户的待办任务
     *
     * @param pageVO
     * @return
     */
    @Override
    public R2 queryTodoTaskList(PageDto pageVO) {
        TaskQueryParamDto taskQueryParamDto = BeanUtil.copyProperties(pageVO, TaskQueryParamDto.class);
        taskQueryParamDto.setAssign(LoginHelper.getUserId() + "");
        R2<PageResultDto<TaskDto>> r = CoreHttpUtil.queryTodoTask(taskQueryParamDto);
        PageResultDto<TaskDto> pageResultDto = r.getData();
        List<TaskDto> records = pageResultDto.getRecords();
        if (CollUtil.isEmpty(records)) {
            return R2.success(pageResultDto);
        }

        Set<String> processInstanceIdSet = records.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());
        //流程实例记录
        List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().in(ProcessInstanceRecord::getProcessInstanceId, processInstanceIdSet).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();

        //发起人
        Set<String> startUserIdSet = processInstanceRecordList.stream().map(w -> w.getUserId()).collect(Collectors.toSet());

        List<RemoteUserVo> remoteUserVos = remoteUserService.selectListByIds(startUserIdSet.stream().map(Long::parseLong).toList());
        Map<String, RemoteUserVo> users = remoteUserVos.stream().collect(Collectors.toMap(e -> e.getUserId() + "", w -> w));

        List<UserDto> startUserList = new ArrayList<>();
        {
            for (String userIds : startUserIdSet) {
                UserDto user = ApiStrategyFactory.getStrategy().getUser(userIds);
                startUserList.add(user);
            }
        }
        //流程配置
        Set<String> flowIdSet = processInstanceRecordList.stream().map(w -> w.getFlowId()).collect(Collectors.toSet());
        List<Process> processList = processService.lambdaQuery().in(Process::getFlowId, flowIdSet).eq(Process::getTenantId, TenantUtil.get()).list();

        for (TaskDto record : records) {
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordList.stream().filter(w -> StrUtil.equals(w.getProcessInstanceId(), record.getProcessInstanceId())).findAny().orElse(null);
            if (processInstanceRecord != null) {
                record.setProcessName(processInstanceRecord.getName());
                UserDto startUser = startUserList.stream().filter(w -> w.getId().equals(processInstanceRecord.getUserId())).findAny().orElse(null);

                record.setRootUserId(processInstanceRecord.getUserId());
                record.setGroupName(processInstanceRecord.getGroupName());
                record.setRootUserName(startUser.getName());
                record.setRootUserAvatarUrl(startUser.getAvatarUrl());
                record.setStartTime(processInstanceRecord.getCreateTime());
                record.setStartTimeShow(com.sentury.approvalflow.common.utils.DateUtil.dateShow(processInstanceRecord.getCreateTime()));
                record.setProcessInstanceBizCode(processInstanceRecord.getProcessInstanceBizCode());
            }
            //进行中  查询谁在处理
            List<TaskDto> taskDtoList = CoreHttpUtil.queryTaskAssignee(null, record.getProcessInstanceId()).getData();
            if (!taskDtoList.isEmpty()) {
                //正则处理人
                Set<String> taskAssign = taskDtoList.stream().map(w -> w.getAssign()).collect(Collectors.toSet());
                String taskAssignId = CollUtil.newArrayList(taskAssign).get(0);
                UserDto userDto1 = ApiStrategyFactory.getStrategy().getUser(taskAssignId);
                if (taskAssign.size() == 1) {
                    record.setTaskAssignShow(userDto1.getName());
                } else {
                    String format = StrUtil.format("{}等{}人", userDto1.getName(), taskAssign.size());
                    record.setTaskAssignShow(format);
                }
            }

            //是否是子流程发起任务
            Map<String, Object> paramMap = record.getParamMap();

            //处理表单数据
            Process process = processList.stream().filter(w -> StrUtil.equals(w.getFlowId(), record.getFlowId())).findAny().orElse(null);
            if (process != null) {
                if (!process.getName().equals(pageVO.getProcessName())) {
                    continue;
                }
                List<Dict> formValueShowList = getFormValueShowList(process, record.getFlowId(), record.getNodeId(), paramMap);
                record.setFormValueShowList(formValueShowList);

                // 每个审批选取前三个字段用于展示
                List<FormItemVO> formItemVOS = JsonUtil.parseArray(process.getFormItems(), FormItemVO.class);
                List<FormItemVO> titles = formItemVOS.stream().filter(e -> e.getType().equals("Input") || e.getType().equals("SingleSelect") || e.getType().equals("Date")).toList();
                if (titles.size() >= 3) {
                    Map<String, Object> paramMap1 = record.getParamMap();
                    FormItemVO one = titles.get(0);
                    FormItemVO two = titles.get(1);
                    FormItemVO three = titles.get(2);
                    TitleDto title1 = new TitleDto();
                    title1.setName(one.getName());
                    title1.setValue(getValue(one, paramMap1));
                    TitleDto title2 = new TitleDto();
                    title2.setName(two.getName());
                    title2.setValue(getValue(two, paramMap1));
                    TitleDto title3 = new TitleDto();
                    title3.setName(three.getName());
                    title3.setValue(getValue(three, paramMap1));
                    record.setTitle(List.of(title1, title2, title3));
                }
            }
            if (record.getRootUserId() != null) {
                RemoteUserVo remoteUserVo = users.get(record.getRootUserId());
                record.setRootUserCode(remoteUserVo.getUserCode());
            }
        }

        return R2.success(pageResultDto);
    }

    private String getValue(FormItemVO form, Map<String, Object> paramMap) {
        if (form.getType().equals("Input") || form.getType().equals("Date")) {
            Object o = paramMap.get(form.getId());
            return o == null ? "" : o.toString();
        } else if (form.getType().equals("SingleSelect")) {
            Object o = paramMap.get(form.getId());
            if (o == null) {
                return "";
            }
            if(o.toString().contains("value=")){
                return  o.toString().split("value=")[1].replace("}]","");
            }
        }
        return "";
    }

    /**
     * 查询已办任务
     *
     * @param pageVO
     * @return
     */
    @Override
    public R2 queryFinishedTaskList(ProcessDataQueryVO pageVO) {
        TaskQueryParamDto taskQueryParamDto = BeanUtil.copyProperties(pageVO, TaskQueryParamDto.class);
        //taskQueryParamDto.setAssign(LoginHelper.getUserId()+"");
        taskQueryParamDto.setAssign(LoginHelper.getUserId() + "");

        R2<PageResultDto<TaskDto>> r = CoreHttpUtil.queryCompletedTask(taskQueryParamDto);


        PageResultDto<TaskDto> pageResultDto = r.getData();
        List<TaskDto> records = pageResultDto.getRecords();
        if (CollUtil.isEmpty(records)) {
            return R2.success(pageResultDto);

        }


        Set<String> processInstanceIdSet = records.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());

        //流程实例记录
        List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().in(ProcessInstanceRecord::getProcessInstanceId, processInstanceIdSet).list();

        //发起人
        Set<String> startUserIdSet = processInstanceRecordList.stream().map(w -> w.getUserId()).collect(Collectors.toSet());
        List<RemoteUserVo> remoteUserVos = remoteUserService.selectListByIds(startUserIdSet.stream().map(Long::parseLong).toList());
        Map<String, RemoteUserVo> users = remoteUserVos.stream().collect(Collectors.toMap(e -> e.getUserId() + "", w -> w));
        List<UserDto> startUserList = new ArrayList<>();
        {
            for (String userIds : startUserIdSet) {
                UserDto user = ApiStrategyFactory.getStrategy().getUser(userIds);
                startUserList.add(user);
            }
        }
        //流程配置
        Set<String> flowIdSet = processInstanceRecordList.stream().map(w -> w.getFlowId()).collect(Collectors.toSet());
        List<Process> processList = processService.lambdaQuery().in(Process::getFlowId, flowIdSet).list();

        for (TaskDto record : records) {

            ProcessInstanceRecord processInstanceRecord = processInstanceRecordList.stream().filter(w -> StrUtil.equals(w.getProcessInstanceId(), record.getProcessInstanceId())).findAny().orElse(null);

            if (processInstanceRecord != null) {

                record.setProcessName(processInstanceRecord.getName());


                UserDto startUser = startUserList.stream().filter(w -> w.getId().equals(processInstanceRecord.getUserId())).findAny().orElse(null);
                record.setRootUserId(processInstanceRecord.getUserId());
                record.setGroupName(processInstanceRecord.getGroupName());
                record.setRootUserName(startUser.getName());
                record.setRootUserAvatarUrl(startUser.getAvatarUrl());
                record.setStartTime(processInstanceRecord.getCreateTime());
                record.setStartTimeShow(com.sentury.approvalflow.common.utils.DateUtil.dateShow(processInstanceRecord.getCreateTime()));
                record.setProcessInstanceResult(processInstanceRecord.getResult());
                record.setProcessInstanceBizCode(processInstanceRecord.getProcessInstanceBizCode());


                if (processInstanceRecord.getStatus() == ProcessInstanceRecordStatusEnum.JXZ.getCode()) {

                    //进行中  查询谁在处理
                    List<TaskDto> taskDtoList = CoreHttpUtil.queryTaskAssignee(null, record.getProcessInstanceId()).getData();
                    if (!taskDtoList.isEmpty()) {
                        //正则处理人
                        Set<String> taskAssign = taskDtoList.stream().map(w -> w.getAssign()).collect(Collectors.toSet());
                        String taskAssignId = CollUtil.newArrayList(taskAssign).get(0);
                        UserDto userDto1 = ApiStrategyFactory.getStrategy().getUser(taskAssignId);
                        if (taskAssign.size() == 1) {
                            record.setTaskAssignShow(userDto1.getName());
                        } else {
                            String format = StrUtil.format("{}等{}人", userDto1.getName(), taskAssign.size());
                            record.setTaskAssignShow(format);
                        }
                    }
                }


                //查询流程实例变量
                Map<String, Object> paramMap = CoreHttpUtil.queryExecutionVariables(processInstanceRecord.getProcessInstanceId()).getData();

                //处理表单数据
                Process process = processList.stream().filter(w -> StrUtil.equals(w.getFlowId(), record.getFlowId())).findFirst().get();
                List<Dict> formValueShowList = getFormValueShowList(process, record.getFlowId(), record.getNodeId(), paramMap);

                // 每个审批选取前三个字段用于展示
                List<FormItemVO> formItemVOS = JsonUtil.parseArray(process.getFormItems(), FormItemVO.class);
                List<FormItemVO> titles = formItemVOS.stream().filter(e -> e.getType().equals("Input") || e.getType().equals("SingleSelect") || e.getType().equals("Date")).toList();
                if (titles.size() >= 3) {
                    Map<String, Object> paramMap1 = record.getParamMap();
                    FormItemVO one = titles.get(0);
                    FormItemVO two = titles.get(1);
                    FormItemVO three = titles.get(2);
                    TitleDto title1 = new TitleDto();
                    title1.setName(one.getName());
                    title1.setValue(getValue(one, paramMap1));
                    TitleDto title2 = new TitleDto();
                    title2.setName(two.getName());
                    title2.setValue(getValue(two, paramMap1));
                    TitleDto title3 = new TitleDto();
                    title3.setName(three.getName());
                    title3.setValue(getValue(three, paramMap1));
                    record.setTitle(List.of(title1, title2, title3));
                }
                record.setFormValueShowList(formValueShowList);
            }

            if (record.getRootUserId() != null) {
                RemoteUserVo remoteUserVo = users.get(record.getRootUserId());
                record.setRootUserCode(remoteUserVo.getUserCode());
            }

        }


        return R2.success(pageResultDto);
    }

    /**
     * 查询我发起的
     *
     * @param pageDto
     * @return
     */
    @Override
    public R2 queryInitiatedTaskList(ProcessDataQueryVO pageDto) {


        //String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId() + "";

        LoginUser userInfo = remoteUserService.getUserInfo(Long.parseLong(userId), null);

        //查询所有的流程id
        List<String> allFlowIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(pageDto.getFlowIdList())) {
            List<String> data = processService.getAllRelatedFlowId(pageDto.getFlowIdList()).getData();
            allFlowIdList.addAll(data);
        }
        LambdaQueryChainWrapper<ProcessInstanceRecord> wrapper = processInstanceRecordService.lambdaQuery();
        if (StringUtils.isNotBlank(pageDto.getProcessName())) {
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().select(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName).eq(ProcessInstanceRecord::getName, pageDto.getProcessName()).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();
            List<String> processInstanceRecordIds = processInstanceRecordList.stream().map(ProcessInstanceRecord::getProcessInstanceId).toList();
            wrapper.in(ProcessInstanceRecord::getProcessInstanceId, processInstanceRecordIds);
        }
        Page<ProcessInstanceRecord> instanceRecordPage = wrapper.eq(ProcessInstanceRecord::getUserId, userId).eq(ProcessInstanceRecord::getTenantId, TenantUtil.get()).in(CollUtil.isNotEmpty(allFlowIdList), ProcessInstanceRecord::getFlowId, allFlowIdList).orderByDesc(ProcessInstanceRecord::getCreateTime).page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()));

        List<ProcessInstanceRecord> records = instanceRecordPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return R2.success(instanceRecordPage);
        }


        //流程配置
        Set<String> flowIdSet = records.stream().map(w -> w.getFlowId()).collect(Collectors.toSet());
        List<Process> processList = processService.lambdaQuery().in(Process::getFlowId, flowIdSet).eq(Process::getTenantId, TenantUtil.get()).list();

        List<ProcessInstanceRecordVO> processInstanceRecordVOList = BeanUtil.copyToList(records, ProcessInstanceRecordVO.class);

        UserDto userDto = ApiStrategyFactory.getStrategy().getUser(userId);

        for (ProcessInstanceRecordVO record : processInstanceRecordVOList) {

            //处理表单数据
            Process process = processList.stream().filter(w -> StrUtil.equals(w.getFlowId(), record.getFlowId())).findFirst().get();


            record.setFormData(null);
            record.setProcess(null);
            record.setStartTimeShow(com.sentury.approvalflow.common.utils.DateUtil.dateShow(record.getCreateTime()));
            record.setRootUserAvatarUrl(userDto.getAvatarUrl());
            record.setRootUserName(userDto.getName());
            record.setCancelEnable(false);
            record.setFlowUniqueId(process.getUniqueId());

            //查询流程变量
            Map<String, Object> paramMap = CoreHttpUtil.queryExecutionVariables(record.getProcessInstanceId()).getData();
            List<Dict> formValueShowList = getFormValueShowList(process, record.getFlowId(), ProcessInstanceConstant.VariableKey.START_NODE, paramMap);
            record.setFormValueShowList(formValueShowList);

            if (record.getStatus() == ProcessInstanceRecordStatusEnum.JXZ.getCode()) {


                //进行中  查询谁在处理
                List<TaskDto> taskDtoList = CoreHttpUtil.queryTaskAssignee(null, record.getProcessInstanceId()).getData();
                if (!taskDtoList.isEmpty()) {
                    //正则处理人
                    Set<String> taskAssign = taskDtoList.stream().map(w -> w.getAssign()).collect(Collectors.toSet());
                    String taskAssignId = CollUtil.newArrayList(taskAssign).get(0);
                    UserDto userDto1 = ApiStrategyFactory.getStrategy().getUser(taskAssignId);
                    if (taskAssign.size() == 1) {
                        record.setTaskAssignShow(userDto1.getName());
                    } else {
                        String format = StrUtil.format("{}等{}人", userDto1.getName(), taskAssign.size());
                        record.setTaskAssignShow(format);
                    }
                }
            }
            record.setRootUserCode(userInfo.getUserCode());
            // 每个审批选取前三个字段用于展示
            List<FormItemVO> formItemVOS = JsonUtil.parseArray(process.getFormItems(), FormItemVO.class);
            List<FormItemVO> titles = formItemVOS.stream().filter(e -> e.getType().equals("Input") || e.getType().equals("SingleSelect") || e.getType().equals("Date")).toList();
            if (titles.size() >= 3) {
                Map<String, Object> paramMap1 = CoreHttpUtil.queryExecutionVariables(record.getProcessInstanceId()).getData();
                FormItemVO one = titles.get(0);
                FormItemVO two = titles.get(1);
                FormItemVO three = titles.get(2);
                TitleDto title1 = new TitleDto();
                title1.setName(one.getName());
                title1.setValue(getValue(one, paramMap1));
                TitleDto title2 = new TitleDto();
                title2.setName(two.getName());
                title2.setValue(getValue(two, paramMap1));
                TitleDto title3 = new TitleDto();
                title3.setName(three.getName());
                title3.setValue(getValue(three, paramMap1));
                record.setTitle(List.of(title1, title2, title3));
            }
        }
        Page page = BeanUtil.copyProperties(instanceRecordPage, Page.class);
        page.setRecords(processInstanceRecordVOList);


        return R2.success(page);
    }

    /**
     * 查询抄送给我的
     *
     * @param pageDto
     * @return
     */
    @Override
    public R2 queryCopiedTaskList(ProcessDataQueryVO pageDto) {


        //String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId() + "";
        LambdaQueryChainWrapper<ProcessInstanceCopy> wrapper = processCopyService.lambdaQuery();
        if (StringUtils.isNotBlank(pageDto.getProcessName())) {
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().select(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName).eq(ProcessInstanceRecord::getName, pageDto.getProcessName()).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();
            List<String> processInstanceRecordIds = processInstanceRecordList.stream().map(ProcessInstanceRecord::getProcessInstanceId).toList();
            wrapper.in(ProcessInstanceCopy::getProcessInstanceId, processInstanceRecordIds);
        }
        Page<ProcessInstanceCopy> page = processCopyService.lambdaQuery().eq(ProcessInstanceCopy::getUserId, userId).eq(ProcessInstanceCopy::getTenantId, TenantUtil.get()).in(CollUtil.isNotEmpty(pageDto.getFlowIdList()), ProcessInstanceCopy::getFlowId, pageDto.getFlowIdList()).orderByDesc(ProcessInstanceCopy::getNodeTime).page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()));

        List<ProcessInstanceCopy> records = page.getRecords();

        List<ProcessInstanceCopyVo> processCopyVoList = BeanUtil.copyToList(records, ProcessInstanceCopyVo.class);

        if (CollUtil.isNotEmpty(records)) {

            //发起人
            Set<String> startUserIdSet = records.stream().map(w -> w.getStartUserId()).collect(Collectors.toSet());
            List<RemoteUserVo> remoteUserVos = remoteUserService.selectListByIds(startUserIdSet.stream().map(Long::parseLong).toList());
            Map<String, RemoteUserVo> users = remoteUserVos.stream().collect(Collectors.toMap(e -> e.getUserId() + "", w -> w));
            List<UserDto> startUserList = new ArrayList<>();
            for (String s : startUserIdSet) {
                UserDto user = ApiStrategyFactory.getStrategy().getUser(s);
                startUserList.add(user);
            }
            Set<String> processInstanceIdSet = records.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());

            //流程实例记录
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().in(ProcessInstanceRecord::getProcessInstanceId, processInstanceIdSet).eq(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();

            //流程配置
            Set<String> flowIdSet = processInstanceRecordList.stream().map(w -> w.getFlowId()).collect(Collectors.toSet());
            List<Process> processList = processService.lambdaQuery().in(Process::getFlowId, flowIdSet).eq(Process::getTenantId, TenantUtil.get()).list();


            for (ProcessInstanceCopyVo record : processCopyVoList) {

                ProcessInstanceRecord processInstanceRecord = processInstanceRecordList.stream().filter(w -> StrUtil.equals(w.getProcessInstanceId(), record.getProcessInstanceId())).findFirst().get();


                UserDto startUser = startUserList.stream().filter(w -> w.getId().equals(record.getStartUserId())).findAny().orElse(null);
                record.setStartUserName(startUser.getName());
                record.setStartUserAvatarUrl(startUser.getAvatarUrl());


                Process process = processList.stream().filter(w -> StrUtil.equals(w.getFlowId(), record.getFlowId())).findFirst().get();

                List<Dict> formValueShowList = getFormValueShowList(process, record.getFlowId(), ProcessInstanceConstant.VariableKey.START_NODE, JsonUtil.parseObject(record.getFormData(), new JsonUtil.TypeReference<Map<String, Object>>() {
                }));

                record.setFormValueShowList(formValueShowList);
                record.setFormData(null);
                record.setStartTimeShow(com.sentury.approvalflow.common.utils.DateUtil.dateShow(processInstanceRecord.getCreateTime()));
                record.setProcessInstanceResult(processInstanceRecord.getResult());
                record.setProcessInstanceBizCode(processInstanceRecord.getProcessInstanceBizCode());

                if (processInstanceRecord.getStatus() == ProcessInstanceRecordStatusEnum.JXZ.getCode()) {


                    //进行中  查询谁在处理
                    List<TaskDto> taskDtoList = CoreHttpUtil.queryTaskAssignee(null, record.getProcessInstanceId()).getData();
                    if (!taskDtoList.isEmpty()) {
                        //正则处理人
                        Set<String> taskAssign = taskDtoList.stream().map(w -> w.getAssign()).collect(Collectors.toSet());
                        String taskAssignId = CollUtil.newArrayList(taskAssign).get(0);
                        UserDto userDto1 = ApiStrategyFactory.getStrategy().getUser(taskAssignId);
                        if (taskAssign.size() == 1) {
                            record.setTaskAssignShow(userDto1.getName());
                        } else {
                            String format = StrUtil.format("{}等{}人", userDto1.getName(), taskAssign.size());
                            record.setTaskAssignShow(format);
                        }
                    }

                }
                // 每个审批选取前三个字段用于展示
                List<FormItemVO> formItemVOS = JsonUtil.parseArray(process.getFormItems(), FormItemVO.class);
                List<FormItemVO> titles = formItemVOS.stream().filter(e -> e.getType().equals("Input") || e.getType().equals("SingleSelect") || e.getType().equals("Date")).toList();
                if (titles.size() >= 3) {
                    Map<String, Object> paramMap1 = CoreHttpUtil.queryExecutionVariables(record.getProcessInstanceId()).getData();
                    FormItemVO one = titles.get(0);
                    FormItemVO two = titles.get(1);
                    FormItemVO three = titles.get(2);
                    TitleDto title1 = new TitleDto();
                    title1.setName(one.getName());
                    title1.setValue(getValue(one, paramMap1));
                    TitleDto title2 = new TitleDto();
                    title2.setName(two.getName());
                    title2.setValue(getValue(two, paramMap1));
                    TitleDto title3 = new TitleDto();
                    title3.setName(three.getName());
                    title3.setValue(getValue(three, paramMap1));
                    record.setTitle(List.of(title1, title2, title3));
                }

                if (record.getStartUserId() != null) {
                    RemoteUserVo remoteUserVo = users.get(record.getStartUserId());
                    record.setRootUserCode(remoteUserVo.getUserCode());
                }
            }
        }

        Page p = BeanUtil.copyProperties(page, Page.class);

        p.setRecords(processCopyVoList);

        return R2.success(p);
    }

    /**
     * 获取列表 显示的表单数据 姓名：张三 格式
     *
     * @param process
     * @param flowId
     * @param nodeId
     * @param paramMap
     * @return
     */
    private List<Dict> getFormValueShowList(Process process, String flowId, String nodeId, Map<String, Object> paramMap) {
        String formItems = process.getFormItems();
        List<FormItemVO> formItemVOList = JsonUtil.parseArray(formItems, FormItemVO.class);

        Map<String, String> formPermMap = new HashMap<>();

        if (StrUtil.isNotBlank(nodeId)) {
            Node node = processNodeDataService.getNode(flowId, nodeId).getData();
            SuperUserRootNode superUserRootNode = (SuperUserRootNode) node;
            Map<String, String> map = superUserRootNode.getFormPerms();


        } else {
            for (FormItemVO formItemVO : formItemVOList) {
                formPermMap.put(formItemVO.getId(), ProcessInstanceConstant.FormPermClass.READ);
            }
        }


        List<Dict> formValueShowList = new ArrayList<>();


        buildFormValueShow(paramMap, formItemVOList, formPermMap, formValueShowList);

        List<Dict> list = formValueShowList.size() > 3 ? (formValueShowList.subList(0, 3)) : formValueShowList;
        return list;
    }


    private static void buildFormValueShow(Map<String, Object> paramMap, List<FormItemVO> formItemVOList, Map<String, String> formPermMap, List<Dict> formValueShowList) {
        for (FormItemVO formItemVO : formItemVOList) {

            String id = formItemVO.getId();
            String formItemVOName = formItemVO.getName();

            String perm = formPermMap.get(id);
            if (StrUtil.isBlank(perm) || ProcessInstanceConstant.FormPermClass.HIDE.equals(perm)) {
                continue;
            }
            Object o = paramMap.get(id);
            if (o == null || StrUtil.isBlankIfStr(o)) {
                formValueShowList.add(Dict.create().set("key", formItemVOName).set("label", ""));
                continue;
            }
            //表单类型
            String type = formItemVO.getType();

            {
                if (o == null || StrUtil.isBlankIfStr(o)) {
                    formValueShowList.add(Dict.create().set("key", formItemVOName).set("label", ""));
                    return;

                }


            }


        }
    }

}
