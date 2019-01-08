package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.PartnerInterfaceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PartnerInterfaceConfigMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, partner_id, \n" +
            "    interface_url, interface_type ";

    String TABLE = " partner_interface_config ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;

    int deleteByPrimaryKey(Long id);

    int insert(PartnerInterfaceConfig record);

    int insertSelective(PartnerInterfaceConfig record);

    PartnerInterfaceConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerInterfaceConfig record);

    int updateByPrimaryKey(PartnerInterfaceConfig record);

    @Select(SELECT + " and partner_id = #{partnerId} and interface_type = #{type} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    PartnerInterfaceConfig findConfigByPartnerAndType(@Param("partnerId") Long partner, @Param("type") byte type);

    List<PartnerInterfaceConfig> findConfigByPartnerIdAndType(@Param("list") List<Long> partnerIds, @Param("interfaceType") byte interfaceType);
}