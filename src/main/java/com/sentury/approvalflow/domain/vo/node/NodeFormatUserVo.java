package com.sentury.approvalflow.domain.vo.node;

import com.sentury.approvalflow.common.constants.TaskTypeEnum;
import com.sentury.approvalflow.domain.constants.NodeFormatUserVoStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 节点用户
 */

@Schema(description = "节点用户")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeFormatUserVo {

    /**
     * 用户od
     */

    @Schema(description = "用户od")
    private String id;
    /**
     * 用户名称
     */

    @Schema(description = "用户名称")
    private String name;

    /**
     * 操作时间
     */

    @Schema(description = "操作时间")
    private Date showTime;
    /**
     * 操作时间格式化字符串显示
     */

    @Schema(description = "操作时间格式化字符串显示")
    private String showTimeStr;
    /**
     * 签署合同的url
     */

    @Schema(description = "签署合同的url")
    private String signUrl;
    /**
     * 头像
     */

    @Schema(description = "头像")
    private String avatar;
    /**
     * 签署合同结果
     */

    @Schema(description = "签署合同结果")
    private Boolean signContractResult;

    /**
     * 操作类型 {@link TaskTypeEnum}
     */

    @Schema(description = "操作类型 {@link TaskTypeEnum}")
    private String operType;
    /**
     * 状态 1进行中2已完成 {@link NodeFormatUserVoStatusEnum}
     */

    @Schema(description = "状态 1进行中2已完成 {@link NodeFormatUserVoStatusEnum}")
    private Integer status = 0;

}
