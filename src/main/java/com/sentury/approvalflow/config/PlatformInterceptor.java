package com.sentury.approvalflow.config;

import com.sentury.approvalflow.common.utils.ThreadLocalUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 平台拦截器 区分手机还是pc
 */
@Configuration
@Slf4j
public class PlatformInterceptor implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                        String userAgent = request.getHeader("User-Agent");
                        UserAgent agent = UserAgentUtil.parse(userAgent);

                        ThreadLocalUtil.putUserAgent(agent);
                        ThreadLocalUtil.putUserAgentStr(userAgent);

                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                        ThreadLocalUtil.clear();
                    }
                })
                .addPathPatterns("/**/**");
    }


}
