package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.order.domain.OrdersTrack;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersTrackMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, order_sn, \n" +
            "    partner_sn, status, last_status ";

    String TABLE = " orders ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(OrdersTrack record);

    int insertSelective(OrdersTrack record);

    OrdersTrack selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrdersTrack record);

    int updateByPrimaryKey(OrdersTrack record);
}