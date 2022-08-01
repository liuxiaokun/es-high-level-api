package com.example.eshighlevelapi;

import com.alibaba.fastjson.JSON;
import com.example.eshighlevelapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    /**
     * 测试文档的添加，如果id存在就是更行
     */
    @Test
    public void testCreateDoc() throws IOException {
        // 准备好数据
        User user = new User("小名", 28, "男");
        // 创建好index请求
        IndexRequest indexRequest = new IndexRequest("user");
        // 设置索引
        indexRequest.id("3");
        // 设置超时时间（默认）
        indexRequest.timeout(TimeValue.timeValueSeconds(5));
        // 往请求中添加数据
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //执行添加请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(indexResponse.toString());
    }

    @Test
    public void testMatchMulti() throws IOException {
        SearchRequest request = new SearchRequest("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        String key = "红";
        builder.query(QueryBuilders.multiMatchQuery(key, "name", "sex"));
        request.source(builder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //获取搜索之后的数据
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            log.info(hit.getSourceAsString());
        }
    }


    @Test
    public void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        String key = "红";
        builder.query(QueryBuilders.matchAllQuery());
        request.source(builder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //获取搜索之后的数据
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            log.info(hit.getSourceAsString());
        }
    }

    @Test
    public void bulkOperation() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        //  bulkRequest.add 里可以写增删改操作，批量操作可以理解成, 运行之后一次性执行装填在bulk里的操作
        User user1 = new User("人1", 12, "女");
        User user2 = new User("人2", 13, "男");
        User user3 = new User("人3", 14, "女");
        bulkRequest.add(new IndexRequest().index("user").source(JSON.toJSONString(user1), XContentType.JSON));
        bulkRequest.add(new IndexRequest().index("user").source(JSON.toJSONString(user2), XContentType.JSON));
        bulkRequest.add(new IndexRequest().index("user").source(JSON.toJSONString(user3), XContentType.JSON));

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        log.info(bulk.toString());
    }
}
