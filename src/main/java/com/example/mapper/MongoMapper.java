package com.example.mapper;

import java.util.*;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.validation.Validator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.MongoModel;
import com.example.model.StatusModel;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description: mongodb 访问 参考资料: https://learnku.com/articles/60403
 */
@Repository
public class MongoMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMapper.class);

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 检测集合【是否存在】
     *
     * @return 集合是否存在
     */
    public boolean collectionExists(String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }

    /**
     * 查询结合
     *
     * @return 返回结果
     */
    public Set<String> getCollectionNames() {
        LOGGER.info("current db: {}", mongoTemplate.getDb().getName());
        return mongoTemplate.getCollectionNames();
    }

    /**
     * 创建一个大小没有限制的集合（默认集合创建方式）
     *
     * @return 创建集合的结果
     */
    public boolean createCollection(String collectionName) {
        mongoTemplate.createCollection(collectionName);
        return collectionExists(collectionName);
    }

    /**
     * 创建集合并设置 `capped=true` 创建 `固定大小集合`,可以配置参数 `size` 限制文档大小,可以配置参数 `max` 限制集合文档数量。 固定集合是指有着固定大小的集合,当达到最大值时,它会自动覆盖最早的文档
     * size = 1024L; max = 5L;
     *
     * @return 创建集合的结果
     */
    public boolean createCollectionFixedSize(String collectionName, long size, long max) {
        CollectionOptions collectionOptions = CollectionOptions.empty().capped().size(size).maxDocuments(max);
        mongoTemplate.createCollection(collectionName, collectionOptions);
        return collectionExists(collectionName);
    }

    /**
     * 创建集合并在文档"插入"与"更新"时进行数据效验,如果符合创建集合设置的条件就进允许更新与插入,否则则按照设置的设置的策略进行处理。 * 效验级别: - disable: 关闭数据校验。 - strict: (默认值)
     * 对所有的文档"插入"与"更新"操作有效。 - moderate: 仅对"插入"和满足校验规则的"文档"做"更新"操作有效。对已存在的不符合校验规则的"文档"无效。 * 执行策略: - error: (默认值)
     * 文档必须满足校验规则,才能被写入。 - warn: 对于"文档"不符合校验规则的 MongoDB 允许写入,但会记录一条告警到 mongod.log 中去。日志内容记录报错信息以及该"文档"的完整记录。 例如:
     * .where("age").gt(20);
     *
     * @return 创建集合结果
     */
    public boolean createCollectionValidation(String collectionName, String whereField, Object whereValue) {
        CriteriaDefinition criteria = Criteria.where(whereField).gt(whereValue);
        CollectionOptions collectionOptions = CollectionOptions.empty().validator(Validator.criteria(criteria))
            .strictValidation().failOnValidationError();
        mongoTemplate.createCollection(collectionName, collectionOptions);
        return collectionExists(collectionName);
    }

    /**
     * 删除【集合】
     *
     * @return 创建集合结果
     */
    public boolean dropCollection(String collectionName) {
        mongoTemplate.getCollection(collectionName).drop();
        return !mongoTemplate.collectionExists(collectionName);
    }

    /**
     * 创建视图 设置条件,用于筛选集合中的文档数据,只有符合条件的才会映射到视图中 例如: .parse("{\"$match\":{\"sex\":\"女\"}}")
     *
     * @return 创建视图结果
     */
    public boolean createView(String collectionName, String newViewName, String json) {
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add(Document.parse(json));
        mongoTemplate.getDb().createView(newViewName, collectionName, pipeline);
        return collectionExists(newViewName);
    }

    /**
     * 删除视图
     *
     * @return 删除视图结果
     */
    public boolean dropView(String viewName) {
        if (mongoTemplate.collectionExists(viewName)) {
            mongoTemplate.getDb().getCollection(viewName).drop();
        }
        return !collectionExists(viewName);
    }

    /**
     * 插入一条文档数据
     *
     * @return 插入的数据
     */
    public MongoModel insert() {
        MongoModel MongoModel = new MongoModel();
        StatusModel StatusModel = new StatusModel();
        StatusModel.setHeight(180);
        StatusModel.setWeight(150);
        MongoModel.setId("11");
        MongoModel.setAge(22);
        MongoModel.setSex("男");
        MongoModel.setRemake("无");
        MongoModel.setSalary(1500);
        MongoModel.setName("shiyi");
        MongoModel.setBirthday(new Date());
        MongoModel.setStatus(StatusModel);
        return mongoTemplate.insert(MongoModel);
    }

    /**
     * 插入【多条】文档数据,如果文档信息已经【存在就抛出异常】
     *
     * @return 插入的多个文档信息
     */
    public Collection<MongoModel> insertMany(String collectionName) {
        MongoModel mongoModel1 = new MongoModel();
        StatusModel StatusModel = new StatusModel();
        StatusModel.setHeight(180);
        StatusModel.setWeight(150);
        mongoModel1.setId("11");
        mongoModel1.setAge(22);
        mongoModel1.setSex("男");
        mongoModel1.setRemake("无");
        mongoModel1.setSalary(1500);
        mongoModel1.setName("shiyi");
        mongoModel1.setBirthday(new Date());
        mongoModel1.setStatus(StatusModel);
        List<MongoModel> mongoModelList = new ArrayList<>();
        mongoModelList.add(mongoModel1);
        return mongoTemplate.insert(mongoModelList, collectionName);
    }

    /**
     * 插入【多条】文档数据,如果文档信息已经【存在就抛出异常】 不用显示传入 collection name,通过类注解定义
     *
     * @return 插入的多个文档信息
     */
    public Collection<MongoModel> insertMany() {
        MongoModel mongoModel1 = new MongoModel();
        StatusModel StatusModel = new StatusModel();
        StatusModel.setHeight(180);
        StatusModel.setWeight(150);
        mongoModel1.setId("11");
        mongoModel1.setAge(22);
        mongoModel1.setSex("男");
        mongoModel1.setRemake("无");
        mongoModel1.setSalary(1500);
        mongoModel1.setName("shiyi");
        mongoModel1.setBirthday(new Date());
        mongoModel1.setStatus(StatusModel);
        List<MongoModel> mongoModelList = new ArrayList<>();
        mongoModelList.add(mongoModel1);
        return mongoTemplate.insert(mongoModelList, MongoModel.class);
    }

    /**
     * 存储【一条】用户信息,如果文档信息已经【存在就执行更新】
     *
     * @return 存储的文档信息
     */
    public MongoModel save(String collectionName) {
        MongoModel mongoModel1 = new MongoModel();
        StatusModel StatusModel = new StatusModel();
        StatusModel.setHeight(180);
        StatusModel.setWeight(150);
        mongoModel1.setId("11");
        mongoModel1.setAge(22);
        mongoModel1.setSex("男");
        mongoModel1.setRemake("无");
        mongoModel1.setSalary(1500);
        mongoModel1.setName("shiyi");
        mongoModel1.setBirthday(new Date());
        mongoModel1.setStatus(StatusModel);
        return mongoTemplate.save(mongoModel1, collectionName);
    }

    /**
     * 查询集合中的【全部】文档数据
     *
     * @return 全部文档列表
     */
    public List<MongoModel> findAll(String collectionName) {
        return mongoTemplate.findAll(MongoModel.class, collectionName);
    }

    /**
     * 根据【文档ID】查询集合中文档数据
     *
     * @return 文档信息
     */
    public MongoModel findById(String collectionName, String id) {
        return mongoTemplate.findById(id, MongoModel.class, collectionName);
    }

    /**
     * 根据【条件】查询集合中【符合条件】的文档,只取【第一条】数据
     *
     * @return 符合条件的第一条文档
     */
    public MongoModel findOne(String collectionName, String whereField, Object whereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【条件】查询集合中【符合条件】的文档,获取其【文档列表】
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> find(String collectionName, String whereField, Object whereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【条件】查询集合中【符合条件】的文档,获取其【文档列表】并【排序】
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findSort(String collectionName, String whereField, Object whereValue, String sortKey) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria).with(Sort.by(sortKey));
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【单个条件】查询集合中的文档数据,并【按指定字段进行排序】与【限制指定数目】
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findSortLimit(String collectionName, String whereField, Object whereValue, String sortKey,
        int limit) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria).with(Sort.by(sortKey)).limit(limit);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【单个条件】查询集合中的文档数据,并【按指定字段进行排序】与【并跳过指定数目】
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findSortSkip(String collectionName, String whereField, Object whereValue, String sortKey,
        long skip) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria).with(Sort.by(sortKey)).skip(skip);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 查询【存在指定字段名称】的文档数据
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findByExistsField(String collectionName, String whereField) {
        Criteria criteria = Criteria.where(whereField).exists(true);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【AND】关联多个查询条件,查询集合中的文档数据
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findByAnd(String collectionName, String whereField1, Object whereValue1, String whereField2,
        Object whereValue2) {
        Criteria criteria1 = Criteria.where(whereField1).is(whereValue1);
        Criteria criteria2 = Criteria.where(whereField2).is(whereValue2);
        Criteria criteria = new Criteria().andOperator(criteria1, criteria2);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【OR】关联多个查询条件,查询集合中的文档数据
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findByOr(String collectionName, String whereField1, Object whereValue1, String whereField2,
        Object whereValue2) {
        Criteria criteria1 = Criteria.where(whereField1).is(whereValue1);
        Criteria criteria2 = Criteria.where(whereField2).is(whereValue2);
        Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【IN】关联多个查询条件,查询集合中的文档数据
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findByIn(String collectionName, String whereField, Collection<?> whereValue) {
        Criteria criteria = Criteria.where(whereField).in(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【逻辑运算符】查询集合中的文档数据
     *
     * @return 符合条件的文档列表
     */
    public List<MongoModel> findByOperator(String collectionName, String whereField, Object gt, Object lte) {
        Criteria criteria = Criteria.where(whereField).gt(gt).lte(lte);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 根据【正则表达式】查询集合中的文档数据
     *
     * @return 符合条件的文档列表
     */
    public Object findByRegex(String collectionName, String whereField, String regex) {
        Criteria criteria = Criteria.where(whereField).regex(regex);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MongoModel.class, collectionName);
    }

    /**
     * 统计集合中符合【查询条件】的文档【数量】
     *
     * @return 符合条件的文档列表
     */
    public long countNumber(String collectionName, String whereField, Object whereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.count(query, MongoModel.class, collectionName);
    }

    /**
     * 更新集合中【匹配】查询到的第一条文档数据,如果没有找到就【创建并插入一个新文档】
     *
     * @return 执行更新的结果
     */
    public UpdateResult update(String collectionName, String whereField, Object whereValue, String updateWhereField,
        Object updateWhereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        Update update = new Update().set(updateWhereField, updateWhereValue);
        return mongoTemplate.upsert(query, update, MongoModel.class, collectionName);
    }

    /**
     * 更新集合中【匹配】查询到的【文档数据集合】中的【第一条数据】
     *
     * @return 执行更新的结果
     */
    public UpdateResult updateFirst(String collectionName, String whereField, Object whereValue, String sortKey,
        String updateWhereField, Object updateWhereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria).with(Sort.by(sortKey).ascending());
        Update update = new Update().set(updateWhereField, updateWhereValue);
        return mongoTemplate.updateFirst(query, update, MongoModel.class, collectionName);
    }

    /**
     * 更新【匹配查询】到的【文档数据集合】中的【所有数据】
     *
     * @return 执行更新的结果
     */
    public UpdateResult updateMany(String collectionName, String whereField, Object whereValue, String sortKey,
        String updateWhereField, Object updateWhereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria).with(Sort.by(sortKey).ascending());
        Update update = new Update().set(updateWhereField, updateWhereValue);
        return mongoTemplate.updateMulti(query, update, MongoModel.class, collectionName);
    }

    /**
     * 删除集合中【符合条件】的【一个]或[多个】文档
     *
     * @return 删除用户信息的结果
     */
    public DeleteResult remove(String collectionName, String whereField1, Object whereValue1, String whereField2,
        Object whereValue2) {
        Criteria criteria = Criteria.where(whereField1).is(whereValue1).and(whereField2).is(whereValue2);
        Query query = new Query(criteria);
        return mongoTemplate.remove(query, collectionName);
    }

    /**
     * 删除【符合条件】的【单个文档】,并返回删除的文档。
     *
     * @return 删除的用户信息
     */
    public MongoModel findAndRemove(String collectionName, String whereField, Object whereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.findAndRemove(query, MongoModel.class, collectionName);
    }

    /**
     * 删除【符合条件】的【全部文档】,并返回删除的文档。
     *
     * @return 删除的全部用户信息
     */
    public List<MongoModel> findAllAndRemove(String collectionName, String whereField, Object whereValue) {
        Criteria criteria = Criteria.where(whereField).is(whereValue);
        Query query = new Query(criteria);
        return mongoTemplate.findAllAndRemove(query, MongoModel.class, collectionName);
    }

    /**
     * 使用管道操作符 $group 结合 $count 方法进行聚合统计
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupCount(String collectionName, String groupField, String asName) {
        AggregationOperation group = Aggregation.group(groupField).count().as(asName);
        Aggregation aggregation = Aggregation.newAggregation(group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $max 进行聚合统计
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupMax(String collectionName, String groupField, String maxKey,
        String asName) {
        AggregationOperation group = Aggregation.group(groupField).max(maxKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $min 进行聚合统计
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupMin(String collectionName, String groupField, String minKey,
        String asName) {
        AggregationOperation group = Aggregation.group(groupField).min(minKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $sum 进行聚合统计
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupSum(String collectionName, String groupField, String sumKey,
        String asName) {
        AggregationOperation group = Aggregation.group(groupField).sum(sumKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $avg 进行聚合统计
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupAvg(String collectionName, String groupField, String avgKey,
        String asName) {
        AggregationOperation group = Aggregation.group(groupField).avg(avgKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $first 获取每个组的包含某字段的文档的第一条数据
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupFirst(String collectionName, String groupField, String firstKey,
        String asName) {
        AggregationOperation sort = Aggregation.sort(Sort.by(firstKey).ascending());
        AggregationOperation group = Aggregation.group(groupField).first(firstKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(sort, group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $last 获取每个组的包含某字段的文档的最后一条数据
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupLast(String collectionName, String groupField, String lastKey,
        String asName) {
        AggregationOperation sort = Aggregation.sort(Sort.by(lastKey).ascending());
        AggregationOperation group = Aggregation.group(groupField).last(lastKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(sort, group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用管道操作符 $group 结合表达式操作符 $push 获取某字段列表
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregationGroupPush(String collectionName, String groupField, String pushKey,
        String asName) {
        AggregationOperation push = Aggregation.group(groupField).push(pushKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(push);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $match 聚合,先使用 $match 过滤文档,然后再使用 $group 进行分组
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateGroupMatch(String collectionName, String whereField, Object ltValue,
        String groupField, String maxKey, String asName) {
        AggregationOperation match = Aggregation.match(Criteria.where(whereField).lt(ltValue));
        AggregationOperation group = Aggregation.group(groupField).max(maxKey).as(asName);
        Aggregation aggregation = Aggregation.newAggregation(match, group);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $sort 聚合,先使用 $group 进行分组,然后再使用 $sort 排序
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateGroupSort(String collectionName, String groupField, String maxField,
        String maxAs, String countAs) {
        AggregationOperation group = Aggregation.group(groupField).max(maxField).as(maxAs).count().as(countAs);
        AggregationOperation sort = Aggregation.sort(Sort.by(maxAs).ascending());
        Aggregation aggregation = Aggregation.newAggregation(group, sort);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $limit 聚合,先使用 $group 进行分组,然后再使用 $limit 限制一定数目文档
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateGroupLimit(String collectionName) {
        AggregationOperation group = Aggregation.group("age").sum("salary").as("sumSalary").max("salary")
            .as("maxSalary").min("salary").as("minSalary").avg("salary").as("avgSalary");
        AggregationOperation limit = Aggregation.limit(5L);
        Aggregation aggregation = Aggregation.newAggregation(group, limit);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $skip 聚合,先使用 $group 进行分组,然后再使用 $skip 跳过一定数目文档
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateGroupSkip(String collectionName) {
        AggregationOperation group = Aggregation.group("age").sum("salary").as("sumSalary").max("salary")
            .as("maxSalary").min("salary").as("minSalary").avg("salary").as("avgSalary");
        AggregationOperation limit = Aggregation.skip(2L);
        Aggregation aggregation = Aggregation.newAggregation(group, limit);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $project 聚合,先使用 $group 进行分组,然后再使用 $project 限制显示的字段
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateGroupProject(String collectionName) {
        AggregationOperation group =
            Aggregation.group("age").max("salary").as("maxSalary").min("salary").as("minSalary");
        AggregationOperation project = Aggregation.project("maxSalary");
        Aggregation aggregation = Aggregation.newAggregation(group, project);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 使用 $group 和 $unwind 聚合,先使用 $project 进行分组,然后再使用 $unwind 拆分文档中的数组为一条新文档记录
     *
     * @return 聚合结果
     */
    public AggregationResults<Map> aggregateProjectUnwind(String collectionName) {
        AggregationOperation project = Aggregation.project("name", "age", "title");
        AggregationOperation unwind = Aggregation.unwind("title");
        Aggregation aggregation = Aggregation.newAggregation(project, unwind);
        return mongoTemplate.aggregate(aggregation, collectionName, Map.class);
    }

    /**
     * 创建升序索引
     *
     * @return 索引信息
     */
    public String createAscendingIndex(String collectionName, String field) {
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(field));
    }

    /**
     * 创建降序索引
     *
     * @return 索引信息
     */
    public String createDescendingIndex(String collectionName, String field) {
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.descending(field));
    }

    /**
     * 创建升序复合索引
     *
     * @return 索引信息
     */
    public String createCompositeIndex(String collectionName, String field1, String field2) {
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(field1, field2));
    }

    /**
     * 创建文字索引
     *
     * @return 索引信息
     */
    public String createTextIndex(String collectionName, String field) {
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.text(field));
    }

    /**
     * 创建哈希索引
     *
     * @return 索引信息
     */
    public String createHashIndex(String collectionName, String field) {
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.hashed(field));
    }

    /**
     * 创建升序唯一索引
     *
     * @return 索引信息
     */
    public String createUniqueIndex(String collectionName, String field) {
        IndexOptions options = new IndexOptions();
        options.unique(true);
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(field), options);
    }

    /**
     * 创建局部索引
     *
     * @return 索引信息
     */
    public String createPartialIndex(String collectionName, String field) {
        IndexOptions options = new IndexOptions();
        options.partialFilterExpression(Filters.exists(field, true));
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(field), options);
    }

    /**
     * 获取当前【集合】对应的【所有索引】的【名称列表】
     *
     * @return 当前【集合】所有【索引名称列表】
     */
    public List<Document> getIndexAll(String collectionName) {
        ListIndexesIterable<Document> indexList = mongoTemplate.getCollection(collectionName).listIndexes();
        List<Document> result = new ArrayList<>();
        indexList.forEach(result::add);
        return result;
    }

    /**
     * 根据索引名称移除索引
     */
    public void removeIndex(String collectionName, String field) {
        mongoTemplate.getCollection(collectionName).dropIndex(field);
    }

    /**
     * 移除全部索引
     */
    public void removeIndexAll(String collectionName) {
        mongoTemplate.getCollection(collectionName).dropIndexes();
    }

    /**
     * 执行 mongoDB 自定义命令,详情可以查看：https://docs.mongodb.com/manual/reference/command/
     *
     * @return 执行命令返回结果的 Json 结果
     */
    public Document runCommand(String collectionName, String json) {
        return mongoTemplate.getDb().runCommand(Document.parse(json));
    }

    /**
     * 事物测试
     *
     * @return 事物
     */
    @Transactional(rollbackFor = Exception.class)
    public Object transactionTest(String collectionName) {
        StatusModel status = new StatusModel();
        status.setHeight(180);
        status.setWeight(150);
        MongoModel MongoModel = new MongoModel();
        MongoModel.setId("11");
        MongoModel.setAge(22);
        MongoModel.setSex("男");
        MongoModel.setRemake("无");
        MongoModel.setSalary(1500);
        MongoModel.setName("shiyi");
        MongoModel.setBirthday(new Date());
        MongoModel.setStatus(status);
        MongoModel newMongoModel1 = mongoTemplate.insert(MongoModel, collectionName);
        // 抛出异常,观察数据是否进行回滚
        int error = 1 / 2;
        return newMongoModel1;
    }
}
