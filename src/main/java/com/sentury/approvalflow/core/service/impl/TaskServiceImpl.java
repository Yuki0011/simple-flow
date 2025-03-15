package com.sentury.approvalflow.core.service.impl;

import com.sentury.approvalflow.common.constants.ApproveAttachmentTypeEnum;
import com.sentury.approvalflow.common.constants.ApproveDescTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.constants.TaskTypeEnum;
import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.UploadValue;
import com.sentury.approvalflow.common.service.core.ITaskService;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import com.sentury.approvalflow.core.vo.TaskCommentDto;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.FLOW_UNIQUE_ID;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-10-16 16:59
 */
@Component("coreTaskService")
@Slf4j
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskService taskService;
    @Resource
    private HistoryService historyService;



    @Resource
    private RuntimeService runtimeService;

    /**
     * 完成任务
     *
     * @param taskParamDto
     * @return
     */
    @Transactional
    @Override
    public R2 complete(TaskParamDto taskParamDto) {

        Task task =
                taskService.createTaskQuery()
                        .taskTenantId(TenantUtil.get())
                        .taskId(taskParamDto.getTaskId()).singleResult();
        if (task == null) {
            return R2.fail("任务不存在");
        }

        boolean approveResult = taskParamDto.getApproveResult();
        runtimeService.setVariableLocal(task.getExecutionId(), ProcessInstanceConstant.VariableKey.APPROVE_RESULT,
                approveResult);

        //非自动完成
        runtimeService.setVariableLocal(task.getExecutionId(),
                ProcessInstanceConstant.VariableKey.AUTO_COMPLETE_TASK, false);
        //保存任务类型
        taskService.setVariableLocal(task.getId(), ProcessInstanceConstant.VariableKey.TASK_TYPE,
                approveResult ? TaskTypeEnum.PASS.getValue() : TaskTypeEnum.REFUSE.getValue());
        //保存变量
        if (approveResult) {
            taskService.setVariableLocal(task.getId(), ProcessInstanceConstant.VariableKey.TASK_VARIABLES,
                    taskParamDto.getParamMap());
        }


        String descType = approveResult ? ApproveDescTypeEnum.PASS.getType() : ApproveDescTypeEnum.REFUSE.getType();
        String commentId = null;
        if (StrUtil.isNotBlank(taskParamDto.getApproveDesc())) {
            Comment comment = saveUserCommentToTask(descType,
                    taskParamDto.getApproveDesc(),
                    taskParamDto.getUserId(), "提交任务并添加了评论", task.getId(), task.getProcessInstanceId());
            commentId = comment.getId();
        } else {
            Comment comment = saveSysCommentToTask(descType, "提交任务", taskParamDto.getUserId(), task.getId(), task.getProcessInstanceId());
            commentId = comment.getId();

        }
        //保存图片和文件
        saveAttachment(taskParamDto, commentId, task.getId(), task.getProcessInstanceId());


        //通过才设置变量
        if (approveResult) {
            Map<String, Object> paramMap = taskParamDto.getParamMap();
            taskService.complete(task.getId(), paramMap);
        } else {
            taskService.complete(task.getId());

        }


        return R2.success();
    }



    private void saveAttachment(TaskParamDto taskParamDto, String commentId, String taskId, String processInstanceId) {

        log.info("保存附件的任务id：{}", taskId);
        List<UploadValue> approveImageList = taskParamDto.getApproveImageList();
        List<UploadValue> approveFileList = taskParamDto.getApproveFileList();
        if (CollUtil.isNotEmpty(approveImageList)) {
            for (UploadValue uploadValue : approveImageList) {
                taskService.createAttachment(ApproveAttachmentTypeEnum.IMAGE.getType(), taskId,
                        processInstanceId, uploadValue.getName()
                        , commentId, uploadValue.getUrl());


            }

        }
        if (CollUtil.isNotEmpty(approveFileList)) {
            for (UploadValue uploadValue : approveFileList) {
                taskService.createAttachment(ApproveAttachmentTypeEnum.FILE.getType(), taskId,
                        processInstanceId, uploadValue.getName()
                        , commentId, uploadValue.getUrl());

            }

        }
    }

    private Comment saveUserCommentToTask(String type, String desc, String userId, String descTitle, String taskId, String processInstanceId) {

        TaskCommentDto taskCommentDto = TaskCommentDto.builder().content(desc).title(descTitle).sys(false).userId(userId).build();
        Comment comment = taskService.addComment(taskId, processInstanceId,
                type, JsonUtil.toJSONString(taskCommentDto));
        return comment;


    }

    private Comment saveSysCommentToTask(String type, String desc, String userId, String taskId, String processInstanceId) {
        TaskCommentDto taskCommentDto = TaskCommentDto.builder().content(desc).sys(true).userId(userId).build();

        Comment comment = taskService.addComment(taskId, processInstanceId,
                type, JsonUtil.toJSONString(taskCommentDto));
        return comment;


    }

    /**
     * 查询任务
     *
     * @param taskId
     * @param userId
     * @return
     */
    @Override
    public R2 queryTask(String taskId, String userId) {


        DelegationState delegationState = null;


        //实例id
        String processInstanceId = null;
        Object delegateVariable = false;


        String processDefinitionId = null;

        //nodeid
        String taskDefinitionKey = null;
        String executionId = null;
        String flowUniqueId = null;
        String assignee = null;
        String nodeName = null;

        boolean taskExist = true;

        {
            TaskQuery taskQuery = taskService.createTaskQuery().taskTenantId(TenantUtil.get());

            Task task = taskQuery.taskId(taskId).singleResult();
            if (task == null) {
                HistoricTaskInstance historicTaskInstance =
                        historyService.createHistoricTaskInstanceQuery().taskTenantId(TenantUtil.get()).taskId(taskId).singleResult();
                if (historicTaskInstance == null) {
                    return R2.fail("任务不存在");
                }
                taskExist = false;
                taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
                nodeName = historicTaskInstance.getName();
                processInstanceId = historicTaskInstance.getProcessInstanceId();
                executionId = historicTaskInstance.getExecutionId();
                assignee = historicTaskInstance.getAssignee();
                processDefinitionId = historicTaskInstance.getProcessDefinitionId();
                HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .taskId(taskId)
                        .variableName(FLOW_UNIQUE_ID).singleResult();
                flowUniqueId = historicVariableInstance == null ? null : Convert.toStr(historicVariableInstance.getValue());
            } else {
                processDefinitionId = task.getProcessDefinitionId();
                taskDefinitionKey = task.getTaskDefinitionKey();
                delegationState = task.getDelegationState();
                processInstanceId = task.getProcessInstanceId();
                executionId = task.getExecutionId();
                nodeName = task.getName();
                assignee = task.getAssignee();
                delegateVariable = taskService.getVariableLocal(taskId, "delegate");

                flowUniqueId = taskService.getVariable(taskId, FLOW_UNIQUE_ID, String.class);

            }
        }


        //流程id
        String flowId = FlowableUtils.getFlowId(processDefinitionId, TenantUtil.get());

        Map<String, Object> variableAll = new HashMap<>();

        //表单处理


        if (taskExist) {


            Map<String, Object> variables = taskService.getVariables(taskId);
            variableAll.putAll(variables);

        }


        TaskResultDto taskResultDto = new TaskResultDto();
        taskResultDto.setFlowId(flowId);
        taskResultDto.setUserId(assignee);
        taskResultDto.setNodeId(taskDefinitionKey);
        taskResultDto.setNodeName(nodeName);
        taskResultDto.setCurrentTask(taskExist && StrUtil.equals(userId, assignee));
        taskResultDto.setExecutionId(executionId);
        taskResultDto.setDelegate(Convert.toBool(delegateVariable, false));
        taskResultDto.setVariableAll(variableAll);
        taskResultDto.setProcessInstanceId(processInstanceId);
        taskResultDto.setFrontJoinTask(delegationState == null ? false : StrUtil.equals(delegationState.toString(), ProcessInstanceConstant.VariableKey.PENDING));
        taskResultDto.setFlowUniqueId(flowUniqueId);

        return R2.success(taskResultDto);
    }

    /**
     * 查询任务评论
     *
     * @param paramDto
     * @return
     */
    @Override
    public R2 queryTaskComments(VariableQueryParamDto paramDto) {


        String taskId = paramDto.getTaskId();


        List<Comment> taskComments = new ArrayList<>();

        for (String s : ApproveDescTypeEnum.getTypeList()) {
            List<Comment> approveDescList = taskService.getTaskComments(taskId, s);
            taskComments.addAll(approveDescList);


        }

        //查询所有的附件
        List<Attachment> taskAttachments = taskService.getTaskAttachments(taskId);


        List<SimpleApproveDescDto> simpleApproveDescDtoList = new ArrayList<>();


        for (Comment comment : taskComments) {
            String id = comment.getId();
            Date time = comment.getTime();
            String fullMessage = comment.getFullMessage();
            TaskCommentDto taskCommentDto = JsonUtil.parseObject(fullMessage, TaskCommentDto.class);


            String userId = taskCommentDto.getUserId();
            Boolean isSys = taskCommentDto.getSys();


            SimpleApproveDescDto simpleApproveDescDto = new SimpleApproveDescDto();
            simpleApproveDescDto.setDate(time);
            simpleApproveDescDto.setMsgId(id);
            simpleApproveDescDto.setSys(isSys);
            simpleApproveDescDto.setUserId(userId);
            simpleApproveDescDto.setType(comment.getType());
            simpleApproveDescDto.setMessage(fullMessage);


            //图片文件
            {
                List<Attachment> collect = taskAttachments.stream()
                        .filter(w -> StrUtil.equals(w.getDescription(), id))
                        .filter(w -> StrUtil.equals(w.getType(), ApproveAttachmentTypeEnum.IMAGE.getType()))
                        .collect(Collectors.toList());
                List<UploadValue> approveImageList = new ArrayList<>();
                for (Attachment attachment : collect) {
                    UploadValue uploadValue = new UploadValue();
                    uploadValue.setUrl(attachment.getUrl());
                    uploadValue.setName(attachment.getName());
                    approveImageList.add(uploadValue);
                }
                simpleApproveDescDto.setApproveImageList(approveImageList);
            }

            {
                List<Attachment> collect = taskAttachments.stream()
                        .filter(w -> StrUtil.equals(w.getDescription(), id))
                        .filter(w -> StrUtil.equals(w.getType(), ApproveAttachmentTypeEnum.FILE.getType()))
                        .collect(Collectors.toList());
                List<UploadValue> approveImageList = new ArrayList<>();
                for (Attachment attachment : collect) {
                    UploadValue uploadValue = new UploadValue();
                    uploadValue.setUrl(attachment.getUrl());
                    uploadValue.setName(attachment.getName());
                    approveImageList.add(uploadValue);
                }
                simpleApproveDescDto.setApproveFileList(approveImageList);
            }



            simpleApproveDescDtoList.add(simpleApproveDescDto);


        }
        return R2.success(simpleApproveDescDtoList);
    }

    /**
     * 查询任务的执行人
     *
     * @param taskParamDto
     * @return
     */
    @Override
    public R2<List<TaskDto>> queryTaskAssignee(TaskParamDto taskParamDto) {

        TaskQuery taskQuery = taskService.createTaskQuery().taskTenantId(TenantUtil.get());
        if (StrUtil.isNotBlank(taskParamDto.getNodeId())) {
            taskQuery = taskQuery.taskDefinitionKey(taskParamDto.getNodeId());
        }
        List<Task> list =
                taskQuery.processInstanceId(taskParamDto.getProcessInstanceId()).list();


        List<TaskDto> taskDtoList = new ArrayList<>();
        for (Task task : list) {
            TaskDto taskDto = new TaskDto();
            taskDto.setAssign(task.getAssignee());
            taskDto.setExecutionId(task.getExecutionId());
            taskDto.setTaskId(task.getId());
            taskDto.setTaskName(task.getName());
            taskDto.setNodeId(task.getTaskDefinitionKey());
            taskDto.setProcessInstanceId(task.getProcessInstanceId());

            String processDefinitionId = task.getProcessDefinitionId();
            //流程id
            String flowId = FlowableUtils.getFlowId(processDefinitionId, TenantUtil.get());

            taskDto.setFlowId(flowId);
            taskDtoList.add(taskDto);
        }


        return R2.success(taskDtoList);
    }





}
