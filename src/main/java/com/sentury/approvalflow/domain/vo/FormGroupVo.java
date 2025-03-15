package com.sentury.approvalflow.domain.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 流程组
 */

@Schema(description = "流程组")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormGroupVo {
    /**
     * id
     */

    @Schema(description = "id")
    private Long id;
    /**
     * 名字
     */

    @Schema(description = "名字")
    private String name;

    /**
     * 流程
     */

    @Schema(description = "流程")
    private List<FlowVo> items;

    /**
     * 流程对象
     */

    @Schema(description = "流程对象")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlowVo {
        /**
         * 流程id
         */

        @Schema(description = "流程id")
        private String flowId;

        /**
         * 发起范围
         */

        @Schema(description = "发起范围")
        private String rangeShow;
        /**
         * 名字
         */

        @Schema(description = "名字")
        private String name;

        private String nameSecond;
        /**
         * 图标
         */

        @Schema(description = "图标")
        private String logo;
        /**
         * 是否已停止
         */

        @Schema(description = "是否已停止")
        private Boolean stop;
        /**
         * 备注
         */

        @Schema(description = "备注")
        private String remark;
        /**
         * 更新日期
         */

        @Schema(description = "更新日期")
        private Date updated;
        /**
         * 是否记录报表
         */

        @Schema(description = "是否记录报表")
        private Boolean reportEnable;
        /**
         * 流程唯一id
         */

        @Schema(description = "流程唯一id")
        private String uniqueId;
    }


}
