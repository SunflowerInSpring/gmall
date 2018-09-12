package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;


@Controller
public class ListController {
    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;
    @RequestMapping("list.html")

    public String getList(SkuLsParams skuLsParams, Map<String ,Object> map){
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        map.put("skuLsInfoList",skuLsResult.getSkuLsInfoList());
        List<String> attrValueIdList =
                skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList =manageService.getAttrList(attrValueIdList);
        map.put("attrList",attrList);
        String urlParam = makeUrlParam(skuLsParams);
        map.put("urlParam",urlParam);
        return "list";
    }

    public String makeUrlParam(SkuLsParams skuLsParam){
        String urlParam="";
        if(skuLsParam.getKeyword()!=null){
            urlParam+="keyword="+skuLsParam.getKeyword();
        }
        if (skuLsParam.getCatalog3Id()!=null){
            if (urlParam.length()>0){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+skuLsParam.getCatalog3Id();
        }
        // 构造属性参数
        if (skuLsParam.getValueId()!=null && skuLsParam.getValueId().length>0){
            for (int i=0;i<skuLsParam.getValueId().length;i++){
                String valueId = skuLsParam.getValueId()[i];
                if (urlParam.length()>0){
                    urlParam+="&";
                }
                urlParam+="valueId="+valueId;
            }
        }
        return  urlParam;
    }




}
