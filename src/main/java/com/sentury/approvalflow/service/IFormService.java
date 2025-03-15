package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.domain.vo.FormRemoteSelectOptionParamVo;
import com.sentury.approvalflow.domain.vo.QueryFormListParamVo;

import java.util.List;

/**
 * 表单服务
 */
public interface IFormService {
    /**
     * 远程请求下拉选项
     *
     * @param formRemoteSelectOptionParamVo
     * @return
     */
    R2 selectOptions(FormRemoteSelectOptionParamVo formRemoteSelectOptionParamVo);

    /**
     * 获取表单数据
     *
     * @param taskDto
     * @return
     */
    R2<List<FormItemVO>> getFormList(QueryFormListParamVo taskDto);

    /**
     * 表单详情
     * @param taskDto
     * @return
     */
    R2 getFormDetail(QueryFormListParamVo taskDto);

    /**
     * 动态表单
     * @param taskDto
     * @return
     */
    R2<List<FormItemVO>> dynamicFormList(QueryFormListParamVo taskDto);

}
