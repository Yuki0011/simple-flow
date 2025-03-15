package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUrlDto {
    /**
     * 是否是内部地址  如果是内部地址，如下url类似于 /page/a 否则就是完整的http请求
     */

    @Schema(description = "是否是内部地址  如果是内部地址，如下url类似于 /page/a 否则就是完整的http请求")
    private Boolean innerUrl;
    /**
     * 登录地址
     */

    @Schema(description = "登录地址")
    private String url;
    /**
     * 是否开启验证码
     * 默认开启的
     */

    @Schema(description = "是否开启验证码 默认开启的")
    private Boolean captcha = true;

}
