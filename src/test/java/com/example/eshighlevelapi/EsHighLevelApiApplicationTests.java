package com.example.eshighlevelapi;

import com.alibaba.fastjson.JSON;
import com.example.eshighlevelapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@Slf4j
class EsHighLevelApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
    }

    // 测试文档的添加
    @Test
    public void testCreateDoc() throws IOException {
        // 准备好数据
        User user = new User("小杰", 23, "男");
        // 创建好index请求
        IndexRequest indexRequest = new IndexRequest("user");
        // 设置索引
        indexRequest.id("1");
        // 设置超时时间（默认）
        indexRequest.timeout(TimeValue.timeValueSeconds(5));
        // 往请求中添加数据
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //执行添加请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(indexResponse.toString());
    }
}
