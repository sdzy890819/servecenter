package com.fdz.content.mapper;

import com.fdz.content.domain.PartnerProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartnerProductMapper {

    int deleteByPrimaryKey(Long id);

    int insert(PartnerProduct record);

    int insertSelective(PartnerProduct record);

    PartnerProduct selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerProduct record);

    int updateByPrimaryKey(PartnerProduct record);
}