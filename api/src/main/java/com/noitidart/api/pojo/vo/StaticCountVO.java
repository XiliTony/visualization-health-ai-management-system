package com.noitidart.api.pojo.vo;

/**
 * 仪表盘静态数据 VO 类
 */
public class StaticCountVO {

    /**
     * 存量用户
     */
    private Integer userCount;

    /**
     * 收录健康项
     */
    private Integer itemCount;

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}
