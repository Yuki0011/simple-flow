package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.constants.OperTypeEnum;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskParamDto;
import com.sentury.approvalflow.common.dto.TaskResultDto;
import com.sentury.approvalflow.common.dto.flow.UploadValue;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.ProcessInstanceOperRecord;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.mapper.ProcessInstanceOperRecordMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessInstanceOperRecordService;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-11-03 17:46
 */
@Service
@Slf4j
public class ProcessInstanceOperRecordServiceImpl extends ServiceImpl<ProcessInstanceOperRecordMapper, ProcessInstanceOperRecord>
        implements IProcessInstanceOperRecordService, IClearService {

    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;

    /**
     * 保存记录
     *
     * @param userId
     * @param taskParamDto
     * @param operType
     * @param desc
     * @return
     */
    @Override
    public R2 saveRecord(String userId, TaskParamDto taskParamDto, String operType, String desc) {
        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);

        ProcessInstanceOperRecord processInstanceOperRecord = new ProcessInstanceOperRecord();
        processInstanceOperRecord.setUserId(userId);
        processInstanceOperRecord.setProcessInstanceId(taskParamDto.getProcessInstanceId());
        processInstanceOperRecord.setComment(taskParamDto.getApproveDesc());

        R2<TaskResultDto> taskResultDtoR = CoreHttpUtil.queryTask(taskParamDto.getTaskId(), null);
        if (!taskResultDtoR.isOk()) {
            return taskResultDtoR;
        }
        TaskResultDto taskResultDto = taskResultDtoR.getData();

        processInstanceOperRecord.setNodeId(taskResultDto.getNodeId());
        processInstanceOperRecord.setNodeName(taskResultDto.getNodeName());
        processInstanceOperRecord.setFlowId(taskResultDto.getFlowId());

        List<UploadValue> approveImageList = taskParamDto.getApproveImageList();
        List<UploadValue> approveFileList = taskParamDto.getApproveFileList();


        processInstanceOperRecord.setImageList(JsonUtil.toJSONString(approveImageList));
        processInstanceOperRecord.setFileList(JsonUtil.toJSONString(approveFileList));

        processInstanceOperRecord.setOperType(operType);
        processInstanceOperRecord.setTenantId(TenantUtil.get());
        processInstanceOperRecord.setOperDesc(StrUtil.format("{}[{}] / {} / {} / {} / {} / {} ", user.getName(),
                userId,
                taskResultDto.getNodeName(), DateUtil.formatDateTime(new Date()), desc,
                OperTypeEnum.getByValue(operType).getName(), StrUtil.blankToDefault(taskParamDto.getApproveDesc(),"")

        ));


        this.save(processInstanceOperRecord);
        return R2.success();
    }

    /**
     * 撤销流程
     *
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    public R2 saveCancelProcessRecord(String userId, String processInstanceId) {



        ProcessInstanceRecord processInstanceRecord =
                processInstanceRecordService.getByProcessInstanceId(processInstanceId);


        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);

        ProcessInstanceOperRecord processInstanceOperRecord = new ProcessInstanceOperRecord();
        processInstanceOperRecord.setUserId(userId);
        processInstanceOperRecord.setTenantId(TenantUtil.get());
        processInstanceOperRecord.setProcessInstanceId(processInstanceId);
        processInstanceOperRecord.setFlowId(processInstanceRecord.getFlowId());


        processInstanceOperRecord.setOperType(OperTypeEnum.CANCEL.getValue());
        processInstanceOperRecord.setOperDesc(StrUtil.format("{}[{}] / {} / {} / {} / {} / {}", user.getName(),
                userId,
                "", DateUtil.formatDateTime(new Date()), "撤销流程",
                OperTypeEnum.CANCEL.getName(), ""

        ));


        this.save(processInstanceOperRecord);
        return R2.success();
    }

    /**
     * 发起流程
     *
     * @param userId
     * @param processInstanceId
     * @param flowId
     * @return
     */
    @Override
    public R2 saveStartProcessRecord(String userId, String processInstanceId, String flowId) {

        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);

        ProcessInstanceOperRecord processInstanceOperRecord = new ProcessInstanceOperRecord();
        processInstanceOperRecord.setUserId(userId);
        processInstanceOperRecord.setProcessInstanceId(processInstanceId);
        processInstanceOperRecord.setFlowId(flowId);
        processInstanceOperRecord.setTenantId(TenantUtil.get());
        processInstanceOperRecord.setNodeName("开始");


        processInstanceOperRecord.setOperType(OperTypeEnum.START.getValue());
        processInstanceOperRecord.setOperDesc(StrUtil.format("{}[{}] / {} / {} / {} / {} / {}", user.getName(),
                userId,
                "开始节点", DateUtil.formatDateTime(new Date()), "发起流程",
                OperTypeEnum.START.getName(), ""

        ));


        this.save(processInstanceOperRecord);
        return R2.success();
    }

    /**
     * 清理数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId      租户id
     */
    @Override
    public void clearProcess(String uniqueId, List<String> flowIdList, List<Long> processIdList, String tenantId) {

        this.lambdaUpdate()
                .in(ProcessInstanceOperRecord::getFlowId, flowIdList)
                .eq(ProcessInstanceOperRecord::getTenantId, tenantId)
                .remove();
    }
}
