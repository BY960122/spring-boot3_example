package com.example.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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

    /**
     * @param resolvers 配置入参转换器 - 重写HTTP请求参数转换规则(前端请求参数为下划线类型,自动转换为驼峰形式进行对象参数封装)
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomServletModelAttributeMethodProcessor(true));
    }
}
