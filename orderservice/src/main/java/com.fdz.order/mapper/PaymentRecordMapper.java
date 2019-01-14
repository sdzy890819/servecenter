package com.fdz.order.mapper;

import com.fdz.common.utils.Page;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.PaymentRecordSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PaymentRecord record);

    int insertSelective(PaymentRecord record);

    PaymentRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentRecord record);

    int updateByPrimaryKey(PaymentRecord record);

    Integer searchPaymentRecordCount(@Param("p") PaymentRecordSearchDto dto);

    List<PaymentRecord> searchPaymentRecord(@Param("p") PaymentRecordSearchDto dto, @Param("page") Page page);
}