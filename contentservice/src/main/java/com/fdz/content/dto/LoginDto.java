package com.fdz.content.dto;

import com.fdz.common.utils.EncryptUtil;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginDto {

    @NotNull(message = "用户名不可以为空")
    private String userName;

    @NotNull(message = "密码不可以为空")
    private String password;

    public static void main(String[] args) {
        System.out.println(EncryptUtil.encryptPwd("sanjie", "sanjie2019"));
    }

}
