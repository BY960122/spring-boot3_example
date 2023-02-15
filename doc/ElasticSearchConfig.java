package com.example.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: ElasticSearch 配置类
 * 版本 7.15-
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch", ignoreUnknownFields = false)
public class ElasticSearchConfig {
    private String host;
    private int port;
    private int connectTimeout;
    private int socketTimeout;
    private int maxConnectNum;
    private int maxConnectPerRoute;
    private int connectionRequestTimeout;

    /**
     * 新版
     * @return 返回 ElasticsearchClient
     */
//    @Bean
//    public ElasticsearchClient elasticsearchClient() {
//        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(host, port));
//        // 异步连接延时配置
//        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
//            requestConfigBuilder.setConnectTimeout(connectTimeout);
//            requestConfigBuilder.setSocketTimeout(socketTimeout);
//            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
//            return requestConfigBuilder;
//        });
//        // 异步连接数配置
//        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
//            httpClientBuilder.setMaxConnTotal(maxConnectNum);
//            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
//            return httpClientBuilder;
//        });
//        ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());
//        return new ElasticsearchClient(transport);
//    }

//    @Bean
//    public RestHighLevelClient restHighLevelClientOld() {
//        TransportClient
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(host + ":" + port)
//                .withConnectTimeout(Duration.ofSeconds(connectTimeout))
//                .withSocketTimeout(Duration.ofSeconds(socketTimeout))
//                .build();
//        return RestClients.create(clientConfiguration).rest();
//    }

    @Bean
    public RestHighLevelClient restHighLevelClientOlder() {
        HttpHost httpHost = new HttpHost(host, port, "http");
        RestClientBuilder builder = RestClient.builder(httpHost);
        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
        });
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
