package com.fdz.content.manager;

import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerInterfaceConfig;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.mapper.PartnerInterfaceConfigMapper;
import com.fdz.content.mapper.PartnerMapper;
import com.fdz.content.mapper.PartnerProductMapper;
import com.fdz.content.mapper.PartnerUserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    public PartnerUser findPartnerUserByUserName(String userName) {
        return partnerUserMapper.findPartnerUserByUserName(userName);
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

}
