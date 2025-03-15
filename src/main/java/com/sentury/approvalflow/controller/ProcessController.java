package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.domain.constants.ValidGroup;
import com.sentury.approvalflow.service.IProcessService;
import com.sentury.approvalflow.domain.vo.ProcessVO;
import com.sentury.approvalflow.common.dto.R2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 流程接口
 */
@Tag(name = "流程接口", description = "流程接口")
@Validated
@RestController
@RequestMapping(value = "process")
public class ProcessController {

    @Resource
    private IProcessService processService;
    /**
     * 根据流程唯一标识查询流程列表
     * @param uniqueId
     * @return
     */
    @Parameter(name = "uniqueId", description = "", in = ParameterIn.PATH, required = true)
    @Operation(summary = "根据流程唯一标识查询流程列表", description = "根据流程唯一标识查询流程列表")
    @GetMapping("/getListByUniqueId/{uniqueId}")
    public R2<ProcessVO> getListByUniqueId(@PathVariable String uniqueId){
        return processService.getListByUniqueId(uniqueId);
    }

    /**
     * 设置主流程
     * @param flowId
     * @return
     */
    @Parameter(name = "flowId", description = "", in = ParameterIn.PATH, required = true)
    @Operation(summary = "设置主流程", description = "设置主流程")
    @PostMapping("setMainProcess/{flowId}")
    public R2 setMainProcess(@PathVariable String flowId){
        return processService.setMainProcess(flowId);
    }
    /**
     * 获取详细数据
     *
     * @param flowId 流程id
     * @return
     */
    @Operation(summary = "获取详细数据", description = "获取详细数据")
    @GetMapping("getDetail")
    public R2<ProcessVO> getDetail(String flowId) {
        return processService.getDetail(flowId);
    }

    /**
     * 创建流程
     *
     * @param processVO
     * @return
     */
    @Operation(summary = "创建流程", description = "创建流程")
    @PostMapping("create")
    public R2 create(@Validated(value= ValidGroup.Crud.Create.class) @RequestBody ProcessVO processVO) {
        return processService.create(processVO);
    }



    /**
     * 编辑表单
     *
     * @param flowId 摸板ID
     * @param type       类型 stop using delete
     * @param groupId 流程组id
     * @return 操作结果
     */
    @Parameters({
            @Parameter(name = "flowId", description = "摸板ID", in = ParameterIn.PATH, required = true),
            @Parameter(name = "type", description = "类型 stop using delete", in = ParameterIn.QUERY, required = true),
            @Parameter(name = "groupId", description = "流程组id", in = ParameterIn.QUERY)
    })
    @Operation(summary = "编辑表单", description = "编辑表单")
    @PutMapping("update/{flowId}")
   public R2 update(@PathVariable String flowId,
                    @RequestParam String type,
                    @RequestParam(required = false) Long groupId){
        return processService.update(flowId, type, groupId);
    }



}
