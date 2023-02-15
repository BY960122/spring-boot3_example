package com.example.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

/**
 * @author: BYDylan
 * @date: 2022/10/18
 * @description: 实现异步调用
 */
@Configuration
public class AsyncEventConfig {
    @Bean
    public SimpleApplicationEventMulticaster
        applicationEventMulticaster(@Qualifier("defaultThreadPoolExecutor") ThreadPoolExecutor executor) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(executor);
        return multicaster;
    }
}
