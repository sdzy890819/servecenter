package com.fdz.content.service;

import com.fdz.common.constant.Constants;
import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.security.vo.LoginUser;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.utils.UserDisassembly;
import com.fdz.content.domain.Manager;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.manager.ManageManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service("managerService")
public class ManagerServiceImpl implements ManagerService, UserDetailsService {

    @Resource
    private ManageManager manageManager;

    @Resource
    private RedisDataManager redisDataManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private PartnerService partnerService;

    @Override
    public Manager findManagerByUserName(String userName) {
        return manageManager.findManagerByUserName(userName);
    }

    @Override
    public Manager selectManagerByPrimaryKey(Long id) {
        return manageManager.selectManagerByPrimaryKey(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("userId为空");
        }
        if (UserDisassembly.isM(username)) {
            username = UserDisassembly.disassembly(username);
            if (username.matches("[0-9]+")) {
                try {
                    Manager manager = selectManagerByPrimaryKey(Long.parseLong(username));
                    if (manager != null) {
                        LoginUser loginUser = new LoginUser();
                        loginUser.setPassword(passwordEncoder.encode(manager.getPassword()));
                        loginUser.setId(manager.getId());
                        loginUser.setUserName(UserDisassembly.assembleM(manager.getId()));
                        redisDataManager.set(Constants.RedisKey.MANAGE_LOGIN_LABEL + loginUser.getUsername(), loginUser, 1, TimeUnit.DAYS);
                        return loginUser;
                    }
                } catch (NumberFormatException e) {
                    throw new UsernameNotFoundException("解析错误");
                }
            }
        }
        if (UserDisassembly.isP(username)) {
            username = UserDisassembly.disassembly(username);
            if (username.matches("[0-9]+")) {
                try {
                    PartnerUser partnerUser = partnerService.selectPartnerUserByPrimaryKey(Long.parseLong(username));
                    if (partnerUser != null) {
                        LoginUser loginUser = new LoginUser();
                        loginUser.setId(partnerUser.getPartnerId());
                        loginUser.setPassword(passwordEncoder.encode(partnerUser.getPassword()));
                        loginUser.setUserName(UserDisassembly.assembleP(partnerUser.getPartnerId()));
                        redisDataManager.set(Constants.RedisKey.PARTNER_LOGIN_LABEL + loginUser.getUsername(), loginUser, 1, TimeUnit.DAYS);
                        return loginUser;
                    }
                } catch (NumberFormatException e) {
                    throw new UsernameNotFoundException("解析错误");
                }
            }
        }
        throw new UsernameNotFoundException("不是当前的用户体系");
    }


    public int updateByPrimaryKeySelective(Manager manager) {
        return manageManager.updateByPrimaryKeySelective(manager);
    }

    public int insertSelective(Manager record) {
        return manageManager.insertSelective(record);
    }
}
