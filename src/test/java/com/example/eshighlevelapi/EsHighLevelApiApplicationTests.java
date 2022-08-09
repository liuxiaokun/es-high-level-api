package com.example.eshighlevelapi;

import com.alibaba.fastjson.JSON;
import com.example.eshighlevelapi.model.SysMenuLog;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public void testCreateLog() throws IOException {
        // 准备好数据
        SysMenuLog sysMenuLog = new SysMenuLog("ceateLog", "18521599183", "ceateLog@qq.com",
                "首页", new Date(), "http://www.baidu.com/ceateLog/update");
        // 创建好index请求
        IndexRequest indexRequest = new IndexRequest("sys_menu_log");
        // 设置超时时间（默认）
        indexRequest.timeout(TimeValue.timeValueSeconds(5));
        // 往请求中添加数据
        indexRequest.source(JSON.toJSONString(sysMenuLog), XContentType.JSON);
        //执行添加请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        log.info(indexResponse.toString());
    }

    @Test
    public void testMatchMultiUser() throws IOException {
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
    public void testHighlight() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String key = "张首道";
        // match_phrase 完全匹配，不进行拆词
//      QueryBuilders.matchPhraseQuery("all", key);
        //match 进行拆词，然后将拆开的词进行 or 匹配，匹配到一个就可以
//		QueryBuilders.matchQuery("all", key);
        // 多个字段进行搜索， 第一个字段：搜索的内容，会进行拆词， 后面一次写入需要 匹配的字段
//        sourceBuilder.query(QueryBuilders.matchPhraseQuery("name",key));
        sourceBuilder.query(QueryBuilders.multiMatchQuery(key, "name", "email", "mobile"));
        // 获取记录数，默认10
        sourceBuilder.from(0);
        sourceBuilder.size(100);
        // 第一个是获取字段，第二个是过滤的字段，默认获取全部
//        sourceBuilder.fetchSource(new String[]{"id", "title", "content", "url"}, new String[]{});

        HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        highlightBuilder.field("name");      //高亮查询字段
        highlightBuilder.field("email");    //高亮查询字段
        highlightBuilder.field("mobile");    //高亮查询字段
        highlightBuilder.field("menu_name");    //高亮查询字段
        highlightBuilder.field("menu_url");    //高亮查询字段
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
//        highlightBuilder.preTags("<span>");   //高亮设置
//        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段
        //配置高亮
        sourceBuilder.highlighter(highlightBuilder);
        SearchRequest searchRequest = new SearchRequest("sys_menu_log");
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        if(RestStatus.OK.equals(response.status())){
            List<SysMenuLog> list = new LinkedList<>();
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Object name = hit.getSourceAsMap().get("name");
                Object mobile = hit.getSourceAsMap().get("mobile");
                Object email = hit.getSourceAsMap().get("email");
                Object menuName = hit.getSourceAsMap().get("menu_name");
                Object menuTime = hit.getSourceAsMap().get("menu_time");
                Object menuUrl = hit.getSourceAsMap().get("menu_url");

                SysMenuLog sysMenuLog = new SysMenuLog(name.toString(), mobile.toString(), email.toString(), menuName.toString(), new Date((Long) menuTime), menuUrl.toString());
                log.info(sysMenuLog.toString());
                list.add(sysMenuLog);

                //获取高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                HighlightField mobileField = highlightFields.get("mobile");
                HighlightField menuTimeField = highlightFields.get("menu_time");
                String id = hit.getId();
//			list.add(hit.getSourceAsString());
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    if(fragments != null){
                        String fragmentStr = fragments[0].string();
                        log.info("name highlight : " + fragmentStr);
                    }
                }
                if (mobileField != null) {
                    Text[] fragments = mobileField.getFragments();
                    if(fragments != null){
                        String fragmentStr = fragments[0].string();
                        log.info("mobile highlight : " + fragmentStr);
                    }
                }
                if (menuTimeField != null) {
                    Text[] fragments = menuTimeField.getFragments();
                    if(fragments != null){
                        String fragmentStr = fragments[0].string();
                        log.info("menuTimeField highlight : " + fragmentStr);
                    }
                }
            }
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
