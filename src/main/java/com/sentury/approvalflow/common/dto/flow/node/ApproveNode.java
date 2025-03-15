package com.sentury.approvalflow.common.dto.flow.node;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sentury.approvalflow.common.dto.flow.Nobody;
import com.sentury.approvalflow.common.dto.flow.Refuse;
import com.sentury.approvalflow.common.dto.flow.node.parent.SuperUserNode;
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
@Data
@JsonTypeName("1")
public class ApproveNode extends SuperUserNode {
    /**
     * 没有人时处理方式
     */

    @Schema(description = "没有人时处理方式")
    private Nobody nobody;

    /**
     * 审批拒绝时
     */

    @Schema(description = "审批拒绝时")
    private Refuse refuse;

    /**
     * 操作列表
     */

    @Schema(description = "操作列表")
    private List operList;


}
