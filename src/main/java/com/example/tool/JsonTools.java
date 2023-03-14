package com.example.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.constant.GlobalResponseEnum;
import com.example.exception.CustomException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author: BYDylan
 * @date: 2022/2/22
 * @description: Jackson 工具类
 */
public class JsonTools {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTools.class);

    static {
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 允许对象有json不存在的字段
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许有空值
        // MAPPER.getSerializerProvider().setNullKeySerializer(new Jsr310NullKeySerializer());
        // 允许有注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    /**
     * 转换成 json string
     *
     * @param object 传入对象
     * @return 返回 string
     */
    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) {
            throw new CustomException(GlobalResponseEnum.FAILED, "object is null, unable to convert");
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }

    /**
     * 将 json 转为对象
     *
     * @param json 传入 json
     * @param aClass 对象定义
     * @param <T> 泛型
     * @return 返回对象
     */
    public static <T> T toObject(String json, Class<T> aClass) {
        try {
            return MAPPER.readValue(json, aClass);
        } catch (IOException e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }

    /**
     * 将 json 转为 list
     *
     * @param json 传入 json
     * @param cla 对象定义
     * @param <T> 泛型
     * @return 返回 集合
     */
    public static <T> List<T> toArray(String json, Class<T> cla) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, cla);
        try {
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }

    /**
     * json 转 类型
     *
     * @param json 传入 json
     * @param type type
     * @param <T> 泛型定义
     * @return 返回对象
     */
    public static <T> T toObject(String json, Type type) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructType(type));
        } catch (Exception e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }

    /**
     * 输入流转对象
     *
     * @param inputStream 输入流
     * @param aClass 对象定义
     * @param <T> 泛型
     * @return 返回对象
     */
    public static <T> T toObject(InputStream inputStream, Class<T> aClass) {
        if (inputStream == null) {
            throw new CustomException(GlobalResponseEnum.FAILED, "InputStream is null");
        }
        try {
            return MAPPER.readValue(inputStream, aClass);
        } catch (IOException e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }

    /**
     * reader 转对象
     *
     * @param reader reader
     * @param aClass 对象定义
     * @param <T> 泛型
     * @return 返回对象
     */
    public static <T> T toObject(Reader reader, Class<T> aClass) {
        if (reader == null) {
            throw new CustomException(GlobalResponseEnum.FAILED, "reader is null");
        }
        try {
            return MAPPER.readValue(reader, aClass);
        } catch (IOException e) {
            LOGGER.error("json transformation error: ", e);
            throw new CustomException(GlobalResponseEnum.FAILED, "json transformation error");
        }
    }
}