package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: BYDylan
 * @date: 2022/2/7
 * @description: mysql 数据库访问层
 */
@Mapper
public interface MysqlMapper {
    @Transactional
    @Select("show databases")
    List<String> showDatabases();
}
