package com.noitidart.api.utils;

import com.noitidart.api.pojo.vo.ChartVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    // 假设你的字符串格式是 "yyyy-MM-dd HH:mm:ss"
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public static List<ChartVO> countDatesRange(Integer dayRange, List<String> dateTimes) {
        // 当前日期（不含时间）
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(dayRange); // 包含今天，共 dayRange+1 天

        List<ChartVO> chartVOS = new ArrayList<>();

        // 预处理：将所有字符串转为 LocalDate，过滤无效数据
        List<LocalDate> localDates = new ArrayList<>();
        for (String dt : dateTimes) {
            if (dt == null || dt.trim().isEmpty()) continue;
            try {
                LocalDateTime ldt = LocalDateTime.parse(dt, DB_FORMATTER);
                localDates.add(ldt.toLocalDate());
            } catch (Exception e) {
                // 日志记录无效时间格式（可选）
                // log.warn("Invalid datetime format: {}", dt);
            }
        }

        // 遍历每一天
        for (int offset = 0; offset <= dayRange; offset++) {
            LocalDate currentDate = startDate.plusDays(offset);
            String displayDate = currentDate.format(DISPLAY_FORMATTER);

            long count = localDates.stream()
                    .filter(date -> date.equals(currentDate))
                    .count();

            // 只保留 count > 0 的数据点（实现“断点”效果）
            if (count > 0) {
                chartVOS.add(new ChartVO(displayDate, (int) count));
            }
        }

        return chartVOS;
    }
}