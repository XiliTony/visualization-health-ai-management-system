package com.noitidart.api.controller;

import com.github.pagehelper.PageInfo;
import com.noitidart.api.common.Result;
import com.noitidart.api.pojo.dto.HealthItemQueryDTO;
import com.noitidart.api.pojo.entity.HealthItem;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.pojo.vo.HealthItemCountVO;
import com.noitidart.api.pojo.vo.HealthItemToolTipVO;
import com.noitidart.api.service.HealthItemService;
import com.noitidart.api.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 健康项控制层
 */
@RestController
@RequestMapping("/health-item")
public class HealthItemController {

    @Resource
    private HealthItemService healthItemService;

    /**
     * 新增
     *
     * @param healthItem 实体数据
     * @return Result 响应结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody HealthItem healthItem) {
        healthItemService.add(healthItem);
        return Result.success();
    }

    /**
     * 修改
     *
     * @param healthItem 实体数据
     * @return Result 响应结果
     */
    @PutMapping("/update")
    public Result update(@RequestBody HealthItem healthItem) {
        healthItemService.update(healthItem);
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
        healthItemService.deleteById(id);
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
        healthItemService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param healthItemQueryDTO 查询参数
     * @param pageNum 当前要查询的页码，默认1
     * @param pageSize 每页显示的条数，默认5
     * @return Result 响应结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(HealthItemQueryDTO healthItemQueryDTO,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<HealthItem> pageInfo = healthItemService.selectPage(healthItemQueryDTO, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有公有健康项并构造选择器
     *
     * return Result 响应结果
     */
    @GetMapping("/options")
    public Result options() {
        List<HealthItemToolTipVO> optionsVOS = healthItemService.options();
        return Result.success(optionsVOS);
    }

    /**
     * 查询所有公有健康项及用户自己的私有健康项并构造选择器
     *
     * return Result 响应结果
     */
    @GetMapping("/optionsUser")
    public Result optionsUser() {
        return Result.success(healthItemService.optionsUser());
    }

    /**
     * 健康项统计
     *
     * return Result 响应结果
     */
    @GetMapping("/itemCount")
    public Result itemCount() {
        HealthItemCountVO healthItemCountVO = healthItemService.itemCount();
        return Result.success(healthItemCountVO);
    }

    /**
     * 判断手动添加的健康项是否已存在同名的
     *
     * @param name
     * @return Result 响应结果
     */
    @GetMapping("/checkExists")
    public Result checkExists(@RequestParam String name) {
        User user = TokenUtils.getCurrentUser();
        boolean exists = healthItemService.hasHealthItem(name, user.getId());
        return Result.success(exists);
    }

}
