package com.atguigu.gmall.order.Task;


import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.service.OderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class OrderTask {

    @Autowired
    OderService oderService;

    @Async
    @Scheduled(cron = "0/10 * * * * ?")
    public void checkOrder(){
        System.out.println(Thread.currentThread().getName());
        List<OrderInfo> expiredOrderList = oderService.getExpiredOrderList();
        System.out.println(expiredOrderList);
        for (OrderInfo orderInfo : expiredOrderList) {
            // 处理未完成订单
            oderService.execExpiredOrder(orderInfo);
        }
    }

}
