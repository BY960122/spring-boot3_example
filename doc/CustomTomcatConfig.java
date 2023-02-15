package com.example.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: BYDylan
 * @date: 2022/5/31
 * @description: 自定义 tomcat 容器配置
 */
@Component
public class CustomTomcatConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Value("${server.port}")
    private int httpsPort;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(connector -> {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            protocol.setMaxConnections(100);
            protocol.setMaxThreads(100);
            protocol.setSelectorTimeout(30000);
            protocol.setSessionTimeout(30000);
            protocol.setConnectionTimeout(30000);
            protocol.setPort(httpsPort);
            protocol.setSSLEnabled(true);
        });
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setMaxPostSize(81920);
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(8090);
        connector.setRedirectPort(httpsPort);
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setMaxHttpHeaderSize(81920);
        ((TomcatServletWebServerFactory) factory).addAdditionalTomcatConnectors(connector);
    }
}
