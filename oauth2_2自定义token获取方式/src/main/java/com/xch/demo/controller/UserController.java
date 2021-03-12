package com.xch.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/3/11 22:43
 */
@RestController
public class UserController {

    @GetMapping("index")
    public Object index(Authentication authentication) {
        return authentication;
    }
}
