package com.sentury.approvalflow.biz.security.captcha;

import com.sentury.approvalflow.common.dto.R2;
import cn.hutool.core.util.IdUtil;
import com.sentury.approvalflow.biz.config.CaptchaConfig;
import com.sentury.approvalflow.domain.constants.SecurityConstants;
import com.sentury.approvalflow.domain.vo.CaptchaResult;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * EasyCaptcha 业务类
 *
 * @author haoxr
 * @since 2023/03/24
 */
@Component
@RequiredArgsConstructor
public class EasyCaptchaService {

    private final EasyCaptchaProducer easyCaptchaProducer;

    private final RedisTemplate redisTemplate;

    private final CaptchaConfig captchaConfig;

    /**
     * 获取验证码
     *
     * @return
     */
    public R2<CaptchaResult> getCaptcha() {
        // 获取验证码
        Captcha captcha = easyCaptchaProducer.getCaptcha();
        String captchaText = captcha.text(); // 验证码文本
        String captchaBase64 = captcha.toBase64(); // 验证码图片Base64字符串

        // 验证码文本缓存至Redis，用于登录校验
        String verifyCodeKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(SecurityConstants.VERIFY_CODE_CACHE_PREFIX + verifyCodeKey, captchaText,
                captchaConfig.getTtl(), TimeUnit.SECONDS);

        CaptchaResult build = CaptchaResult.builder()
                .verifyCodeKey(verifyCodeKey)
                .verifyCodeBase64(captchaBase64)
                .build();
        return R2.success(build);
    }

}
