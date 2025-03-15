package com.sentury.approvalflow.common.dto.flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 上传的文件对象
 * @author Huijun Zhao
 * @description
 * @date 2023-07-28 10:36
 */
@Schema(description = "上传的文件对象")
@Data
public class UploadValue {
    /**
     * 请求url
     */

    @Schema(description = "请求url")
    private String url;
    /**
     * 文件名字
     */

    @Schema(description = "文件名字")
    private String name;

}
