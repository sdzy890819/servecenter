package com.fdz.order.mapper;

import com.fdz.order.domain.OrdersProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PathVariable;

@Mapper
public interface OrdersProductMapper {
    int deleteByPrimaryKey(@PathVariable("id") Long id);

    int insert(OrdersProduct record);

    int insertSelective(OrdersProduct record);

    OrdersProduct selectByPrimaryKey(@PathVariable("id") Long id);

    int updateByPrimaryKeySelective(OrdersProduct record);

    int updateByPrimaryKey(OrdersProduct record);
}