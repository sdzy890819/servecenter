package com.fdz.content.service;

import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.security.vo.LoginUser;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.utils.UserDisassembly;
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerInterfaceConfig;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.manager.PartnerManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return partnerManager.updateByPrimaryKeySelective(partnerProduct);
    }

    public int updateByPrimaryKeySelective(PartnerUser partnerUser) {
        return partnerManager.updateByPrimaryKeySelective(partnerUser);
    }

    public int insertSelective(Partner partner) {
        return partnerManager.insertSelective(partner);
    }

    public int insertSelective(PartnerInterfaceConfig partnerInterfaceConfig) {
        return partnerManager.insertSelective(partnerInterfaceConfig);
    }

    public int insertSelective(PartnerProduct partnerProduct) {
        return partnerManager.insertSelective(partnerProduct);
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
}
