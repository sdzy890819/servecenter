package com.fdz.common.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.exception.BizException;
import com.fdz.common.security.vo.Token;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private String secretKey;

    private ObjectMapper objectMapper;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;


    public TokenProvider(String secretKey, long tokenValidityInMilliseconds) {
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.secretKey = secretKey;
    }

    public TokenProvider(String secretKey, long tokenValidityInMilliseconds, long tokenValidityInMillisecondsForRememberMe, ObjectMapper objectMapper) {
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.tokenValidityInMillisecondsForRememberMe = tokenValidityInMillisecondsForRememberMe;
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    public String createToken(Authentication authentication, Boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        try {
            return Jwts.builder()
                    .setId("jwt")
                    .setSubject(objectMapper.writeValueAsString(authentication.getPrincipal()))
                    .claim(AUTHORITIES_KEY, authorities)
                    .signWith(SignatureAlgorithm.HS512, generalKey())
                    .setIssuedAt(nowDate)
                    .setExpiration(validity)
                    .compact();
        } catch (JsonProcessingException e) {
            log.error("object mapper write value as string error", e);
            throw new BizException("登录错误");
        }
    }

    public SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(secretKey);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token)
                .getBody();
        String authnor = claims.get(AUTHORITIES_KEY) != null ? claims.get(AUTHORITIES_KEY).toString() : "ROLE_REGULAR";
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authnor.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Token principal = null;
        try {
            principal = objectMapper.readValue(claims.getSubject(), Token.class);
        } catch (IOException e) {
            log.error("出现错误");
        }

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(generalKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJqd3QiLCJpYXQiOjE1MzM3MTIzMDQsInN1YiI6IntcImFsbG93X2V4cFwiOjE1MzYzMDQzMDk4NDMsXCJtb2JpbGVcIjpcIjEzNjYzNjM3MzQxXCIsXCJpc3NcIjpcIm91ZGluZy5jb21cIixcInNlc3Npb25JZFwiOlwiN2Y3YzVmZmItZDAyZi00OGViLTgwOWMtYzhkZTEyNDg4YTMyXCIsXCJleHBcIjoxNTM2MzA0MzA0ODQzLFwidXNlcklkXCI6XCI1ODMzMTBcIixcImlhdFwiOjE1MzM3MTIzMDQ4NDN9IiwiZXhwIjoxNTM2MzA0MzA0fQ.nuVnk94K9OwUT-6zuUvktd4fva6PTPgArpX3ywdyWjE";
        TokenProvider tokenProvider = new TokenProvider("nulloudin1mu2zhi3ruan4jian5", 30 * 24 * 60 * 60 * 1000L);
        tokenProvider.getAuthentication(token);
    }
}
