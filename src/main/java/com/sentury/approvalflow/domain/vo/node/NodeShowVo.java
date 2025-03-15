package com.sentury.approvalflow.domain.vo.node;


import com.sentury.approvalflow.domain.constants.ProcessInstanceNodeRecordStatusEnum;
import com.sentury.approvalflow.domain.vo.ProcessFormatNodeApproveDescVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程节点显示对象
 */

@Schema(description = "流程节点显示对象")
@Data
public class NodeShowVo {
    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String id;

    /**
     * 用户列表
     */

    @Schema(description = "用户列表")
    private List<NodeFormatUserVo> userVoList;

    /**
     * 显示
     */

    @Schema(description = "显示")
    private String placeholder;
    /**
     * 状态 1进行中2已完成3已撤销0未开始 {@link ProcessInstanceNodeRecordStatusEnum}
     */

    @Schema(description = "状态 1进行中2已完成3已撤销0未开始 {@link ProcessInstanceNodeRecordStatusEnum}")
    private Integer status;


    /**
     * 节点名称
     */

    @Schema(description = "节点名称")
    private String name;
    /**
     * 节点类型
     */

    @Schema(description = "节点类型")
    private Integer type;
    /**
     * 发起人选择用户
     */

    @Schema(description = "发起人选择用户")
    private Boolean selectUser;


    /**
     * 是否多选
     */

    @Schema(description = "是否多选")
    private Boolean multiple;
    /**
     * 子级列表
     */

    @Schema(description = "子级列表")
    private List<NodeShowVo> children;
    /**
     * 分支列表
     */

    @Schema(description = "分支列表")
    private List<NodeShowVo> branch;
    /**
     * 显示时间
     */

    @Schema(description = "显示时间")
    private String showTimeStr;

    /**
     * 评论列表
     */

    @Schema(description = "评论列表")
    private List<ProcessFormatNodeApproveDescVo> approveDescList;
    /**
     * 子流程列表
     */

    @Schema(description = "子流程列表")
    private List<SubProcessShowVo> subProcessShowVoList;

    /**
     * 子流程显示对象
     */

    @Schema(description = "子流程显示对象")
    @Data
    public static class SubProcessShowVo {
        /**
         * 流程实例id
         */

        @Schema(description = "流程实例id")
        private String processInstanceId;
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
         * 流程名称
         */

        @Schema(description = "流程名称")
        private String processName;
        /**
         * 流程状态
         */

        @Schema(description = "流程状态")
        private Integer processStatus;
    }
}
