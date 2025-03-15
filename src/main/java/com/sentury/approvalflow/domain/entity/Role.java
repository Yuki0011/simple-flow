package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */

@Schema(description = "<p> 角色 </p>")
@Getter
@Setter
@Accessors(chain = true)
public class Role  extends BaseEntityForWorkFlow {


    /**
     * 角色名字
     */

    @Schema(description = "角色名字")
    @TableField("`name`")
    private String name;

    /**
     * 创建人
     */

    @Schema(description = "创建人")
    @TableField("`user_id`")
    private Long userId;
    /**
     * 角色唯一编码
     */

    @Schema(description = "角色唯一编码")
    @TableField("`key`")
    private String key;
    /**
     * 角色状态 1正常2禁用
     */

    @Schema(description = "角色状态 1正常2禁用")
    @TableField("`status`")
    private Integer status;


}
