package com.xch.demo.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/2/25 19:55
 */
@RestController
public class TestController {
    @RequestMapping("hello")
    public String login() {
        return "hello-world";
    }

//    @GetMapping("index")
//    public Object index() {
//        return SecurityContextHolder.getContext().getAuthentication();
//    }

    @GetMapping("index")
    public Object index(Authentication authentication) {
        return authentication;
    }
}
