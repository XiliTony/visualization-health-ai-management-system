package com.noitidart.api.pojo.dto;

/**
 * 用户登录参数 DTO 类
 */
public class UserLoginDTO {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;  // 密码

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
