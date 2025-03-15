package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema
@Data
public class SignContractInfoDto {
    /**
     * 地址
     */

    @Schema(description = "地址")
    private String url;
    /**
     * pdf文件 转成图片的url集合
     */

    @Schema(description = "pdf文件 转成图片的url集合")
    private List<String> imgUrlList;
    /**
     * 结果
     */

    @Schema(description = "结果")
    private Boolean result;
    /**
     * 结束时间
     */

    @Schema(description = "结束时间")
    private Date finishTime;
    /**
     * 顺序
     */

    @Schema(description = "顺序")
    private Integer index;
    /**
     * 是否可以拒绝
     */

    @Schema(description = "是否可以拒绝")
    private Boolean refuseAble;
}
