package com.sentury.approvalflow.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sentury.approvalflow.common.constants.ApproveResultEnum;
import com.sentury.approvalflow.common.constants.NodeUserTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.service.biz.IRemoteService;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.constants.ProcessInstanceRecordStatusEnum;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.entity.ProcessInstanceAssignUserRecord;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.biz.form.FormStrategyFactory;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;
import com.sentury.approvalflow.domain.vo.ProcessInstanceRecordVO;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;
import com.sentury.approvalflow.producer.NormalRabbitProducer;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实例进程服务
 */
@Service
@Slf4j
public class ProcessInstanceServiceImpl implements IProcessInstanceService {
    @Resource
    private IFileService fileService;
    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;
    @Resource
    private IProcessInstanceAssignUserRecordService processInstanceAssignUserRecordService;
    @Resource
    private IProcessInstanceCopyService processCopyService;

    @Resource
    private IProcessInstanceOperRecordService processInstanceOperRecordService;

    @Resource
    private IProcessNodeDataService processNodeDataService;

    @Resource
    private IProcessService processService;
    @Resource
    private IProcessInstanceNodeRecordService processNodeRecordService;
    @Resource
    private IProcessInstanceAssignUserRecordService processNodeRecordAssignUserService;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    @Lazy
    private IRemoteService remoteService;

    @Autowired
    private NormalRabbitProducer normalRabbitProducer;

    @Resource
    private IFormService formService;
    @Resource
    private TaskService taskService;

    private static List<String> SINGLE_PROCESS = List.of("转正流程", "离职流程", "异动流程", "二次入职流程");


    /**
     * 启动流程
     *
     * @param processInstanceParamDto
     * @return
     */
    @Override
    public R2 startProcessInstance(ProcessInstanceParamDto processInstanceParamDto) {

        String uniqueId = processInstanceParamDto.getUniqueId();
        //业务key
        Process process = processService.getByUniqueId(uniqueId);
        if (process == null) {
            return R2.fail("流程不存在");
        }
        if (process.getHidden() || process.getStop()) {
            return R2.fail("流程被禁用");
        }
        if (StrUtil.isNotBlank(processInstanceParamDto.getFlowId())) {
            if (!StrUtil.equals(process.getFlowId(), processInstanceParamDto.getFlowId())) {
                return R2.fail("流程不存在");
            }
        }

        // todo 审批流前置判断
        this.checkBeforeProcessStart(processInstanceParamDto.getParamMap(), process.getName());


        processInstanceParamDto.setFlowId(process.getFlowId());


        //String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId() + "";


        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);


        processInstanceParamDto.setStartUserId(String.valueOf(userId));
        Map<String, Object> paramMap = processInstanceParamDto.getParamMap();
        NodeUser rootUser = NodeUser.builder().id(userId).name(user.getName()).type(NodeUserTypeEnum.USER.getKey()).build();
        paramMap.put(ProcessInstanceConstant.VariableKey.STARTER_USER, CollUtil.newArrayList(rootUser));


        processInstanceParamDto.setBizKey(process.getUniqueId());


        //判断字段格式
        String formItems = process.getFormItems();
        List<FormItemVO> formItemVOS = JsonUtil.parseArray(formItems, FormItemVO.class);
        R2 checkValueResult = FormStrategyFactory.checkValue(formItemVOS, paramMap);
        if (!checkValueResult.isOk()) {
            return R2.fail(checkValueResult.getMsg());
        }


        R2<String> r = CoreHttpUtil.startProcess(processInstanceParamDto);

        if (!r.isOk()) {
            return R2.fail(r.getMsg());
        }
        String data = r.getData();


        processInstanceOperRecordService.saveStartProcessRecord(userId, data, processInstanceParamDto.getFlowId());


        // 流程开始->发送消息用于业务回调
        normalRabbitProducer.send(this.processInformationJson(processInstanceParamDto.getFlowId(), data, process.getName(), "开始"));
        return R2.success(data);
    }

    @Override
    public R2 startProcessInstanceWithOutLogin(ProcessInstanceParamDto processInstanceParamDto, String userId) {
        String uniqueId = processInstanceParamDto.getUniqueId();
        //业务key
        Process process = processService.getByUniqueId(uniqueId);
        if (process == null) {
            return R2.fail("流程不存在");
        }
        if (process.getHidden() || process.getStop()) {
            return R2.fail("流程被禁用");
        }
        if (StrUtil.isNotBlank(processInstanceParamDto.getFlowId())) {
            if (!StrUtil.equals(process.getFlowId(), processInstanceParamDto.getFlowId())) {
                return R2.fail("流程不存在");
            }
        }

        // todo 审批流前置判断
        this.checkBeforeProcessStart(processInstanceParamDto.getParamMap(), process.getName());


        processInstanceParamDto.setFlowId(process.getFlowId());


        //String userId = LoginHelper.getUserId()+"";
        //String userId = LoginHelper.getUserId() + "";


        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);


        processInstanceParamDto.setStartUserId(String.valueOf(userId));
        Map<String, Object> paramMap = processInstanceParamDto.getParamMap();
        NodeUser rootUser = NodeUser.builder().id(userId).name(user.getName()).type(NodeUserTypeEnum.USER.getKey()).build();
        paramMap.put(ProcessInstanceConstant.VariableKey.STARTER_USER, CollUtil.newArrayList(rootUser));


        processInstanceParamDto.setBizKey(process.getUniqueId());


        //判断字段格式
        String formItems = process.getFormItems();
        List<FormItemVO> formItemVOS = JsonUtil.parseArray(formItems, FormItemVO.class);
        R2 checkValueResult = FormStrategyFactory.checkValue(formItemVOS, paramMap);
        if (!checkValueResult.isOk()) {
            return R2.fail(checkValueResult.getMsg());
        }


        R2<String> r = CoreHttpUtil.startProcess(processInstanceParamDto);

        if (!r.isOk()) {
            return R2.fail(r.getMsg());
        }
        String data = r.getData();


        processInstanceOperRecordService.saveStartProcessRecord(userId, data, processInstanceParamDto.getFlowId());


        // 流程开始->发送消息用于业务回调
        normalRabbitProducer.send(this.processInformationJson(processInstanceParamDto.getFlowId(), data, process.getName(), "开始"));
        return R2.success(data);
    }


    private void checkBeforeProcessStart(Map<String, Object> forms, String flowName) {
        // 不允许同一个流程发起多次
        if (SINGLE_PROCESS.contains(flowName) && flowName != null) {
            for (Map.Entry<String, Object> entry : forms.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof ArrayList<?>) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) value;
                    if (CollectionUtils.isEmpty(data)) {
                        continue;
                    }
                    Map<String, Object> map = data.get(0);
                    List<String> flowNames = (List<String>) map.get("flowNames");
                    if (CollectionUtils.isEmpty(flowNames)) {
                        continue;
                    }
                    Assert.isFalse(flowNames.contains(flowName), "已存在进行中的" + flowName);
                }
            }
        }
    }


    @Override
    public String processInformationJson(String flowId, String instanceId, String processName, String flag) {
        List<FormItemVO> froms = this.getFormAfterStartProcess(flowId, instanceId);
        Map<String, String> processData = new HashMap<>();
        // 表单信息
        processData.put("forms", JsonUtils.toJsonString(froms));
        // 流程实例id
        processData.put("instanceId", instanceId);
        // 流程名称
        processData.put("processName", processName);
        // 流程标志
        processData.put("flag", flag);
        return JsonUtils.toJsonString(processData);
    }

    @Override
    public boolean removeProcessInstance(String processInstanceId) {
        processInstanceRecordService.remove(new QueryWrapper<ProcessInstanceRecord>().eq("process_instance_id", processInstanceId));
        List<ProcessInstanceAssignUserRecord> assignUserRecord = processInstanceAssignUserRecordService.list(new QueryWrapper<ProcessInstanceAssignUserRecord>().eq("process_instance_id", processInstanceId));
        processInstanceAssignUserRecordService.remove(new QueryWrapper<ProcessInstanceAssignUserRecord>().eq("process_instance_id", processInstanceId));
        for (ProcessInstanceAssignUserRecord processInstanceAssignUserRecord : assignUserRecord) {
            taskService.resolveTask(processInstanceAssignUserRecord.getTaskId());
            taskService.complete(processInstanceAssignUserRecord.getTaskId());
        }
        return true;
    }


    private List<FormItemVO> getFormAfterStartProcess(String flowId, String processId) {
        QueryFormListParamVo paramVo = new QueryFormListParamVo();
        paramVo.setFlowId(flowId);
        paramVo.setProcessInstanceId(processId);
        paramVo.setFrom("start");
        return formService.getFormList(paramVo).getData();
    }


    /**
     * 查询已办任务的流程实例
     *
     * @param pageVO
     * @return
     */
    @Override
    public R2 queryMineDoneProcessInstance(ProcessDataQueryVO pageVO) {

        List<String> flowIdList = pageVO.getFlowIdList();
        if (CollUtil.isNotEmpty(flowIdList)) {
            flowIdList = processService.getAllRelatedFlowId(flowIdList).getData();
        }

        ProcessQueryParamDto processQueryParamDto = BeanUtil.copyProperties(pageVO, ProcessQueryParamDto.class);
        processQueryParamDto.setAssign(LoginHelper.getUserId() + "");
        processQueryParamDto.setFlowIdList(flowIdList);
        R2<PageResultDto<ProcessInstanceDto>> r = CoreHttpUtil.queryCompletedProcessInstance(processQueryParamDto);

        PageResultDto<ProcessInstanceDto> pageResultDto = r.getData();
        List<ProcessInstanceDto> records = pageResultDto.getRecords();
        if (CollUtil.isEmpty(records)) {
            return R2.success(pageResultDto);

        }


        Set<String> processInstanceIdSet = records.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());

        //流程实例记录
        List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().in(ProcessInstanceRecord::getProcessInstanceId,
            processInstanceIdSet).list();

        //发起人
        Set<String> startUserIdSet =
            processInstanceRecordList.stream().map(w -> w.getUserId()).collect(Collectors.toSet());

        List<UserDto> startUserList = new ArrayList<>();
        {
            for (String userIds : startUserIdSet) {
                UserDto user = ApiStrategyFactory.getStrategy().getUser(userIds);
                startUserList.add(user);
            }
        }


        for (ProcessInstanceDto record : records) {

            ProcessInstanceRecord processInstanceRecord = processInstanceRecordList.stream().filter(w -> StrUtil.equals(w.getProcessInstanceId(),
                record.getProcessInstanceId())).findAny().orElse(null);

            if (processInstanceRecord != null) {

                record.setProcessName(processInstanceRecord.getName());


                UserDto startUser = startUserList.stream().filter(w -> w.getId()
                    .equals(processInstanceRecord.getUserId())).findAny().orElse(null);
                record.setGroupName(processInstanceRecord.getGroupName());
                record.setStartUserName(startUser.getName());
                record.setProcessInstanceResult(processInstanceRecord.getResult());
                record.setProcessInstanceStatus(processInstanceRecord.getStatus());
            }

        }

        return R2.success(pageResultDto);
    }

    /**
     * 流程结束
     *
     * @param processInstanceParamDto
     * @return
     */
    @Override
    public R2 processEndEvent(ProcessInstanceParamDto processInstanceParamDto) {
        processInstanceRecordService.lambdaUpdate()
            .set(ProcessInstanceRecord::getEndTime, new Date())
            .set(!processInstanceParamDto.getCancel(), ProcessInstanceRecord::getStatus,
                ProcessInstanceRecordStatusEnum.YJS.getCode())
            .set(processInstanceParamDto.getCancel(), ProcessInstanceRecord::getStatus, ProcessInstanceRecordStatusEnum.YCX.getCode())
            .set(processInstanceParamDto.getCancel(), ProcessInstanceRecord::getResult, ApproveResultEnum.CANCEL.getValue())
            .set(!processInstanceParamDto.getCancel(), ProcessInstanceRecord::getResult, processInstanceParamDto.getResult())
            .eq(ProcessInstanceRecord::getProcessInstanceId, processInstanceParamDto.getProcessInstanceId())
            .eq(ProcessInstanceRecord::getTenantId, processInstanceParamDto.getTenantId())
            .eq(ProcessInstanceRecord::getStatus, ProcessInstanceRecordStatusEnum.JXZ.getCode())
            .update(new ProcessInstanceRecord());


        return R2.success();
    }


    /**
     * 查询流程实例
     *
     * @param pageDto
     * @return
     */
    @Override
    public R2 queryList(ProcessDataQueryVO pageDto) {


        //查询所有的流程id
        List<String> allFlowIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(pageDto.getFlowIdList())) {
            List<String> data = processService.getAllRelatedFlowId(pageDto.getFlowIdList()).getData();
            allFlowIdList.addAll(data);
        }

        List<NodeUser> starterList = pageDto.getStarterList();
        List<String> startTime = pageDto.getStartTime();
        List<String> finishTime = pageDto.getFinishTime();
        Page<ProcessInstanceRecord> instanceRecordPage = processInstanceRecordService.lambdaQuery()
            .eq(ProcessInstanceRecord::getTenantId, TenantUtil.get())
            .in(CollUtil.isNotEmpty(allFlowIdList), ProcessInstanceRecord::getFlowId,
                allFlowIdList)
            .eq(pageDto.getStatus() != null, ProcessInstanceRecord::getStatus, pageDto.getStatus())
            .ge(CollUtil.isNotEmpty(startTime) && startTime.size() >= 2, ProcessInstanceRecord::getCreateTime,
                (CollUtil.isNotEmpty(startTime) && startTime.size() >= 2) ? (DateUtil.parseDate(startTime.get(0))) : null
            )
            .le(CollUtil.isNotEmpty(startTime) && startTime.size() >= 2, ProcessInstanceRecord::getCreateTime,
                (CollUtil.isNotEmpty(startTime) && startTime.size() >= 2) ? (DateUtil.endOfDay(DateUtil.parseDate(startTime.get(1)))) : null
            )
            .ge(CollUtil.isNotEmpty(finishTime) && finishTime.size() >= 2, ProcessInstanceRecord::getEndTime,
                (CollUtil.isNotEmpty(finishTime) && finishTime.size() >= 2) ? (DateUtil.parseDate(finishTime.get(0))) : null
            )
            .le(CollUtil.isNotEmpty(finishTime) && finishTime.size() >= 2, ProcessInstanceRecord::getEndTime,
                (CollUtil.isNotEmpty(finishTime) && finishTime.size() >= 2) ? (DateUtil.endOfDay(DateUtil.parseDate(finishTime.get(1)))) : null
            )
            .in(CollUtil.isNotEmpty(starterList), ProcessInstanceRecord::getUserId, starterList == null ? new ArrayList<>() : starterList.stream().map(w -> w.getId()).collect(Collectors.toList()))
            .eq(StrUtil.isNotBlank(pageDto.getProcessBizCode()), ProcessInstanceRecord::getProcessInstanceBizCode, pageDto.getProcessBizCode())
            .orderByDesc(ProcessInstanceRecord::getCreateTime)
            .page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()));

        List<ProcessInstanceRecord> records = instanceRecordPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return R2.success(instanceRecordPage);
        }


        //流程配置
        Set<String> flowIdSet = records.stream().map(w -> w.getFlowId()).collect(Collectors.toSet());
        List<Process> processList = processService.lambdaQuery()
            .in(Process::getFlowId, flowIdSet)
            .eq(Process::getTenantId, TenantUtil.get())
            .list();

        List<ProcessInstanceRecordVO> processInstanceRecordVOList = BeanUtil.copyToList(records, ProcessInstanceRecordVO.class);


        for (ProcessInstanceRecordVO record : processInstanceRecordVOList) {

            UserDto userDto = ApiStrategyFactory.getStrategy().getUser(record.getUserId());


            record.setFormData(null);
            record.setProcess(null);
            record.setRootUserAvatarUrl(userDto.getAvatarUrl());
            record.setRootUserName(userDto.getName());

        }
        Page page = BeanUtil.copyProperties(instanceRecordPage, Page.class);
        page.setRecords(processInstanceRecordVOList);


        return R2.success(page);
    }

    /**
     * 查询流程实例详情
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public R2 queryDetailByProcessInstanceId(String processInstanceId) {


        ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.lambdaQuery()
            .eq(ProcessInstanceRecord::getProcessInstanceId, processInstanceId)
            .eq(ProcessInstanceRecord::getTenantId, TenantUtil.get())
            .one();


        ProcessInstanceRecordVO record = BeanUtil.copyProperties(processInstanceRecord, ProcessInstanceRecordVO.class);


        //流程配置

        UserDto userDto = ApiStrategyFactory.getStrategy().getUser(processInstanceRecord.getUserId());


        record.setFormData(null);
        record.setProcess(null);
        record.setRootUserAvatarUrl(userDto.getAvatarUrl());
        record.setRootUserName(userDto.getName());
        record.setCancelEnable(false);


        return R2.success(record);
    }


    /**
     * 查询处理中的任务
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public R2 queryTaskListInProgress(String processInstanceId) {

        R2<List<TaskDto>> listR = CoreHttpUtil.queryTaskAssignee(null, processInstanceId);
        if (!listR.isOk()) {
            return listR;
        }
        List<TaskDto> taskDtoList = listR.getData();
        for (TaskDto taskDto : taskDtoList) {
            UserDto userDto = ApiStrategyFactory.getStrategy().getUser(taskDto.getAssign());
            taskDto.setUserName(userDto.getName());
        }

        return listR;
    }


}
