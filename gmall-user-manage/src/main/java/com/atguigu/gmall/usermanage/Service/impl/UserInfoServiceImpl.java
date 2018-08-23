package com.atguigu.gmall.usermanage.Service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bena.UserAddress;
import com.atguigu.gmall.bena.UserInfo;
import com.atguigu.gmall.usermanage.mapper.UserAddressMapper;
import com.atguigu.gmall.usermanage.mapper.UserInfoMapper;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    public List<UserInfo> getUserInfoList() {
        UserInfo userInfo = new UserInfo();
        Example example = new Example(UserInfo.class);
        example.createCriteria().andLike("nickName","%海%");
        return userInfoMapper.selectByExample(example);
    }


    public void deleteUserInfo(UserInfo userInfo){

        userInfoMapper.deleteByPrimaryKey(userInfo);
    }
    public void updateUserInfo(UserInfo userInfo){

        userInfoMapper.insertSelective(userInfo);
    }
    public void addUserInfo(UserInfo userInfo){

        userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }


    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);
    }


}
