package com.sentury.approvalflow.domain.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 格式化对象参数
 */

@Schema(description = "格式化对象参数")
@Data
public class NodeFormatParamVo {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
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
    private String ccId;
    /**
     * 来源哪里 比如 有流程实例id 可能来自发起（start） 因为有复制发起的功能
     */

    @Schema(description = "来源哪里 比如 有流程实例id 可能来自发起（start） 因为有复制发起的功能")
    private String from;
    /**
     * 参数集合
     */

    @Schema(description = "参数集合")
    private Map<String,Object> paramMap;

}
