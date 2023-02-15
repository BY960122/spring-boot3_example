package com.example.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;

/**
 * @author: BYDylan
 * @date: 2022/2/21
 * @description: Elasticsearch 访问 版本 7.15- 参考资料: https://mp.weixin.qq.com/s/8csYNRg2066NpVPlsrZeHA
 *               https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-javadoc.html
 */
@Mapper
public class ElasticSearchMapper<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMapper.class);
    private final ElasticsearchClient client;

    public ElasticSearchMapper(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引
     * @return boolean
     */
    public boolean existsIndex(String index) {
        try {
            ExistsRequest existsRequest = new ExistsRequest.Builder().index(index).build();
            return client.indices().exists(existsRequest).value();
        } catch (IOException exception) {
            LOGGER.error("There is an error while getting index: {}", exception.getMessage());
        }
        return false;
    }

    /**
     * 创建索引
     *
     * @param indexName 索引
     * @param aliasesName 别名
     * @return boolean
     */
    public boolean createIndex(String indexName, String aliasesName, int numOfShards,
        Map<String, Property> properties) {
        try {
            TypeMapping typeMapping = new TypeMapping.Builder().properties(properties).build();
            IndexSettings indexSettings =
                new IndexSettings.Builder().numberOfShards(String.valueOf(numOfShards)).build();
            CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(indexName)
                .aliases(aliasesName, new Alias.Builder().isWriteIndex(true).build()).mappings(typeMapping)
                .settings(indexSettings).build();
            CreateIndexResponse response = client.indices().create(createIndexRequest);
            return response.acknowledged();
        } catch (IOException exception) {
            LOGGER.error("There is an error while creating index: {}", exception.getMessage());
        }
        return false;
    }

    /**
     * 删除索引
     *
     * @param indexList indexList
     * @return boolean
     */
    public boolean deleteIndex(List<String> indexList) {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexList).build();
            DeleteIndexResponse response = client.indices().delete(deleteIndexRequest);
            return response.acknowledged();
        } catch (IOException exception) {
            LOGGER.error("There is an error while deleting index: {}", exception.getMessage());
        }
        return false;
    }

    /**
     * 判断文档是否存在
     *
     * @param index index
     * @param id id
     * @return boolean
     */
    public boolean existsDocument(String index, String id, Class<T> clazz) {
        try {
            GetRequest getRequest = new GetRequest.Builder().index(index).id(id).build();
            GetResponse<T> getResponse = client.get(getRequest, clazz);
            return getResponse.found();
        } catch (IOException exception) {
            LOGGER.error("There is an error while judging if the document exists: {}", exception.getMessage());
        }
        return false;
    }

    /**
     * 保存文档 如果文档存在则更新文档
     *
     * @param index index
     * @param id id
     * @param data 数据
     * @return IndexResponse
     */
    public IndexResponse saveOrUpdateDocument(String index, String id, T data) {
        try {
            IndexRequest<T> indexRequest = new IndexRequest.Builder<T>().index(index).id(id).document(data).build();
            return client.index(indexRequest);
        } catch (IOException exception) {
            LOGGER.error("There is an error while saving the document: {}", exception.getMessage());
        }
        return null;
    }

    /**
     * 不指定IO保存文档
     *
     * @param index 索引
     * @param data 数据
     * @return IndexResponse
     */
    public IndexResponse saveOrUpdateDocument(String index, T data) {
        try {
            IndexRequest<T> indexRequest = new IndexRequest.Builder<T>().index(index).document(data).build();
            return client.index(indexRequest);
        } catch (IOException exception) {
            LOGGER.error("There is an error while saving the document: {}", exception.getMessage());
        }
        return null;
    }

    /**
     * 根据id获取文档
     *
     * @param index index
     * @param id id
     * @param clazz clazz
     * @return T
     */
    public T getById(String index, String id, Class<T> clazz) {
        try {
            GetRequest getRequest = new GetRequest.Builder().index(index).id(id).build();
            GetResponse<T> getResponse = client.get(getRequest, clazz);
            return getResponse.source();
        } catch (IOException exception) {
            LOGGER.error("There is an error while getting the document: {}", exception.getMessage());
        }
        return null;
    }

    /**
     * 根据id列表获取文档
     *
     * @param index index
     * @param idList id
     * @param clazz clazz
     * @return List<T>
     */
    public List<T> getByIdList(String index, List<String> idList, Class<T> clazz) {
        try {
            List<T> tList = new ArrayList<>(idList.size());
            for (String id : idList) {
                tList.add(client.get(new GetRequest.Builder().index(index).id(id).build(), clazz).source());
            }
            return tList;
        } catch (IOException exception) {
            LOGGER.error("There is an error while getting the document list: {}", exception.getMessage());
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param index index
     * @param pageNo pageNo
     * @param pageSize pageSize
     * @param clazz clazz
     * @return HitsMetadata<T>
     */
    public HitsMetadata<T> searchByPages(String index, Integer pageNo, Integer pageSize, Class<T> clazz) {
        try {
            SearchRequest searchRequest =
                new SearchRequest.Builder().index(Collections.singletonList(index)).from(pageNo).size(pageSize).build();
            SearchResponse<T> searchResponse = client.search(searchRequest, clazz);
            return searchResponse.hits();
        } catch (IOException exception) {
            LOGGER.error("There is an error while searching by pages: {}", exception.getMessage());
        }
        return null;
    }

    /**
     * 根据id删除文档
     *
     * @param id id
     */
    public boolean deleteById(String index, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder().index(index).id(id).build();
            DeleteResponse deleteResponse = client.delete(deleteRequest);
            return "deleted".equals(deleteResponse.result().jsonValue());
        } catch (IOException exception) {
            LOGGER.error("There is an error while deleting id document: {}", exception.getMessage());
        }
        return false;
    }
}
