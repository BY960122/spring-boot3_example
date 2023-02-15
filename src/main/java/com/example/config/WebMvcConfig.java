package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.LoginInterceptor;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description: 拦截器配置类 addInterceptor().addPathPatterns("/testMysql") 指定拦截的路径正则
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/testEntity");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
