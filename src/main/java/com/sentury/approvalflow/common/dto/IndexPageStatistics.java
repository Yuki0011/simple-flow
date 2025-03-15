package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页统计数据
 */
@Schema(description = "首页统计数据")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexPageStatistics {
    /**
     * 待办数量
     */

    @Schema(description = "待办数量")
    private Long pendingNum;
    /**
     * 发起数量
     */

    @Schema(description = "发起数量")
    private Long startedNum;
    /**
     * 抄送任务
     */

    @Schema(description = "抄送任务")
    private Long copyNum;

    /**
     * 完成数量
     */

    @Schema(description = "完成数量")
    private Long completedNum;

}
