package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.common.dto.*;
import com.sentury.approvalflow.service.IProcessInstanceService;
import com.sentury.approvalflow.service.IProcessInstanceUserCopyService;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;
import com.sentury.approvalflow.domain.vo.ProcessInstanceCopyVo;
import com.sentury.approvalflow.domain.vo.ProcessInstanceRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程实例接口
 */
@Tag(name = "流程实例接口", description = "流程实例接口")
@RestController
@RequestMapping( "process-instance")
public class ProcessInstanceController {

    @Resource
    private IProcessInstanceService processInstanceService;

    @Resource
    private IProcessInstanceUserCopyService processInstanceUserCopyService;



    /**
     * 启动流程
     *
     * @param processInstanceParamDto
     * @return
     */
    @Operation(summary = "启动流程", description = "启动流程")
    @SneakyThrows
    @PostMapping("startProcessInstance")
    public R2 startProcessInstance(@RequestBody ProcessInstanceParamDto processInstanceParamDto) {

        return processInstanceService.startProcessInstance(processInstanceParamDto);

    }



    /**
     * 查询当前登录用户已办任务的流程实例
     *
     * @param pageDto
     * @return
     */
    @Operation(summary = "查询当前登录用户已办任务的流程实例", description = "查询当前登录用户已办任务的流程实例")
    @SneakyThrows
    @PostMapping("queryMineDoneProcessInstance")
    public R2<PageResultDto<ProcessInstanceDto>> queryMineDoneProcessInstance(@RequestBody ProcessDataQueryVO pageDto) {

        return processInstanceService.queryMineDoneProcessInstance(pageDto);

    }


    /**
     * 发起的流程列表
     *
     * @param pageDto
     * @return
     */
    @Operation(summary = "发起的流程列表", description = "发起的流程列表")
    @PostMapping("queryList")
    public R2<Page<ProcessInstanceRecordVO>> queryList(@RequestBody ProcessDataQueryVO pageDto) {
        return processInstanceService.queryList(pageDto);
    }

    /**
     * 查询流程实例详情
     *
     * @param processInstanceId
     * @return
     */
    @Operation(summary = "查询流程实例详情", description = "查询流程实例详情")
    @GetMapping("queryDetailByProcessInstanceId")
    public R2<ProcessInstanceRecordVO> queryDetailByProcessInstanceId(String processInstanceId) {
        return processInstanceService.queryDetailByProcessInstanceId(processInstanceId);
    }



    /**
     * 查询抄送我的流程实例
     *
     * @param pageDto
     * @return
     */
    @Operation(summary = "查询抄送我的流程实例", description = "查询抄送我的流程实例")
    @SneakyThrows
    @PostMapping("queryMineCCProcessInstance")
    public R2<Page<ProcessInstanceCopyVo>> queryMineCCProcessInstance(@RequestBody ProcessDataQueryVO pageDto) {
        return processInstanceUserCopyService.queryMineCCProcessInstance(pageDto);
    }





    /**
     * 查询进行中的任务
     *
     * @param processInstanceId
     * @return
     */
    @Parameter(name = "processInstanceId", description = "", in = ParameterIn.PATH, required = true)
    @Operation(summary = "查询进行中的任务", description = "查询进行中的任务")
    @SneakyThrows
    @PostMapping("queryTaskListInProgress/{processInstanceId}")
    public R2<List<TaskDto>> queryTaskListInProgress(@PathVariable String processInstanceId) {

        return processInstanceService.queryTaskListInProgress(processInstanceId);

    }



}
