package com.sentury.approvalflow.config;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 租户拦截器
 */
//@Configuration
@Slf4j
public class TenantInterceptor implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

                        //获取请求头的租户id
                        String tenantId = request.getHeader(ProcessInstanceConstant.VariableKey.HTTP_HEADER_TENANT_ID_KEY);
                        if(StrUtil.isNotBlank(tenantId)){
                            TenantUtil.put(tenantId);
                        }else {
                            String servletPath = request.getServletPath();
                            if(!StrUtil.containsAny(servletPath,"/file/show","/login/getLoginUrl","/login/captcha")){
                                log.warn("http请求头未找到租户id:{}", servletPath);
                            }
                        }

                        return true;
                    }



            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

                TenantUtil.clear();

            }
        }).addPathPatterns("/**")
                .excludePathPatterns("/doc.html","/v3/api-docs/**")
        ;



    }


}
