package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.service.IFormService;
import com.sentury.approvalflow.domain.vo.FormRemoteSelectOptionParamVo;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 表单相关控制器
 */
@Tag(name = "表单相关控制器", description = "表单相关控制器")
@RestController
@RequestMapping(value = "form")
public class FormController {
    @Resource
    private IFormService formService;

    /**
     * 获取下拉选项
     *
     * @return
     */
    @Operation(summary = "获取下拉选项", description = "获取下拉选项")
    @PostMapping("selectOptions")
    public R2 selectOptions(@RequestBody FormRemoteSelectOptionParamVo formRemoteSelectOptionParamVo) {

        return formService.selectOptions(formRemoteSelectOptionParamVo);
    }

    /**
     * 获取表单数据
     *
     * @param taskDto 参数
     * @return
     */
    @Operation(summary = "获取表单数据", description = "获取表单数据")
    @PostMapping("getFormList")
    public R2<List<FormItemVO>> getFormList(@RequestBody QueryFormListParamVo taskDto) {
        return formService.getFormList(taskDto);
    }

    /**
     * 获取表单详细数据
     *
     * @param taskDto 参数
     * @return
     */
    @Operation(summary = "获取表单详细数据", description = "获取表单详细数据")
    @PostMapping("getFormDetail")
    public R2 getFormDetail(@RequestBody QueryFormListParamVo taskDto) {
        return formService.getFormDetail(taskDto);
    }
    /**
     * 动态表单
     *
     * @param taskDto
     * @return
     */
    @Operation(summary = "动态表单", description = "动态表单")
    @PostMapping("dynamicFormList")
    public R2<List<FormItemVO>> dynamicFormList(@RequestBody QueryFormListParamVo taskDto) {
        return formService.dynamicFormList(taskDto);
    }
}
