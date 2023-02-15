package com.example.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.example.constant.GlobalResponseEnum;
import com.example.exception.CustomException;

/**
 * @author: BYDylan
 * @date: 2022/2/13
 * @description: 设置动态数据源
 */
@Primary
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);
    static ThreadLocal<String> dataSource = new ThreadLocal<>();
    private static Map<Object, Object> targetDataSources = new HashMap<>();

    private final DataSource mysqlDataSource;
    private final DataSource mysqlDataSource2;

    public DynamicDataSource(DataSource mysqlDataSource, DataSource mysqlDataSource2) {
        this.mysqlDataSource = mysqlDataSource;
        this.mysqlDataSource2 = mysqlDataSource2;
    }

    // 返回当前数据源标识
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = dataSource.get();
        if (!targetDataSources.containsKey(dataSourceName) && dataSourceName != null) {
            LOGGER.error("unknow datasource: {}", dataSourceName);
            throw new CustomException(GlobalResponseEnum.FAILED, "unknow datasource: " + dataSourceName);
        }
        return dataSourceName;

    }

    @Override
    public void afterPropertiesSet() {
        targetDataSources.put("mysqlDataSource", mysqlDataSource);
        targetDataSources.put("mysqlDataSource2", mysqlDataSource2);
        super.setTargetDataSources(targetDataSources);
        super.setDefaultTargetDataSource(mysqlDataSource);
        super.afterPropertiesSet();
    }
}
