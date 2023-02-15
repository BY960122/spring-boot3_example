package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * @uthor: BYDylan
 * @date: 2022/2/5
 * @description: 数据源配置类 注解: @PropertySource, 必须要配置,如果以 Spring boot 启动,配置在启动类 如果测试类启动,配置在此处
 *               注解: @ConfigurationPropertiesScan 同理,不过只要同级父目录下设置一个即可.
 */
@Configuration
public class MongoDBConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConfig.class);

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mongodb")
    public DruidDataSource mongodbSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "mongoTemplate")
    public MongoTemplate primaryJdbcTemplate(@Qualifier("mongodbSource") DruidDataSource dataSource) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(dataSource.getUrl()));
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
