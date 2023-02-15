package com.example.mapper;

import com.example.model.UserInfoEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: Elasticsearch 访问
 * 版本 7.15-
 * 参考资料: https://mp.weixin.qq.com/s/8csYNRg2066NpVPlsrZeHA
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-javadoc.html
 */
@Repository("elasticSearchMapper")
public class ElasticSearchMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMapper.class);
    private final RestHighLevelClient client;

    public ElasticSearchMapper(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean createIndex(String indexName) throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .field("dynamic", true)
                .startObject("properties")
                .startObject("name")
                .field("type", "text")
                .startObject("fields")
                .startObject("keyword")
                .field("type", "keyword")
                .endObject()
                .endObject()
                .endObject()
                .startObject("address")
                .field("type", "text")
                .startObject("fields")
                .startObject("keyword")
                .field("type", "keyword")
                .endObject()
                .endObject()
                .endObject()
                .startObject("remark")
                .field("type", "text")
                .startObject("fields")
                .startObject("keyword")
                .field("type", "keyword")
                .endObject()
                .endObject()
                .endObject()
                .startObject("age")
                .field("type", "integer")
                .endObject()
                .startObject("salary")
                .field("type", "float")
                .endObject()
                .startObject("birthDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .startObject("createTime")
                .field("type", "date")
                .endObject()
                .endObject()
                .endObject();
        Settings settings = Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .build();
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(settings);
        request.mapping(mapping);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    public IndexResponse addDocument(String indexName, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(id);
        UserInfoEntity userInfo = new UserInfoEntity();
        userInfo.setName("张三");
        userInfo.setAge(29);
        userInfo.setSalary(100.00f);
        userInfo.setAddress("北京市");
        userInfo.setRemark("来自北京市的张先生");
        userInfo.setCreateTime(new Date());
        userInfo.setBirthDate("1990-01-10");
        // 设置文档内容
        ObjectMapper mapper = new ObjectMapper();
        indexRequest.source(mapper.writeValueAsString(userInfo), XContentType.JSON);
        // 执行增加文档
        return client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public GetResponse getDocument(String indexName, String id) throws IOException {
        // 获取请求对象
        GetRequest getRequest = new GetRequest(indexName, id);
        // 获取文档信息
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        // 将 JSON 转换成对象
        ObjectMapper mapper = new ObjectMapper();
        if (getResponse.isExists()) {
            UserInfoEntity userInfo = mapper.readValue(getResponse.getSourceAsBytes(), UserInfoEntity.class);
            LOGGER.info("员工信息：{}", userInfo);
        }
        return getResponse;
    }

    public UpdateResponse updateDocument(String indexName, String id) throws IOException {
        // 创建索引请求对象
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        // 设置员工更新信息
        UserInfoEntity userInfo = new UserInfoEntity();
        userInfo.setSalary(200.00f);
        userInfo.setAddress("北京市海淀区");
        // 将对象转换为 byte 数组
        // 设置更新文档内容
        ObjectMapper mapper = new ObjectMapper();
        updateRequest.doc(mapper.writeValueAsString(userInfo), XContentType.JSON);
        // 执行更新文档
        return client.update(updateRequest, RequestOptions.DEFAULT);
    }

    /**
     * 精确查询
     * GET test-index/_search
     * {
     * "query": {
     * "term": {
     * "address.keyword": {
     * "value": "北京市通州区"
     * }
     * }
     * }
     * }
     */
    public SearchResponse termQuery(String indexName) throws IOException {
        // 构建查询条件（注意：termQuery 支持多种格式查询，如 boolean,int,double,string 等，这里使用的是 string 的查询）
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("address.keyword", "北京市通州区"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 多个内容在一个字段中进行查询
     * GET test-index/_search
     * {
     * "query": {
     * "terms": {
     * "address.keyword": [
     * "北京市丰台区",
     * "北京市昌平区",
     * "北京市大兴区"
     * ]
     * }
     * }
     * }
     */
    public SearchResponse termsQuery(String indexName) throws IOException {
        // 构建查询条件（注意：termsQuery 支持多种格式查询，如 boolean、int、double、string 等，这里使用的是 string 的查询）
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery("address.keyword", "北京市丰台区", "北京市昌平区", "北京市大兴区"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 匹配查询符合条件的所有数据,并设置分页
     * GET test-index/_search
     * {
     * "query": {
     * "match_all": {}
     * },
     * "from": 0,
     * "size": 10,
     * "sort": [
     * {
     * "salary": {
     * "order": "asc"
     * }
     * }
     * ]
     * }
     */
    public SearchResponse matchAllQuery(String indexName) throws IOException {
        // 构建查询条件
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        // 创建查询源构造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQueryBuilder);
        // 设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(3);
        // 设置排序
        searchSourceBuilder.sort("salary", SortOrder.ASC);
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 匹配查询数据
     * GET test-index/_search
     * {
     * "query": {
     * "match": {
     * "address": "通州区"
     * }
     * }
     * }
     */
    public SearchResponse matchQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "*通州区"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 词语匹配查询
     * GET test-index/_search
     * {
     * "query": {
     * "match_phrase": {
     * "address": "北京市通州区"
     * }
     * }
     * }
     */
    public SearchResponse matchPhraseQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("address", "北京市通州区"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 内容在多字段中进行查询
     * GET test-index/_search
     * {
     * "query": {
     * "multi_match": {
     * "query": "北京",
     * "fields": ["address","remark"]
     * }
     * }
     * }
     */
    public SearchResponse matchMultiQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("北京市", "address", "remark"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 模糊查询所有以 三 结尾的姓名
     * GET test-index/_search
     * {
     * "query": {
     * "fuzzy": {
     * "name": "三"
     * }
     * }
     * }
     */
    public SearchResponse fuzzyQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name", "三").fuzziness(Fuzziness.AUTO));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 查询岁数 ≥ 30 岁的员工数据
     * GET /test-index/_search
     * {
     * "query": {
     * "range": {
     * "age": {
     * "gte": 30
     * }
     * }
     * }
     * }
     */
    public SearchResponse rangeQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("age").gte(30));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 查询距离现在 30 年间的员工数据
     * [年(y)、月(M)、星期(w)、天(d)、小时(h)、分钟(m)、秒(s)]
     * 例如：
     * now-1h 查询一小时内范围
     * now-1d 查询一天内时间范围
     * now-1y 查询最近一年内的时间范围
     * <p>
     * GET test-index/_search
     * {
     * "query": {
     * "range": {
     * "birthDate": {
     * "gte": "now-30y"
     * }
     * }
     * }
     * }
     */
    public SearchResponse dateRangeQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // includeLower（是否包含下边界）、includeUpper（是否包含上边界）
        searchSourceBuilder.query(QueryBuilders.rangeQuery("birthDate")
                .gte("now-30y").includeLower(true).includeUpper(true));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 查询所有以 “三” 结尾的姓名
     * *：表示多个字符（0个或多个字符）
     * ?：表示单个字符
     * <p>
     * GET test-index/_search
     * {
     * "query": {
     * "wildcard": {
     * "name.keyword": {
     * "value": "*三"
     * }
     * }
     * }
     * }
     */
    public SearchResponse wildcardQuery(String indexName) throws IOException {
        // 构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword", "*三"));
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
        return searchResponse;
    }

    /**
     * 查询出生在 1990-1995 年期间，且地址在 北京市昌平区、北京市大兴区、北京市房山区 的员工信息
     * GET /test-index/_search
     * {
     * "query": {
     * "bool": {
     * "filter": {
     * "range": {
     * "birthDate": {
     * "format": "yyyy",
     * "gte": 1990,
     * "lte": 1995
     * }
     * }
     * },
     * "must": [
     * {
     * "terms": {
     * "address.keyword": [
     * "北京市昌平区",
     * "北京市大兴区",
     * "北京市房山区"
     * ]
     * }
     * }
     * ]
     * }
     * }
     * }
     */
    public void boolQuery(String indexName) throws IOException {
        // 创建 Bool 查询构建器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 构建查询条件
        boolQueryBuilder.must(QueryBuilders.termsQuery("address.keyword", "北京市昌平区", "北京市大兴区", "北京市房山区"))
                .filter().add(QueryBuilders.rangeQuery("birthDate").format("yyyy").gte("1990").lte("1995"));
        // 构建查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        // 创建查询请求对象，将查询对象配置到其中
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        // 执行查询，然后处理响应结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 根据状态和数据条数验证是否返回了数据
        if (RestStatus.OK.equals(searchResponse.status()) && searchResponse.getHits().getTotalHits().value > 0) {
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                ObjectMapper mapper = new ObjectMapper();
                UserInfoEntity userInfo = mapper.readValue(hit.getSourceAsString(), UserInfoEntity.class);
                LOGGER.info(userInfo.toString());
            }
        }
    }

    /**
     * 统计员工总数、工资最高值、工资最低值、工资平均工资、工资总和：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_stats": {
     * "stats": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationStats(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.stats("salary_stats").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        // 设置查询结果不返回，只返回聚合结果
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Stats 对象
            ParsedStats aggregation = aggregations.get("salary_stats");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("count：{}", aggregation.getCount());
            LOGGER.info("avg：{}", aggregation.getAvg());
            LOGGER.info("max：{}", aggregation.getMax());
            LOGGER.info("min：{}", aggregation.getMin());
            LOGGER.info("sum：{}", aggregation.getSum());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 统计员工工资最低值：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_min": {
     * "min": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationMin(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.min("salary_min").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Min 对象
            ParsedMin aggregation = aggregations.get("salary_min");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("min：{}", aggregation.getValue());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 统计员工工资最高值：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_max": {
     * "max": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationMax(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.max("salary_max").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Max 对象
            ParsedMax aggregation = aggregations.get("salary_max");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("max：{}", aggregation.getValue());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }


    /**
     * 统计员工工资平均值：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_avg": {
     * "avg": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationAvg(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.avg("salary_avg").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Avg 对象
            ParsedAvg aggregation = aggregations.get("salary_avg");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("avg：{}", aggregation.getValue());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 统计员工工资总值：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_sum": {
     * "sum": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationSum(String indexName) throws IOException {
        // 设置聚合条件
        SumAggregationBuilder aggr = AggregationBuilders.sum("salary_sum").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Sum 对象
            ParsedSum aggregation = aggregations.get("salary_sum");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("sum：{}", aggregation.getValue());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 统计员工总数：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "employee_count": {
     * "value_count": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationCount(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.count("employee_count").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 ValueCount 对象
            ParsedValueCount aggregation = aggregations.get("employee_count");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            LOGGER.info("count：{}", aggregation.getValue());
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 统计员工工资百分位：
     * <p>
     * GET /test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_percentiles": {
     * "percentiles": {
     * "field": "salary"
     * }
     * }
     * }
     * }
     */
    public SearchResponse aggregationPercentiles(String indexName) throws IOException {
        // 设置聚合条件
        AggregationBuilder aggr = AggregationBuilders.percentiles("salary_percentiles").field("salary");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggr);
        searchSourceBuilder.size(0);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status()) || aggregations != null) {
            // 转换为 Percentiles 对象
            ParsedPercentiles aggregation = aggregations.get("salary_percentiles");
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Percentile percentile : aggregation) {
                LOGGER.info("百分位：{}：{}", percentile.getPercent(), percentile.getValue());
            }
            LOGGER.info("-------------------------------------------");
        }
        // 根据具体业务逻辑返回不同结果，这里为了方便直接将返回响应对象Json串
        return response;
    }

    /**
     * 按岁数进行聚合分桶，统计各个岁数员工的人数：
     * <p>
     * GET test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "age_bucket": {
     * "terms": {
     * "field": "age",
     * "size": "10"
     * }
     * }
     * }
     * }
     */
    public void aggrBucketTerms(String indexName) throws IOException {
        AggregationBuilder aggr = AggregationBuilders.terms("age_bucket").field("age");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10);
        searchSourceBuilder.aggregation(aggr);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status())) {
            // 分桶
            Terms byCompanyAggregation = aggregations.get("age_bucket");
            List<? extends Terms.Bucket> buckets = byCompanyAggregation.getBuckets();
            // 输出各个桶的内容
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Terms.Bucket bucket : buckets) {
                LOGGER.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
            }
            LOGGER.info("-------------------------------------------");
        }
    }


    /**
     * 按工资范围进行聚合分桶，统计工资在 3000-5000、5000-9000 和 9000 以上的员工信息：
     * <p>
     * GET test-index/_search
     * {
     * "aggs": {
     * "salary_range_bucket": {
     * "range": {
     * "field": "salary",
     * "ranges": [
     * {
     * "key": "低级员工",
     * "to": 3000
     * },{
     * "key": "中级员工",
     * "from": 5000,
     * "to": 9000
     * },{
     * "key": "高级员工",
     * "from": 9000
     * }
     * ]
     * }
     * }
     * }
     * }
     */
    public void aggrBucketRange(String indexName) throws IOException {
        AggregationBuilder aggr = AggregationBuilders.range("salary_range_bucket")
                .field("salary")
                .addUnboundedTo("低级员工", 3000)
                .addRange("中级员工", 5000, 9000)
                .addUnboundedFrom("高级员工", 9000);
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(aggr);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status())) {
            // 分桶
            Range byCompanyAggregation = aggregations.get("salary_range_bucket");
            List<? extends Range.Bucket> buckets = byCompanyAggregation.getBuckets();
            // 输出各个桶的内容
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Range.Bucket bucket : buckets) {
                LOGGER.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
            }
            LOGGER.info("-------------------------------------------");
        }
    }

    /**
     * 按照时间范围进行分桶，统计 1985-1990 年和 1990-1995 年出生的员工信息：
     * <p>
     * GET test-index/_search
     * {
     * "size": 10,
     * "aggs": {
     * "date_range_bucket": {
     * "date_range": {
     * "field": "birthDate",
     * "format": "yyyy",
     * "ranges": [
     * {
     * "key": "出生日期1985-1990的员工",
     * "from": "1985",
     * "to": "1990"
     * },{
     * "key": "出生日期1990-1995的员工",
     * "from": "1990",
     * "to": "1995"
     * }
     * ]
     * }
     * }
     * }
     * }
     */
    public void aggrBucketDateRange(String indexName) throws IOException {
        AggregationBuilder aggr = AggregationBuilders.dateRange("date_range_bucket")
                .field("birthDate")
                .format("yyyy")
                .addRange("1985-1990", "1985", "1990")
                .addRange("1990-1995", "1990", "1995");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(aggr);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status())) {
            // 分桶
            Range byCompanyAggregation = aggregations.get("date_range_bucket");
            List<? extends Range.Bucket> buckets = byCompanyAggregation.getBuckets();
            // 输出各个桶的内容
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Range.Bucket bucket : buckets) {
                LOGGER.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
            }
            LOGGER.info("-------------------------------------------");
        }
    }

    /**
     * 按工资多少进行聚合分桶，设置统计的最小值为 0，最大值为 12000，区段间隔为 3000：
     * <p>
     * GET test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "salary_histogram": {
     * "histogram": {
     * "field": "salary",
     * "extended_bounds": {
     * "min": 0,
     * "max": 12000
     * },
     * "interval": 3000
     * }
     * }
     * }
     * }
     */
    public void aggrBucketHistogram(String indexName) {
        try {
            AggregationBuilder aggr = AggregationBuilders.histogram("salary_histogram")
                    .field("salary")
                    .extendedBounds(0, 12000)
                    .interval(3000);
            // 查询源构建器
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(aggr);
            // 创建查询请求对象，将查询条件配置到其中
            SearchRequest request = new SearchRequest(indexName);
            request.source(searchSourceBuilder);
            // 执行请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 获取响应中的聚合信息
            Aggregations aggregations = response.getAggregations();
            // 输出内容
            if (RestStatus.OK.equals(response.status())) {
                // 分桶
                Histogram byCompanyAggregation = aggregations.get("salary_histogram");
                List<? extends Histogram.Bucket> buckets = byCompanyAggregation.getBuckets();
                // 输出各个桶的内容
                LOGGER.info("-------------------------------------------");
                LOGGER.info("聚合信息:");
                for (Histogram.Bucket bucket : buckets) {
                    LOGGER.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
                }
                LOGGER.info("-------------------------------------------");
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * 按出生日期进行分桶：
     * <p>
     * GET test-index/_search
     * {
     * "size": 0,
     * "aggs": {
     * "birthday_histogram": {
     * "date_histogram": {
     * "format": "yyyy",
     * "field": "birthDate",
     * "interval": "year"
     * }
     * }
     * }
     * }
     */
    public void aggrBucketDateHistogram(String indexName) throws IOException {
        AggregationBuilder aggr = AggregationBuilders.dateHistogram("birthday_histogram")
                .field("birthDate")
//                    .interval(1)
//                    .dateHistogramInterval(DateHistogramInterval.YEAR)
                .format("yyyy");
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(aggr);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status())) {
            // 分桶
            Histogram byCompanyAggregation = aggregations.get("birthday_histogram");

            List<? extends Histogram.Bucket> buckets = byCompanyAggregation.getBuckets();
            // 输出各个桶的内容
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Histogram.Bucket bucket : buckets) {
                LOGGER.info("桶名：{} | 总数：{}", bucket.getKeyAsString(), bucket.getDocCount());
            }
            LOGGER.info("-------------------------------------------");
        }
    }

    /**
     * 按照员工岁数分桶、然后统计每个岁数员工工资最高值:
     * <p>
     * GET test-index/_search
     * <p>
     * {
     * "size": 0,
     * "aggs": {
     * "salary_bucket": {
     * "terms": {
     * "field": "age",
     * "size": "10"
     * },
     * "aggs": {
     * "salary_max_user": {
     * "top_hits": {
     * "size": 1,
     * "sort": [
     * {
     * "salary": {
     * "order": "desc"
     * }
     * }
     * ]
     * }
     * }
     * }
     * }
     * }
     * }
     */
    public void aggregationTopHits(String indexName) throws IOException {
        AggregationBuilder testTop = AggregationBuilders.topHits("salary_max_user")
                .size(1)
                .sort("salary", SortOrder.DESC);
        AggregationBuilder salaryBucket = AggregationBuilders.terms("salary_bucket")
                .field("age")
                .size(10);
        salaryBucket.subAggregation(testTop);
        // 查询源构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(salaryBucket);
        // 创建查询请求对象，将查询条件配置到其中
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 获取响应中的聚合信息
        Aggregations aggregations = response.getAggregations();
        // 输出内容
        if (RestStatus.OK.equals(response.status())) {
            // 分桶
            Terms byCompanyAggregation = aggregations.get("salary_bucket");
            List<? extends Terms.Bucket> buckets = byCompanyAggregation.getBuckets();
            // 输出各个桶的内容
            LOGGER.info("-------------------------------------------");
            LOGGER.info("聚合信息:");
            for (Terms.Bucket bucket : buckets) {
                LOGGER.info("桶名：{}", bucket.getKeyAsString());
                ParsedTopHits topHits = bucket.getAggregations().get("salary_max_user");
                for (SearchHit hit : topHits.getHits()) {
                    LOGGER.info(hit.getSourceAsString());
                }
            }
            LOGGER.info("-------------------------------------------");
        }
    }

    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(request, RequestOptions.DEFAULT);
        return acknowledgedResponse.isAcknowledged();
    }

}
