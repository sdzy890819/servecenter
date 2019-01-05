package com.fdz.content.mapper;

import com.fdz.content.domain.PartnerInterfaceConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartnerInterfaceConfigMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PartnerInterfaceConfig record);

    int insertSelective(PartnerInterfaceConfig record);

    PartnerInterfaceConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerInterfaceConfig record);

    int updateByPrimaryKey(PartnerInterfaceConfig record);
}