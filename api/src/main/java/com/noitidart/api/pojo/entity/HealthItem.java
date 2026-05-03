package com.noitidart.api.pojo.entity;

/**
 * 健康项实体，关联数据库 health_item表
 */
public class HealthItem {

    /**
     * 健康项信息表主键ID，自增
     */
    private Integer id;

    /**
     * 健康项名
     */
    private String name;

    /**
     * 简介
     */
    private String detail;

    /**
     * 图标
     */
    private String iconUrl;

    /**
     * 单位
     */
    private String unit;

    /**
     * 符号
     */
    private String symbol;

    /**
     * 正常值范围
     */
    private String normalValue;

    /**
     * 用户ID，外键，关联的是用户表
     */
    private Integer userId;

    /**
     * 是否是公有健康项
     */
    private Boolean isGlobal;

    /**
     * 创建时间
     */
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNormalValue() {
        return normalValue;
    }

    public void setNormalValue(String normalValue) {
        this.normalValue = normalValue;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsGlobal() {
        return isGlobal;
    }

    public void setIsGlobal(Boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
