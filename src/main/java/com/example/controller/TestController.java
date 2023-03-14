package com.example.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.example.constant.GlobalResponseEnum;
import com.example.event.OrderEvent;
import com.example.exception.CustomException;
import com.example.mapper.MongoMapper;
import com.example.mapper.MysqlMapper;
import com.example.model.*;
import com.example.service.AsyncService;
import com.example.tool.JsonTools;
import com.example.tool.SpringTools;

import jakarta.validation.Valid;

/**
 * @author: BYDylan
 * @date: 2022/1/31
 * @description: 注解 RestController = Controller + ResponseBody
 */
@RestController
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    private final MysqlMapper mysqlMapper;
    private final MongoMapper mongoMapper;
    private final PersonModel personModel;
    private final AsyncService asyncService;

    public TestController(MysqlMapper mysqlMapper, MongoMapper mongoMapper, PersonModel personModel,
        AsyncService asyncService) {
        this.mysqlMapper = mysqlMapper;
        this.mongoMapper = mongoMapper;
        this.personModel = personModel;
        this.asyncService = asyncService;
    }

    @GetMapping("/test/model")
    public Object testEntity() {
        LOGGER.info("person model: {}", JsonTools.toJsonString(personModel));
        Map<Object, Object> responseMap = new HashMap<>();
        responseMap.put("content", "test");
        responseMap.put("entity", personModel);
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS, responseMap);
    }

    @GetMapping("/test/mongodb")
    public Object testMongodb() {
        LOGGER.info("mongoMapper: {}", mongoMapper);
        // mongoMapper.createCollection("test");
        LOGGER.info("collectionExists: {}", mongoMapper.collectionExists("test"));
        MongoModel insert = mongoMapper.insert();
        Collection<MongoModel> yyyy = mongoMapper.insertMany("yyyy");
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS, mongoMapper.getCollectionNames());
    }

    @GetMapping("/test/mysql")
    public Object testMysql() {
        LOGGER.info("mysqlMapper: {}", mysqlMapper);
        List<String> tables = mysqlMapper.showDatabases();
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS, tables);
    }

    @GetMapping("/test/exception")
    public void testException() {
        throw new CustomException(GlobalResponseEnum.FAILED, "request error");
    }

    @PostMapping("/test/validation")
    public Object testValidation(@RequestBody @Valid DogModel dog) {
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS, dog);
    }

    @GetMapping("/test/async")
    public Object testAsync(@RequestParam(value = "count") Integer count) {
        for (int i = 1; i <= count; i++) {
            asyncService.execute(i);
        }
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS);
    }

    /**
     * 观察者模式
     */
    @PostMapping("/test/event")
    public Object testEvent(@RequestParam(value = "count") Integer count) {
        for (int index = 1; index <= count; index++) {
            OrderModel orderModel = new OrderModel();
            orderModel.setOrderId((long)index);
            orderModel.setBuyerName("Tom-" + index);
            orderModel.setSellerName("judy-" + index);
            orderModel.setAmount(100L);
            // 发布Spring事件通知
            LOGGER.info("product event id: {}", orderModel.getOrderId());
            SpringTools.getApplicationContext().publishEvent(new OrderEvent(orderModel));
        }
        return GlobalResponseModel.setResponse(GlobalResponseEnum.SUCCESS);
    }
}
