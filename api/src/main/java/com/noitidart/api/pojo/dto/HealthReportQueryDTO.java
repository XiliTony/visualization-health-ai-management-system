package com.noitidart.api.pojo.dto;

import lombok.Data;

/**
 *  AI 生成健康报告所需要查找的数据DTO类
 */
@Data
public class HealthReportQueryDTO {

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 健康项 ID（可选）
     */
    private Integer healthItemId;

    /**
     * 起始时间，格式: yyyy-MM-dd
     */
    private String startTime;

    /**
     * 结束时间，格式: yyyy-MM-dd
     */
    private String endTime;
}
