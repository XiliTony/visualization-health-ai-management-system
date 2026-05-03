package com.noitidart.api.pojo.dto;

/**
 * 用户查询参数 DTO 类
 */
public class UserQueryDTO {

    /**
     * 昵称
     */
    private String username;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色
     */
    private Integer role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
