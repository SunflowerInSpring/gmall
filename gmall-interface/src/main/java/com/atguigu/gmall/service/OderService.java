package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.enums.ProcessStatus;

import java.util.List;

public interface OderService {
    public  String  saveOrder(OrderInfo orderInfo);
    String getTradeNo(String userId);
    boolean checkTradeCode(String userId,String tradeCodeNo);
    void delTradeCode(String userId);

    boolean checkStock(String skuId,Integer skuNum);

    OrderInfo getOrderInfo(String orderId);


    void updateOrderStatus(String orderId, ProcessStatus paid);

    void sendOrderStatus(String orderId);

    List<OrderInfo> getExpiredOrderList();

    void execExpiredOrder(OrderInfo orderInfo);
}
