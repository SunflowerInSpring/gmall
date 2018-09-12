package com.atguigu.gmall.order.config;


import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.service.OderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class OrderConsumer {

    @Autowired
    OderService oderService;
    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumerPaymentResult(MapMessage mapMessage) throws JMSException {
        String orderId = mapMessage.getString("orderId");
        String result = mapMessage.getString("result");

        if("success".equals(result)){
            oderService.updateOrderStatus(orderId, ProcessStatus.PAID);
            oderService.sendOrderStatus(orderId);
            oderService.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        }else {
            oderService.updateOrderStatus(orderId,ProcessStatus.UNPAID);
        }
    }

    @JmsListener(destination = "SKU_DEDUCT_QUEUE",containerFactory = "jmsQueueListener")
    public void consumeSkuDeduct(MapMessage mapMessage) throws JMSException{
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        if("DEDUCTED".equals(status)){
            oderService.updateOrderStatus(orderId,ProcessStatus.WAITING_DELEVER);
        }else {
            oderService.updateOrderStatus(orderId, ProcessStatus.STOCK_EXCEPTION);
        }
    }
}
