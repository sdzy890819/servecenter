package com.fdz.content.mapper;

import com.fdz.content.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductImageMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ProductImage record);

    int insertSelective(ProductImage record);

    ProductImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductImage record);

    int updateByPrimaryKey(ProductImage record);
}