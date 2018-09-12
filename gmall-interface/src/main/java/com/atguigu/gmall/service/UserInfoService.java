package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;


import java.util.List;

public interface UserInfoService {

    List<UserInfo> getUserInfoList();
    List<UserAddress> getUserAddressList(String userId);
    UserInfo login(UserInfo userInfo);

    UserInfo verify(String userId);
}
