package com.noitidart.api.pojo.dto;

/**
 * 健康记录条件查询类
 */
public class HealthRecordQueryDTO {

    /**
     * 健康项 ID
     */
    private Integer healthItemId;

    /**
     * 用户ID，外键，与数据库用户表关联
     */
    private Integer userId;

    /**
     * 查询天数
     */
    private Integer days;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    public Integer getHealthItemId() {
        return healthItemId;
    }

    public void setHealthItemId(Integer healthItemId) {
        this.healthItemId = healthItemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
