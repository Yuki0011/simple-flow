package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 查询对象
 */

@Schema(description = "查询对象")
@Data
public class QueryFormListParamVo {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 参数集合
     */

    @Schema(description = "参数集合")
    private Map<String,Object> paramMap;


    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;

    /**
     * 任务id
     */

    @Schema(description = "任务id")
    private String taskId;
    /**
     * 抄送id
     */

    @Schema(description = "抄送id")
    private Long ccId;
    /**
     * 表单唯一id
     */

    @Schema(description = "表单唯一id")
    private String formUniqueId;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 表单列表
     */

    @Schema(description = "表单列表")
    private List<FormItemVO> formItemVOList;
    /**
     * 来源  现在有值就是start 表示来自发起
     */

    @Schema(description = "来源  现在有值就是start 表示来自发起")
    private String from;
}
