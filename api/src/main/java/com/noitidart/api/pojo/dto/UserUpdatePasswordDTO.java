package com.noitidart.api.pojo.dto;

/**
 * 用户修改密码 DTO 类
 */
public class UserUpdatePasswordDTO {

    /**
     * 账号 ID
     */
    private Integer id;

    /**
     * 原密码
     */
    private String password;

    /**
     * 修改的新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmNewPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
