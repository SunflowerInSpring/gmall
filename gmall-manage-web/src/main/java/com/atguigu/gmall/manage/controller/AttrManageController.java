package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bena.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class AttrManageController {
    @Reference
    private ManageService manageService;

    @RequestMapping("attrListPage")
    public String getAttrListPage(){
        return "attrListPage";
    }
    /**
     * 删除属性
     */
    @RequestMapping("deleteAttrInfo")
    @ResponseBody
    public String deleteAttrInfo(String id){

      manageService.deleteAttrInfo(id);
        return "删除成功";
    }
    /**
     * 获取单个属性对象值
     */
   @RequestMapping("getAttrValueList")
   @ResponseBody
    public List<BaseAttrValue> updateAttrInfo(String attrId){
       List<BaseAttrValue> attrInfo = manageService.getAttrInfo(attrId);
       return attrInfo;
    }
    /**
     *更新属性
     */
    @RequestMapping("updateAttr")
    @ResponseBody
    public String BaseAttrInfo(BaseAttrInfo baseAttrInfo){
        manageService.saveAttr(baseAttrInfo);
        return "编辑成功";
    }



    /**
     * 添加属性
     */
    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return "保存成功";
    }

    /**
     * 获取属性列表
     */
    @RequestMapping("getAttrList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }


    /***
     * 获得一级分类
     */
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1> getCatalog1(){
        List<BaseCatalog1> catalog1List = manageService.getCatalog1();
        return catalog1List;
    }


    /***
     * 获得二级分类
     *
     */
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2> getCatalog2(@RequestParam Map<String,String> map){
        String catalog1Id =   map.get("catalog1Id") ;
        List<BaseCatalog2> catalog2List = manageService.getCatalog2(catalog1Id);
        return catalog2List;
    }

    /***
     * 获得三级分类
     *
     */
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(@RequestParam Map<String,String> map){
        String catalog2Id =   map.get("catalog2Id") ;
        List<BaseCatalog3> catalog3List = manageService.getCatalog3(catalog2Id);
        return catalog3List;
    }




}
