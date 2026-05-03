package com.noitidart.api.pojo.entity;

/**
 * 健康记录实体，关联数据库 health_record表
 */
public class HealthRecord {

    /**
     * 用户健康记录表自增 ID
     */
    private Integer id;

    /**
     * 用户ID，外键，关联的是用户表
     */
    private Integer userId;

    /**
     * 健康项ID，外键，关联的是健康项信息表
     */
    private Integer healthItemId;

    /**
     * 记录的值
     */
    private Double value;

    /**
     * 记录时间
     */
    private String createTime;

    /**
     * 【AI 交互专用】健康项名称（不持久化到数据库）
     * transient 表示不参与序列化/数据库映射
     */
    private transient String healthItemName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getHealthItemId() {
        return healthItemId;
    }

    public void setHealthItemId(Integer healthItemId) {
        this.healthItemId = healthItemId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHealthItemName() {
        return healthItemName;
    }

    public void setHealthItemName(String healthItemName) {
        this.healthItemName = healthItemName;
    }
}
