package com.atguigu.gmall.manage.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ManageServiceImpl implements ManageService{
    private final BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private  SkuSaleAttrValueMapper    skuSaleAttrValueMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    public ManageServiceImpl(BaseCatalog1Mapper baseCatalog1Mapper) {
        this.baseCatalog1Mapper = baseCatalog1Mapper;
    }


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
        //如果有id说明是更新的
        if(baseAttrInfo.getId()!=null&&baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
            List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
            BaseAttrValue baseAttrValue1 = new BaseAttrValue();
            baseAttrValue1.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValue1);
            for(BaseAttrValue baseAttrValue:valueList){
                        baseAttrValue.setId(null);
                        baseAttrValue.setAttrId(baseAttrInfo.getId());
                        baseAttrValueMapper.insert(baseAttrValue);




            }
        //这是添加的
        }else {
                    baseAttrInfo.setId(null);
            baseAttrInfoMapper.insert(baseAttrInfo);
            String id = baseAttrInfo.getId();

            List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();

            for(BaseAttrValue baseAttrValue:valueList){

                baseAttrValue.setAttrId(id);
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }

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
    public void deleteAttrInfo(String id) {
        System.out.print("id");
         baseAttrInfoMapper.deleteByPrimaryKey(id);

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(id);
        baseAttrValueMapper.delete(baseAttrValue);

    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {

        if(spuInfo.getId()!=null && spuInfo.getId().length()==0){
            spuInfo.setId(null);
            spuInfoMapper.insert(spuInfo);
        }else {
            spuInfoMapper.updateByPrimaryKey(spuInfo);
        }
        /**
         * 删除原来的img信息添加新的
         */
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        SpuImage spuImage1 = new SpuImage();
        spuImage1.setSpuId(spuInfo.getId());
        spuImageMapper.delete(spuImage1);
        if(spuImageList!=null){
            for (SpuImage spuImage : spuImageList) {
                if(spuImage.getId()!=null&&spuImage.getId().length()==0){
                    spuImage.setId(null);
                }
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }


        /**
         * 添加销售属性
         * 1.先删除原来表中的销售属性，
         * 2.插入新的销售属性
         */

        SpuSaleAttr spuSaleAttr1 = new SpuSaleAttr();
        spuSaleAttr1.setSpuId(spuInfo.getId());
        spuSaleAttrMapper.delete(spuSaleAttr1);
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                if(spuSaleAttr.getId()==null||spuSaleAttr.getId().length()==0){
                    spuSaleAttr.setId(null);
                }
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //再插入销售属性时候插入销售属性值
                //先删除再添加
          /*      SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValueMapper.delete(spuSaleAttrValue);*/
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList!=null){
                    for (SpuSaleAttrValue saleAttrValue : spuSaleAttrValueList) {
                        if (saleAttrValue.getId()==null||saleAttrValue.getId().length()==0){
                            saleAttrValue.setId(null);
                        }
                        saleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insert(saleAttrValue);
                    }
                }


            }
        }



    }

    @Override
    @Transactional
    public void deleteSpuInfo(String id) {
        spuInfoMapper.deleteByPrimaryKey(id);

        SpuImage spuImage1 = new SpuImage();
        spuImage1.setSpuId(id);
        spuImageMapper.delete(spuImage1);

        SpuSaleAttr spuSaleAttr1 = new SpuSaleAttr();
        spuSaleAttr1.setSpuId(id);
        spuSaleAttrMapper.delete(spuSaleAttr1);

        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(id);
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);

    }

    @Override
    public SpuInfo getspuInfo(SpuInfo spuInfo) {

        SpuImage spuImage1 = new SpuImage();
        spuImage1.setSpuId(spuInfo.getId());
        List<SpuImage> spuImageList = spuImageMapper.select(spuImage1);
        spuInfo.setSpuImageList(spuImageList);

        SpuSaleAttr spuSaleAttr1 = new SpuSaleAttr();
        spuSaleAttr1.setSpuId(spuInfo.getId());
        List<SpuSaleAttr> select = spuSaleAttrMapper.select(spuSaleAttr1);

        for (SpuSaleAttr spuSaleAttr : select) {
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSpuId(spuInfo.getId());
            spuSaleAttrValue.setSaleAttrId(spuSaleAttr.getSaleAttrId());
            List<SpuSaleAttrValue> attrValueList = spuSaleAttrValueMapper.select(spuSaleAttrValue);
           /* spuSaleAttr.setSpuSaleAttrValueList(attrValueList);*/
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(spuSaleAttr.getSaleAttrName(),attrValueList);
            spuSaleAttr.setSpuSaleAttrValueJson(map);
        }
        spuInfo.setSpuSaleAttrList(select);

        return spuInfo;
    }

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<BaseAttrInfo> selsetAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.select(baseAttrInfo);
        if (attrInfoList != null) {
            for (BaseAttrInfo attrInfo : attrInfoList) {
                BaseAttrValue baseAttrValue = new BaseAttrValue();
                baseAttrValue.setAttrId(attrInfo.getId());
                List<BaseAttrValue> valueList = baseAttrValueMapper.select(baseAttrValue);
                attrInfo.setAttrValueList(valueList);
            }
        }
        return attrInfoList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(Long.parseLong(spuId));
    }



    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo){
        if(skuInfo.getId()==null||skuInfo.getId().length()==0){
            skuInfo.setId(null);
            skuInfoMapper.insertSelective(skuInfo);
        }else {
            skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
        }


        Example example=new Example(SkuImage.class);
        example.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuImageMapper.deleteByExample(example);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuInfo.getId());
            if(skuImage.getId()!=null&&skuImage.getId().length()==0) {
                skuImage.setId(null);
            }
            skuImageMapper.insertSelective(skuImage);
        }


        Example skuAttrValueExample=new Example(SkuAttrValue.class);
        skuAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuAttrValueMapper.deleteByExample(skuAttrValueExample);

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuInfo.getId());
            if(skuAttrValue.getId()!=null&&skuAttrValue.getId().length()==0) {
                skuAttrValue.setId(null);
            }
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }


        Example skuSaleAttrValueExample=new Example(SkuSaleAttrValue.class);
        skuSaleAttrValueExample.createCriteria().andEqualTo("skuId",skuInfo.getId());
        skuSaleAttrValueMapper.deleteByExample(skuSaleAttrValueExample);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValue.setId(null);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }

    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {

        SkuInfo skuInfo=null;

        try {
            Jedis jedis = redisUtil.getJedis();
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            if(jedis.exists(skuKey)){
                String s = jedis.get(skuKey);
                skuInfo = JSON.parseObject(s, SkuInfo.class);
                jedis.close();
                return skuInfo;
            }else {
                skuInfo = getSkuInfoDB(skuId);
                String s = JSON.toJSONString(skuInfo);
                jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, s);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSkuInfoDB(skuId);
    }

   private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo;
        skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);

        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> imageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(imageList);


       SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
       skuSaleAttrValue.setSkuId(skuId);
       List<SkuSaleAttrValue> saleAttrValues = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo.setSkuSaleAttrValueList(saleAttrValues);

       SkuAttrValue skuAttrValue = new SkuAttrValue();
       skuAttrValue.setSkuId(skuId);
       List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
       skuInfo.setSkuAttrValueList(skuAttrValues);


       return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String attrValueIds = StringUtils.join(attrValueIdList.toArray(), ",");
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectAttrInfoListByIds(attrValueIds);



        return baseAttrInfoList;
    }


}
