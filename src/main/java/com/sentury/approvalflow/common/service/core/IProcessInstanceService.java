package com.sentury.approvalflow.common.service.core;

import com.sentury.approvalflow.common.dto.IndexPageStatistics;
import com.sentury.approvalflow.common.dto.ProcessInstanceParamDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.VariableQueryParamDto;

import java.util.Map;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-08-04 16:40
 */
public interface IProcessInstanceService {

    /**
     * 删除流程
     *
     * @param processInstanceParamDto
     * @return
     */
    R2 delete(ProcessInstanceParamDto processInstanceParamDto);


    /**
     * 查询统计数据
     *
     * @param userId
     * @return
     */
    R2<IndexPageStatistics> querySimpleData(String userId);

    /**
     * 查询变量
     *
     * @param paramDto
     * @return
     */
    R2<Map<String, Object>> queryVariables(VariableQueryParamDto paramDto);
}
