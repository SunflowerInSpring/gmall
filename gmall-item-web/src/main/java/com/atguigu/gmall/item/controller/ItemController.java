package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.cofing.LoginRequire;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {



    /*
        1。根据skuId查出skuInfo对象把信息展现到前台
        2.根据skuInfo对象的获取到spuId查询出spu的销售属性和属性值
        再判断当前的销售属性属于spu的哪种销售属性，是的添加字段isChecked值1不是0；
        （前台便利时候1则显示红边框）
        返回一个spuSaleAttr对象集合传到前台用便利销售属性和属性值
        （sql语句思路 ）
        3.
     */
    @Reference

    private ManageService manageService;
    @RequestMapping("{skuId}.html")
    @LoginRequire
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Map<String,Object> map){

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        map.put("skuInfo",skuInfo);
        // 1、查出该商品的spu的所有销售属性和属性值
        //2、标识出本商品对应的销售属性
        List<SpuSaleAttr> spuSaleAttrList = manageService.selectSpuSaleAttrListCheckBySku(skuInfo);
        map.put("spuSaleAttrList",spuSaleAttrList);
       //查询出spu下的所有sku，
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
         String skuKye ="";
        HashMap<String, Object> valueSkuMap = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);

            if(skuKye.length()!=0){
                skuKye+="|";
            }
            skuKye += skuSaleAttrValue.getSaleAttrValueId();

            if((i+1)==skuSaleAttrValueListBySpu.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){
                valueSkuMap.put(skuKye,skuSaleAttrValue.getSkuId());
                skuKye="";
            }

        }
        String valuesSkuJson = JSON.toJSONString(valueSkuMap);
        map.put("valuesSkuJson",valuesSkuJson);
        System.out.println(valuesSkuJson);
        return "item";
    }
}
