package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bena.UserAddress;
import com.atguigu.gmall.service.UserInfoService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class OrderController {

    @Reference
    private UserInfoService userInfoService;
    @RequestMapping("AddcessByUserId")
    @ResponseBody
    public List<UserAddress> getUserAddcessList(String userId){

      return   userInfoService.getUserAddressList(userId);
    }
}
