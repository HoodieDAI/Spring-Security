package com.xch.demo.smscode;

import com.xch.demo.service.RedisCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private RedisCodeService redisCodeService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsAnyIgnoreCase("/login/mobile", request.getRequestURI())
                && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(request));
            } catch (Exception e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException(e.getMessage()));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws Exception {
        String smsCodeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobileInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");

        String codeInRedis = redisCodeService.get(servletWebRequest, mobileInRequest);

        if (StringUtils.isBlank(smsCodeInRequest)) {
            throw new Exception("验证码不能为空");
        }
        if (codeInRedis == null) {
            throw new Exception("验证码已过期");
        }
        if (!StringUtils.equalsIgnoreCase(codeInRedis, smsCodeInRequest)) {
            throw new Exception("验证码不正确!");
        }
        redisCodeService.remove(servletWebRequest, mobileInRequest);
    }
}
