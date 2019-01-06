package com.fdz.content.service;

import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerInterfaceConfig;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.PartnerUser;

public interface PartnerService {

    PartnerUser findPartnerUserByUserName(String userName);

    PartnerUser selectPartnerUserByPrimaryKey(Long id);

    Partner selectPartnerByPrimaryKey(Long id);

    PartnerInterfaceConfig selectPartnerInterfaceConfigByPrimaryKey(Long id);

    PartnerProduct selectPartnerProductByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Partner partner);

    int updateByPrimaryKeySelective(PartnerInterfaceConfig partnerInterfaceConfig);

    int updateByPrimaryKeySelective(PartnerProduct partnerProduct);

    int updateByPrimaryKeySelective(PartnerUser partnerUser);

    int insertSelective(Partner partner);

    int insertSelective(PartnerInterfaceConfig partnerInterfaceConfig);

    int insertSelective(PartnerProduct partnerProduct);

    int insertSelective(PartnerUser partnerUser);

    Partner findPartnerByUniqueKey(String uniqueKey);

    PartnerInterfaceConfig findConfigByPartnerAndType(Long partner, byte type);
}
