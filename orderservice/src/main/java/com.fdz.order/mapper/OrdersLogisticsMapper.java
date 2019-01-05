package com.fdz.order.mapper;

import com.fdz.order.domain.OrdersLogistics;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersLogisticsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(OrdersLogistics record);

    int insertSelective(OrdersLogistics record);

    OrdersLogistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdersLogistics record);

    int updateByPrimaryKey(OrdersLogistics record);
}