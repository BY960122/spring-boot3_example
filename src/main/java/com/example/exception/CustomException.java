package com.example.exception;

import com.alibaba.druid.util.StringUtils;
import com.example.constant.GlobalResponseEnum;

/**
 * @author: BYDylan
 * @date: 2022/2/7
 * @description: 自定义异常
 */
public class CustomException extends RuntimeException {
    /**
     * 响应结果
     */
    private GlobalResponseEnum responseStatusEnum;

    public CustomException(GlobalResponseEnum responseStatusEnum) {
        super(responseStatusEnum.getMessage());
        this.responseStatusEnum = responseStatusEnum;
    }

    public CustomException(GlobalResponseEnum responseStatusEnum, String customMessage) {
        super(StringUtils.isEmpty(customMessage) ? responseStatusEnum.getMessage() : customMessage);
        this.responseStatusEnum = responseStatusEnum;
    }

    public GlobalResponseEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }
}
