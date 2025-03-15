package com.sentury.approvalflow.common.dto;

import cn.hutool.core.lang.Dict;
import com.sentury.approvalflow.common.constants.ApproveResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务对象
 */
@Schema(description = "任务对象")
@Data
public class TaskDto {
    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;

    /**
     * 参数集合
     */

    @Schema(description = "参数集合")
    private Map<String,Object> paramMap;
    /**
     *  表单值显示
     */

    @Schema(description = "表单值显示")
    private List<Dict> formValueShowList;

    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;
    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;
    /**
     * 耗时
     */

    @Schema(description = "耗时")
    private Long durationInMillis;
    /**
     * 任务id
     */

    @Schema(description = "任务id")
    private String taskId;
    /**
     * 执行人
     */

    @Schema(description = "执行人")
    private String assign;
    /**
     * 任务名称
     */

    @Schema(description = "任务名称")
    private String taskName;
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 任务创建时间
     */

    @Schema(description = "任务创建时间")
    private Date taskCreateTime;
    /**
     * 任务结束时间
     */

    @Schema(description = "任务结束时间")
    private Date taskEndTime;
    /**
     * 流程组名字
     */

    @Schema(description = "流程组名字")
    private String groupName;
    /**
     * 发起人id
     */

    @Schema(description = "发起人id")
    private String rootUserId;
    /**
     * 发起人名字
     */

    @Schema(description = "发起人名字")
    private String rootUserName;

    private String rootUserCode;
    /**
     * 发起人头像
     */

    @Schema(description = "发起人头像")
    private String rootUserAvatarUrl;

    /**
     * 发起时间
     */

    @Schema(description = "发起时间")
    private Date startTime;
    /**
     * 流程实例业务编码
     */

    @Schema(description = "流程实例业务编码")
    private String processInstanceBizCode;
    /**
     * 流程名称
     */

    @Schema(description = "流程名称")
    private String processName;

    /**
     * 需要发起人选择的节点id
     */

    @Schema(description = "需要发起人选择的节点id")
    private List<String> selectUserNodeId;
    /**
     * 流程结果 {@link ApproveResultEnum}
     */

    @Schema(description = "流程结果 {@link ApproveResultEnum}")
    private Integer processInstanceResult;
    /**
     * 用户名字
     */

    @Schema(description = "用户名字")
    private String userName;

    /**
     * 流程唯一id
     */

    @Schema(description = "流程唯一id")
    private String flowUniqueId;

    /**
     * 正在处理的人员显示
     */

    @Schema(description = "正在处理的人员显示")
    private String taskAssignShow;
    /**
     * 发起时间显示
     */

    @Schema(description = "发起时间显示")
    private String startTimeShow;

    private List<TitleDto> title;

}
