package com.noitidart.api.controller;

import com.noitidart.api.common.Result;
import com.noitidart.api.pojo.vo.ChartVO;
import com.noitidart.api.pojo.vo.OptionsVO;
import com.noitidart.api.pojo.vo.StaticCountVO;
import com.noitidart.api.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 仪表盘控制层
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 静态数据统计
     *
     * @return Result 响应结果
     */
    @GetMapping("/staticCount")
    public Result staticCount() {
        List<StaticCountVO> list = dashboardService.staticCount();
        return Result.success(list);
    }

    /**
     * 收录健康项统计折线图
     *
     * @return Result 响应结果
     */
    @GetMapping("/itemInfo/{days}")
    public Result itemInfo(@PathVariable Integer days) {
        List<ChartVO> list = dashboardService.itemInfo(days);
        return Result.success(list);
    }

}
