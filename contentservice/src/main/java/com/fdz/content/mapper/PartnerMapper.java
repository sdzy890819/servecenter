package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.Partner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PartnerMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, `name`, short_name,\n" +
            "    nature, code, contacts, contact_mobile, service_rate, public_key,unique_key ";

    String TABLE = " partner ";

    String RESULT_MAP = "BaseResultMap";

    int deleteByPrimaryKey(Long id);

    int insert(Partner record);

    int insertSelective(Partner record);

    Partner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Partner record);

    int updateByPrimaryKey(Partner record);

    @Select("select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED + " and unique_key = #{userName} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    Partner findPartnerByUniqueKey(@Param("uniqueKey") String uniqueKey);
}