package com.example.eshighlevelapi.agg;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

/**
 * 聚合使用
 * Bucket, Metrics
 * @author liuxiaokun
 */
@SpringBootTest
@Slf4j
public class AggregationTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void bucketAgg() throws IOException {

        //1,准备request
        SearchRequest searchRequest = new SearchRequest("sys_menu_log");
        // 2，dsl
        //不需要文档，只要聚合
        searchRequest.source().size(0);
        // 对字段进行聚合,此段可以重复多次，对于多个进行聚合。
        searchRequest.source().aggregation(AggregationBuilders.terms("mobileAggr")
                .field("mobile").size(10));

        //3，请求
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        Aggregations aggregations = response.getAggregations();
        Terms mobileTerm = aggregations.get("mobileAggr");
        // 获取buckets
        List<? extends Terms.Bucket> buckets = mobileTerm.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            log.info(keyAsString);
        }
    }
}
