package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.order.domain.OrdersLogistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrdersLogisticsMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, order_sn, \n" +
            "    partner_sn, partner_id, receiver, receiver_address, receiver_mobile, logistics, logistics_sn, \n" +
            "    logistics_status, delivery_status,business_delivery_status ";

    String TABLE = " orders_logistics ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(OrdersLogistics record);

    int insertSelective(OrdersLogistics record);

    OrdersLogistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdersLogistics record);

    int updateByPrimaryKey(OrdersLogistics record);

    @Select(SELECT + " and partner_sn=#{partnerSn}")
    @ResultMap(RESULT_MAP)
    OrdersLogistics findOrdersLogisticsByPartnerSn(@Param("partnerSn") String partnerSn);

    @Select(SELECT + " and order_sn=#{orderSn}")
    @ResultMap(RESULT_MAP)
    OrdersLogistics findOrdersLogisticsByOrderSn(@Param("orderSn") String orderSn);
}