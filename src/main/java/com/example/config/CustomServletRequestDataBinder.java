package com.example.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestDataBinder;

import jakarta.servlet.ServletRequest;

/**
 * @author: BYDylan
 * @date: 2023/3/13
 * @description: 自定义CustomServletRequestDataBinder类, 继承ServletRequestDataBinder,
 *               重写addBindValues方法,将入参通过underLineToCamel方法进行转换处理
 */
public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    private final Pattern underLinePattern = Pattern.compile("_(\\w)");

    public CustomServletRequestDataBinder(final Object target) {
        super(target);
    }

    /**
     * 遍历请求参数对象 把请求参数的名转换成驼峰体,重写addBindValues绑定数值的方法
     * 
     * @param mpvs 请求参数列表
     * @param request 请求
     */
    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        List<PropertyValue> pvs = mpvs.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue pv : pvs) {
            String name = pv.getName();
            String camel = this.underLineToCamel(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, pv.getValue()));
            }
        }
        pvs.addAll(adds);
    }

    /**
     * 下划线转驼峰方法(如: 把app_id转换成appId)
     * 
     * @param value 要转换的下划线字符串
     * @return 驼峰体字符串
     */
    private String underLineToCamel(final String value) {
        final StringBuilder sb = new StringBuilder();
        Matcher m = this.underLinePattern.matcher(value);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase(Locale.ROOT));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}