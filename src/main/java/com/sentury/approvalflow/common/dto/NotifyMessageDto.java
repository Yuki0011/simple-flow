package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 消息通知对象
 * @author Huijun Zhao
 * @description
 * @date 2023-09-14 17:01
 */
@Schema(description = "消息通知对象")
@Data
public class NotifyMessageDto {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
    private String processInstanceId;
    /**
     * 消息通知id
     */

    @Schema(description = "消息通知id")
    private String messageNotifyId;

}
