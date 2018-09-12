package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    public List<BaseCatalog1> getCatalog1();

    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    public List<BaseAttrValue> getAttrInfo(String attrId);

    public void deleteAttrInfo(String id);


    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    // 查询基本销售属性表
    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    void deleteSpuInfo(String id);

    SpuInfo getspuInfo(SpuInfo spuInfo);

    List<SpuImage> getSpuImageList(String spuId);

    public List<BaseAttrInfo> selsetAttrList(String catalog3Id);

    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    void saveSkuInfo(SkuInfo skuInfo);

     SkuInfo getSkuInfo(String skuId);


    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);


    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
