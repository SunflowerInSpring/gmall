package com.atguigu.gmall.usermanage.Service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import com.atguigu.gmall.usermanage.mapper.UserAddressMapper;
import com.atguigu.gmall.usermanage.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private RedisUtil redisUtil;
    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60;

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

    @Override
    public UserInfo login(UserInfo userInfo) {
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);
        UserInfo info = userInfoMapper.selectOne(userInfo);

        if (info!=null){
            // 获得到redis ,将用户存储到redis中
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(userKey_prefix+info.getId()+userinfoKey_suffix,userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return  info;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        String key = userKey_prefix + userId + userinfoKey_suffix;
        Jedis jedis = redisUtil.getJedis();

            String userInfo = jedis.get(key);
        if(userInfo!=null&&userInfo.length()>0){
            jedis.expire(key,userKey_timeOut);
            UserInfo info = JSON.parseObject(userInfo, UserInfo.class);
            jedis.close();
            return  info;
        }
        jedis.close();
        return null;
    }


}
