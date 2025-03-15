package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 操作类型对象
 * @author Huijun Zhao
 * @description
 * @date 2023-10-12 09:47
 */

@Schema(description = "操作类型对象")
@Data
public class TaskOperDataResultVO {

    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
    private String processInstanceId;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 任务是否存在
     */

    @Schema(description = "任务是否存在")
    private Boolean taskExist;
    /**
     * 是否是前加签
     */

    @Schema(description = "是否是前加签")
    private Boolean frontJoinTask;
    /**
     * 操作列表
     */

    @Schema(description = "操作列表")
    private List operList;
    /**
     * 当前节点
     */

    @Schema(description = "当前节点")
    private Node node;
    /**
     * 原始流程数据
     */

    @Schema(description = "原始流程数据")
    private Node process;
    /**
     * 是否需求签字
     */

    @Schema(description = "是否需求签字")
    private Boolean needSignature;

}
