package com.sentury.approvalflow.common.dto.flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点用户对象
 */
@Schema(description = "节点用户对象")
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class NodeUser implements Serializable {

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String id;
    /**
     * 用户名称
     */

    @Schema(description = "用户名称")
    private String name;
    /**
     * 类型
     */

    @Schema(description = "类型")
    private String type;
    /**
     * 选择
     */

    @Schema(description = "选择")
    private Boolean selected;

    /**
     * 头像
     */

    @Schema(description = "头像")
    private String avatar;

    /**
     * 是否包含下级部门
     */

    @Schema(description = "是否包含下级部门")
    private Boolean containChildrenDept = false;


}
