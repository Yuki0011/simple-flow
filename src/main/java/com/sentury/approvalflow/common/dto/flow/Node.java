package com.sentury.approvalflow.common.dto.flow;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.sentury.approvalflow.common.config.NodeJacksonTypeIdResolver;
import com.sentury.approvalflow.common.constants.NodeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点对象
 */

@Schema(description = "节点对象")
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM,property = "type",defaultImpl = Node.class
        ,visible = true)
@JsonTypeIdResolver(NodeJacksonTypeIdResolver.class)

@Data
public class Node implements Cloneable, Serializable {
    /**
     * 节点唯一id
     */

    @Schema(description = "节点唯一id")
    private String id;


    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;

    /**
     * 临时id
     */

    @Schema(description = "临时id")
    private String tempId;
    /**
     * 上级id
     */

    @Schema(description = "上级id")
    private String parentId;
    /**
     * 头部id 用户处理创建流程时用
     */

    @Schema(description = "头部id 用户处理创建流程时用")
    private String headId;
    /**
     * 尾部id 用户处理创建流程时用
     */

    @Schema(description = "尾部id 用户处理创建流程时用")
    private String tailId;
    /**
     * 提示内容
     */

    @Schema(description = "提示内容")
    private String placeHolder;

    /**
     * 节点类型 {@link NodeTypeEnum}
     */

    @Schema(description = "节点类型 {@link NodeTypeEnum}")
    private Integer type;

    /**
     * 节点名称
     */

    @Schema(description = "节点名称")
    private String nodeName;
    /**
     * 是否错误
     */

    @Schema(description = "是否错误")
    private Boolean error;

    /**
     * 子节点
     */

    @Schema(description = "子节点")
    private Node childNode;












    /**
     * 在渲染流程节点显示是，如果是true表示被标记了 不能再次被标记，即使没有执行id
     */
    @Schema(description = "在渲染流程节点显示是，如果是true表示被标记了 不能再次被标记，即使没有执行id")
    private boolean remarkedAtNodeShow = false;

}
