package com.noitidart.api.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.noitidart.api.common.Result;
import com.noitidart.api.pojo.dto.HealthRecordQueryDTO;
import com.noitidart.api.pojo.entity.HealthRecord;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.pojo.vo.HealthRecordLineChartVO;
import com.noitidart.api.pojo.vo.HealthRecordVO;
import com.noitidart.api.service.HealthRecordService;
import com.noitidart.api.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康记录控制层
 */
@RestController
@RequestMapping("/health-record")
public class HealthRecordController {

    @Resource
    private HealthRecordService healthRecordService;

    /**
     * 批量新增
     *
     * @param healthRecordList 实体数据
     * @return Result 响应结果
     */
    @PostMapping("/addBatch")
    public Result add(@RequestBody List<HealthRecord> healthRecordList) {
        healthRecordService.addBatch(healthRecordList);
        return Result.success();
    }

    /**
     * 删除
     *
     * @param id 主键 ID
     * @return Result 响应结果
     */
    @DeleteMapping("/deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        healthRecordService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     *
     * @param ids 主键 ID 列表
     * @return Result 响应结果
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        healthRecordService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param healthRecordQueryDTO 查询参数
     * @param pageNum 当前要查询的页码，默认1
     * @param pageSize 每页显示的条数，默认5
     * @return Result 响应结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(HealthRecordQueryDTO healthRecordQueryDTO,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<HealthRecordVO> pageInfo = healthRecordService .selectPage(healthRecordQueryDTO, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 用户查询自己的健康记录列表
     *
     * @param healthRecordQueryDTO 查询参数
     * @param pageNum 当前要查询的页码，默认1
     * @param pageSize 每页显示的条数，默认5
     * @return Result 响应结果
     */
    @GetMapping("/selectUserPage")
    public Result selectUserPage(HealthRecordQueryDTO healthRecordQueryDTO,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<HealthRecordVO> pageInfo = healthRecordService.selectUserPage(healthRecordQueryDTO, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 健康数据可视化(折线图)
     *
     * @param healthRecordQueryDTO 查询参数
     * @return Result 响应结果
     */
    @PostMapping("/lineChartListUser")
    public Result lineChartListUser(@RequestBody HealthRecordQueryDTO healthRecordQueryDTO) {
        List<HealthRecordLineChartVO> healthRecordLineChartVOS = healthRecordService.lineChartListUser(healthRecordQueryDTO);
        return Result.success(healthRecordLineChartVOS);
    }

    /**
     * 将当前登录用户的健康记录导出为 Excel 文件。
     *
     * 该接口受 JWT 权限保护，仅允许已登录用户访问，并自动根据 Token 中的用户 ID
     * 进行数据隔离，确保用户只能导出自己的健康记录。
     *
     * 支持按健康项 ID 筛选导出内容：
     *   - 若 healthItemId 为 null，则导出该用户所有健康项的记录；
     *   - 若 healthItemId 有值，则只导出指定健康项的记录。
     *
     * 导出字段包括：记录项名称、记录值（含单位）、指标情况（正常/异常）、创建时间。
     * 其中“指标情况”根据健康项预设的正常值范围（normal_value）动态判断。
     *
     * @param healthItemId 健康项 ID（可选）。若为 null，表示导出全部健康项记录。
     * @param response     HttpServletResponse 对象，用于向客户端输出 Excel 文件流。
     * @throws Exception 当 Excel 生成、写入或网络输出过程中发生异常时抛出。
     */
    @GetMapping("/exportUser")
    public void exportUser(
            @RequestParam(required = false) Integer healthItemId,
            HttpServletResponse response) throws Exception {

        // 1. 从 Token 中解析当前登录用户，确保数据权限隔离
        User user = TokenUtils.getCurrentUser();

        // 2. 构建查询条件：绑定当前用户 ID，并可选绑定健康项 ID
        HealthRecordQueryDTO dto = new HealthRecordQueryDTO();
        dto.setUserId(user.getId());
        dto.setHealthItemId(healthItemId); // null 表示不限制健康项

        // 3. 调用服务层查询所有符合条件的健康记录（不分页，全量导出）
        List<HealthRecordVO> records = healthRecordService.selectAll(dto);

        // 4. 使用 Hutool 的 ExcelWriter 构建 Excel 内容
        ExcelWriter writer = ExcelUtil.getWriter(true); // true 表示创建 .xlsx 格式

        // 4.1 设置中文表头别名（字段名 → 显示名）
        writer.addHeaderAlias("healthItemName", "记录项");
        writer.addHeaderAlias("valueWithUnit", "记录值");
        writer.addHeaderAlias("status", "指标情况");
        writer.addHeaderAlias("createTime", "创建时间");

        // 4.2 构造导出数据列表，每行对应一条健康记录
        List<Map<String, Object>> exportList = new ArrayList<>();
        for (HealthRecordVO vo : records) {
            Map<String, Object> row = new LinkedHashMap<>(); // 保持字段顺序

            // 记录项名称（如：血压、血糖）
            row.put("healthItemName", vo.getHealthItemName());

            // 记录值 + 单位（如：120 mmHg）
            String unit = vo.getHealthItemUnit() != null ? vo.getHealthItemUnit() : "";
            row.put("valueWithUnit", vo.getValue() + unit);

            // 判断指标是否在正常范围内
            // normalValue 格式示例："90,140" 表示 (90, 140) 为正常区间
            String[] range = vo.getNormalValue().split(",");
            if (range.length != 2) {
                // 防御性处理：若格式异常，默认视为“异常”
                row.put("status", "异常");
            } else {
                try {
                    double lower = Double.parseDouble(range[0]);
                    double upper = Double.parseDouble(range[1]);
                    boolean normal = vo.getValue() > lower && vo.getValue() < upper;
                    row.put("status", normal ? "正常" : "异常");
                } catch (NumberFormatException e) {
                    // 数值解析失败，也视为异常
                    row.put("status", "异常");
                }
            }

            // 创建时间（格式由 LocalDateTime 默认 toString 决定，或由 VO 中格式化）
            row.put("createTime", vo.getCreateTime());

            exportList.add(row);
        }

        // 5. 将数据写入 Excel，true 表示写出表头
        writer.write(exportList, true);

        // 6. 仅输出设置了别名的字段，忽略其他属性
        writer.setOnlyAlias(true);

        // 7. 设置 HTTP 响应头，触发浏览器下载行为
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("我的健康记录", "UTF-8");
        // 注意：部分浏览器需指定 filename* 以支持 UTF-8，但主流现代浏览器支持以下写法
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 8. 获取输出流，将 Excel 内容写入响应
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);
        writer.close();
        os.close(); // 显式关闭流，避免资源泄漏
    }
}
