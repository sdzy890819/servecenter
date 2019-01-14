package com.fdz.content.service;

import com.fdz.common.enums.InterfaceExecStatus;
import com.fdz.common.enums.InterfaceTypeEnums;
import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.security.vo.LoginUser;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.utils.UserDisassembly;
import com.fdz.content.domain.*;
import com.fdz.content.dto.PartnerDto;
import com.fdz.content.dto.RecordDto;
import com.fdz.content.manager.PartnerManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("partnerService")
public class PartnerServiceImpl implements PartnerService, UserDetailsService {

    @Resource
    private PartnerManager partnerManager;

    @Resource
    private RedisDataManager redisDataManager;

    @Override
    public PartnerUser findPartnerUserByUserName(String userName) {
        return partnerManager.findPartnerUserByUserName(userName);
    }

    @Override
    public PartnerUser selectPartnerUserByPrimaryKey(Long id) {
        return partnerManager.selectPartnerUserByPrimaryKey(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("userId为空");
        }
        if (UserDisassembly.isP(username)) {
            username = UserDisassembly.disassembly(username);
            if (username.matches("[0-9]+")) {
                try {
                    PartnerUser partnerUser = selectPartnerUserByPrimaryKey(Long.parseLong(username));
                    if (partnerUser != null) {
                        LoginUser loginUser = new LoginUser();
                        loginUser.setId(partnerUser.getId());
                        loginUser.setPassword(partnerUser.getPassword());
                        loginUser.setUserName(username);
                        redisDataManager.set(username, loginUser, 1, TimeUnit.HOURS);
                        return loginUser;
                    }
                } catch (NumberFormatException e) {
                    throw new UsernameNotFoundException("解析错误");
                }
            }
        }
        throw new UsernameNotFoundException("不是当前的用户体系");
    }

    public Partner selectPartnerByPrimaryKey(Long id) {
        return partnerManager.selectPartnerByPrimaryKey(id);
    }

    public PartnerInterfaceConfig selectPartnerInterfaceConfigByPrimaryKey(Long id) {
        return partnerManager.selectPartnerInterfaceConfigByPrimaryKey(id);
    }

    public PartnerProduct selectPartnerProductByPrimaryKey(Long id) {
        return partnerManager.selectPartnerProductByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Partner partner) {
        return partnerManager.updateByPrimaryKeySelective(partner);
    }

    public int updateByPrimaryKeySelective(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerManager.updateByPrimaryKeySelective(partnerInterfaceConfig);
    }

    public int updateByPrimaryKeySelective(PartnerProduct partnerProduct) {
        int p = partnerManager.updateByPrimaryKeySelective(partnerProduct);
        syncPartnerProduct(partnerProduct.getId());
        return p;
    }

    public int updateByPrimaryKeySelective(PartnerUser partnerUser) {
        return partnerManager.updateByPrimaryKeySelective(partnerUser);
    }

    @Override
    public int updateByPrimaryKeySelective(InterfaceExecRecord interfaceExecRecord) {
        return partnerManager.updateByPrimaryKeySelective(interfaceExecRecord);
    }

    @Override
    public int insertSelective(InterfaceExecRecord interfaceExecRecord) {
        return partnerManager.insertSelective(interfaceExecRecord);
    }

    public int insertSelective(Partner partner) {
        return partnerManager.insertSelective(partner);
    }

    public int insertSelective(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerManager.insertSelective(partnerInterfaceConfig);
    }

    public int insertSelective(PartnerProduct partnerProduct) {
        int p = partnerManager.insertSelective(partnerProduct);
        syncPartnerProduct(partnerProduct.getId());
        return p;
    }

    public int insertSelective(PartnerUser partnerUser) {
        return partnerManager.insertSelective(partnerUser);
    }

    @Override
    public Partner findPartnerByUniqueKey(String uniqueKey) {
        return partnerManager.findPartnerByUniqueKey(uniqueKey);
    }

    @Override
    public PartnerInterfaceConfig findConfigByPartnerAndType(Long partner, byte type) {
        return partnerManager.findConfigByPartnerAndType(partner, type);
    }

    @Override
    public int countPartnerProductByProductId(Long productId) {
        return partnerManager.countPartnerProductByProductId(productId);
    }

    @Override
    public List<PartnerProduct> findPartnerProductByProductId(Long productId) {
        return partnerManager.findPartnerProductByProductId(productId);
    }

    @Override
    public List<PartnerInterfaceConfig> findConfigByPartnerIdAndType(List<Long> partnerIds, byte interfaceType) {
        return partnerManager.findConfigByPartnerIdAndType(partnerIds, interfaceType);
    }

    @Override
    public int insertInterfaceExecRecordList(List<InterfaceExecRecord> list) {
        return partnerManager.insertInterfaceExecRecordList(list);
    }

    /**
     * 同步产品.
     *
     * @param id
     */
    @Override
    public void syncProduct(Long id) {
        if (id != null && id > 0) {
            int count = countPartnerProductByProductId(id);
            if (count > 0) {
                List<PartnerProduct> list = findPartnerProductByProductId(id);
                if (StringUtils.isNotEmpty(list)) {
                    List<Long> partnerIds = new ArrayList<>();
                    //获取partner product、根据partnerId获取接口信息.
                    list.forEach(a -> partnerIds.add(a.getPartnerId()));
                    saveExecRecords(partnerIds);
                }
            }
        }
    }

    /**
     * 查找record 根据partnerid and type and status
     *
     * @param partnerId
     * @param type
     * @param status
     * @return
     */
    @Override
    public List<InterfaceExecRecord> findRecordPartnerIdAndTypeAndStatus(List<Long> partnerId, byte type, byte status) {
        return partnerManager.findRecordPartnerIdAndTypeAndStatus(partnerId, type, status);
    }

    /**
     * 查找Record根据partnerId and type and status
     * result map
     *
     * @param partnerId
     * @param type
     * @param status
     * @return
     */
    private Map<Long, Long> findRecordPartnerIdAndTypeAndStatusResultMap(List<Long> partnerId, byte type, byte status) {
        List<InterfaceExecRecord> list = findRecordPartnerIdAndTypeAndStatus(partnerId, type, status);
        Map<Long, Long> map = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            list.forEach(a -> map.put(a.getPartnerId(), a.getPartnerId()));
        }
        return map;
    }


    /**
     * 保存执行计划.
     *
     * @param partnerIds
     */
    private void saveExecRecords(List<Long> partnerIds) {
        List<PartnerInterfaceConfig> configs = findConfigByPartnerIdAndType(partnerIds, InterfaceTypeEnums.SYNC_PARTNER_PRODUCT.getType());
        Map<Long, Long> map = findRecordPartnerIdAndTypeAndStatusResultMap(partnerIds, InterfaceTypeEnums.SYNC_PARTNER_PRODUCT.getType(), InterfaceExecStatus.WAIT.getStatus());
        if (StringUtils.isNotEmpty(configs)) {
            List<InterfaceExecRecord> execRecords = new ArrayList<>();
            configs.forEach(a -> {
                if (map.get(a.getPartnerId()) == null) {
                    InterfaceExecRecord record = new InterfaceExecRecord();
                    record.setInterfaceType(a.getInterfaceType());
                    record.setInterfaceUrl(a.getInterfaceUrl());
                    record.setPartnerId(a.getPartnerId());
                    record.setStatus(InterfaceExecStatus.WAIT.getStatus());
                    execRecords.add(record);
                }
            });
            insertInterfaceExecRecordList(execRecords);
        }
    }

    /**
     * 同步数据
     *
     * @param id
     */
    @Override
    public void syncPartnerProduct(Long id) {
        PartnerProduct partnerProduct = partnerManager.selectPartnerProductByPrimaryKey(id);
        List<Long> partnerIds = new ArrayList<>();
        partnerIds.add(partnerProduct.getPartnerId());
        saveExecRecords(partnerIds);
    }

    @Override
    public List<InterfaceExecRecord> queryRecordByStatus(byte status) {
        return partnerManager.queryRecordByStatus(status);
    }

    @Override
    public void record(RecordDto recordDto) {
        PartnerInterfaceConfig partnerInterfaceConfig = findConfigByPartnerAndType(recordDto.getPartnerId(), recordDto.getInterfaceType());
        InterfaceExecRecord interfaceExecRecord = new InterfaceExecRecord();
        interfaceExecRecord.setStatus(InterfaceExecStatus.WAIT.getStatus());
        interfaceExecRecord.setPartnerId(recordDto.getPartnerId());
        interfaceExecRecord.setInterfaceUrl(partnerInterfaceConfig.getInterfaceUrl());
        interfaceExecRecord.setInterfaceType(recordDto.getInterfaceType());
        interfaceExecRecord.setData(recordDto.getData());
        insertSelective(interfaceExecRecord);
    }

    @Override
    public Map<Long, Partner> findPartnerByIdResultMap(List<Long> partnerIds) {
        List<Partner> partnerList = findPartnerByIds(partnerIds);
        Map<Long, Partner> result = new HashMap<>();
        if (StringUtils.isNotEmpty(partnerList)) {
            partnerList.forEach(a -> result.put(a.getId(), a));
        }
        return result;
    }

    public List<Partner> findPartnerByIds(List<Long> partnerIds) {
        return partnerManager.findPartnerByIds(partnerIds);
    }

    @Override
    public List<Partner> searchPartner(PartnerDto dto, Page page) {
        Integer count = partnerManager.searchPartnerCount(dto);
        page.setCount(count);
        if (count != null) {
            List<Partner> list = partnerManager.searchPartner(dto, page);
            return list;
        }
        return null;
    }

    @Override
    public List<Partner> findAll() {
        return partnerManager.findAll();
    }

    @Override
    public List<PartnerInterfaceConfig> searchConfig(PartnerInterfaceConfig partnerInterfaceConfig, Page page) {
        Integer count = partnerManager.searchConfigCount(partnerInterfaceConfig);
        page.setCount(count);
        if(page.isQuery()) {
            return partnerManager.searchConfig(partnerInterfaceConfig, page);
        }
        return null;
    }
}
