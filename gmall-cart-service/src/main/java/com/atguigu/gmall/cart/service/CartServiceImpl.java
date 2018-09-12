package com.atguigu.gmall.cart.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.constant.CartConst;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Reference
    private ManageService manageService;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        //先查看数据库的购物车是否有该商品
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        CartInfo cartInfo1 = null;

       cartInfo1 = cartInfoMapper.selectOne(cartInfo);
        if(cartInfo1!=null){
            //更新购物车商品的数量
            cartInfo1.setSkuNum(skuNum+cartInfo1.getSkuNum());
            cartInfoMapper.updateByPrimaryKeySelective(cartInfo1);
        }else{
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);
            cartInfoMapper.insertSelective(cartInfo1);

        }
        //构建key user:userID:cart
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String jsonString = JSON.toJSONString(cartInfo1);
      jedis.hset(userCartKey,skuId, jsonString);
      //更新购物车过期时间
        String userInfoKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;
        Long ttl = jedis.ttl(userInfoKey);
        jedis.expire(userInfoKey,ttl.intValue());
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        List<String> hvals = jedis.hvals(userCartKey);
        List<CartInfo> cartInfoList = new ArrayList<>();
        if(hvals!=null&&hvals.size()>0){
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getSkuId().compareTo(o2.getSkuId());
                }
            });
            return cartInfoList;
        }else {

            // 从数据库中查询，其中cart_price 可能是旧值，所以需要关联sku_info 表信息。
            cartInfoList = loadCartCache(userId);
            return  cartInfoList;

        }


    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        List<CartInfo> cartList = cartInfoMapper.selectCartListWithCurPrice(userId);
        for (CartInfo cartInfo : cartListFromCookie) {//cookie的商品
            boolean flag =false;
            for (CartInfo info : cartList) {//登录状态的商品
             if(cartInfo.getSkuId().equals(info.getSkuId())){
                 info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());
                 cartInfoMapper.updateByPrimaryKeySelective(info);
                 flag=true;
                 break ;
             }
            }
            if(!flag){
                cartInfo.setUserId(userId);
                cartList.add(cartInfo);

                cartInfoMapper.insertSelective(cartInfo);
            }

            // 从新在数据库中查询并返回数据,
            List<CartInfo> cartInfoList = loadCartCache(userId);
            for (CartInfo cartInfo1 : cartInfoList) {
                for (CartInfo info : cartListFromCookie) {
                    if (cartInfo1.getSkuId().equals(info.getSkuId())){
                        // 只有被勾选的才会进行更改
                        if (info.getIsChecked().equals("1")){
                            cartInfo1.setIsChecked(info.getIsChecked());
                            // 更新redis中的isChecked
                            checkCart(cartInfo1.getSkuId(),info.getIsChecked(),userId);
                        }
                    }
                }
            }
            return cartInfoList;


        }


        return null;

    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = redisUtil.getJedis();
        String cartJson = jedis.hget(userCartKey, skuId);

        CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);

        cartInfo.setIsChecked(isChecked);
        String jsonString = JSON.toJSONString(cartInfo);
       jedis.hset(userCartKey, skuId,jsonString);

        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        if(isChecked.equals("1")){
            jedis.hset(userCheckedKey,skuId,jsonString);

        }else {
            jedis.hdel(userCheckedKey,skuId);
        }
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String userCheckedKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        List<CartInfo> listCart = new ArrayList<>();
        List<String> hvals = jedis.hvals(userCheckedKey);
        for (String hval : hvals) {
            CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
            listCart.add(cartInfo);

        }

        return listCart;
    }

    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if(cartInfoList!=null&&cartInfoList.size()>0){
            String userCartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
            Jedis jedis = redisUtil.getJedis();
            HashMap<String, String> cartString = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                cartString.put(cartInfo.getSkuId(),JSON.toJSONString(cartInfo));
            }
            jedis.hmset(userCartKey,cartString);
            jedis.close();
            return  cartInfoList;
        }
        return  null;
    }
}
