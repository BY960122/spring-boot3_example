package com.example.factory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import com.alibaba.druid.util.StringUtils;

/**
 * @uthor: BYDylan
 * @date: 2022/2/5
 * @description: 使 PropertySource注解支持 yaml格式配置文件
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    /**
     * 支持 解析 yaml 文件
     *
     * @param name 配置名
     * @param resource 资源
     * @return 返回
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        Properties ymlProperties = factory.getObject();
        String propertyName = !StringUtils.isEmpty(name) ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(propertyName, ymlProperties);
    }

    /**
     * 通用 解析 yaml 文件
     *
     * @param name 配置名
     * @param resource 资源
     * @return 返回
     */
    public PropertySource<?> commonCreatePropertySource(@Nullable String name, EncodedResource resource)
        throws IOException {
        String resourceName = Optional.ofNullable(name).orElse(resource.getResource().getFilename());
        if (resourceName.endsWith(".yml") || resourceName.endsWith(".yaml")) {
            List<PropertySource<?>> yamlSources =
                new YamlPropertySourceLoader().load(resourceName, resource.getResource());
            return yamlSources.get(0);
        } else {
            return new DefaultPropertySourceFactory().createPropertySource(name, resource);
        }
    }
}