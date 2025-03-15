package com.sentury.approvalflow.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema
@Data
public class ProcessInstanceCopyDto {



    /**
     * 当前节点时间
     */

    @Schema(description = "当前节点时间")
    @JsonFormat( timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date nodeTime;

    /**
     * 发起人
     */

    @Schema(description = "发起人")
    private String startUserId;

    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;

    /**
     * 实例id
     */

    @Schema(description = "实例id")
    private String processInstanceId;

    /**
     * 节点id
     */

    @Schema(description = "节点id")
    private String nodeId;


    /**
     * 节点 名称
     */

    @Schema(description = "节点 名称")
    private String nodeName;

    /**
     * 表单数据
     */

    @Schema(description = "表单数据")
    private String formData;


    /**
     *  抄送人
     */
    @Schema(description = "抄送人")
    private List<String> userIdList;

    /**
     * 租户id
     */

    @Schema(description = "租户id")
    private String tenantId;
}
