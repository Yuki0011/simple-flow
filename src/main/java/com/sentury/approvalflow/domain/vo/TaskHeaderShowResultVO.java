package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.constants.ApproveResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 *
 * 表单头部显示对象
 * @author Huijun Zhao
 * @description
 * @date 2023-10-12 09:47
 */

@Schema(description = "表单头部显示对象")
@Data
public class TaskHeaderShowResultVO {

    /**
     * 流程实例id
     */

    @Schema(description = "流程实例id")
    private String processInstanceId;
    /**
     * 发起人姓名
     */

    @Schema(description = "发起人姓名")
    private String starterName;
    /**
     * 发起人头像
     */

    @Schema(description = "发起人头像")
    private String starterAvatarUrl;
    /**
     * 流程名称
     */

    @Schema(description = "流程名称")
    private String processName;
    /**
     * 发起时间
     */

    @Schema(description = "发起时间")
    private Date startTime;
    /**
     * 列出结果 {@link  ApproveResultEnum}
     */

    @Schema(description = "列出结果 {@link  ApproveResultEnum}")
    private Integer processInstanceResult;
    /**
     * 流程业务key
     */

    @Schema(description = "流程业务key")
    private String processInstanceBizKey;
    /**
     * 流程业务编码
     */

    @Schema(description = "流程业务编码")
    private String processInstanceBizCode;


    private String deptName;


}
