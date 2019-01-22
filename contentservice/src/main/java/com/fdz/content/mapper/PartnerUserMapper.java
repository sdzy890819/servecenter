package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.PartnerUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PartnerUserMapper {

    String SQL = "id, create_time, modify_time, create_by, modify_by, remark, is_delete, user_name, \n" +
            "    password, real_name, partner_id";

    String RESULT_MAP = "BaseResultMap";

    String TABLE = " partner_user ";

    int deleteByPrimaryKey(Long id);

    int insert(PartnerUser record);

    int insertSelective(PartnerUser record);

    PartnerUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerUser record);

    int updateByPrimaryKey(PartnerUser record);

    @Select("select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED + " and user_name = #{userName} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    PartnerUser findPartnerUserByUserName(@Param("userName") String userName);

    @Select("select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED + " and partner_id = #{partnerId} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    PartnerUser findUserByPartnerId(@Param("partnerId") Long partnerId);

}