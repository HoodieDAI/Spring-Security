package com.xch.demo.demo.config;

import com.xch.demo.filter.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/2/25 20:02
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
//                .loginPage("/login.html") //配置登录页面请求URL
                .loginPage("/authentication/require")
                .loginProcessingUrl("/login") //对应页面表单 form 的action=“/login"
                .successHandler(authenticationSuccessHandler) //配置成功处理机制
                .failureHandler(authenticationFailureHandler) //配置失败处理机制
                .and()
                .authorizeRequests() //授权配置
                .antMatchers("/authentication/require", "/login.html", "/code/image").permitAll() // 跳转 /login.html 请求不会被拦截
                .anyRequest() //所有请求
                .authenticated() //都需要认证
                .and().csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
