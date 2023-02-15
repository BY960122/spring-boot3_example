package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * @author: BYDylan
 * @date: 2022/2/5
 * @description: 数据源配置类 注解: @PropertySource, 必须要配置,如果以 Spring boot 启动,配置在启动类 如果测试类启动,配置在此处
 *               注解: @ConfigurationPropertiesScan 同理,不过只要同级父目录下设置一个即可.
 */
@Configuration
public class JdbcConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConfig.class);

    @Value("${mybatis.mapper-locations}")
    private String mapperLocation;

    // @Value("${mybatis.config-location}")
    // private String configLocation;

    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql1")
    public DruidDataSource mysqlDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mysql2")
    public DruidDataSource mysqlDataSource2() {
        return DruidDataSourceBuilder.create().build();
    }

    // @Bean(name = "sqlSessionFactory")
    // public SqlSessionFactory sqlSessionFactory() throws Exception {
    // SqlSessionFactoryBean sqlSessionFactoryBean = null;
    // try {
    // // 加载JNDI配置
    // Context context = new InitialContext();
    // // 实例化 SqlSessionFactory
    // sqlSessionFactoryBean = new SqlSessionFactoryBean();
    // // 配置数据源
    // sqlSessionFactoryBean.setDataSource(mysqlDataSource());
    // // 加载 Mybatis 配置文件
    // PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    // // 可以加载多个
    // sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(mapperLocation));
    // // 配置 Mybatis 的 config 文件
    // sqlSessionFactoryBean.setConfigLocation(resourcePatternResolver.getResource(configLocation));
    // } catch (Exception e) {
    // LOGGER.error("create SqlSessionFactory error: {}", e.getMessage());
    // }
    // return sqlSessionFactoryBean.getObject();
    // }
}
