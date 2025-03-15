package com.sentury.approvalflow.domain.vo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 前端版本对象
 * @author Huijun Zhao
 * @description
 * @date 2023-10-20 16:39
 */

@Schema(description = "前端版本对象")
@Data
public class WebVersionVO {


    @Schema(hidden = true)
    private String versionNo;

}
