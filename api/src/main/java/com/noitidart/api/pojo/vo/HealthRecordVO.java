package com.noitidart.api.pojo.vo;

import com.noitidart.api.pojo.entity.HealthRecord;

/**
 * 健康记录 VO 类
 */
public class HealthRecordVO extends HealthRecord {

    /**
     * 昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 健康项名称
     */
    private String healthItemName;

    /**
     * 健康项单位
     */
    private String healthItemUnit;

    /**
     * 健康项阈值
     */
    private String normalValue;

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

    public String getHealthItemName() {
        return healthItemName;
    }

    public void setHealthItemName(String healthItemName) {
        this.healthItemName = healthItemName;
    }

    public String getHealthItemUnit() {
        return healthItemUnit;
    }

    public void setHealthItemUnit(String healthItemUnit) {
        this.healthItemUnit = healthItemUnit;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }
}
