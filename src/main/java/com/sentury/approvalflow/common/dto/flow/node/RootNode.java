package com.sentury.approvalflow.common.dto.flow.node;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserRootNode;
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
@JsonTypeName("0")
@Data
public class RootNode extends SuperUserRootNode {



    /**
     * 操作列表
     */
    @Schema(description = "操作列表")
    private List operList;


}
