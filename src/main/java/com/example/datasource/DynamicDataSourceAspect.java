package com.example.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.example.annotation.CustomDataSource;

/**
 * @author: BYDylan
 * @date: 2022/2/13
 * @description: 通过 aspect 获取注解的数据源
 */
@Aspect
@Component
public class DynamicDataSourceAspect implements Ordered {
    @Before("@annotation(customDataSource)")
    public void before(JoinPoint point, CustomDataSource customDataSource) {
        DynamicDataSource.dataSource.set(customDataSource.value());
    }

    @After("@annotation(customDataSource)")
    public void restoreDataSource(JoinPoint point, CustomDataSource customDataSource) {
        DynamicDataSource.dataSource.remove();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
