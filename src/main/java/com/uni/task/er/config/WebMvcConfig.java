package com.uni.task.er.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtTokenInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration ir = registry.addInterceptor(authorizationInterceptor);
        ir.addPathPatterns("/**");
        ir.excludePathPatterns("/auth");
        ir.excludePathPatterns("/users/register");
        ir.excludePathPatterns("/swagger-ui/**");
        ir.excludePathPatterns("/v3/api-docs/**");
        ir.excludePathPatterns("/google/**"); 
        ir.excludePathPatterns("/*.html"); 
        ir.excludePathPatterns("/static/**");
    }
}