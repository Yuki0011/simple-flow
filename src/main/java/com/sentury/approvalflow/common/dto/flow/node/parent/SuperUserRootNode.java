package com.sentury.approvalflow.common.dto.flow.node.parent;

import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/4 21:24
 */
@Schema(description = ": TODO")
@Data
public class SuperUserRootNode extends Node {

    /**
     * 表单权限
     */

    @Schema(description = "表单权限")
    private Map<String, String> formPerms;

    /**
     * 多人审批方式
     */

    @Schema(description = "多人审批方式")
    private Integer multipleMode;


}
