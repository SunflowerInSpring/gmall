package com.atguigu.gmall.manage.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bena.*;

import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;

import org.springframework.beans.factory.annotation.Autowired;

import java.security.PrivateKey;
import java.util.List;
@Service
public class ManageServiceImpl implements ManageService{
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseCatalog1> getCatalog1() {


        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);

        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);

        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return  baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        baseAttrInfoMapper.insertSelective(baseAttrInfo);
        String id = baseAttrInfo.getId();
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        for(BaseAttrValue baseAttrValue:valueList){
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }


    }

    @Override
    public List<BaseAttrValue> getAttrInfo(String attrId) {


        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> select = baseAttrValueMapper.select(baseAttrValue);
        return select;
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {

        String id = baseAttrInfo.getId();
        List<BaseAttrValue> list = baseAttrInfo.getAttrValueList();
        BaseAttrValue baseAttrValue1 = new BaseAttrValue();
        baseAttrValue1.setAttrId(id);
        baseAttrValueMapper.delete(baseAttrValue1);
        for (BaseAttrValue baseAttrValue : list) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }

    }


    @Override
    public void deleteAttrInfo(String id) {
        System.out.print("id");
         baseAttrInfoMapper.deleteByPrimaryKey(id);

        BaseAttrValue baseAttrValue1 = new BaseAttrValue();
        baseAttrValue1.setAttrId(id);
        baseAttrValueMapper.delete(baseAttrValue1);

    }
}
