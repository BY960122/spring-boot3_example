package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.factory.YamlPropertySourceFactory;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/***
 * @author: BYDylan
 * @date: 2022/2/7
 * @description: 注解: MapperScan 必须要配置,否则找不到
 */
@MapperScan("com.example.mapper")
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S", defaultLockAtLeastFor = "PT1S")
@ServletComponentScan
@PropertySource(value = {"classpath:application.yml", "classpath:datasource.yml"},
    factory = YamlPropertySourceFactory.class)
@SpringBootApplication
public class WebRestfulApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebRestfulApplication.class, args);
    }

}
