package com.atguigu.gmall.usermanage.controller;

import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("getAll")
    public List<UserInfo> getAll(){
        System.out.print("ffffffffffffffffffff");
        return  userInfoService.getUserInfoList();
    }

    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return "ggggg";
    }
}
