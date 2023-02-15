package com.example.runner;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import com.example.model.PersonModel;
import com.example.tools.JsonTool;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description: 项目启动后, 自动运行初始化一些配置项之类的.注解 @Order ,当有多个实现时,value 越小,优先级越高
 */
@Order(value = 1)
@Component
public class TestRunner implements ApplicationRunner, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
    private final PersonModel personModel;
    private ApplicationContext applicationContext;

    public TestRunner(PersonModel personModel) {
        this.personModel = personModel;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("test runner: {}", JsonTool.toJsonString(personModel));
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : methodMap.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            String uri = getUriFromMappingInfo(requestMappingInfo);
            String method = getMethodFromMappingInfo(requestMappingInfo);
            LOGGER.info("interface method: {}, uri: {}", method, uri);
        }
    }

    private String getUriFromMappingInfo(RequestMappingInfo requestMappingInfo) {
        PathPatternsRequestCondition patternsCondition = requestMappingInfo.getPathPatternsCondition();
        Set<PathPattern> patterns = patternsCondition.getPatterns();
        if (!CollectionUtils.isEmpty(patterns)) {
            return patterns.iterator().next().toString();
        }
        return "";
    }

    private String getMethodFromMappingInfo(RequestMappingInfo requestMappingInfo) {
        RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
        Set<RequestMethod> methods = methodsCondition.getMethods();
        if (!CollectionUtils.isEmpty(methods)) {
            return methods.iterator().next().toString();
        }
        return "";
    }
}
