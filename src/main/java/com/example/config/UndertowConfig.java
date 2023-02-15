package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * @author: BYDylan
 * @date: 2022/7/26
 * @description:
 */
@Configuration
public class UndertowConfig {
    @Value("${server.http-port}")
    private int httpPort;
    @Value("${server.port}")
    private int httpsPort;

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowListenerCustomizer() {
        return (factory) -> {
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(httpPort, "0.0.0.0"));
            factory.addDeploymentInfoCustomizers(deploymentInfo -> {
                WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
                webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
                deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo",
                    webSocketDeploymentInfo);
            });
        };
    }
}
