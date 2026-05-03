package com.noitidart.api.pojo.vo;

/**
 * 健康数据折线图工具配置 VO
 */
public class HealthItemToolTipVO {

    /**
     * 值 => ID
     */
    private Integer value;

    /**
     * 名称
     */
    private String label;

    /**
     * 单位
     */
    private String unit;

    /**
     * 简介
     */
    private String detail;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
