package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;



@Schema
@Getter
@Setter
@Accessors(chain = true)
public class ProcessGroup  extends BaseEntityForWorkFlow {


    /**
     * 分组名
     */

    @Schema(description = "分组名")
    @TableField("`group_name`")
    private String groupName;

    /**
     * 排序
     */

    @Schema(description = "排序")
    @TableField("`sort`")
    private Integer sort;


}
