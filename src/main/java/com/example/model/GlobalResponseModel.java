package com.example.model;

import com.example.constant.GlobalResponseEnum;

/**
 * @author: BYDylan
 * @date: 2022/2/7
 * @description: 全局返回实体类
 */
public class GlobalResponseModel {
    private Object code;
    private Object message;
    private Object data;

    private GlobalResponseModel(GlobalResponseEnum responseStatusEnum, Object data) {
        this.code = responseStatusEnum.getHttpStatus();
        this.message = responseStatusEnum.getMessage();
        this.data = data;
    }

    private GlobalResponseModel(Object code, Object message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static GlobalResponseModel setResponse(GlobalResponseEnum responseStatusEnum) {
        return new GlobalResponseModel(responseStatusEnum, null);
    }

    /**
     * 不让外部 new 对象
     *
     * @param responseStatusEnum 设置 http
     * @param data data
     * @return 返回实体
     */
    public static GlobalResponseModel setResponse(GlobalResponseEnum responseStatusEnum, Object data) {
        return new GlobalResponseModel(responseStatusEnum, data);
    }

    /**
     * 不让外部 new 对象
     *
     * @param code http code
     * @param message http message
     * @param data data
     * @return 返回实体
     */
    public static GlobalResponseModel setResponse(Object code, Object message, Object data) {
        return new GlobalResponseModel(code, message, data);
    }

    public Object getCode() {
        return code;
    }

    public Object getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
