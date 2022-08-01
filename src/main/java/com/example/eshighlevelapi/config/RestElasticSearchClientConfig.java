package com.example.eshighlevelapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestElasticSearchClientConfig {

    @Value("${elasticsearch.hostname}")
    private String hostname;

    /**
     * 将方法的返回结果交给spring管理
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 主机ip和端口号以及协议
        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, 9200, "http")));
    }
}