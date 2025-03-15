package com.sentury.approvalflow.common.dto.third;

import com.sentury.approvalflow.common.constants.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 通知消息
 * </p>
 *
 * @author xiaoge
 * @since 2023-07-25
 */
@Schema(description = "<p> 通知消息 </p>")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto   implements Serializable {


    /**
     * 类型
     * {@link MessageTypeEnum}
     */

    @Schema(description = "类型 {@link MessageTypeEnum}")
    private String type;

    /**
     * 是否已读
     */

    @Schema(description = "是否已读")
    private Boolean readed;

    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String userId;

    /**
     * 唯一id
     */

    @Schema(description = "唯一id")
    private String bizUniqueId;

    /**
     * 消息参数
     */

    @Schema(description = "消息参数")
    private String param;

    /**
     * 消息内容
     */

    @Schema(description = "消息内容")
    private String content;

    /**
     * 消息头
     */

    @Schema(description = "消息头")
    private String title;
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     *  流程实例id
     */

    @Schema(description = "流程实例id")
    private String processInstanceId;
    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;
    /**
     * 发起人id
     */
    @Schema(description = "发起人id")
    private String startUserId;
    /**
     * 发起人姓名
     */
    @Schema(description = "发起人姓名")
    private String startUserName;
    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String groupName;
    /**
     * 流程名称
     */
    @Schema(description = "流程名称")
    private String flowName;

}
