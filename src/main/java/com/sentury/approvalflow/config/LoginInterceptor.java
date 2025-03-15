package com.sentury.approvalflow.config;

import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.exception.LoginExpiredException;
import com.sentury.approvalflow.common.exception.ResultCode;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author Huijun Zhao
 * @description
 * @date 2023-08-04 10:20
 */
//@Configuration
@Slf4j
public class LoginInterceptor implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                        boolean login = StpUtil.isLogin();
                        if (!login) {
                            log.debug("未登录地址:{}", request.getServletPath());
                            throw new LoginExpiredException(ResultCode.TOKEN_EXPIRED.getMsg(),
                                    ResultCode.TOKEN_EXPIRED.getCode(),
                                    "");
                        }
                        //String loginIdAsString = LoginHelper.getUserId()+"";
                        String loginIdAsString = LoginHelper.getUserId()+"";
                        UserDto userDto = ApiStrategyFactory.getStrategy().getUser(loginIdAsString);
                        if (userDto == null) {
                            log.info("已登录 未查询到用户 地址:{}", request.getServletPath());

                            throw new LoginExpiredException(ResultCode.TOKEN_EXPIRED.getMsg(),
                                    ResultCode.TOKEN_EXPIRED.getCode(),
                                    "");
                        }
                        return true;
                    }
                }).addPathPatterns("/**")
                .excludePathPatterns(
                        "/login/*",
                        "/api/login/*",
                        "/remote/*",
                        "/test/*",
                        "/web/*",
                        "/css/*",
                        "/img/*",
                        "/fonts/*",
                        "/js/*",
                        "*.ico",
                        "/api/process-instance/notifyMsgEvent",
                        "/process-instance/notifyMsgEvent",
                        "/api/file/show/*",
                        "/api/file/show/*/*",
                        "/file/show/*/*",
                        "/file/show/*",
                        "/koTime",
                        "/error",
                        "/koTime/*"
                )
        ;


    }


}
