package com.sentury.approvalflow.common.dto;

import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建流程对象
 * @author Huijun Zhao
 * @description
 * @date 2023-09-22 10:45
 */
@Schema(description = "创建流程对象")
@Data
public class CreateFlowDto {
    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String userId;
    /**
     * 节点
     */

    @Schema(description = "节点")
    private Node node;
    /**
     * 流程名称
     */

    @Schema(description = "流程名称")
    private String processName;

}
