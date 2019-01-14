package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.Page;
import com.fdz.order.domain.OrderStatistics;
import com.fdz.order.domain.Orders;
import com.fdz.order.domain.OrdersAndLogistics;
import com.fdz.order.dto.SearchOrdersDto;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrdersMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, order_sn, \n" +
            "    partner_sn, partner_id, partner_product_id, amount, buy_time, confirm_time, business_delivery_time, \n" +
            "    end_time, delivery_status, status, order_status, business_delivery_status ";

    String TABLE = " orders ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(Orders record);

    int insertSelective(Orders record);

    Orders selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Orders record);

    int updateByPrimaryKey(Orders record);

    @Select(SELECT + " and partner_sn=#{partnerSn}")
    @ResultMap(RESULT_MAP)
    Orders findOrdersByPartnerSn(@Param("partnerSn") String partnerSn);

    @Select(SELECT + " and order_sn=#{orderSn}")
    @ResultMap(RESULT_MAP)
    Orders findOrdersByOrderSn(@Param("orderSn") String orderSn);

    List<OrdersAndLogistics> searchOrders(@Param("search") SearchOrdersDto dto, @Param("page") Page page);

    Integer searchOrdersCount(@Param("search") SearchOrdersDto dto);

    @Select("select DATE_FORMAT(create_time, '%Y-%m-%d') as orderDate, partner_id as partnerId, count(1) as num, sum(amount) as amount from " + TABLE + Constants.Sql.NOT_DELETED + " and delivery_status = 1 " +
            "and business_delivery_status = 0 and create_time >= #{start} and create_time <= #{end} group by orderDate, partnerId ")
    @ResultType(OrderStatistics.class)
    List<OrderStatistics> findOrderStatistics(@Param("start") Date start, @Param("end") Date end);

    @Select("select DATE_FORMAT(create_time, '%Y-%m-%d') as orderDate, partner_id as partnerId, count(1) as num, business_delivery_status as deliveryStatus sum(amount) as amount from " + TABLE + Constants.Sql.NOT_DELETED + " and delivery_status = 1 " +
            " and create_time >= #{start} and create_time <= #{end} group by orderDate, partnerId, deliveryStatus ")
    @ResultType(OrderStatistics.class)
    List<OrderStatistics> findOrderStatisticsByBusiness(@Param("start") Date start, @Param("end") Date end);
}