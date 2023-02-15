package com.example.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description: 测试监听器 传统 spring web 项目使用 ContextLoaderListener ,来初始化一些代码 springboot 通过 @WebListener 注解 + implements
 *               ServletContextListener 实现,同时需要在启动类配置 @ServletComponentScan 注解
 */
@WebListener
public class TestListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        LOGGER.info("TestListener - contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
        LOGGER.info("TestListener - contextDestroyed");
    }
}
