package com.fdz.content.mapper;

import com.fdz.content.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    String TABLE = " product_image ";

    int deleteByPrimaryKey(Long id);

    int insert(ProductImage record);

    int insertSelective(ProductImage record);

    ProductImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductImage record);

    int updateByPrimaryKey(ProductImage record);

    List<ProductImage> findByIds(@Param("list") List<Long> productIds);

    int insertList(@Param("list") List<ProductImage> list);

    @Select("delete from " + TABLE + " where product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
}