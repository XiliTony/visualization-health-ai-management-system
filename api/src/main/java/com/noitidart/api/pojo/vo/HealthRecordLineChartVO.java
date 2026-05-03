package com.noitidart.api.pojo.vo;

/**
 * 健康数据折线图 VO
 */
public class HealthRecordLineChartVO {

    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 记录值
     */
    private Double value;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
