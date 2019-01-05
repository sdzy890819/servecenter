package com.fdz.content.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePwdDto {

    @NotNull(message = "旧密码不可以为空")
    private String pwd;

    @NotNull(message = "新密码不可以为空")
    private String newPwd;

    @NotNull(message = "新密码确认不可以为空")
    private String newPwd2;
}
