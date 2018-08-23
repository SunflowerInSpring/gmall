package com.atguigu.gmall.service;

import com.atguigu.gmall.bena.UserAddress;
import com.atguigu.gmall.bena.UserInfo;


import java.util.List;

public interface UserInfoService {

    List<UserInfo> getUserInfoList();
    List<UserAddress> getUserAddressList(String userId);
}
