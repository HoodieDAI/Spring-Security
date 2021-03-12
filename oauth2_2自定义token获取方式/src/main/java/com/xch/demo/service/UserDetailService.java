package com.xch.demo.service;

import com.xch.demo.entity.MyUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/3/11 22:12
 */
@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //这里应该通过用户查找数据库里面的用户
        MyUser user = new MyUser();
        user.setUserName(username);
        user.setPassword(this.passwordEncoder.encode("123456"));

        System.out.println(user.getPassword());
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase("xch", username)) {
            authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        } else {
            authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("test");
        }
        return new User(username, user.getPassword(), user.isEnabled(),
                user.isCredentialsNonExpired(), user.isAccountNonLocked(),
                user.isCredentialsNonExpired(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
