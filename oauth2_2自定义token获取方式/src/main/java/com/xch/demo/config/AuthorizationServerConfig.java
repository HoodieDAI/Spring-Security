package com.xch.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/3/11 22:17
 */


/**
 * 总结： /oauth/token 这个地址下的授权码和密码登录方式，是需要在AuthorizationServerConfig下继承WebSecurityConfigurerAdapter实现的。
 * 然后本文通过自定义地址login，是需要去除AuthorizationServerConfig的继承，然后 ResourceServerConfig继承ResourceServerConfigurerAdapter 来实现的。
 */
//public class AuthorizationServerConfig extends WebSecurityConfigurerAdapter {
@Configuration
//创建认证服务器
@EnableAuthorizationServer
public class AuthorizationServerConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

