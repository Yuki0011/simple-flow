package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema
@Data
public class BaseEntityForWorkFlow {


    /**
     * 租户id
     */

    @Schema(description = "租户id")
    @TableField("`tenant_id`")
    private String tenantId;

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 逻辑删除字段
     */

    @Schema(description = "逻辑删除字段")
    @TableLogic(value = "0",delval = "1")
    private Boolean delFlag;
    /**
     * 创建时间
     */

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
