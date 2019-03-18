package com.fdz.content.controller;

import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.security.jwt.TokenProvider;
import com.fdz.common.utils.CookieUtil;
import com.fdz.common.utils.EncryptUtil;
import com.fdz.common.utils.UserDisassembly;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.domain.Manager;
import com.fdz.content.dto.LoginDto;
import com.fdz.content.dto.LoginResult;
import com.fdz.content.dto.ManagerLoginInfoResult;
import com.fdz.content.dto.UpdatePwdDto;
import com.fdz.content.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ApiVersion("1")
@RequestMapping("/content/manager")
@Api("后台登录接口")
public class ManagerLoginController {


    @Resource
    private ManagerService managerService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenProvider tokenProviderManager;

    @Resource
    private RedisDataManager redisDataManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @ApiOperation("登录")
    @PostMapping("/login")
    RestResponse<LoginResult> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginResult loginResult = new LoginResult();
        Manager manager = managerService.findManagerByUserName(loginDto.getUserName());
        if (manager == null) {
            throw new BizException("用户名、或密码错误");
        }
        String pwd = EncryptUtil.encryptPwd(loginDto.getUserName(), loginDto.getPassword());
        if (!manager.getPassword().equals(pwd)) {
            throw new BizException("用户名、或密码错误");
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(UserDisassembly.assembleM(manager.getId()), pwd);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProviderManager.createToken(authentication, false);
        String bearJWT = "Bearer " + jwt;
        CookieUtil.addCookie(response, Constants.Common.TOKEN_M, bearJWT, 60 * 60 * 2);
        loginResult.setToken(bearJWT);
        return RestResponse.success(loginResult);
    }

    @ApiOperation("修改密码")
    @PostMapping("/update-pwd")
    RestResponse updatePwd(@RequestBody UpdatePwdDto dto) {
        Long userId = SecurityUtils.checkLoginAndGetUserByPartner();
        Manager manager = managerService.selectManagerByPrimaryKey(userId);
        String pwd = EncryptUtil.encryptPwd(manager.getUserName(), dto.getPwd());
        if (!dto.getNewPwd().equals(dto.getNewPwd2())) {
            throw new BizException("输入的两次密码不一致");
        }
        if (manager.getPassword().equals(pwd)) {
            Manager updateManager = new Manager(manager.getId());
            updateManager.setPassword(passwordEncoder.encode(EncryptUtil.encryptPwd(manager.getUserName(), dto.getNewPwd())));
            managerService.updateByPrimaryKeySelective(updateManager);
        } else {
            throw new BizException("旧密码不对.请重新输入");
        }
        return RestResponse.success(null);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/current-user")
    RestResponse<ManagerLoginInfoResult> currentUser() {
        Long userId = SecurityUtils.checkLoginAndGetUserByManager();
        Manager manager = managerService.selectManagerByPrimaryKey(userId);
        ManagerLoginInfoResult managerLoginInfoResult = new ManagerLoginInfoResult();
        managerLoginInfoResult.setRealName(manager.getRealName());
        return RestResponse.success(managerLoginInfoResult);
    }

    @ApiOperation("退出")
    @GetMapping("/exit")
    RestResponse<?> exit(HttpServletRequest request, HttpServletResponse response) {
        Long userId = SecurityUtils.checkLoginAndGetUserByManager();
        redisDataManager.delete(UserDisassembly.assembleM(userId));
        CookieUtil.delCookieVal(request, response, Constants.Common.TOKEN_M);
        return RestResponse.success(null);
    }

}
