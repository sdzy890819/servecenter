package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.Page;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.PaymentRecordSearchDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, sn, order_sn, partner_sn, \n" +
            "    payment_type, amount, surplus_amount, pay_time, pay_status, pay_sn, pay_route, partner_id, frozen ";

    String TABLE = " payment_record ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(PaymentRecord record);

    int insertSelective(PaymentRecord record);

    PaymentRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentRecord record);

    int updateByPrimaryKey(PaymentRecord record);

    Integer searchPaymentRecordCount(@Param("p") PaymentRecordSearchDto dto);

    List<PaymentRecord> searchPaymentRecord(@Param("p") PaymentRecordSearchDto dto, @Param("page") Page page);

    @Select(SELECT + " and partner_id = #{partnerId} and payment_type = #{paymentType} and order_sn = #{orderSn} and frozen = #{frozen} limit 1")
    @ResultMap(RESULT_MAP)
    PaymentRecord findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(@Param("partnerId") Long partnerId,
                                                                  @Param("paymentType") Byte paymentType,
                                                                  @Param("orderSn") String orderSn,
                                                                  @Param("frozen") Boolean frozen);
}