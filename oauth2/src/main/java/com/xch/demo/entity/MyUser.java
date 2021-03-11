package com.xch.demo.entity;

import java.io.Serializable;

/**
 * @author 许昌豪
 * @version 1.0
 * @date 2021/3/11 22:10
 */
public class MyUser implements Serializable {
    private String userName;
    private String password;
    private boolean accountNonExpire = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonExpire() {
        return accountNonExpire;
    }

    public void setAccountNonExpire(boolean accountNonExpire) {
        this.accountNonExpire = accountNonExpire;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", accountNonExpire=" + accountNonExpire +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}
