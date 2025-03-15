package com.sentury.approvalflow.core.service.impl;

import com.sentury.approvalflow.common.dto.IndexPageStatistics;
import com.sentury.approvalflow.common.dto.ProcessInstanceParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.VariableQueryParamDto;
import com.sentury.approvalflow.common.service.core.IProcessInstanceService;
import com.sentury.approvalflow.common.utils.TenantUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-11-08 17:30
 */
@Component("coreProcessInstanceService")
public class ProcessInstanceServiceImpl implements IProcessInstanceService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;


    @Resource
    private HistoryService historyService;

    /**
     * 删除流程
     *
     * @param processInstanceParamDto
     * @return
     */
    @Override
    public R2 delete(ProcessInstanceParamDto processInstanceParamDto) {

        List<String> processInstanceIdList = processInstanceParamDto.getProcessInstanceIdList();
        for (String processInstanceId : processInstanceIdList) {
            if (runtimeService.createProcessInstanceQuery().processInstanceTenantId(TenantUtil.get()).processInstanceId(processInstanceId).count() > 0) {
                runtimeService.deleteProcessInstance(processInstanceId, processInstanceParamDto.getReason());
            }
        }


        return R2.success();
    }

    /**
     * 查询统计数据
     *
     * @param userId
     * @return
     */
    @Override
    public R2<IndexPageStatistics> querySimpleData(String userId) {
        String tenantId = TenantUtil.get();
        TaskQuery taskQuery = taskService.createTaskQuery().taskTenantId(tenantId);

        //待办数量
        long pendingNum = taskQuery.taskAssignee((userId)).count();
        //已完成任务
        HistoricActivityInstanceQuery historicActivityInstanceQuery =
                historyService.createHistoricActivityInstanceQuery().activityTenantId(tenantId);

        long completedNum = historicActivityInstanceQuery.taskAssignee(String.valueOf(userId)).finished().count();


        IndexPageStatistics indexPageStatistics = IndexPageStatistics.builder().pendingNum(pendingNum).completedNum(completedNum).build();

        return R2.success(indexPageStatistics);
    }

    /**
     * 查询变量
     *
     * @param paramDto
     * @return
     */
    @Override
    public R2<Map<String, Object>> queryVariables(VariableQueryParamDto paramDto) {
        long count =
                runtimeService.createProcessInstanceQuery().processInstanceTenantId(TenantUtil.get()).processInstanceId(paramDto.getExecutionId()).count();
        if(count>0){

            Map<String, Object> variables = runtimeService.getVariables(paramDto.getExecutionId());


            return R2.success(variables);
        }

        List<HistoricVariableInstance> list =
                historyService.createHistoricVariableInstanceQuery().processInstanceId(paramDto.getExecutionId()).list();

        Map<String, Object> variables=new HashMap<>();
        for (HistoricVariableInstance historicVariableInstance : list) {
            variables.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
        }

        return R2.success(variables);
    }
}
