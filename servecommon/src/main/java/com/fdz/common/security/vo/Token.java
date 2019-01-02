package com.fdz.common.security.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fdz.common.constant.Constants;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Data
public class Token implements UserDetails {

    private Long userId;

    private String mobile;

    private String sessionId;

    private String iss;

    private Long iat;

    private Long exp;

    @JsonProperty("allow_exp")
    private Long allowExp;

    private String password;

    private boolean blocked;

    public Token() {
        iss = "ouding.com";
        sessionId = UUID.randomUUID().toString();
        iat = System.currentTimeMillis();
        exp = System.currentTimeMillis() + Constants.Common.JWT_TTL;
        allowExp = System.currentTimeMillis() + Constants.Common.JWT_TTL + 5000;
    }


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_REGULAR"));
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return String.valueOf(getUserId());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return !blocked;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return !blocked;
    }
}
