package com.sentury.approvalflow.service.impl;

import cn.hutool.http.useragent.UserAgent;
import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.FormChangeRecordDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskResultDto;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.SelectValue;
import com.sentury.approvalflow.common.dto.flow.node.CopyNode;
import com.sentury.approvalflow.common.dto.flow.node.RootNode;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.utils.HttpUtil;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import com.sentury.approvalflow.common.utils.ThreadLocalUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.entity.ProcessInstanceCopy;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.biz.utils.FormUtil;
import com.sentury.approvalflow.domain.vo.FormRemoteSelectOptionParamVo;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.lang.Validator.isMobile;

@Service
@Slf4j
public class FormServiceImpl implements IFormService {

    @Resource
    private IProcessService processService;

    @Resource
    private IProcessNodeDataService nodeDataService;


    @Resource
    private IDeptUserService deptUserService;

    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;
    @Resource
    private IProcessInstanceCopyService processCopyService;

    /**
     * 远程请求下拉选项
     *
     * @param formRemoteSelectOptionParamVo
     * @return
     */
    @Override
    public R2 selectOptions(FormRemoteSelectOptionParamVo formRemoteSelectOptionParamVo) {

        String s =
            HttpUtil.flowExtenstionHttpRequest(formRemoteSelectOptionParamVo.getHttpSetting(),
                formRemoteSelectOptionParamVo.getParamMap(), formRemoteSelectOptionParamVo.getFlowId(),
                formRemoteSelectOptionParamVo.getProcessInstanceId(), null, null, null,
                TenantUtil.get());

        List<SelectValue> selectValueList = JsonUtil.parseArray(s, SelectValue.class);

        if (CollUtil.isEmpty(selectValueList)) {
            return R2.fail("选择配置格式错误");
        }

        Set<String> keySet = selectValueList.stream().filter(w -> StrUtil.isNotBlank(w.getKey())).map(w -> w.getKey()).collect(Collectors.toSet());
        if (keySet.size() < selectValueList.size()) {
            return R2.fail("选项值格式错误");
        }

        Set<String> valueSet =
            selectValueList.stream().filter(w -> StrUtil.isNotBlank(w.getValue())).map(w -> w.getValue()).collect(Collectors.toSet());
        if (valueSet.size() < selectValueList.size()) {
            return R2.fail("选项名称格式错误");
        }

        return R2.success(selectValueList);
    }

    /**
     * 获取表单数据
     *
     * @param taskDto
     * @return
     */
    @Override
    public R2<List<FormItemVO>> getFormList(QueryFormListParamVo taskDto) {
        String processInstanceId = taskDto.getProcessInstanceId();
        String flowId = taskDto.getFlowId();
        String taskId = taskDto.getTaskId();
        Long ccId = taskDto.getCcId();

        List<FormItemVO> formItemVOList = new ArrayList<>();

        if (ccId != null) {
            formItemVOList.addAll(getCCFormList(ccId));

        } else if (StrUtil.isAllBlank(processInstanceId, taskId) || StrUtil.equals(taskDto.getFrom(), "start")) {
            //没有流程实例 没有任务


            formItemVOList.addAll(getStartFormList(flowId, processInstanceId));

        } else if (StrUtil.isNotBlank(taskId)) {

            formItemVOList.addAll(getTaskFormList(taskId));

        } else if (StrUtil.isNotBlank(processInstanceId)) {
            formItemVOList.addAll(getStartedProcessInstanceFormList(processInstanceId));

        }


        return R2.success(formItemVOList);
    }

    /**
     * 表单详情
     *
     * @param taskDto
     * @return
     */
    @Override
    public R2 getFormDetail(QueryFormListParamVo taskDto) {


        String flowId = taskDto.getFlowId();
        String taskId = taskDto.getTaskId();
        String processInstanceId = taskDto.getProcessInstanceId();
        Long ccId = taskDto.getCcId();

        TaskResultDto taskResultDto = null;
        if (StrUtil.isNotBlank(taskId)) {
            taskResultDto = CoreHttpUtil.queryTask(taskId, LoginHelper.getUserId() + "").getData();

        }

        if (StrUtil.isBlank(flowId)) {
            if (null != (ccId)) {
                ProcessInstanceCopy processCopy = processCopyService.getById(ccId);
                flowId = processCopy.getFlowId();
                processInstanceId = processCopy.getProcessInstanceId();
            } else if (StrUtil.isNotBlank(taskId)) {
                flowId = taskResultDto.getFlowId();
                processInstanceId = taskResultDto.getProcessInstanceId();
            } else if (StrUtil.isNotBlank(processInstanceId)) {


                ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);

                flowId = processInstanceRecord.getFlowId();

            }
        }


        Process process = processService.getByFlowId(flowId);
        if (process == null) {
            return R2.fail("流程不存在");
        }
        List<FormItemVO> formItemVOList = getFormList(taskDto).getData();

        Boolean dynamic = false;

        //处理流程里表单变更记录
        Map<String, List<FormChangeRecordDto>> formChangeRecordMap = new HashMap<>();


        Dict set = Dict.create().set("formList", formItemVOList).set("dynamic", dynamic).set("formChangeRecord",
            formChangeRecordMap).set("selectStartDept", false).set("startUserDeptList", new ArrayList<>());
        //如果是发起表单 选择当前发起流程的部门
        if (StrUtil.equals("start", taskDto.getFrom())) {
            //String loginIdAsString = LoginHelper.getUserId()+"";
//            Long userId = LoginHelper.getUserId();
//            UserDto userDto = ApiStrategyFactory.getStrategy().getUser(userId+"");
            //  List<String> deptIdList = userDto.getDeptIdList();
            Long deptId = LoginHelper.getDeptId();

            List<DeptDto> deptList = ApiStrategyFactory.getStrategy().getDeptList(List.of(deptId + ""));

            //说明是重新发起的
            if (StrUtil.isBlank(processInstanceId)) {
                set.put("startUserDeptList", deptList);
                set.put("selectStartDept", true);
            } else {
                ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);
                String mainDeptId = processInstanceRecord.getMainDeptId();
                //找出来不同于流程的主部门id
                List<DeptDto> neDeptList =
                    deptList.stream().filter(w -> !StrUtil.equals(w.getId(), mainDeptId)).collect(Collectors.toList());
                //找出来相同的部门id 将之前流程发起的部门放在第一位
                List<DeptDto> eqDeptList =
                    deptList.stream().filter(w -> StrUtil.equals(w.getId(), mainDeptId)).collect(Collectors.toList());
                eqDeptList.addAll(neDeptList);
                set.put("startUserDeptList", eqDeptList);
                set.put("selectStartDept", true);

            }

        } else if (StrUtil.isNotBlank(processInstanceId) && StrUtil.isNotBlank(taskId) && taskResultDto.getCurrentTask()) {
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);
            String mainDeptId = processInstanceRecord.getMainDeptId();
            if (StrUtil.isBlank(mainDeptId)) {
                set.put("selectStartDept", true);

                String loginIdAsString = LoginHelper.getUserId() + "";
                UserDto userDto = ApiStrategyFactory.getStrategy().getUser(loginIdAsString);
                List<String> deptIdList = userDto.getDeptIdList();

                List<DeptDto> deptList = ApiStrategyFactory.getStrategy().getDeptList(deptIdList);
                set.put("startUserDeptList", deptList);
            }

        }

        return R2.success(set);
    }

    /**
     * 动态表单
     *
     * @param taskDto
     * @return
     */
    @Override
    public R2<List<FormItemVO>> dynamicFormList(QueryFormListParamVo taskDto) {
        String flowId = taskDto.getFlowId();
        String processInstanceId = taskDto.getProcessInstanceId();
        String taskId = taskDto.getTaskId();
        Long ccId = taskDto.getCcId();


        Process process = processService.getByFlowId(flowId);
        if (process == null) {
            return R2.fail("流程不存在");
        }
        List<FormItemVO> formItemVOList = taskDto.getFormItemVOList();


        if (ccId != null) {
            return R2.success(formItemVOList);


        }


        return R2.success(formItemVOList);
    }

    private List<FormItemVO> getCCFormList(long ccId) {

        ProcessInstanceCopy processCopy = processCopyService.getById(ccId);

        String flowId = processCopy.getFlowId();
        Process oaForms = processService.getByFlowId(flowId);

        String formData = processCopy.getFormData();

        Map<String, Object> variableMap = JsonUtil.parseObject(formData, new JsonUtil.TypeReference<Map<String, Object>>() {
        });

        String nodeId = processCopy.getNodeId();

        String processInstanceId = processCopy.getProcessInstanceId();


        String data = nodeDataService.getNodeData(flowId, nodeId).getData();
        Node node = JsonUtil.parseObject(data, Node.class);

        CopyNode copyNode = (CopyNode) node;

        Map<String, String> formPerms = copyNode.getFormPerms();


        String formItemString = oaForms.getFormItemsPc();
        boolean mobile = ThreadLocalUtil.getUserAgent().isMobile();
        if (mobile) {
            formItemString = oaForms.getFormItems();
        }
        List<FormItemVO> formItemVOList = JsonUtil.parseArray(formItemString, FormItemVO.class);
        for (FormItemVO formItemVO : formItemVOList) {

            FormUtil.handFormPerm(formItemVO, formPerms, ProcessInstanceConstant.FormPermClass.HIDE,
                ProcessInstanceConstant.FormPermClass.READ);

            FormUtil.handFormValueAndOptions(formItemVO, variableMap, flowId, processInstanceId);


            FormUtil.handFormMarkBlank(formItemVO);

        }
        return formItemVOList;
    }


    /**
     * 我发起的流程表单
     *
     * @param processInstanceId
     * @return
     */
    private List<FormItemVO> getStartedProcessInstanceFormList(String processInstanceId) {


        ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);


        String flowId = processInstanceRecord.getFlowId();
        Process oaForms = processService.getByFlowId(flowId);


        //发起人变量数据
        Map<String, Object> variableMap = CoreHttpUtil.queryExecutionVariables(processInstanceId).getData();
        //发起人表单权限
        String process = oaForms.getProcess();
        Node nodeDto = JsonUtil.parseObject(process, Node.class);
        Map<String, String> formPerms1 = null;

        if (nodeDto instanceof RootNode) {
            RootNode rootNode = (RootNode) nodeDto;
            formPerms1 = rootNode.getFormPerms();
        }

        if (nodeDto instanceof SuperUserNode) {
            SuperUserNode rootNode = (SuperUserNode) nodeDto;
            formPerms1 = rootNode.getFormPerms();
        }


        String formItemString = oaForms.getFormItemsPc();
        boolean mobile = ThreadLocalUtil.getUserAgent().isMobile();
        if (mobile) {
            formItemString = oaForms.getFormItems();
        }
        List<FormItemVO> formItemVOList = JsonUtil.parseArray(formItemString, FormItemVO.class);
        for (FormItemVO formItemVO : formItemVOList) {

            FormUtil.handFormPerm(formItemVO, formPerms1, ProcessInstanceConstant.FormPermClass.READ, ProcessInstanceConstant.FormPermClass.READ);

            FormUtil.handFormValueAndOptions(formItemVO, variableMap, flowId, processInstanceId);

            FormUtil.handFormMarkBlank(formItemVO);


        }
        return formItemVOList;
    }

    /**
     * 查询任务表单列表
     *
     * @param taskId
     * @return
     */
    private List<FormItemVO> getTaskFormList(String taskId) {


        String userId = LoginHelper.getUserId() + "";


        R2<TaskResultDto> r = CoreHttpUtil.queryTask(taskId, userId);

        TaskResultDto taskResultDto = r.getData();

        String flowId = taskResultDto.getFlowId();

        String processInstanceId = taskResultDto.getProcessInstanceId();
        Process oaForms = processService.getByFlowId(flowId);


        //变量
        Map<String, Object> paramMap = taskResultDto.getVariableAll();
        Boolean currentTask = taskResultDto.getCurrentTask();
        if (!currentTask) {
            Map<String, Object> map = CoreHttpUtil.queryExecutionVariables(taskResultDto.getProcessInstanceId()).getData();
            paramMap.putAll(map);

        }


        //当前节点数据
        String nodeId = taskResultDto.getNodeId();
        if (StrUtil.startWith(nodeId, ProcessInstanceConstant.VariableKey.START_NODE)) {
            nodeId = ProcessInstanceConstant.VariableKey.START_NODE;
        }

        Node node = nodeDataService.getNode(flowId, nodeId).getData();
        Map<String, String> formPerms = null;

        if (node instanceof RootNode) {
            RootNode rootNode = (RootNode) node;
            formPerms = rootNode.getFormPerms();
        }

        if (node instanceof SuperUserNode) {
            SuperUserNode rootNode = (SuperUserNode) node;
            formPerms = rootNode.getFormPerms();
        }


        String formItemString = oaForms.getFormItemsPc();
        boolean mobile = false;
        UserAgent userAgent = ThreadLocalUtil.getUserAgent();
        if(userAgent!=null){
            mobile = userAgent.isMobile();
        }
        if (mobile) {
            formItemString = oaForms.getFormItems();
        }

        List<FormItemVO> formItemVOList = JsonUtil.parseArray(formItemString, FormItemVO.class);
        for (FormItemVO formItemVO : formItemVOList) {

            FormUtil.handFormPerm(formItemVO, formPerms, ProcessInstanceConstant.FormPermClass.READ,
                (!currentTask) ?
                    ProcessInstanceConstant.FormPermClass.READ :
                    null
            );

            FormUtil.handFormValueAndOptions(formItemVO, paramMap, flowId, processInstanceId);

            FormUtil.handFormMarkBlank(formItemVO);


        }


        return formItemVOList;

    }

    /**
     * 发起流程表单
     *
     * @param flowId
     * @param processInstanceId
     * @return
     */
    private List<FormItemVO> getStartFormList(String flowId, String processInstanceId) {


        Process oaForms = processService.getByFlowId(flowId);

        String process = oaForms.getProcess();
        String formItemString = oaForms.getFormItemsPc();
        UserAgent userAgent = ThreadLocalUtil.getUserAgent();
        boolean mobile = false;
        if (userAgent != null) {
            mobile = userAgent.isMobile();
        }
        if (mobile) {
            formItemString = oaForms.getFormItems();
        }
        Node startNode = JsonUtil.parseObject(process, Node.class);


        Map<String, String> formPerms = null;

        if (startNode instanceof RootNode) {
            RootNode rootNode = (RootNode) startNode;
            formPerms = rootNode.getFormPerms();
        }

        if (startNode instanceof SuperUserNode) {
            SuperUserNode rootNode = (SuperUserNode) startNode;
            formPerms = rootNode.getFormPerms();
        }

        List<FormItemVO> t = JsonUtil.parseArray(formItemString, FormItemVO.class);


        Map<String, Object> processParamMap = new HashMap<>();

        if (StrUtil.isNotBlank(processInstanceId)) {
            Map<String, Object> data = CoreHttpUtil.queryExecutionVariables(processInstanceId).getData();
            processParamMap.putAll(data);
        }

        for (FormItemVO formItemVO : t) {
            String perm = MapUtil.getStr(formPerms, formItemVO.getId(), ProcessInstanceConstant.FormPermClass.EDIT);
            formItemVO.setPerm(perm);

            FormItemVO.Props formItemVOProps = formItemVO.getProps();
            {

                handleForm(formItemVO, processParamMap, flowId, processInstanceId);
            }

        }
        return t;
    }


    /**
     * 处理表单
     *
     * @param formItemVO
     * @param flowId
     * @param processInstanceId
     */
    private static void handleForm(FormItemVO formItemVO, Map<String, Object> processParamMap, String flowId,
                                   String processInstanceId) {
        FormItemVO.Props formItemVOProps = formItemVO.getProps();

        //处理单选多选选项F

        if (StrUtil.equalsAny(formItemVO.getType(), FormTypeEnum.SINGLE_SELECT.getType(),
            FormTypeEnum.MULTI_SELECT.getType())) {

            FormUtil.handSelectOptions(formItemVO, processParamMap, flowId, processInstanceId);


        }

        if (CollUtil.isNotEmpty(processParamMap)) {
            Object o = processParamMap.get(formItemVO.getId());
            formItemVOProps.setValue(o);
            return;
        }


    }
}
