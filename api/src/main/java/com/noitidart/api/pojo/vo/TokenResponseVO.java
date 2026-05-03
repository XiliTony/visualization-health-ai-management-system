package com.noitidart.api.pojo.vo;

/**
 * 登录返回给前端的数据 VO 类
 */
public class TokenResponseVO {

    /**
     * JWT 鉴权
     */
    private String token;

    /**
     * 账号 ID
     */
    private Integer id;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色
     */
    private String role;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
