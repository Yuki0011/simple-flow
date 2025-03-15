package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.IndexPageStatistics;
import com.sentury.approvalflow.common.dto.ProcessInstanceNodeRecordParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskResultDto;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.dto.flow.node.ApproveNode;
import com.sentury.approvalflow.common.dto.flow.node.RootNode;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.constants.SystemConstants;
import com.sentury.approvalflow.domain.entity.*;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import com.sentury.approvalflow.biz.utils.NodeFormatUtil;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.vo.*;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BaseServiceImpl implements IBaseService {

    @Resource
    private IProcessInstanceCopyService processCopyService;
    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;

    @Resource
    private IProcessNodeDataService processNodeDataService;

    @Resource
    private IProcessService processService;
    @Resource
    private IProcessInstanceNodeRecordService processNodeRecordService;
    @Resource
    private IProcessInstanceAssignUserRecordService processNodeRecordAssignUserService;
    @Resource
    private IProcessInstanceOperRecordService processInstanceOperRecordService;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IFileService fileService;

    /**
     * 修改前端版本号
     *
     * @param webVersionVO
     * @return
     */
    @Override
    public R2 setWebVersion(WebVersionVO webVersionVO) {

        redisTemplate.opsForValue().set(SystemConstants.VERSION_REDIS_KEY, webVersionVO.getVersionNo());

        return R2.success();
    }

    /**
     * 获取当前系统前端版本号
     *
     * @return
     */
    @Override
    public R2 getWebVersion() {
        Object o = redisTemplate.opsForValue().get(SystemConstants.VERSION_REDIS_KEY);

        return R2.success(o);
    }

    /**
     * 首页数据
     *
     * @return
     */
    @Override
    public R2<IndexPageStatistics> index() {

       // String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId()+"";


        Long coypNum = processCopyService.lambdaQuery().eq(ProcessInstanceCopy::getUserId, userId).count();

        Long startendNum = processInstanceRecordService.lambdaQuery().eq(ProcessInstanceRecord::getUserId, userId).count();

        IndexPageStatistics indexPageStatistics = CoreHttpUtil.querySimpleData(userId, TenantUtil.get()).getData();
        indexPageStatistics.setCopyNum(coypNum);
        indexPageStatistics.setStartedNum(startendNum);

        return R2.success(indexPageStatistics);
    }

    /**
     * 获取所有地区数据
     *
     * @return
     */
    @Override
    public R2 areaList() {

        ClassPathResource classPathResource = new ClassPathResource("area.json");
        String json = FileUtil.readUtf8String(classPathResource.getFile());

        return R2.success(JsonUtil.parseArrayOfMap(json));
    }

    /**
     * 同步数据
     *
     * @return
     */
    @Override
    public R2 loadRemoteData() {
        ApiStrategyFactory.getStrategy().loadRemoteData();
        return R2.success();
    }

    /**
     * 格式化流程显示
     *
     * @param nodeFormatParamVo
     * @return
     */
    @Override
    public R2<NodeFormatResultVo> formatStartNodeShow(NodeFormatParamVo nodeFormatParamVo) {

        String flowId = nodeFormatParamVo.getFlowId();
        String processInstanceId = nodeFormatParamVo.getProcessInstanceId();

        String taskId = nodeFormatParamVo.getTaskId();
        String ccId = nodeFormatParamVo.getCcId();


        if (StrUtil.isAllBlank(flowId, processInstanceId)) {

            if (StrUtil.isNotBlank(taskId)) {
                TaskResultDto taskResultDto = CoreHttpUtil.queryTask(taskId, null).getData();
                flowId = taskResultDto.getFlowId();
                processInstanceId = taskResultDto.getProcessInstanceId();
            } else if (StrUtil.isNotBlank(ccId)) {
                ProcessInstanceCopy copy = processCopyService.getById(ccId);

                flowId = copy.getFlowId();
                processInstanceId = copy.getProcessInstanceId();
            }

        }
        if (StrUtil.isBlank(flowId) && StrUtil.isNotBlank(processInstanceId)) {
            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);
            flowId = processInstanceRecord.getFlowId();
        }
        if (StrUtil.isAllBlank(flowId, processInstanceId)) {
            return R2.success(new ArrayList<>());
        }

        //处理参数
        Map<String, Object> paramMap = nodeFormatParamVo.getParamMap();

        String process = null;
        if (StrUtil.isNotBlank(processInstanceId) && !StrUtil.equals(nodeFormatParamVo.getFrom(), "start")) {


            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);

            flowId = processInstanceRecord.getFlowId();
            process = processInstanceRecord.getProcess();

            //流程发起了 设置主部门id
            paramMap.put(ProcessInstanceConstant.VariableKey.START_USER_MAIN_DEPTID_KEY,
                    processInstanceRecord.getMainDeptId());

        }
        if (StrUtil.isBlank(process)) {
            Process oaForms = processService.getByFlowId(flowId);
            process = oaForms.getProcess();
        }
        Node nodeDto = JsonUtil.parseObject(process, Node.class);


        List<String> selectUserNodeIdList = new ArrayList<>();

        //禁止指定审批人
        boolean disableSelectUser = true;

        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        if (StrUtil.isNotBlank(ccId)) {
            //抄送节点

            ProcessInstanceCopy processInstanceCopy = processCopyService.getById(ccId);
            Map<String, Object> variableMap = JsonUtil.parseObject(processInstanceCopy.getFormData(),
                    new JsonUtil.TypeReference<Map<String, Object>>() {
                    });
            if (variableMap == null) {
                variableMap = new HashMap<>();
            }

            paramMap.putAll(variableMap);
        } else if (StrUtil.isNotBlank(nodeFormatParamVo.getTaskId())) {
            //有任务的


            R2<TaskResultDto> r = CoreHttpUtil.queryTask(nodeFormatParamVo.getTaskId(), LoginHelper.getUserId()+"");


            TaskResultDto taskResultDto = r.getData();
            if (!r.isOk() || !taskResultDto.getCurrentTask()) {

                List<ProcessInstanceAssignUserRecord> list = processNodeRecordAssignUserService.lambdaQuery()
                        .eq(ProcessInstanceAssignUserRecord::getTaskId, nodeFormatParamVo.getTaskId())
                        .orderByDesc(ProcessInstanceAssignUserRecord::getEndTime)
                        .list();

                String data = list.get(0).getData();
                Map<String, Object> variableMap = JsonUtil.parseObject(data, new JsonUtil.TypeReference<Map<String, Object>>() {
                });
                if (variableMap == null) {
                    variableMap = new HashMap<>();
                }

                paramMap.putAll(variableMap);


            } else {
                //待办任务
                Map<String, Object> variableMap = taskResultDto.getVariableAll();
                variableMap.putAll(paramMap);
                paramMap.putAll(variableMap);


            }


        } else if (StrUtil.isNotBlank(processInstanceId) && !StrUtil.equals(nodeFormatParamVo.getFrom(), "start")) {

            ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);


            //任务里没有
            String formData = processInstanceRecord.getFormData();
            Map<String, Object> map = JsonUtil.parseObject(formData, new JsonUtil.TypeReference<Map<String, Object>>() {
            });
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (paramMap.get(key) == null) {
                    paramMap.put(key, value);
                }
            }
        } else {
            //发起节点


        }


        //查询所有的节点
        List<ProcessInstanceNodeRecordParamDto> processInstanceNodeRecordParamDtoList = new ArrayList<>();
        if (StrUtil.isNotBlank(processInstanceId) && !StrUtil.equals(nodeFormatParamVo.getFrom(), "start")) {
            List<ProcessInstanceNodeRecord> list = processNodeRecordService.lambdaQuery().eq(ProcessInstanceNodeRecord::getProcessInstanceId, processInstanceId).list();
            processInstanceNodeRecordParamDtoList.addAll(BeanUtil.copyToList(list, ProcessInstanceNodeRecordParamDto.class));
        }
        List<NodeShowVo> processNodeShowDtos = NodeFormatUtil.formatProcessNodeShow(nodeDto,
                StrUtil.equals(nodeFormatParamVo.getFrom(), "start") ? null : processInstanceId, paramMap,
                processInstanceNodeRecordParamDtoList, selectUserNodeIdList);

        NodeFormatResultVo nodeFormatResultVo = NodeFormatResultVo.builder()
                .processNodeShowDtoList(processNodeShowDtos)
                .selectUserNodeIdList(disableSelectUser ? new ArrayList<>() : selectUserNodeIdList)
                .disableSelectUser(disableSelectUser)
                .build();


        return R2.success(nodeFormatResultVo);
    }

    /**
     * 查询头部显示数据
     *
     * @param nodeFormatParamVo
     * @return
     */
    @Override
    public R2<TaskHeaderShowResultVO> queryHeaderShow(QueryFormListParamVo nodeFormatParamVo) {
        String taskId = nodeFormatParamVo.getTaskId();
        Long ccId = nodeFormatParamVo.getCcId();
        String processInstanceId = nodeFormatParamVo.getProcessInstanceId();

        if (ccId != null) {

            ProcessInstanceCopy processCopy = processCopyService.getById(ccId);
            processInstanceId = processCopy.getProcessInstanceId();

        } else if (StrUtil.isAllBlank(processInstanceId, taskId)) {
            //没有流程实例 没有任务


        }
        //没有流程实例id 但是任务id存在 去查询
        if (StrUtil.isBlank(processInstanceId) && StrUtil.isNotBlank(taskId)) {
            TaskResultDto taskResultDto = CoreHttpUtil.queryTask(taskId, null).getData();
            processInstanceId = taskResultDto.getProcessInstanceId();
        }


        ProcessInstanceRecord processInstanceRecord = processInstanceRecordService.getByProcessInstanceId(processInstanceId);

        String starterUserId = processInstanceRecord.getUserId();
        UserDto starterUser = ApiStrategyFactory.getStrategy().getUser(starterUserId);


        TaskHeaderShowResultVO taskHeaderShowResultVO = new TaskHeaderShowResultVO();
        taskHeaderShowResultVO.setProcessInstanceId(processInstanceRecord.getProcessInstanceId());
        taskHeaderShowResultVO.setStarterName(starterUser.getName());
        taskHeaderShowResultVO.setStarterAvatarUrl(starterUser.getAvatarUrl());
        taskHeaderShowResultVO.setProcessName(processInstanceRecord.getName());
        taskHeaderShowResultVO.setStartTime(processInstanceRecord.getCreateTime());
        taskHeaderShowResultVO.setProcessInstanceResult(processInstanceRecord.getResult());
        taskHeaderShowResultVO.setProcessInstanceBizKey(processInstanceRecord.getProcessInstanceBizKey());
        taskHeaderShowResultVO.setProcessInstanceBizCode(processInstanceRecord.getProcessInstanceBizCode());
        taskHeaderShowResultVO.setDeptName(starterUser.getDeptName());

        return R2.success(taskHeaderShowResultVO);
    }

    /**
     * 获取任务操作数据
     *
     * @param taskId
     * @return
     */
    @Override
    public R2<TaskOperDataResultVO> queryTaskOperData(String taskId) {
        //String userId = LoginHelper.getUserId()+"";
        String userId = LoginHelper.getUserId()+"";


        R2<TaskResultDto> r = CoreHttpUtil.queryTask(taskId, userId);

        if (!r.isOk()) {
            return R2.fail(r.getMsg());
        }

        TaskResultDto taskResultDto = r.getData();
        Boolean currentTask = taskResultDto.getCurrentTask();
        if (!currentTask) {

            TaskOperDataResultVO taskOperDataResultVO = new TaskOperDataResultVO();
            taskOperDataResultVO.setProcessInstanceId(taskResultDto.getProcessInstanceId());

            taskOperDataResultVO.setTaskExist(false);


            return R2.success(taskOperDataResultVO);

        }

        String flowId = taskResultDto.getFlowId();
        Process oaForms = processService.getByFlowId(flowId);
        if (oaForms == null) {
            return R2.fail("流程不存在");
        }
        //当前节点数据
        String nodeId = taskResultDto.getNodeId();
        if (StrUtil.startWith(nodeId, ProcessInstanceConstant.VariableKey.START_NODE)) {
            nodeId = ProcessInstanceConstant.VariableKey.START_NODE;
        }
        String nodeDataJson =
                processNodeDataService.getNodeData(flowId, nodeId).getData();
        Node node = JsonUtil.parseObject(nodeDataJson, Node.class);


        String process = oaForms.getProcess();

        TaskOperDataResultVO taskOperDataResultVO = new TaskOperDataResultVO();
        taskOperDataResultVO.setProcessInstanceId(taskResultDto.getProcessInstanceId());
        taskOperDataResultVO.setNodeId(nodeId);
        taskOperDataResultVO.setTaskExist(currentTask);
        taskOperDataResultVO.setFrontJoinTask(taskResultDto.getFrontJoinTask());

        if (node instanceof ApproveNode) {
            ApproveNode approveNode = (ApproveNode) node;
            List operList = approveNode.getOperList();
            taskOperDataResultVO.setOperList(operList);


        }

        if (node instanceof RootNode) {
            RootNode rootNode = (RootNode) node;
            List operList = rootNode.getOperList();
            taskOperDataResultVO.setOperList(operList);
        }

        taskOperDataResultVO.setNode(node);
        taskOperDataResultVO.setProcess(JsonUtil.parseObject(process, Node.class));


        taskOperDataResultVO.setNeedSignature(false);


        return R2.success(taskOperDataResultVO);
    }

}
