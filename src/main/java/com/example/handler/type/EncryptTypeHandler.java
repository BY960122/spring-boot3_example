package com.example.handler.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.StringUtils;
import com.example.constant.GlobalResponseEnum;
import com.example.exception.CustomException;

/**
 * @author: BYDylan
 * @date: 2022/7/24
 * @description: 加解密TypeHandler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String.class)
public class EncryptTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
        throws SQLException {
        if (StringUtils.isEmpty(parameter)) {
            ps.setString(i, null);
            return;
        }
        try {
            ps.setString(i, ConfigTools.encrypt(parameter));
        } catch (Exception e) {
            throw new CustomException(GlobalResponseEnum.FAILED, "encrypt error");
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) {
        try {
            return ConfigTools.decrypt(rs.getString(columnName));
        } catch (Exception e) {
            throw new CustomException(GlobalResponseEnum.FAILED, "decrypt error");
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) {
        try {
            return ConfigTools.decrypt(rs.getString(columnIndex));
        } catch (Exception e) {
            throw new CustomException(GlobalResponseEnum.FAILED, "decrypt error");
        }
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) {
        try {
            return ConfigTools.decrypt(cs.getString(columnIndex));
        } catch (Exception e) {
            throw new CustomException(GlobalResponseEnum.FAILED, "decrypt error");
        }
    }
}
