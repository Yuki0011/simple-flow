package com.sentury.approvalflow.domain.vo;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应对象
 *
 * @author haoxr
 * @since 2023/03/24
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResult {
    /**
     * 验证码关联key
     */

    private String verifyCodeKey;
    /**
     * 图片base64
     */

    private String verifyCodeBase64;

}
