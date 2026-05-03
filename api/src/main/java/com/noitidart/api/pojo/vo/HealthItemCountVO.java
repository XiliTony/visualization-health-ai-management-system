package com.noitidart.api.pojo.vo;

/**
 * 模型统计 VO 类
 */
public class HealthItemCountVO {

    /**
     * 公有健康项数
     */
    private Integer globalItemCount;
    /**
     * 私有健康项数
     */
    private Integer privateItemCount;

    public Integer getGlobalItemCount() {
        return globalItemCount;
    }

    public void setGlobalItemCount(Integer globalItemCount) {
        this.globalItemCount = globalItemCount;
    }

    public Integer getPrivateItemCount() {
        return privateItemCount;
    }

    public void setPrivateItemCount(Integer privateItemCount) {
        this.privateItemCount = privateItemCount;
    }
}