package com.example.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.service.AsyncService;

/**
 * @author: BYDylan
 * @date: 2022/10/17
 * @description:
 */
@Service
public class AsyncServiceImpl implements AsyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Async("defaultThreadPoolExecutor")
    @Override
    public void execute(Integer id) {
        LOGGER.info("exec aysnc task id: {}", id);
    }
}
