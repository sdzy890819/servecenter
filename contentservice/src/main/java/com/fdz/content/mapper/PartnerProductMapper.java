package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.Page;
import com.fdz.content.domain.PartnerProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PartnerProductMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, product_id, \n" +
            "    sale_price, platform_price, shelf, partner_id, service_fee ";
    String TABLE = " partner_product ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(PartnerProduct record);

    int insertSelective(PartnerProduct record);

    PartnerProduct selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerProduct record);

    int updateByPrimaryKey(PartnerProduct record);

    @Select("select count(1) from " + TABLE + Constants.Sql.NOT_DELETED + " and partner_id = #{partnerId} " + " and shelf = true")
    @ResultType(Integer.class)
    int countPartnerProduct(@Param("partnerId") Long partnerId);

    @Select(SELECT + " and shelf = true " + " and partner_id = #{partnerId} " + Constants.Sql.DEFAULT_ORDER + Constants.Sql.LIMIT_SQL)
    @ResultMap(RESULT_MAP)
    List<PartnerProduct> listPartnerProduct(@Param("partnerId") Long partnerId, @Param("page") Page page);

    int countSearchPartnerProduct(@Param("p") PartnerProduct partnerProduct);

    List<PartnerProduct> searchPartnerProduct(@Param("p") PartnerProduct partnerProduct, @Param("page") Page page);

    @Select("select count(1) from " + TABLE + Constants.Sql.NOT_DELETED + " and product_id = #{productId}")
    @ResultType(Integer.class)
    int countPartnerProductByProductId(@Param("productId") Long productId);

    @Select(SELECT + " and product_id = #{productId} " + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<PartnerProduct> findPartnerProductByProductId(@Param("productId") Long productId);

    List<PartnerProduct> findPPByProductIds(@Param("list") List<Long> partnerProductIds);
}