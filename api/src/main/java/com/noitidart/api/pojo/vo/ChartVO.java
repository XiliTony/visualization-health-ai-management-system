package com.noitidart.api.pojo.vo;

/**
 * 数据源 VO
 */
public class ChartVO {

    /**
     * 数据名
     */
    private String name;

    /**
     * 数据总数
     */
    private Integer count;

    public ChartVO(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
