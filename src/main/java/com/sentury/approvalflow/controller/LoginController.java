package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.biz.security.captcha.EasyCaptchaService;
import com.sentury.approvalflow.service.ILoginService;
import com.sentury.approvalflow.domain.vo.AutoLoginVO;
import com.sentury.approvalflow.domain.vo.CaptchaResult;
import com.sentury.approvalflow.domain.vo.UserBizVO;
import com.sentury.approvalflow.common.config.NotWriteLogAnno;
import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.R2;
import cn.dev33.satoken.stp.SaTokenInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 用户接口
 */
@Tag(name = "用户接口", description = "用户接口")
@RestController
@RequestMapping(value = {"login"})
public class LoginController {

    @Resource
    private ILoginService loginService;

    @Resource
    private EasyCaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @return
     */
    @Operation(summary = "获取验证码", description = "获取验证码")
    @GetMapping("/captcha")
    @NotWriteLogAnno(printResultLog = false)
    public R2<CaptchaResult> getCaptcha() {
        return captchaService.getCaptcha();
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @Operation(summary = "用户登录", description = "用户登录")
    @SneakyThrows
    @PostMapping("login")
    public R2 login(@RequestBody UserBizVO user) {

        return loginService.login(user);

    }

    /**
     * h5用户登录
     *
     * @param user
     * @return
     */
    @Operation(summary = "h5用户登录", description = "h5用户登录")
    @SneakyThrows
    @PostMapping("loginAtH5")
    public R2<SaTokenInfo> loginAtH5(@RequestBody UserBizVO user) {

        return loginService.loginAtH5(user);

    }

    /**
     * 用户token自动登录
     *
     * @param token
     * @return
     */
    @Operation(summary = "用户token自动登录", description = "用户token自动登录")
    @SneakyThrows
    @GetMapping("loginByToken")
    public R2<SaTokenInfo> loginByToken(String token) {

        return loginService.loginByToken(token);

    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @Operation(summary = "用户退出登录", description = "用户退出登录")
    @SneakyThrows
    @PostMapping("logout")
    public R2 logout() {
        return loginService.logout();
    }

    /**
     * 钉钉登录
     *
     * @param authCode 授权码
     * @return
     */
    @Operation(summary = "钉钉登录", description = "钉钉登录")
    @GetMapping("/loginAtDingTalk")
    public R2<SaTokenInfo> loginAtDingTalk(String authCode) {
        return loginService.loginAtDingTalk(authCode);
    }

    /**
     * 根据不同平台获取登录地址，设计网页登录、钉钉登录
     *
     * @return
     */
    @Operation(summary = "根据不同平台获取登录地址，设计网页登录、钉钉登录", description = "根据不同平台获取登录地址，设计网页登录、钉钉登录")
    @GetMapping("/getLoginUrl")
    public R2<LoginUrlDto> getLoginUrl() {
        return loginService.getLoginUrl();
    }

    /**
     * 获取登录参数
     *
     * @return
     */
    @Operation(summary = "获取登录参数", description = "获取登录参数")
    @PostMapping("/getLoginParam")
    public R2 getLoginParam(@RequestBody Map<String, Object> paramMap) {
        return loginService.getLoginParam(paramMap);
    }

    /**
     * 自动登录 第三方系统自动传递登录token和用户id登录
     * @param autoLoginVO 登录参数
     * @return
     */
    @PostMapping("auto")
    public R2 loginAuto(@RequestBody AutoLoginVO autoLoginVO){
        return loginService.loginAuto(autoLoginVO);
    }




}
