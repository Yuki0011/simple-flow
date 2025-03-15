package com.sentury.approvalflow.common.dto.flow.node;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sentury.approvalflow.common.dto.flow.GroupCondition;
import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/3 16:56
 */
@Schema(description = ": TODO")
@JsonTypeName("3")
@Data
public class EmptyNode extends Node {


    /**
     * 路由节点中用到了 指跳转到的节点id
     */

    @Schema(description = "路由节点中用到了 指跳转到的节点id")
    private String nodeId;
    /**
     * false:或|| true： 且&&
     */

    @Schema(description = "false:或|| true： 且&&")
    private Boolean mode;
    /**
     * 条件组之间的关系
     */

    @Schema(description = "条件组之间的关系")
    private Boolean groupRelationMode;
    /**
     * 优先级
     */

    @Schema(description = "优先级")
    private Integer priorityLevel;





    @Schema(hidden = true)
    private Object groupRelation;

    /**
     * 条件列表
     */

    @Schema(description = "条件列表")
    private List<GroupCondition> conditionList;

}
