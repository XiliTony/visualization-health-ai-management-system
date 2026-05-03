package com.noitidart.api.service;

import cn.hutool.core.date.DateUtil;
import com.noitidart.api.mapper.DashboardMapper;
import com.noitidart.api.mapper.HealthItemMapper;
import com.noitidart.api.pojo.dto.HealthItemQueryDTO;
import com.noitidart.api.pojo.entity.HealthItem;
import com.noitidart.api.pojo.vo.ChartVO;
import com.noitidart.api.pojo.vo.StaticCountVO;
import com.noitidart.api.utils.AssertUtils;
import com.noitidart.api.utils.DateUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘业务逻辑类
 */
@Service
public class DashboardService {

    @Resource
    private DashboardMapper dashboardMapper;

    @Resource
    private HealthItemMapper healthItemMapper;

    /**
     * 静态数据统计
     *
     * @return List<StaticCountVO> 静态数据列表
     */

    public List<StaticCountVO> staticCount() {
        return dashboardMapper.staticCount();
    }

    /**
     * 收录健康项统计折线图
     *
     * @return List<OptionsVO> 用于折线图的选择器列表
     */

    public List<ChartVO> itemInfo(Integer days) {
        AssertUtils.notNull(days, "查询日期不能为空");
        // 处理查询时间
        String endTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String startTime = DateUtil.format(
                DateUtil.offsetDay(new Date(), -days), "yyyy-MM-dd HH:mm:ss"
        );
        HealthItemQueryDTO healthItemQueryDTO = new HealthItemQueryDTO();
        healthItemQueryDTO.setStartTime(startTime);
        healthItemQueryDTO.setEndTime(endTime);
        List<HealthItem> healthItems = healthItemMapper.selectAll(healthItemQueryDTO);
        List<String> dateTimeList = healthItems.stream()
                .map(HealthItem::getCreateTime)
                .collect(Collectors.toList());
        return DateUtils.countDatesRange(days, dateTimeList);

    }
}
