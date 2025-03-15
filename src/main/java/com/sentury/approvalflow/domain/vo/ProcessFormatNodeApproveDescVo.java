package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.constants.ApproveDescTypeEnum;
import com.sentury.approvalflow.common.dto.flow.UploadValue;
import com.sentury.approvalflow.domain.vo.node.NodeFormatUserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 节点审批描述对象
 * @author Huijun Zhao
 * @description
 * @date 2023-08-10 10:15
 */

@Schema(description = "节点审批描述对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessFormatNodeApproveDescVo {
    /**
     * 评论内容
     */

    @Schema(description = "评论内容")
    private String desc;
    /**
     * 评论类型 {@link   ApproveDescTypeEnum}
     */

    @Schema(description = "评论类型 {@link   ApproveDescTypeEnum}")
    private String descType;
    /**
     * 是否是系统评论
     */

    @Schema(description = "是否是系统评论")
    private Boolean sys;
    /**
     * 评论类型字符串
     */

    @Schema(description = "评论类型字符串")
    private String descTypeStr;
    /**
     * 显示时间格式化字符串
     */

    @Schema(description = "显示时间格式化字符串")
    private String showTimeStr;
    /**
     * 用户
     */

    @Schema(description = "用户")
    private NodeFormatUserVo user;
    /**
     * 日期时间
     */

    @Schema(description = "日期时间")
    private Date date;

    /**
     * 评论的文件
     */

    @Schema(description = "评论的文件")
    private List<UploadValue> approveFileList;
    /**
     *  评论的图片
     */

    @Schema(description = "评论的图片")
    private List<UploadValue> approveImageList;
    /**
     * 签字的url
     */

    @Schema(description = "签字的url")
    private List<String> signUrlList;
}
