package com.atguigu.gmall.service;

import com.atguigu.gmall.bena.*;

import java.util.List;

public interface ManageService {
    public List<BaseCatalog1> getCatalog1();

    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    public List<BaseAttrValue> getAttrInfo(String attrId);


    void saveAttr(BaseAttrInfo baseAttrInfo);
    public void deleteAttrInfo(String id);
}
