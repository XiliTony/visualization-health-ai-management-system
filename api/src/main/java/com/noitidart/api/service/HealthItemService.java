package com.noitidart.api.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.noitidart.api.mapper.HealthItemMapper;
import com.noitidart.api.pojo.dto.HealthItemQueryDTO;
import com.noitidart.api.pojo.em.IsGlobalEnum;
import com.noitidart.api.pojo.em.RoleEnum;
import com.noitidart.api.pojo.entity.HealthItem;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.pojo.vo.HealthItemCountVO;
import com.noitidart.api.pojo.vo.HealthItemToolTipVO;
import com.noitidart.api.utils.AssertUtils;
import com.noitidart.api.utils.TokenUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 健康项业务逻辑类
 */
@Service
public class HealthItemService {

    @Resource
    private HealthItemMapper healthItemMapper;

    public void add(HealthItem healthItem) {
        // 参数校验
        validParam(healthItem);
        // 设置当前新增的时间
        healthItem.setCreateTime(DateUtil.now());
        // 正常值范围处理
        validNormalValue(healthItem.getNormalValue());
        // 健康项权限处理
        if(healthItem.getUserId() == null) {
            itemValidAuth(healthItem);
        }
        // 数据新增
        healthItemMapper.insert(healthItem);

    }

    public void update(HealthItem healthItem) {
        // 参数校验
        validParam(healthItem);
        // 正常值范围处理
        validNormalValue(healthItem.getNormalValue());
        // 更新健康项权限处理
        validUpdateAuth(healthItem);
        // 数据修改
        healthItemMapper.updateById(healthItem);
    }

    public void deleteById(Integer id) {
        healthItemMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public PageInfo<HealthItem> selectPage(HealthItemQueryDTO healthItemQueryDTO, Integer pageNum, Integer pageSize) {
        // 如果是普通用户，并且查询的是私有健康项，则返回自己的私有健康项
        User user = TokenUtils.getCurrentUser();
        boolean isAdmin = Objects.equals(user.getRole(), RoleEnum.ADMIN.getRole());
        if (!isAdmin && Objects.equals(healthItemQueryDTO.getIsGlobal(), IsGlobalEnum.PRIVATE.getStatus())) {
            healthItemQueryDTO.setUserId(user.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<HealthItem> list =  healthItemMapper.selectAll(healthItemQueryDTO);
        return PageInfo.of(list);
    }

    /**
     * 查询所有公有健康项并构造选择器
     *
     * return List<HealthItemToolTipVO> 返回的选择项列表
     */
    public List<HealthItemToolTipVO> options() {
        // 查询公有健康项
        return healthItemMapper.options(
                IsGlobalEnum.PUBLIC.getStatus() ? 1 : 0,
                null
        );
    }

    /**
     * 查询所有公有健康项及用户自己的私有健康项并构造选择器
     *
     * @return HealthItemToolTipVO 查询的公有健康项及用户自己的私有健康项的选择器
     */
    public List<HealthItemToolTipVO> optionsUser() {
        List<HealthItemToolTipVO> optionsVOS = healthItemMapper.options(
                IsGlobalEnum.PUBLIC.getStatus() ? 1 : 0,
                null
        );
        textPlus(optionsVOS, "（公有健康项）");
        // 查询用户自己的私有健康项
        User user = TokenUtils.getCurrentUser();
        List<HealthItemToolTipVO> optionsUserVOS = healthItemMapper.options(
                IsGlobalEnum.PRIVATE.getStatus() ? 1 : 0,
                user.getId()
        );
        textPlus(optionsUserVOS, "（私有健康项）");
        optionsVOS.addAll(optionsUserVOS);
        return optionsVOS;
    }

    /**
     * 健康项统计
     *
     * return HealthItemToolTipVO 返回的健康项统计列表
     */
    public HealthItemCountVO itemCount() {
        User user = TokenUtils.getCurrentUser();
        return healthItemMapper.itemCount(
                IsGlobalEnum.PUBLIC.getStatus() ? 1 : 0,
                user.getId()
        );
    }

    /**
     * 健康项正常值范围处理
     *
     * @param normalValue 正常值范围
     */
    private void validNormalValue(String normalValue) {
        // 做兼容，如果出现中文逗号，兼容成英文逗号："，" => ","
        if (normalValue.contains("，")) {
            normalValue = normalValue.replace("，", ",");
        }
        // 要求格式必须是xxx（最小值）,xxx（最大值）
        AssertUtils.isTrue(normalValue.contains(","), "正常值范围非法，请重新输入");
    }

    /**
     * 参数校验
     *
     * @param healthItem 实体数据
     */
    private void validParam(HealthItem healthItem) {
        AssertUtils.notNull(healthItem, "实体不能为空");
        AssertUtils.hasText(healthItem.getName(), "项名不能为空");
        AssertUtils.hasText(healthItem.getDetail(), "介绍不能为空");
        AssertUtils.hasText(healthItem.getUnit(), "单位不能为空");
        AssertUtils.hasText(healthItem.getSymbol(), "符号不能为空");
        AssertUtils.hasText(healthItem.getNormalValue(), "正常值范围不能为空");
        AssertUtils.isTrue(healthItem.getName().length() < 100, "项名请控制在100字以内");
        AssertUtils.isTrue(healthItem.getDetail().length() < 200, "介绍请控制在200字以内");
    }

    /**
     * 处理健康项权限
     *
     * @param healthItem 实体数据
     */
    private void itemValidAuth(HealthItem healthItem) {
        // 看是谁人创建的健康项 => 公有健康项？ 私有健康项？
        // 获取当前登录的用户信息
        User user = TokenUtils.getCurrentUser();
        healthItem.setIsGlobal(Objects.equals(user.getRole(), RoleEnum.ADMIN.getRole())
                ? IsGlobalEnum.PUBLIC.getStatus()
                : IsGlobalEnum.PRIVATE.getStatus());
        // 设置上用户 ID
        healthItem.setUserId(user.getId());
    }

    /**
     * 校验修改时的权限，如果是公有健康项需要校验角色
     *
     * @param healthItem 实体数据
     */
    private void validUpdateAuth(HealthItem healthItem) {
        // 如果是公有健康项，只有管理员能够修改
        if (Objects.equals(healthItem.getIsGlobal(), IsGlobalEnum.PUBLIC.getStatus())) {
            // 获取当前登录的用户信息
            User user = TokenUtils.getCurrentUser();
            AssertUtils.isTrue(
                    Objects.equals(user.getRole(), RoleEnum.ADMIN.getRole()),
                    "无操作权限");
        }

    }

    private void textPlus(List<HealthItemToolTipVO> optionsUserVOS, String text) {
        for (HealthItemToolTipVO optionsUserVO : optionsUserVOS) {
            optionsUserVO.setLabel(optionsUserVO.getLabel() + text);
        }
    }


    /**
     * AI 模块查询用户要添加的健康项是否已存在
     *
     * @param name   健康项名
     * @param userId 当前用户 ID
     * @return true=存在（不可添加），false=不存在（可添加）
     */
    public boolean hasHealthItem(String name, Integer userId) {
        return healthItemMapper.hasHealthItem(name, userId);
    }

    /**
     * AI 模块根据健康项名和用户ID查询对应的健康项名称
     *
     * @param name 健康项名
     * @param userId 用户 ID
     * @return 健康项 ID
     */
    public Integer getIdByName(String name, int userId) {
        return healthItemMapper.getIdByName(name, userId);
    }
}
