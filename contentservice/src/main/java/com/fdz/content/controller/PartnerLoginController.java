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
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.dto.LoginDto;
import com.fdz.content.dto.LoginResult;
import com.fdz.content.dto.PartnerLoginInfoResult;
import com.fdz.content.dto.UpdatePwdDto;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ApiVersion("1")
@RequestMapping("/content/partner")
@Api("合作伙伴登录接口")
public class PartnerLoginController {

    @Resource
    private PartnerService partnerService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenProvider tokenProvider;

    @Resource
    private RedisDataManager redisDataManager;

    @ApiOperation("登录")
    @PostMapping("/login")
    RestResponse<LoginResult> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginResult loginResult = new LoginResult();
        PartnerUser partnerUser = partnerService.findPartnerUserByUserName(loginDto.getUserName());
        if (partnerUser == null) {
            throw new BizException("用户名、或密码错误");
        }
        String pwd = EncryptUtil.encryptPwd(loginDto.getUserName(), loginDto.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(UserDisassembly.assembleP(partnerUser.getId()), pwd);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        String bearJWT = "Bearer " + jwt;
        CookieUtil.addCookie(response, Constants.Common.TOKEN_P, bearJWT, 0);
        loginResult.setToken(bearJWT);
        return RestResponse.success(loginResult);
    }

    @ApiOperation("修改密码")
    @PostMapping("/update-pwd")
    RestResponse updatePwd(@RequestBody UpdatePwdDto dto) {
        Long userId = SecurityUtils.checkLoginAndGetUserByPartner();
        PartnerUser partnerUser = partnerService.selectPartnerUserByPrimaryKey(userId);
        String pwd = EncryptUtil.encryptPwd(partnerUser.getUserName(), dto.getPwd());
        if (!dto.getNewPwd().equals(dto.getNewPwd2())) {
            throw new BizException("输入的两次密码不一致");
        }
        if (partnerUser.getPassword().equals(pwd)) {
            PartnerUser updateUser = new PartnerUser(partnerUser.getId());
            updateUser.setPassword(EncryptUtil.encryptPwd(partnerUser.getUserName(), dto.getNewPwd()));
            partnerService.updateByPrimaryKeySelective(updateUser);
        } else {
            throw new BizException("旧密码不对.请重新输入");
        }
        return RestResponse.success(null);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/current-user")
    RestResponse<PartnerLoginInfoResult> currentUser() {
        Long userId = SecurityUtils.checkLoginAndGetUserByPartner();
        PartnerUser partnerUser = partnerService.selectPartnerUserByPrimaryKey(userId);
        Partner partner = partnerService.selectPartnerByPrimaryKey(partnerUser.getPartnerId());
        PartnerLoginInfoResult partnerLoginInfoResult = new PartnerLoginInfoResult();
        partnerLoginInfoResult.setParentId(partner.getId());
        partnerLoginInfoResult.setParentName(partner.getName());
        partnerLoginInfoResult.setParentShortName(partner.getShortName());
        partnerLoginInfoResult.setRealName(partnerUser.getRealName());
        return RestResponse.success(partnerLoginInfoResult);
    }

    @ApiOperation("退出")
    @GetMapping("/exit")
    RestResponse<?> exit(HttpServletRequest request, HttpServletResponse response) {
        Long userId = SecurityUtils.checkLoginAndGetUserByManager();
        redisDataManager.delete(UserDisassembly.assembleP(userId));
        CookieUtil.delCookieVal(request, response, Constants.Common.TOKEN_P);
        return RestResponse.success(null);
    }

}
