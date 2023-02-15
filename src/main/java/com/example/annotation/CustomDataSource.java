package com.example.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: BYDylan
 * @date: 2022/2/13
 * @description: 自定义数据源
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomDataSource {
    String value() default "mysqlDataSource";
}
