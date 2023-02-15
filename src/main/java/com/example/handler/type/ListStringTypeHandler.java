package com.example.handler.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

/**
 * @author: BYDylan
 * @date: 2022/7/23
 * @description: mybaits 字段转换 将数据库中1,2,3 转为 java List 接收.用法,在 @Select 下使用 Results(@Result(property = "deptName", column
 *               = "dept_name",typeHandler = ListTypeHandler.class)}) 如需使用xml形式.
 *               <result column="dept_name" property="deptName" typeHandler="com.example.handler.type.ListTypeHandler"
 *               />
 */
public class ListStringTypeHandler extends BaseTypeHandler<List<String>> {

    private static final String DELIM = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
        throws SQLException {
        String value = StringUtils.collectionToDelimitedString(parameter, DELIM);
        ps.setString(i, value);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return Arrays.asList(StringUtils.tokenizeToStringArray(value, DELIM));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return Arrays.asList(StringUtils.tokenizeToStringArray(value, DELIM));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return Arrays.asList(StringUtils.tokenizeToStringArray(value, DELIM));
    }
}