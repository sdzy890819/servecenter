package com.fdz.content.manager;

import com.fdz.common.utils.Page;
import com.fdz.content.domain.*;
import com.fdz.content.dto.PartnerDto;
import com.fdz.content.mapper.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Repository
@Transactional
public class PartnerManager {

    @Resource
    private PartnerMapper partnerMapper;

    @Resource
    private PartnerInterfaceConfigMapper partnerInterfaceConfigMapper;

    @Resource
    private PartnerProductMapper partnerProductMapper;

    @Resource
    private PartnerUserMapper partnerUserMapper;

    @Resource
    private InterfaceExecRecordMapper interfaceExecRecordMapper;

    public PartnerUser findPartnerUserByUserName(String userName) {
        return partnerUserMapper.findPartnerUserByUserName(userName);
    }

    public PartnerUser findUserByPartnerId(Long partnerId) {
        return partnerUserMapper.findUserByPartnerId(partnerId);
    }


    public PartnerUser selectPartnerUserByPrimaryKey(Long id) {
        return partnerUserMapper.selectByPrimaryKey(id);
    }

    public Partner selectPartnerByPrimaryKey(Long id) {
        return partnerMapper.selectByPrimaryKey(id);
    }

    public PartnerInterfaceConfig selectPartnerInterfaceConfigByPrimaryKey(Long id) {
        return partnerInterfaceConfigMapper.selectByPrimaryKey(id);
    }

    public PartnerProduct selectPartnerProductByPrimaryKey(Long id) {
        return partnerProductMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Partner partner) {
        return partnerMapper.updateByPrimaryKeySelective(partner);
    }

    public int updateByPrimaryKeySelective(InterfaceExecRecord interfaceExecRecord) {
        return interfaceExecRecordMapper.updateByPrimaryKeySelective(interfaceExecRecord);
    }

    public int insertSelective(InterfaceExecRecord interfaceExecRecord) {
        return interfaceExecRecordMapper.insertSelective(interfaceExecRecord);
    }

    public int updateByPrimaryKeySelective(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerInterfaceConfigMapper.updateByPrimaryKeySelective(partnerInterfaceConfig);
    }

    public int updateByPrimaryKeySelective(PartnerProduct partnerProduct) {
        return partnerProductMapper.updateByPrimaryKeySelective(partnerProduct);
    }

    public int updateByPrimaryKeySelective(PartnerUser partnerUser) {
        return partnerUserMapper.updateByPrimaryKeySelective(partnerUser);
    }

    public int insertSelective(Partner partner) {
        return partnerMapper.insertSelective(partner);
    }

    public int insertSelective(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerInterfaceConfigMapper.insertSelective(partnerInterfaceConfig);
    }

    public int insertSelective(PartnerProduct partnerProduct) {
        return partnerProductMapper.insertSelective(partnerProduct);
    }

    public int insertSelective(PartnerUser partnerUser) {
        return partnerUserMapper.insertSelective(partnerUser);
    }

    public Partner findPartnerByUniqueKey(String uniqueKey) {
        return partnerMapper.findPartnerByUniqueKey(uniqueKey);
    }

    public PartnerInterfaceConfig findConfigByPartnerAndType(Long partner, byte type) {
        return partnerInterfaceConfigMapper.findConfigByPartnerAndType(partner, type);
    }

    public int countPartnerProduct() {
        return partnerProductMapper.countPartnerProduct();
    }

    public List<PartnerProduct> listPartnerProduct(Page page) {
        return partnerProductMapper.listPartnerProduct(page);
    }

    public List<PartnerProduct> searchPartnerProduct(PartnerProduct partnerProduct, Page page) {
        return partnerProductMapper.searchPartnerProduct(partnerProduct, page);
    }

    public int countSearchPartnerProduct(PartnerProduct partnerProduct) {
        return partnerProductMapper.countSearchPartnerProduct(partnerProduct);
    }

    public int countPartnerProductByProductId(Long productId) {
        return partnerProductMapper.countPartnerProductByProductId(productId);
    }

    public List<PartnerProduct> findPartnerProductByProductId(Long productId) {
        return partnerProductMapper.findPartnerProductByProductId(productId);
    }

    public List<PartnerInterfaceConfig> findConfigByPartnerIdAndType(List<Long> partnerIds, byte interfaceType) {
        return partnerInterfaceConfigMapper.findConfigByPartnerIdAndType(partnerIds, interfaceType);
    }

    public int insertInterfaceExecRecordList(List<InterfaceExecRecord> list) {
        return interfaceExecRecordMapper.insertInterfaceExecRecordList(list);
    }

    public List<InterfaceExecRecord> findRecordPartnerIdAndTypeAndStatus(List<Long> partnerId, byte type, byte status) {
        return interfaceExecRecordMapper.findRecordPartnerIdAndTypeAndStatus(partnerId, type, status);
    }

    public List<InterfaceExecRecord> queryRecordByStatus(byte status) {
        return interfaceExecRecordMapper.queryRecordByStatus(status);
    }

    public List<PartnerProduct> findPPByProductIds(List<Long> partnerProductIds) {
        return partnerProductMapper.findPPByProductIds(partnerProductIds);
    }

    public List<Partner> findPartnerByIds(List<Long> partnerIds) {
        return partnerMapper.findPartnerByIds(partnerIds);
    }

    public Integer searchPartnerCount(PartnerDto dto) {
        return partnerMapper.searchPartnerCount(dto);
    }

    public List<Partner> searchPartner(PartnerDto dto, Page page) {
        return partnerMapper.searchPartner(dto, page);
    }

    public List<Partner> findAll() {
        return partnerMapper.findAll();
    }

    public List<PartnerInterfaceConfig> searchConfig(PartnerInterfaceConfig partnerInterfaceConfig, Page page) {
        return partnerInterfaceConfigMapper.searchConfig(partnerInterfaceConfig, page);
    }

    public Integer searchConfigCount(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerInterfaceConfigMapper.searchConfigCount(partnerInterfaceConfig);
    }
}
