package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.event.OrderEvent;
import com.example.tool.JsonTools;

/***
 * @author: BYDylan
 * @date: 2022/10/18
 * @description: 相当于 MQ 的消费端
 */
@Component
public class OrderEventListener implements ApplicationListener<OrderEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventListener.class);

    @Override
    public void onApplicationEvent(OrderEvent event) {
        LOGGER.info("comsumer event: {}", JsonTools.toJsonString(event.getSource()));
    }
}
