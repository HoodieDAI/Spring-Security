package com.xch.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/2/25 20:02
 */
//@Configuration
//public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //http.formLogin() 表单验证方式
//        http.httpBasic()
//                .and()
//                .authorizeRequests() //授权配置
//                .anyRequest() //所有请求
//                .authenticated(); //都需要认证
//    }
//}
