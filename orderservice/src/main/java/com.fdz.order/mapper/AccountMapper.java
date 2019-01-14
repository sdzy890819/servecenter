package com.fdz.order.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.order.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, amount, freezing_amount, \n" +
            "    partner_id ";

    String TABLE = " account ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

    @Select(SELECT + " and partner_id=#{partnerId}")
    @ResultMap(RESULT_MAP)
    Account findAccountByPartnerId(@Param("partnerId") Long partnerId);

}