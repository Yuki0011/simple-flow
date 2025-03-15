package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.service.core.IFlowService;
import com.sentury.approvalflow.common.service.core.IProcessInstanceService;
import com.sentury.approvalflow.common.service.core.ITaskService;
import com.sentury.approvalflow.common.utils.HttpUtil;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class CoreHttpUtil {

    public static String getBaseUrl() {
        Environment environment = SpringUtil.getBean(Environment.class);
        String bizUrl = environment.getProperty("core.url");
        return bizUrl;
    }


    public static String post(Object object, String url, String tenantId) {

        String baseUrl = getBaseUrl();

        return HttpUtil.post(object, url, baseUrl, tenantId);


    }

    public static String get(String url) {
        return get(url, TenantUtil.get());


    }

    public static String get(String url, String tenantId) {

        String baseUrl = getBaseUrl();

        return HttpUtil.get(url, baseUrl, tenantId);


    }
    /**
     * 查询任务评论
     * 全部都是
     *
     * @param taskId
     * @return
     */
    public static R2<List<SimpleApproveDescDto>> queryTaskComments(String taskId) {
        VariableQueryParamDto variableQueryParamDto = new VariableQueryParamDto();
        variableQueryParamDto.setTaskId(taskId);

        ITaskService taskService = SpringUtil.getBean(ITaskService.class);
        return taskService.queryTaskComments(variableQueryParamDto);


    }


    /**
     * queryTaskAssignee
     *
     * @param nodeId
     * @param processInstanceId
     * @return
     */
    public static R2<List<TaskDto>> queryTaskAssignee(String nodeId, String processInstanceId) {
        TaskParamDto variableQueryParamDto = new TaskParamDto();
        variableQueryParamDto.setNodeId(nodeId);
        variableQueryParamDto.setProcessInstanceId(processInstanceId);

        ITaskService service = SpringUtil.getBean(ITaskService.class);
        return service.queryTaskAssignee(variableQueryParamDto);



    }

    /**
     * 查询流程变量
     * 全部都是
     *
     * @return
     */
    public static R2<IndexPageStatistics> querySimpleData(String userId, String tenantId) {
        IProcessInstanceService service = SpringUtil.getBean(IProcessInstanceService.class);

        return service.querySimpleData(userId);



    }

    /**
     * 查询流程表单数据
     *
     * @return
     */
    public static R2<Map<String, Object>> queryExecutionVariables(String executionId) {
        VariableQueryParamDto v = new VariableQueryParamDto();
        v.setExecutionId(executionId);

        IProcessInstanceService service = SpringUtil.getBean(IProcessInstanceService.class);

        return service.queryVariables(v);


    }

    /**
     * 创建流程
     *
     * @param node
     * @param processName
     * @param tenantId
     * @return
     */
    public static R2<String> createFlow(Node node, String userId, String processName, String tenantId) {


        CreateFlowDto createFlowDto = new CreateFlowDto();
        createFlowDto.setUserId(userId);
        createFlowDto.setNode(JsonUtil.parseObject(JsonUtil.toJSONString(node),Node.class));
        createFlowDto.setProcessName(processName);

        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.create(createFlowDto);



    }

    /**
     * 启动流程
     *
     * @param jsonObject
     * @return
     */
    public static R2<String> startProcess(ProcessInstanceParamDto jsonObject) {

        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.start(jsonObject);


    }



    /**
     * 清理所有的流程
     *
     * @param jsonObject
     * @return
     */
    public static R2 clearProcess(ClearProcessParamDto jsonObject) {


        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.clearProcess(jsonObject);

    }


    /**
     * 查询指派任务
     *
     * @param jsonObject
     * @return
     */
    public static R2<PageResultDto<TaskDto>> queryTodoTask(TaskQueryParamDto jsonObject) {


        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.queryTodoTask(jsonObject);



    }

    /**
     * 查询已办任务
     *
     * @param jsonObject
     * @return
     */
    public static R2<PageResultDto<TaskDto>> queryCompletedTask(TaskQueryParamDto jsonObject) {

        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.queryCompletedTask(jsonObject);

    }

    /**
     * 查询已办任务的流程实例
     *
     * @param jsonObject
     * @return
     */
    public static R2<PageResultDto<ProcessInstanceDto>> queryCompletedProcessInstance(ProcessQueryParamDto jsonObject) {

        IFlowService service = SpringUtil.getBean(IFlowService.class);

        return service.queryCompletedProcessInstance(jsonObject);


    }

    /**
     * 完成任务
     *
     * @param jsonObject
     * @return
     */
    public static R2 completeTask(TaskParamDto jsonObject) {

        ITaskService taskService = SpringUtil.getBean(ITaskService.class);
        return taskService.complete(jsonObject);


    }


    /**
     * 查询任务
     *
     * @param taskId
     * @param userId
     * @return
     */
    public static R2<TaskResultDto> queryTask(String taskId, String userId) {

        ITaskService taskService = SpringUtil.getBean(ITaskService.class);
        return taskService.queryTask(taskId,userId);


    }





}
