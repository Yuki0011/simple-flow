package com.sentury.approvalflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sentury.approvalflow.common.constants.OperTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskParamDto;
import com.sentury.approvalflow.common.dto.TaskResultDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.third.UserDto;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.ProcessInstanceNodeRecord;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.producer.NormalRabbitProducer;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class TaskServiceImpl implements ITaskService {
    @Resource
    private IFileService fileService;

    @Resource
    private IProcessService processService;
    @Resource
    private IProcessInstanceService processInstanceService;
    @Resource
    private IProcessInstanceNodeRecordService processInstanceNodeRecordService;
    @Resource
    private IProcessNodeDataService nodeDataService;
    @Resource
    private IProcessInstanceAssignUserRecordService processNodeRecordAssignUserService;
    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;
    @Resource
    private IProcessInstanceAssignUserRecordService processInstanceAssignUserRecordService;
    @Resource
    private IProcessInstanceOperRecordService processInstanceOperRecordService;


    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private NormalRabbitProducer normalRabbitProducer;


    /**
     * 完成任务
     *
     * @param taskParamDto
     * @return
     */
    @Transactional
    @Override
    public R2 completeTask(TaskParamDto taskParamDto) {
        //String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId()+"";
        taskParamDto.setUserId(userId);
        UserDto userDto = ApiStrategyFactory.getStrategy().getUser(userId);
        taskParamDto.setUserName(userDto.getName());

        //判断任务是否是合同 如果是合同 不能提交
        R2<TaskResultDto> re = CoreHttpUtil.queryTask(taskParamDto.getTaskId(), userId);
        if (!re.isOk()) {
            return R2.fail(re.getMsg());
        }
        TaskResultDto taskResultDto = re.getData();

        Map<String, Object> paramMap = taskParamDto.getParamMap();

        R2 r = CoreHttpUtil.completeTask(taskParamDto);

        if (!r.isOk()) {
            return R2.fail(r.getMsg());
        }

        //完成任务了 判断如果没有发起人部门id 修改主部门id 主要是子流程的问题
        String mainDeptId = MapUtil.getStr(paramMap, ProcessInstanceConstant.VariableKey.START_USER_MAIN_DEPTID_KEY);
        if (StrUtil.isNotBlank(mainDeptId)) {
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(taskResultDto.getProcessInstanceId());
            String mainDeptIdDb = processInstanceRecord.getMainDeptId();
            if (StrUtil.isBlank(mainDeptIdDb)) {
                processInstanceRecordService.lambdaUpdate()
                        .eq(ProcessInstanceRecord::getProcessInstanceId, taskResultDto.getProcessInstanceId())
                        .set(ProcessInstanceRecord::getMainDeptId,mainDeptId)
                        .update(new ProcessInstanceRecord());
            }
        }


        if (taskParamDto.getApproveResult()) {
            processInstanceOperRecordService.saveRecord(userId, taskParamDto, OperTypeEnum.PASS.getValue(), "提交任务");

            if(overProcess(taskResultDto.getProcessInstanceId())){
                // 审批通过->流程结束->发送消息给业务回调
                ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(taskResultDto.getProcessInstanceId());
                String message = processInstanceService.processInformationJson(processInstanceRecord.getFlowId(),processInstanceRecord.getProcessInstanceId(),processInstanceRecord.getName(),"结束");
                normalRabbitProducer.send(message);
            }
        } else {
            processInstanceOperRecordService.saveRecord(userId, taskParamDto, OperTypeEnum.REFUSE.getValue(), "提交任务");

            // 审批驳回->流程结束->发送消息给业务回调
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(taskResultDto.getProcessInstanceId());
            String message = processInstanceService.processInformationJson(processInstanceRecord.getFlowId(),processInstanceRecord.getProcessInstanceId(),processInstanceRecord.getName(),"驳回");
            normalRabbitProducer.send(message);
        }


        return R2.success();
    }

    // 根据流程实例id判断流程是否结束
    private boolean overProcess(String instanceId){
       return processInstanceNodeRecordService.count(new LambdaQueryWrapper<ProcessInstanceNodeRecord>()
            .eq(ProcessInstanceNodeRecord::getProcessInstanceId, instanceId)
            .eq(ProcessInstanceNodeRecord::getStatus,2)
            .eq(ProcessInstanceNodeRecord::getNodeName,"结束节点")) > 0 ;
    }


    /**
     * 获取任务信息
     *
     * @param taskId 任务id
     * @return
     */
    @Override
    public R2 getTask(String taskId) {
        String userId = LoginHelper.getUserId()+"";
        R2<TaskResultDto> r = CoreHttpUtil.queryTask(taskId, userId);
        if (!r.isOk()) {
            return R2.fail(r.getMsg());
        }
        TaskResultDto taskResultDto = r.getData();
        String flowId = taskResultDto.getFlowId();
        String nodeId = taskResultDto.getNodeId();

        Node node = nodeDataService.getNode(flowId, nodeId).getData();
        Dict set = Dict.create()
                .set("nodeType", node.getType())
                .set("processInstanceId", taskResultDto.getProcessInstanceId())
                .set("flowId", taskResultDto.getFlowId())
                .set("currentTask", taskResultDto.getCurrentTask());

        return R2.success(set);
    }
}
