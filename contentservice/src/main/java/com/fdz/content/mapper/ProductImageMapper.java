package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, product_image, \n" +
            "    product_id ";

    String TABLE = " product_image ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

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

    @Select(SELECT + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<ProductImage> findProductImages(@Param("productId") Long productId);
}