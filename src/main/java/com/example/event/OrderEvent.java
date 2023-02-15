package com.example.event;

import com.example.model.OrderModel;

/***
 * @author: BYDylan
 * @date: 2022/10/18
 * @description: 相当于 MQ 的消息体
 */
public class OrderEvent extends AbstractGenericEvent<OrderModel> {
    public OrderEvent(OrderModel orderModel) {
        super(orderModel);
    }
}
