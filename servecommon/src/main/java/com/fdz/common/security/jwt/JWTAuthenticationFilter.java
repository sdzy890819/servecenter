package com.fdz.common.security.jwt;

import com.fdz.common.utils.CookieUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.https://github.com/shuaicj/zuul-auth-example
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    private final String authorizationHeaderName;
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(TokenProvider tokenProvider, String authorizationHeaderName, AuthenticationManager authenticationManager) {
        this.authorizationHeaderName = authorizationHeaderName;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
            authentication = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = CookieUtil.getCookieVal(request, authorizationHeaderName);
        if (!StringUtils.hasText(bearerToken)) {
            bearerToken = request.getHeader(authorizationHeaderName);
        }
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return bearerToken;
    }
}
