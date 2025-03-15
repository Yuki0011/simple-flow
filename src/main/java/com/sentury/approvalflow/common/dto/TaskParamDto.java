package com.sentury.approvalflow.common.dto;

import com.sentury.approvalflow.common.dto.flow.UploadValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 任务完成参数对象
 *
 */
@Schema(description = "任务完成参数对象")
@Data
public class TaskParamDto {

    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;
    /**
     * 审批结果 同意还是拒绝
     */

    @Schema(description = "审批结果 同意还是拒绝")
    private Boolean approveResult;
    /**
     * 审批意见
     */

    @Schema(description = "审批意见")
    private String approveDesc;
    /**
     * 多实例id集合
     */

    @Schema(description = "多实例id集合")
    private List<String> processInstanceIdList;
    /**
     * 签署顺序
     */

    @Schema(description = "签署顺序")
    private Integer signOrder;
    /**
     * 审批上传的文件
     */

    @Schema(description = "审批上传的文件")
    private List<UploadValue> approveFileList;
    /**
     * 审批上传的图片
     */

    @Schema(description = "审批上传的图片")
    private List<UploadValue> approveImageList;

    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 添加子流程发起人
     */

    @Schema(description = "添加子流程发起人")
    private Boolean appendChildProcessRootId;

    /**
     * 任务id
     */

    @Schema(description = "任务id")
    private String taskId;
    /**
     * 用户id
     */

    @Schema(description = "用户id")
    private String userId;
    /**
     * 用户姓名
     */

    @Schema(description = "用户姓名")
    private String userName;
    /**
     * 模板用户id
     */

    @Schema(description = "模板用户id")
    private String targetUserId;
    /**
     * 模板用户id集合
     */

    @Schema(description = "模板用户id集合")
    private List<String> targetUserIdList;
    /**
     * 目标执行id集合
     */

    @Schema(description = "目标执行id集合")
    private List<String> targetExecutionIdList;
    /**
     * 目标用户名字集合
     */

    @Schema(description = "目标用户名字集合")
    private List<String> targetUserNameList;
    /**
     * 目标用户名称
     */

    @Schema(description = "目标用户名称")
    private String targetUserName;
    /**
     * 参数
     */

    @Schema(description = "参数")
    private Map<String,Object> paramMap;



    /**
     * 目标节点
     */

    @Schema(description = "目标节点")
    private String targetNodeId;
    /**
     * 节点id集合
     */

    @Schema(description = "节点id集合")
    private List<String> nodeIdList;
    /**
     * 任务id集合
     */

    @Schema(description = "任务id集合")
    private List<String> taskIdList;
    /**
     * 执行id
     */

    @Schema(description = "执行id")
    private String executionId;
    /**
     * 签名url
     */

    @Schema(description = "签名url")
    private String signUrl;
    /**
     * 原始文件地址
     */

    @Schema(description = "原始文件地址")
    private String oriFileUrl;
    /**
     * 签章图片的集合
     */

    @Schema(description = "签章图片的集合")
    private List<SignContractImg> signContractImgList;


    /**
     * 签署合同签章图片信息
     */
    @Schema(description = "签署合同签章图片信息")
    @Data
    public static class SignContractImg{
        /**
         * 页面的宽度
         */

        @Schema(description = "页面的宽度")
        private Float pageWidth;
        /**
         * 页面高度
         */

        @Schema(description = "页面高度")
        private Float pageHeight;

        /**
         * 签署合同的url
         */

        @Schema(description = "签署合同的url")
        private String url;
        /**
         * 页面x坐标
         */

        @Schema(description = "页面x坐标")
        private Float fx;
        /**
         * 页面y坐标
         */

        @Schema(description = "页面y坐标")
        private Float fy;
        /**
         * 图片宽度
         */

        @Schema(description = "图片宽度")
        private Float width;
        /**
         * 图片高度
         */

        @Schema(description = "图片高度")
        private Float height;
        /**
         * 页码
         */

        @Schema(description = "页码")
        private Integer page;

    }

}
