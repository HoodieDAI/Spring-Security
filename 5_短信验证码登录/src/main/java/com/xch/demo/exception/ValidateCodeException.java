package com.xch.demo.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/2/28 13:11
 */

//注意AuthenticationException依赖不要导错了 是org.springframework.security.core.AuthenticationException
public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String message) {
        super(message);
    }
}
