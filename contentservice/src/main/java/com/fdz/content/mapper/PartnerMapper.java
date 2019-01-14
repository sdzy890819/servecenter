package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.Page;
import com.fdz.content.domain.Partner;
import com.fdz.content.dto.PartnerDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PartnerMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, `name`, short_name,\n" +
            "    nature, code, contacts, contact_mobile, service_rate, public_key,unique_key ";

    String TABLE = " partner ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(Partner record);

    int insertSelective(Partner record);

    Partner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Partner record);

    int updateByPrimaryKey(Partner record);

    @Select(SELECT + " and unique_key = #{userName} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    Partner findPartnerByUniqueKey(@Param("uniqueKey") String uniqueKey);

    List<Partner> findPartnerByIds(@Param("list") List<Long> partnerIds);

    Integer searchPartnerCount(@Param("p") PartnerDto dto);

    List<Partner> searchPartner(@Param("p") PartnerDto dto, @Param("page") Page page);

    @Select(SELECT + Constants.Sql.DEFAULT_ORDER)
    @ResultMap(RESULT_MAP)
    List<Partner> findAll();
}