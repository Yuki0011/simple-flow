package com.sentury.approvalflow.domain.vo;


import com.sentury.approvalflow.domain.vo.node.NodeShowVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeFormatResultVo {
    /**
     * 节点集合
     */

    @Schema(description = "节点集合")
    private List<NodeShowVo> processNodeShowDtoList;
    /**
     * 是否禁止选择用户
     */

    @Schema(description = "是否禁止选择用户")
    private Boolean disableSelectUser;
    /**
     * 要选择用户的节点集合
     */

    @Schema(description = "要选择用户的节点集合")
    private List<String> selectUserNodeIdList;

}
