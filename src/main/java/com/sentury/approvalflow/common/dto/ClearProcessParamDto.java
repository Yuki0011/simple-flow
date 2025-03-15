package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 清理流程对象
 * @author zhj
 */
@Schema(description = "清理流程对象")
@Data
public class ClearProcessParamDto {
    /**
     * 流程id集合
     */

    @Schema(description = "流程id集合")
    private List<String> flowIdList;
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

}
