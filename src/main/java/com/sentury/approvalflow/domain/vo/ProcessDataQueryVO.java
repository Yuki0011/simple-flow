package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.PageDto;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.domain.constants.ProcessInstanceRecordStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 流程数据参数对象
 */

@Schema(description = "流程数据参数对象")
@Data
public class ProcessDataQueryVO extends PageDto {


    /**
     * 流程id
     */

    @Schema(description = "流程id")
    private String flowId;
    /**
     * 流程编码
     */

    @Schema(description = "流程编码")
    private String processBizCode;
    /**
     * 流程id集合
     */

    @Schema(description = "流程id集合")
    private List<String> flowIdList;
    /**
     * 流程结束时间 开始-结束
     */

    @Schema(description = "流程结束时间 开始-结束")
    private List<String> finishTime;
    /**
     * 流程发起时间  开始结束
     */

    @Schema(description = "流程发起时间  开始结束")
    private List<String> startTime;
    /**
     * 发起人集合
     */

    @Schema(description = "发起人集合")
    private List<NodeUser> starterList;
    /**
     * 流程状态 {@link ProcessInstanceRecordStatusEnum}
     */

    @Schema(description = "流程状态 {@link ProcessInstanceRecordStatusEnum}")
    private Integer status;


}
