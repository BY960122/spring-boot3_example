package com.example.constant;

import org.springframework.http.HttpStatus;

/**
 * @author: BYDylan
 * @date: 2022/2/7
 * @description: 返回体状态封装
 */
public enum GlobalResponseEnum {
    SUCCESS(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()),
    FAILED(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());

    // http 状态码
    private final int httpStatus;
    // 响应消息
    private final String message;

    GlobalResponseEnum(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
