package com.example.config;

import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import jakarta.servlet.ServletRequest;

/**
 * @author: BYDylan
 * @date: 2023/3/13
 * @description: 自定义属性处理器: CustomServletModelAttributeMethodProcessor
 *               继承ServletModelAttributeMethodProcessor类,重写bindRequestParameters方法,
 *               将bind方法替换为我们自定义的CustomServletRequestDataBinder
 */
public class CustomServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {
    CustomServletModelAttributeMethodProcessor(final boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    /**
     * ServletModelAttributeMethodProcessor此处使用的servletBinder.bind(servletRequest)
     * 修改的目的是为了将ServletRequestDataBinder换成自定的CustomServletRequestDataBinder
     */
    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder)binder;
        new CustomServletRequestDataBinder(servletBinder.getTarget()).bind(servletRequest);
    }
}
