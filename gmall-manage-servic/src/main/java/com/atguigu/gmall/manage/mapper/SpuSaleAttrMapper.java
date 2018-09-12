package com.atguigu.gmall.manage.mapper;


import com.atguigu.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    // 写方法 ：根据spuId 查询SpuSaleAttr。 ..获取数据的时候使用get ,如果跟db进行交互查询select
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);
    public List<SpuSaleAttr> selectSpuSaleAttrList(long spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);

}
