package com.fdz.partnergateway.service;

import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.security.vo.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userCheckService")
public class UserCheckServiceImpl implements UserDetailsService {

    @Resource
    private RedisDataManager redisDataManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser loginUser = (LoginUser) redisDataManager.getObject(username);
        if (loginUser == null) {
            throw new UsernameNotFoundException("用户登录已失效, 请重新登录");
        }
        return loginUser;
    }
}
