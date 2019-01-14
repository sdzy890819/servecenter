package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.Page;
import com.fdz.content.domain.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, product_name, \n" +
            "    product_type_no, product_description, product_cover_image, prime_costs, sale_price, \n" +
            "    status, product_model ";
    String TABLE = " product ";

    String RESULT_MAP = " BaseResultMap ";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;


    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> findProductByIds(@Param("list") List<Long> ids);

    @Select("select count(1) from " + TABLE + Constants.Sql.NOT_DELETED)
    @ResultType(Integer.class)
    int countProduct();

    int countSearchProduct(Product product);

    List<Product> searchProductList(@Param("product") Product product, @Param("page") Page page);

    @Select(SELECT + Constants.Sql.DEFAULT_ORDER + Constants.Sql.LIMIT_SQL)
    @ResultMap(RESULT_MAP)
    List<Product> listProduct(@Param("page") Page page);

    @Select(SELECT + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<Product> findAll();
}