package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.order.domain.OrdersProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdersProductMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, order_sn, \n" +
            "    partner_sn, partner_id, partner_product_id, product_name, product_num, product_type_no, \n" +
            "    product_type_name, product_description, product_cover_image, prime_costs, sale_price, \n" +
            "    product_model, product_id, product_sale_price,platform_price ";

    String TABLE = " orders_product ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(OrdersProduct record);

    int insertSelective(OrdersProduct record);

    OrdersProduct selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdersProduct record);

    int insertOrdersProducts(@Param("list") List<OrdersProduct> list);

    int updateByPrimaryKey(OrdersProduct record);

    @Select(SELECT + " and order_sn = #{orderSn} " + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<OrdersProduct> findOrdersProductsByOrderSn(String orderSn);
}