package com.example.tools;

import java.lang.reflect.Array;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class SpringTool implements ApplicationContextAware {

    private static ApplicationContext context;

    private SpringTool() {}

    public static <T> T getBean(Class<T> clazz) {
        if (Objects.isNull(context) || Objects.isNull(clazz)) {
            return null;
        }

        return context.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] getBeans(Class<T> clazz) {
        T[] arr = (T[])Array.newInstance(clazz, 0);
        if (Objects.isNull(context) || Objects.isNull(clazz)) {
            return arr;
        }

        return context.getBeansOfType(clazz).values().toArray(arr);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
