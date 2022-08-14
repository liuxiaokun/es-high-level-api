package com.example.eshighlevelapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.eshighlevelapi.dto.SearchDTO;
import com.example.eshighlevelapi.model.SysMenuLog;
import com.example.eshighlevelapi.service.SysMenuLogService;
import lombok.extern.slf4j.Slf4j;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author liuxiaokun
 * @version 1.0.0
 * @since 2022年8月9日
 */
@Service
@Slf4j
public class SysMenuLogServiceImpl implements SysMenuLogService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void add(SysMenuLog sysMenuLog) {

        IndexRequest indexRequest = new IndexRequest("sys_menu_log");
        indexRequest.timeout(TimeValue.timeValueSeconds(5));
        indexRequest.source(JSON.toJSONString(sysMenuLog), XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(indexResponse.toString());
    }

    @Override
    public List<SysMenuLog> search(SearchDTO searchDTO) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchDTO.getKeyword(), "name", "email", "mobile", "menu_name", "menu_url"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("menu_time").gt(searchDTO.getStartDate().getTime())
                .lte(searchDTO.getEndDate().getTime()));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.rangeQuery("menu_time").gt(searchDTO.getStartDate().getTime())
//                .lte(searchDTO.getEndDate().getTime()));
        // 分页
        sourceBuilder.from((searchDTO.getPageNum() - 1) * searchDTO.getPageSize());
        sourceBuilder.size(searchDTO.getPageSize());
        sourceBuilder.sort("mobile", SortOrder.DESC);
        sourceBuilder.sort("name", SortOrder.DESC);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.field("email");
        highlightBuilder.field("mobile");
        highlightBuilder.field("menu_name");
        highlightBuilder.field("menu_url");
        //如果要多个字段高亮,这项要为false
        highlightBuilder.requireFieldMatch(false);
        //高亮设置
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        //最大高亮分片数
//        highlightBuilder.fragmentSize(800000);
        //从第一个分片获取高亮片段
//        highlightBuilder.numOfFragments(0);
        //配置高亮
        sourceBuilder.highlighter(highlightBuilder);
        SearchRequest searchRequest = new SearchRequest("sys_menu_log");
        searchRequest.source(sourceBuilder);
        searchRequest.source().query(boolQueryBuilder);

        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<SysMenuLog> list = new LinkedList<>();

        if (RestStatus.OK.equals(response.status())) {
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
                HighlightField emailField = highlightFields.get("email");
                HighlightField menuNameField = highlightFields.get("menu_name");
                HighlightField menuUrlField = highlightFields.get("menu_url");
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    if (fragments != null) {
                        String fragmentStr = fragments[0].string();
                        sysMenuLog.setName(fragmentStr);
                    }
                }
                if (mobileField != null) {
                    Text[] fragments = mobileField.getFragments();
                    if (fragments != null) {
                        String fragmentStr = fragments[0].string();
                        sysMenuLog.setMobile(fragmentStr);
                    }
                }

                if (emailField != null) {
                    Text[] fragments = emailField.getFragments();
                    if (fragments != null) {
                        String fragmentStr = fragments[0].string();
                        sysMenuLog.setEmail(fragmentStr);
                    }
                }

                if (menuNameField != null) {
                    Text[] fragments = menuNameField.getFragments();
                    if (fragments != null) {
                        String fragmentStr = fragments[0].string();
                        sysMenuLog.setMenuName(fragmentStr);
                    }
                }
                if (menuUrlField != null) {
                    Text[] fragments = menuUrlField.getFragments();
                    if (fragments != null) {
                        String fragmentStr = fragments[0].string();
                        sysMenuLog.setMenuUrl(fragmentStr);
                    }
                }

            }
        }

        return list;
    }
}
