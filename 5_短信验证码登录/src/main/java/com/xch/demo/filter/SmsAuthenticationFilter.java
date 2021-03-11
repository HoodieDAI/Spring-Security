package com.xch.demo.filter;

import com.xch.demo.token.SmsAuthenticationToken;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/3/11 20:40
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String MOBILE_KEY = "mobile";
    //对应表单的name属性
    private String mobileParameter = MOBILE_KEY;
    private boolean postOnly = true;

    public SmsAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/mobile", "POST"));
    }

    //尝试身份验证
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String mobile = this.obtainMobile(request);
            if (mobile == null) {
                mobile = "";
            }
            mobile = mobile.trim();
            SmsAuthenticationToken authRequest = new SmsAuthenticationToken(mobile);
            setDetails(request, authRequest);
            //将token交给AuthenticationManager处理
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }


    //获取电话号码
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    protected void setDetails(HttpServletRequest request, SmsAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }


    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "Password parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }

    public final String getMobileParameter() {
        return mobileParameter;
    }
}
