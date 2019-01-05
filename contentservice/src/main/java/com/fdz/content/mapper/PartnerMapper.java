package com.fdz.content.mapper;

import com.fdz.content.domain.Partner;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartnerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Partner record);

    int insertSelective(Partner record);

    Partner selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Partner record);

    int updateByPrimaryKey(Partner record);
}