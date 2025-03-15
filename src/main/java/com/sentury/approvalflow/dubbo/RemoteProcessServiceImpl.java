package com.sentury.approvalflow.dubbo;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sentury.approval.api.RemoteProcessService;
import com.sentury.approval.api.domain.enums.ProcessTypeEnum;
import com.sentury.approval.api.domain.vo.RemoteNodeFormatResultVo;
import com.sentury.approval.api.domain.vo.RemoteNodeFormatUserVo;
import com.sentury.approval.api.domain.vo.RemoteNodeShowVo;
import com.sentury.approval.api.domain.vo.RemoteProcessFormatNodeApproveDescVo;
import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.utils.TenantUtil;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.entity.ProcessForm;
import com.sentury.approvalflow.domain.entity.ProcessInstanceAssignUserRecord;
import com.sentury.approvalflow.domain.vo.NodeFormatParamVo;
import com.sentury.approvalflow.domain.vo.NodeFormatResultVo;
import com.sentury.approvalflow.domain.vo.ProcessFormatNodeApproveDescVo;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import com.sentury.approvalflow.dubbo.dto.FormItemDto;
import com.sentury.approvalflow.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 客户端服务
 *
 * @author xutao
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteProcessServiceImpl implements RemoteProcessService {

    private final IProcessInstanceService processInstanceService;

    private final IProcessFormService processFormService;

    private final IProcessService processService;

    private final IBaseService baseService;

    private final IProcessInstanceAssignUserRecordService processInstanceAssignUserRecordService;

    private final ICombinationGroupService combinationGroupService;

    private final ITaskService taskService;

    private final IFormService formService;


    @Override
    public String startProcessInstanceWithDefaultFormWithOutLogin(ProcessTypeEnum processType, Map<String, Object> data, String userId, String deptId) {
        // 通过名称获取流程
        Process process = this.getProcessByName(processType.getDesc());
        String formItems = process.getFormItems();
        List<FormItemDto> formItemDtos = JsonUtils.parseArray(formItems, FormItemDto.class);
        Map<String, String> valueFiledRelFormId = formItemDtos.stream().collect(Collectors.toMap(FormItemDto::getValueFiled, FormItemDto::getId));
        Map<String, Object> formData = new HashMap<>();
        for (Map.Entry<String, String> entry : valueFiledRelFormId.entrySet()) {
            String valueFiled = entry.getKey();
            String formId = entry.getValue();
            formData.put(formId, data.get(valueFiled));
        }
        // 组装参数
        ProcessInstanceParamDto processInstanceParamDto = this.buildParamWithDefault(process, formData);
        processInstanceParamDto.getParamMap().put("startUserMainDeptId", deptId);
        // 发起流程，返回流程实例ID
        R2 result = processInstanceService.startProcessInstanceWithOutLogin(processInstanceParamDto, userId);
        return result.getData().toString();
    }

    @Override
    public String startProcessInstanceWithDefaultForm(ProcessTypeEnum processType, Map<String, Object> data) {
        // 通过名称获取流程
        Process process = this.getProcessByName(processType.getDesc());
        String formItems = process.getFormItems();
        List<FormItemDto> formItemDtos = JsonUtils.parseArray(formItems, FormItemDto.class);
        Map<String, String> valueFiledRelFormId = formItemDtos.stream().collect(Collectors.toMap(FormItemDto::getValueFiled, FormItemDto::getId));
        Map<String, Object> formData = new HashMap<>();
        for (Map.Entry<String, String> entry : valueFiledRelFormId.entrySet()) {
            String valueFiled = entry.getKey();
            String formId = entry.getValue();
            formData.put(formId, data.get(valueFiled));
        }
        // 组装参数
        ProcessInstanceParamDto processInstanceParamDto = this.buildParamWithDefault(process, formData);
        // 发起流程，返回流程实例ID
        R2 result = processInstanceService.startProcessInstance(processInstanceParamDto);
        return result.getData().toString();
    }

    @Override
    public String startProcessInstance(ProcessTypeEnum processType) {
        // 通过名称获取流程
        Process process = this.getProcessByName(processType.getDesc());
        // 组装参数
        ProcessInstanceParamDto processInstanceParamDto = this.buildParam(process);
        // 发起流程，返回流程实例ID
        R2 result = processInstanceService.startProcessInstance(processInstanceParamDto);
        return result.getData().toString();
    }

    @Override
    public RemoteNodeFormatResultVo formatStartNodeShow(String processInstanceId) {
        RemoteNodeFormatResultVo remoteNodeFormatResultVo = new RemoteNodeFormatResultVo();
        NodeFormatResultVo nodeFormatResultVo = this.getNodeFormatResultVo(processInstanceId);
        List<NodeShowVo> processNodeShowDtoList = nodeFormatResultVo.getProcessNodeShowDtoList();
        List<RemoteNodeShowVo> remoteNodeShowVoList = new ArrayList<>();
        for (NodeShowVo nodeShowVo : processNodeShowDtoList) {
            RemoteNodeShowVo remoteNodeShowVo = new RemoteNodeShowVo();
            BeanUtils.copyProperties(nodeShowVo, remoteNodeShowVo);
            List<RemoteProcessFormatNodeApproveDescVo> approveDescVos = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(nodeShowVo.getApproveDescList())) {
                for (ProcessFormatNodeApproveDescVo s : nodeShowVo.getApproveDescList()) {
                    RemoteProcessFormatNodeApproveDescVo approveDescVo = new RemoteProcessFormatNodeApproveDescVo();
                    BeanUtils.copyProperties(s, approveDescVo);
                    RemoteNodeFormatUserVo userVo = new RemoteNodeFormatUserVo();
                    if (s.getUser() != null) {
                        NodeFormatUserVo user = s.getUser();
                        userVo.setName(user.getName());
                        userVo.setId(user.getId());
                    }
                    approveDescVo.setUser(userVo);
                    approveDescVos.add(approveDescVo);
                    remoteNodeShowVo.setApproveDescList(approveDescVos);
                }
            }
            List<RemoteNodeFormatUserVo> userVoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(nodeShowVo.getUserVoList())) {
                for (NodeFormatUserVo s : nodeShowVo.getUserVoList()) {
                    RemoteNodeFormatUserVo userVo = new RemoteNodeFormatUserVo();
                    BeanUtils.copyProperties(s, userVo);
                    userVoList.add(userVo);
                }
            }
            remoteNodeShowVo.setUserVoList(userVoList);
            remoteNodeShowVoList.add(remoteNodeShowVo);
        }
        remoteNodeFormatResultVo.setProcessNodeShowDtoList(remoteNodeShowVoList);
        remoteNodeFormatResultVo.setSelectUserNodeIdList(nodeFormatResultVo.getSelectUserNodeIdList());
        remoteNodeFormatResultVo.setDisableSelectUser(nodeFormatResultVo.getDisableSelectUser());
        return remoteNodeFormatResultVo;
    }

    private NodeFormatResultVo getNodeFormatResultVo(String processInstanceId) {
        ProcessInstanceAssignUserRecord processInstanceAssignUserRecord = this.getLastRecord(processInstanceId);
        NodeFormatParamVo paramVo = new NodeFormatParamVo();
        paramVo.setFlowId(processInstanceAssignUserRecord.getFlowId());
        paramVo.setTaskId(processInstanceAssignUserRecord.getTaskId());
        paramVo.setProcessInstanceId(processInstanceId);
        paramVo.setParamMap(this.buildForm(processInstanceAssignUserRecord.getFlowId()));
        QueryFormListParamVo paramVo1 = new QueryFormListParamVo();
        paramVo1.setProcessInstanceId(processInstanceAssignUserRecord.getProcessInstanceId());
        paramVo1.setFlowId(processInstanceAssignUserRecord.getFlowId());
        paramVo1.setTaskId(processInstanceAssignUserRecord.getTaskId());
//        Dict set = (Dict) formService.getFormDetail(paramVo1).getData();
//        Map<String,Object> formData = new HashMap<>();
//        if(set != null){
//            List<FormItemVO> formItemVOList =  (List<FormItemVO>)set.get("formList");
//            for (FormItemVO formItemVO : formItemVOList) {
//                formData.put(formItemVO.getId(),formItemVO.getProps().getValue());
//            }
//        }
//        paramVo.setParamMap(formData);
        R2<NodeFormatResultVo> nodeFormatResultVoR2 = baseService.formatStartNodeShow(paramVo);
        return nodeFormatResultVoR2.getData();
    }

    /**
     * 获取最近操作节点记录
     */
    public ProcessInstanceAssignUserRecord getLastRecord(String processInstanceId) {
        List<ProcessInstanceAssignUserRecord> list = processInstanceAssignUserRecordService.lambdaQuery().eq(ProcessInstanceAssignUserRecord::getProcessInstanceId, processInstanceId).orderByDesc(ProcessInstanceAssignUserRecord::getCreateTime).list();
        Assert.notEmpty(list, "无操作节点记录，请确认！");
        return list.get(0);
    }

    @Override
    public Boolean ifTodoTask(String processInstanceId) {
        PageDto pageDto = new PageDto();
        pageDto.setPageNum(1);
        pageDto.setPageSize(1000);
        R2 r2 = combinationGroupService.queryTodoTaskList(pageDto);
        PageResultDto<TaskDto> page = (PageResultDto<TaskDto>) r2.getData();
        List<TaskDto> records = page.getRecords();
        for (TaskDto record : records) {
            if (record.getProcessInstanceId().equals(processInstanceId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean completeTask(String processInstanceId, String approveDesc, Boolean approveResult) {
        ProcessInstanceAssignUserRecord lastRecord = getLastRecord(processInstanceId);
        TaskParamDto taskParamDto = new TaskParamDto();
        taskParamDto.setApproveDesc(approveDesc);
        taskParamDto.setApproveResult(approveResult);
        taskParamDto.setParamMap(this.buildForm(lastRecord.getFlowId()));
        taskParamDto.setProcessInstanceId(processInstanceId);
        taskParamDto.setTaskId(lastRecord.getTaskId());
        R2 r2 = taskService.completeTask(taskParamDto);
        return r2.isOk();
    }

    @Override
    public Boolean ifProcessOver(String processInstanceId) {
        NodeFormatResultVo nodeFormatResultVo = this.getNodeFormatResultVo(processInstanceId);
        List<NodeShowVo> processNodeShowDtoList = nodeFormatResultVo.getProcessNodeShowDtoList();
        return processNodeShowDtoList.get(processNodeShowDtoList.size() - 1).getStatus() == 2;
    }

    @Override
    public Boolean removeProcess(String processInstanceId) {
        return processInstanceService.removeProcessInstance(processInstanceId);
    }


    private Process getProcessByName(String processName) {
        // 通过名称查询对应流程
        com.sentury.approvalflow.domain.entity.Process process = processService.lambdaQuery().eq(com.sentury.approvalflow.domain.entity.Process::getTenantId, TenantUtil.get()).eq(com.sentury.approvalflow.domain.entity.Process::getHidden, false).eq(com.sentury.approvalflow.domain.entity.Process::getStop, false).eq(com.sentury.approvalflow.domain.entity.Process::getName, processName).one();
        Assert.notNull(process, processName + "流程不存在，请确认！");
        return process;
    }

    private ProcessInstanceParamDto buildParam(Process process) {
        ProcessInstanceParamDto processInstanceParamDto = new ProcessInstanceParamDto();
        processInstanceParamDto.setFlowId(process.getFlowId());
        processInstanceParamDto.setUniqueId(process.getUniqueId());
        // 表单内容,因为不用系统表单，这里先简单组装一下防止后面出错
        processInstanceParamDto.setParamMap(this.buildForm(process.getFlowId()));
        return processInstanceParamDto;
    }

    private ProcessInstanceParamDto buildParamWithDefault(Process process, Map<String, Object> data) {
        ProcessInstanceParamDto processInstanceParamDto = new ProcessInstanceParamDto();
        processInstanceParamDto.setFlowId(process.getFlowId());
        processInstanceParamDto.setUniqueId(process.getUniqueId());
        // 表单内容,因为不用系统表单，这里先简单组装一下防止后面出错
        processInstanceParamDto.setParamMap(this.buildFormWithDefault(process.getFlowId(), data));
        return processInstanceParamDto;
    }


    private HashMap<String, Object> buildForm(String flowId) {
        HashMap<String, Object> formContents = new HashMap<>();
        List<ProcessForm> froms = processFormService.lambdaQuery().eq(ProcessForm::getTenantId, TenantUtil.get()).eq(ProcessForm::getFlowId, flowId).list();
        for (ProcessForm from : froms) {
            formContents.put(from.getFormId(), "");
        }
        return formContents;
    }

    //有默认值的form
    private HashMap<String, Object> buildFormWithDefault(String flowId, Map<String, Object> data) {
        HashMap<String, Object> formContents = new HashMap<>();
        List<ProcessForm> froms = processFormService.lambdaQuery().eq(ProcessForm::getTenantId, TenantUtil.get()).eq(ProcessForm::getFlowId, flowId).list();
        for (ProcessForm from : froms) {
            formContents.put(from.getFormId(), data.get(from.getFormId()));
        }
        return formContents;
    }


}
