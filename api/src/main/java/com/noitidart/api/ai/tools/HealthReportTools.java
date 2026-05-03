package com.noitidart.api.ai.tools;

import com.noitidart.api.pojo.dto.HealthReportDataDTO;
import com.noitidart.api.pojo.dto.HealthReportQueryDTO;
import com.noitidart.api.service.HealthItemService;
import com.noitidart.api.service.HealthRecordService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.invocation.InvocationParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * AI 生成健康报告工具类
 */
@Slf4j
@Component
public class HealthReportTools {

    @Autowired
    private HealthItemService healthItemService;

    @Autowired
    private HealthRecordService healthRecordService;

    @Tool(name = "获取指定时间范围内的健康记录数据", value = """
                获取用户在指定时间范围内的健康记录数据。如果用户没有指定时间范围，默认查询最近7天
                参数说明：
                - startDate: 开始日期，格式 yyyy-MM-dd
                - endDate: 结束日期，格式 yyyy-MM-dd
                - healthItemName: 健康项名称，可选参数。如果指定，只返回该健康项的数据；如果不指定，返回所有健康项的数据
                返回数据说明：
                - healthItemName: 健康项名称（如：血压、睡眠、体重）
                - healthRecordValue: 记录的具体数值
                - normalValue: 该健康项的正常值范围（格式：最小值,最大值），小于最小值或大于最大值视为异常
                - createTime：该健康记录的创建时间
                使用场景：
                当用户要求生成健康报告时，调用此工具获取数据。
                如果用户指定了具体的健康项（如"血压报告"），则只获取该健康项的数据；
                如果用户要求"全部报告"或没有指定，则获取所有健康项的数据。
            """)
    public Object getHealthRecordForReport(
            @P("开始日期，格式: yyyy-MM-dd") String startDate,
            @P("结束日期，格式: yyyy-MM-dd") String endDate,
            @P("健康项名称，可选，不传则返回所有健康项的数据") String healthItemName,
            InvocationParameters parameters) {
        try {
            // 从 InvocationParameters 中获得当前对话用户ID
            Integer userId = parameters.get("userId");
            if (userId == null) {
                log.error("无法获取当前用户 ID");
                return "❌ 无法获取用户信息，请重新登录";
            }
            // 参数校验
            if (!StringUtils.hasText(startDate) || !StringUtils.hasText(endDate)) {
                log.warn("日期参数为空，startDate={}, endDate={}", startDate, endDate);
                return "❌ 请提供正确的开始日期和结束日期";
            }
            // 打印日志
            log.info("查询健康记录，用户ID={}, 时间范围={} 至 {}, 健康项={}",
                    userId, startDate, endDate, healthItemName == null ? "全部" : healthItemName);
            // 创建生成健康报告需要查找的数据 DTO 类
            HealthReportQueryDTO healthReportQueryDTO = new HealthReportQueryDTO();
            // 设置要查找的用户 ID
            healthReportQueryDTO.setUserId(userId);
            // 设置要查找的开始时间
            healthReportQueryDTO.setStartTime(startDate);
            // 设置要查找的结束时间
            healthReportQueryDTO.setEndTime(endDate);
            if (StringUtils.hasText(healthItemName)) {
                Integer healthItemId = healthItemService.getIdByName(healthItemName, userId);
                if (healthItemId == null) {
                    // 健康项不存在返回提示
                    return String.format(
                            """
                            健康项【%s】不存在。请先创建该健康项，才能生成报告。
                            你可以告诉用户：
                            1. 健康项【%s】还没有被创建
                            2. 询问用户是否需要创建这个健康项
                            3. 如果需要创建，请让用户提供：简介、单位、符号、正常值范围
                            4. 或者选择其他已存在的健康项来生成报告""",
                            healthItemName, healthItemName);
                }
                healthReportQueryDTO.setHealthItemId(healthItemId);
            }
            // 查询数据
            List<HealthReportDataDTO> result = healthRecordService.selectRecordForReport(healthReportQueryDTO);
            // 检查是否有数据
            if (result == null || result.isEmpty()) {
                if (StringUtils.hasText(healthItemName)) {
                    return String.format(
                            """
                            在 %s 至 %s 期间没有【%s】的健康记录。
                            你询问用户：
                            1. 是否需要添加【%s】的健康记录
                            2. 或者选择其他时间段来生成报告""",
                            startDate, endDate, healthItemName, healthItemName);
                } else {
                    return String.format(
                            """
                            在 %s 至 %s 期间没有任何健康记录。
                            你询问用户：
                            1. 是否需要添加健康记录
                            2. 或者选择其他时间段来生成报告""",
                            startDate, endDate);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("查询健康记录失败", e);
            return "❌ 查询健康记录失败：" + e.getMessage() + "，请稍后重试";
        }
    }
}
