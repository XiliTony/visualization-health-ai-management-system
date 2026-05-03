package com.noitidart.api.pojo.dto;

import lombok.Data;

/**
 * 健康报告数据DTO类，每个对象代表一条健康记录
 */
@Data
public class HealthReportDataDTO {

    /**
     * 健康项名称
     */
    private String healthItemName;

    /**
     * 健康记录值
     */
    private Double healthRecordValue;

    /**
     * 正常值范围
     */
    private String normalValue;

    /**
     * 记录时间
     */
    private String createTime;

}
