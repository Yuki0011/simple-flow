package com.sentury.approvalflow.common.dto.flow.node;

import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/3 22:48
 */
@Schema(description = ": TODO")
@Data
public class GatewayNode extends Node {

    /**
     * 条件
     */
    @Schema(description = "条件")
    private List<Node> conditionNodes;



}
