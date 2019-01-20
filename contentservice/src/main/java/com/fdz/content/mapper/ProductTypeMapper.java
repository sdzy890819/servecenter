package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.ProductType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductTypeMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, product_type_name, \n" +
            "    sn ";
    String TABLE = " product_type ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(ProductType record);

    int insertSelective(ProductType record);

    ProductType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductType record);

    int updateByPrimaryKey(ProductType record);

    List<ProductType> findByIds(@Param("list") List<Long> ids);

    List<ProductType> findTypeBySn(@Param("list") List<String> snlist);

    @Select(SELECT + " and sn = #{sn}")
    @ResultMap(RESULT_MAP)
    ProductType findProductTypeBySn(@Param("sn") String sn);

    @Select(SELECT + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<ProductType> findAllTypes();

    @Select("select count(1) from " + TABLE + Constants.Sql.NOT_DELETED + " and (product_type_name = #{typeName} OR sn = #{sn}) ")
    @ResultType(Integer.class)
    int queryTypeBySnAndNameCount(@Param("typeName") String typeName, @Param("sn") String sn);
}