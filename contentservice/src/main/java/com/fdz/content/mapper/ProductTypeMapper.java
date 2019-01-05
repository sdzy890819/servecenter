package com.fdz.content.mapper;

import com.fdz.content.domain.ProductType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductTypeMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ProductType record);

    int insertSelective(ProductType record);

    ProductType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductType record);

    int updateByPrimaryKey(ProductType record);
}