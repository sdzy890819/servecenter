package com.fdz.content.service;

import com.fdz.content.domain.*;
import com.fdz.content.dto.RecordDto;

import java.util.List;

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

    int updateByPrimaryKeySelective(InterfaceExecRecord interfaceExecRecord);

    int insertSelective(InterfaceExecRecord interfaceExecRecord);

    int insertSelective(Partner partner);

    int insertSelective(PartnerInterfaceConfig partnerInterfaceConfig);

    int insertSelective(PartnerProduct partnerProduct);

    int insertSelective(PartnerUser partnerUser);

    Partner findPartnerByUniqueKey(String uniqueKey);

    PartnerInterfaceConfig findConfigByPartnerAndType(Long partner, byte type);

    int countPartnerProductByProductId(Long productId);

    void syncProduct(Long id);

    void syncPartnerProduct(Long id);

    List<PartnerProduct> findPartnerProductByProductId(Long productId);

    List<PartnerInterfaceConfig> findConfigByPartnerIdAndType(List<Long> partnerIds, byte interfaceType);

    int insertInterfaceExecRecordList(List<InterfaceExecRecord> list);

    List<InterfaceExecRecord> findRecordPartnerIdAndTypeAndStatus(List<Long> partnerId, byte type, byte status);

    List<InterfaceExecRecord> queryRecordByStatus(byte status);

    void record(RecordDto recordDto);
}
