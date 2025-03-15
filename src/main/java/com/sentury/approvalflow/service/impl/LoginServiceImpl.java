package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.constants.LoginPlatEnum;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.utils.PlatformUtil;
import com.sentury.approvalflow.common.utils.ThreadLocalUtil;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.constants.SecurityConstants;
import com.sentury.approvalflow.service.ILoginService;
import com.sentury.approvalflow.biz.utils.DingTalkHttpUtil;
import com.sentury.approvalflow.domain.vo.AutoLoginVO;
import com.sentury.approvalflow.domain.vo.UserBizVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class LoginServiceImpl implements ILoginService {


    @Value("${login.captcha}")
    private Boolean captcha;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 登录
     *
     * @param userBizVO
     * @return
     */
    @Override
    public R2 login(UserBizVO userBizVO) {

        return loginAtWeb(userBizVO, LoginPlatEnum.ADMIN);
    }

    /**
     * h5登录
     *
     * @param userBizVO
     * @return
     */
    @Override
    public R2 loginAtH5(UserBizVO userBizVO) {

        return loginAtWeb(userBizVO, LoginPlatEnum.H5);
    }

    /**
     * 网页登录
     *
     * @param userBizVO
     * @param platform
     * @return
     */
    private R2 loginAtWeb(UserBizVO userBizVO, LoginPlatEnum platform) {

        if (captcha) {
            //是否开启了验证码
            Object cacheVerifyCode =
                    redisTemplate.opsForValue().get(SecurityConstants.VERIFY_CODE_CACHE_PREFIX + userBizVO.getVerifyCodeKey());
            if (cacheVerifyCode == null) {
                return R2.fail("验证码错误");
            } else {
                // 验证码比对
                if (!StrUtil.equals(userBizVO.getVerifyCode(), Convert.toStr(cacheVerifyCode))) {
                    return R2.fail("验证码错误");

                }
            }
        }


        String phone = userBizVO.getPhone();
        String password = userBizVO.getPassword();

        //对接登录
        String userId = ApiStrategyFactory.getStrategy().loginWeb(password, phone);


        if (StrUtil.isBlank(userId)) {
            return R2.fail("账号或者密码错误");
        }
        StpUtil.login(userId, platform.getType());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return R2.success(tokenInfo);
    }

    /**
     * token登录
     *
     * @param token
     * @return
     */
    @Override
    public R2 loginByToken(String token) {
        String userId = ApiStrategyFactory.getStrategy().getUserIdByToken(token);
        if (StrUtil.isBlank(userId)) {
            return R2.fail("获取用户失败，请重试");
        }

        String userAgentStr = ThreadLocalUtil.getUserAgentStr();
        UserAgent userAgent = ThreadLocalUtil.getUserAgent();
        if (PlatformUtil.isDingTalk(userAgentStr)) {
            StpUtil.login(userId, LoginPlatEnum.DING_TALK.getType());
        } else if (PlatformUtil.isFeiShu(userAgentStr)) {
            StpUtil.login(userId, LoginPlatEnum.FEI_SHU.getType());
        } else if (PlatformUtil.isWxWork(userAgentStr)) {
            StpUtil.login(userId, LoginPlatEnum.WX_WORK.getType());
        } else if (userAgent.isMobile()) {
            StpUtil.login(userId, LoginPlatEnum.H5.getType());
        } else {
            StpUtil.login(userId, LoginPlatEnum.ADMIN.getType());
        }


        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return R2.success(tokenInfo);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Override
    public R2 logout() {
        boolean login = StpUtil.isLogin();
        if (login) {
            StpUtil.logout(StpUtil.getLoginId());
        }

        return R2.success();
    }

    /**
     * 钉钉登录
     *
     * @param authCode
     * @return
     */
    @Override
    public R2 loginAtDingTalk(String authCode) {


        String userId = DingTalkHttpUtil.getUserIdByCodeAtMiniApp(authCode).getData();


        StpUtil.login(userId, LoginPlatEnum.DING_TALK.getType());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return R2.success(tokenInfo);
    }

    /**
     * 获取登录地址
     *
     * @return
     */
    @Override
    public R2 getLoginUrl() {
        return R2.success(ApiStrategyFactory.getStrategy().getLoginUrl());
    }

    /**
     * 获取登录参数
     *
     * @return
     */
    @Override
    public R2 getLoginParam(Map<String, Object> paramMap) {
        return R2.success(ApiStrategyFactory.getStrategy().getLoginParam(paramMap));

    }

    /**
     * 自动登录
     *
     * @param autoLoginVO
     * @return
     */
    @Override
    public R2 loginAuto(AutoLoginVO autoLoginVO) {
        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setToken(autoLoginVO.getToken());
        StpUtil.login(autoLoginVO.getUserId(), loginModel);
        return R2.success();
    }
}
