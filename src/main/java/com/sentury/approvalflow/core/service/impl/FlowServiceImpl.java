package com.sentury.approvalflow.core.service.impl;

import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.service.core.IFlowService;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import com.sentury.approvalflow.core.utils.ModelUtil;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;
import com.sentury.approvalflow.service.IFormService;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.system.api.RemoteUserService;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.ENABLE_SKIP_EXPRESSION;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-08-04 16:40
 */
@Component("coreFlowService")
@Slf4j
public class FlowServiceImpl implements IFlowService {
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;


    @Resource
    private RuntimeService runtimeService;

    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;



    /**
     * 创建流程模型
     *
     * @param createFlowDto
     * @return
     */
    @Transactional
    @Override
    public R2 create(CreateFlowDto createFlowDto) {
        String flowId = "p" + RandomUtil.randomString(6) + System.currentTimeMillis();


        log.info("flowId={}", flowId);
        BpmnModel bpmnModel = ModelUtil.buildBpmnModel(createFlowDto.getNode(), createFlowDto.getProcessName(), flowId);
        {
            byte[] bpmnBytess = new BpmnXMLConverter().convertToXML(bpmnModel);
            String filename = "/tmp/flowable-deployment/" + flowId + ".bpmn20.xml";
            log.debug("部署时的模型文件：{}", filename);
            FileUtil.writeBytes(bpmnBytess, filename);
        }
        repositoryService.createDeployment()
            .tenantId(TenantUtil.get())
            .addBpmnModel(StrUtil.format("{}.bpmn20.xml", flowId), bpmnModel).deploy();


        return R2.success(flowId);
    }

    /**
     * 发起流程
     *
     * @param processInstanceParamDto
     * @return
     */
    @Transactional
    @Override
    public R2 start(ProcessInstanceParamDto processInstanceParamDto) {
        String flowId = processInstanceParamDto.getFlowId();
        {
            //前置检查
            R2 r = frontCheck(processInstanceParamDto);
            if (!r.isOk()) {
                return r;
            }
        }
        Authentication.setAuthenticatedUserId(processInstanceParamDto.getStartUserId());
        Map<String, Object> paramMap = processInstanceParamDto.getParamMap();
        //支持自动跳过
        paramMap.put(ENABLE_SKIP_EXPRESSION, true);


        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(flowId,
            processInstanceParamDto.getBizKey(),
            paramMap, TenantUtil.get());

        String processInstanceId = processInstance.getProcessInstanceId();
        return R2.success(processInstanceId);
    }

    /**
     * 前置检查
     *
     * @param processInstanceParamDto
     * @return
     */
    private R2 frontCheck(ProcessInstanceParamDto processInstanceParamDto) {

        return R2.success();
    }


    /**
     * 清理所有的流程
     *
     * @param clearProcessParamDto 清理数据对象
     * @return 成功失败
     */
    @Transactional
    @Override
    public R2 clearProcess(ClearProcessParamDto clearProcessParamDto) {
        String tenantId = TenantUtil.get();
        List<String> flowIdList = clearProcessParamDto.getFlowIdList();
        String userName = clearProcessParamDto.getUserName();
        String userId = clearProcessParamDto.getUserId();

        //清理流程
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery()
            .processDefinitionKeys(CollUtil.newHashSet(flowIdList))
            .processInstanceTenantId(tenantId)
            .list();
        if (CollUtil.isNotEmpty(processInstanceList)) {
            Set<String> processInstanceIdSet = processInstanceList.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());
            runtimeService.bulkDeleteProcessInstances(processInstanceIdSet, StrUtil.format("[{}]({})清理流程", userName,
                userId));
        }
        //清理历史流程
        historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKeyIn(flowIdList)
            .processInstanceTenantId(tenantId)
            .list()
            .forEach(w -> {
                historyService.deleteHistoricProcessInstance(w.getId());
            });


        return R2.success();
    }

    /**
     * 查询用户待办任务
     *
     * @param taskQueryParamDto
     * @return
     */
    @Override
    public R2<PageResultDto<TaskDto>> queryTodoTask(TaskQueryParamDto taskQueryParamDto) {

        String processName = taskQueryParamDto.getProcessName();
        TaskQuery taskQuery = taskService.createTaskQuery().taskTenantId(TenantUtil.get());
        if(StringUtils.isNotBlank(processName)) {
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().select(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName).eq(ProcessInstanceRecord::getName, processName).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();
            List<String> processInstanceRecordIds = processInstanceRecordList.stream().map(ProcessInstanceRecord::getProcessInstanceId).toList();
            taskQuery.processInstanceIdIn(processInstanceRecordIds);
        }
        String assign = taskQueryParamDto.getAssign();



        List<Task> tasks = taskQuery.taskAssignee(assign).orderByTaskCreateTime().desc().listPage((taskQueryParamDto.getPageNum() - 1) * taskQueryParamDto.getPageSize(),
            taskQueryParamDto.getPageSize());
        long count = taskQuery.taskAssignee(assign).count();

        List<TaskDto> taskDtoList = new ArrayList<>();
        log.debug("当前有" + count + " 个任务:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String taskId = task.getId();
            String processInstanceId = task.getProcessInstanceId();
            log.debug((i + 1) + ") (" + taskId + ") " + task.getName() + " processInstanceId={} executrionId={}",
                processInstanceId, task.getExecutionId());

            Map<String, Object> taskServiceVariables = taskService.getVariables(task.getId());
            log.debug("任务变量:{}", JSONUtil.toJsonStr(taskServiceVariables));

            //nodeid
            String taskDefinitionKey = task.getTaskDefinitionKey();

            String processDefinitionId = task.getProcessDefinitionId();
            //流程id
            String flowId = FlowableUtils.getFlowId(processDefinitionId, TenantUtil.get());


            TaskDto taskDto = new TaskDto();
            taskDto.setFlowId(flowId);
            taskDto.setTaskCreateTime(task.getCreateTime());
            taskDto.setNodeId(taskDefinitionKey);
            taskDto.setParamMap(taskServiceVariables);
            taskDto.setProcessInstanceId(processInstanceId);
            taskDto.setTaskId(taskId);
            taskDto.setAssign(task.getAssignee());
            taskDto.setTaskName(task.getName());
            taskDtoList.add(taskDto);

        }

        PageResultDto<TaskDto> pageResultDto = new PageResultDto<>();
        pageResultDto.setTotal(count);
        pageResultDto.setRecords(taskDtoList);


        return R2.success(pageResultDto);
    }

    /**
     * 查询用户已办任务
     *
     * @param taskQueryParamDto
     * @return
     */
    @Override
    public R2<PageResultDto<TaskDto>> queryCompletedTask(TaskQueryParamDto taskQueryParamDto) {
        String processName = taskQueryParamDto.getProcessName();
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().taskTenantId(TenantUtil.get());
        if(StringUtils.isNotBlank(processName)) {
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().select(ProcessInstanceRecord::getProcessInstanceId, ProcessInstanceRecord::getName).eq(ProcessInstanceRecord::getName, processName).in(ProcessInstanceRecord::getTenantId, TenantUtil.get()).list();
            List<String> processInstanceRecordIds = processInstanceRecordList.stream().map(ProcessInstanceRecord::getProcessInstanceId).toList();
            historicTaskInstanceQuery.processInstanceIdIn(processInstanceRecordIds);
        }
        List<HistoricTaskInstance> list = historicTaskInstanceQuery
            .taskAssignee(taskQueryParamDto.getAssign())
            .taskWithoutDeleteReason()
            .finished()
            .orderByHistoricTaskInstanceEndTime().desc()
            .listPage((taskQueryParamDto.getPageNum() - 1) * taskQueryParamDto.getPageSize(),
                taskQueryParamDto.getPageSize());

        long count = historicTaskInstanceQuery.taskAssignee(taskQueryParamDto.getAssign()).taskWithoutDeleteReason().finished().count();
        List<TaskDto> taskDtoList = new ArrayList<>();


        for (HistoricTaskInstance instance : list) {
            String activityId = instance.getTaskDefinitionKey();
            String activityName = instance.getName();
            String executionId = instance.getExecutionId();
            String taskId = instance.getId();
            Date startTime = instance.getStartTime();
            Date endTime = instance.getEndTime();
            Long durationInMillis = instance.getDurationInMillis();
            String processInstanceId = instance.getProcessInstanceId();

            String processDefinitionId = instance.getProcessDefinitionId();
            //流程id
            String flowId = FlowableUtils.getFlowId(processDefinitionId, TenantUtil.get());

            TaskDto taskDto = new TaskDto();
            taskDto.setFlowId(flowId);
            taskDto.setTaskCreateTime(startTime);
            taskDto.setTaskEndTime(endTime);
            taskDto.setNodeId(activityId);
            taskDto.setExecutionId(executionId);
            taskDto.setProcessInstanceId(processInstanceId);
            taskDto.setDurationInMillis(durationInMillis);
            taskDto.setTaskId(taskId);
            taskDto.setAssign(instance.getAssignee());
            taskDto.setTaskName(activityName);
            QueryFormListParamVo paramVo = new QueryFormListParamVo();
            paramVo.setTaskId(taskId);
            Map<String, Object> map = CoreHttpUtil.queryExecutionVariables(processInstanceId).getData();
            taskDto.setParamMap(map);
            taskDtoList.add(taskDto);
        }
        PageResultDto<TaskDto> pageResultDto = new PageResultDto<>();
        pageResultDto.setTotal(count);
        pageResultDto.setRecords(taskDtoList);


        return R2.success(pageResultDto);
    }

    /**
     * 获取我已办的流程实例
     *
     * @param processQueryParamDto
     * @return
     */
    @Override
    public R2<PageResultDto<ProcessInstanceDto>> queryCompletedProcessInstance(ProcessQueryParamDto processQueryParamDto) {
        HistoricProcessInstanceQuery historicProcessInstanceQuery =
            historyService.createHistoricProcessInstanceQuery().processInstanceTenantId(TenantUtil.get());

        if (CollUtil.isNotEmpty(processQueryParamDto.getFlowIdList())) {
            historicProcessInstanceQuery = historicProcessInstanceQuery.processDefinitionKeyIn(processQueryParamDto.getFlowIdList());
        }

        List<HistoricProcessInstance> list = historicProcessInstanceQuery

            .involvedUser(processQueryParamDto.getAssign())
            .orderByProcessInstanceStartTime().desc()
            .listPage((processQueryParamDto.getPageNum() - 1) * processQueryParamDto.getPageSize(),
                processQueryParamDto.getPageSize());

        long count = historicProcessInstanceQuery

            .involvedUser(processQueryParamDto.getAssign())
            .count();


        List<ProcessInstanceDto> processInstanceParamDtoList = new ArrayList<>();

        for (HistoricProcessInstance historicProcessInstance : list) {

            HistoricProcessInstanceEntityImpl historicProcessInstanceEntity = (HistoricProcessInstanceEntityImpl) historicProcessInstance;
            String processInstanceId = historicProcessInstanceEntity.getProcessInstanceId();
            String flowId = historicProcessInstanceEntity.getProcessDefinitionKey();
            String processName = historicProcessInstanceEntity.getProcessDefinitionName();

            ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
            processInstanceDto.setProcessInstanceId(processInstanceId);
            processInstanceDto.setFlowId(flowId);
            processInstanceDto.setProcessName(processName);
            processInstanceDto.setStartUserId(historicProcessInstance.getStartUserId());
            processInstanceDto.setStartTime(historicProcessInstance.getStartTime());
            processInstanceDto.setEndTime(historicProcessInstance.getEndTime());
            processInstanceParamDtoList.add(processInstanceDto);


        }
        PageResultDto<ProcessInstanceDto> pageResultDto = new PageResultDto<>();
        pageResultDto.setTotal(count);
        pageResultDto.setRecords(processInstanceParamDtoList);


        return R2.success(pageResultDto);
    }
}
