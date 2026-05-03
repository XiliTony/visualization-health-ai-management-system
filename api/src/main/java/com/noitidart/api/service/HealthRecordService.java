package com.noitidart.api.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.noitidart.api.mapper.HealthRecordMapper;
import com.noitidart.api.pojo.dto.HealthRecordQueryDTO;
import com.noitidart.api.pojo.dto.HealthReportDataDTO;
import com.noitidart.api.pojo.dto.HealthReportQueryDTO;
import com.noitidart.api.pojo.em.RoleEnum;
import com.noitidart.api.pojo.entity.HealthRecord;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.pojo.vo.HealthRecordLineChartVO;
import com.noitidart.api.pojo.vo.HealthRecordVO;
import com.noitidart.api.utils.AssertUtils;
import com.noitidart.api.utils.TokenUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 健康记录业务逻辑类
 */
@Service
public class HealthRecordService {

    @Resource
    private HealthRecordMapper healthRecordMapper;

    public void addBatch(List<HealthRecord> healthRecordList) {
        AssertUtils.notNull(healthRecordList, "请填写数据");
        // 一批数据统一用同一个时间
        String now = DateUtil.now();
        // 获取当前操作者账号信息
        User user = TokenUtils.getCurrentUser();
        // 获取操作者账号 ID
        Integer userId = user.getId();
        for (HealthRecord healthRecord : healthRecordList) {
            // 这一批里面，如果存在健康项没有选定，或者说没有记录具体的值，这一批不作数，返回前端重填
            AssertUtils.notNull(healthRecord.getHealthItemId(), "健康项未选定，请重试");
            AssertUtils.notNull(healthRecord.getValue(), "记录值不能为空");
            //
            // 设置当前操作者用户 ID
            healthRecord.setUserId(userId);
            // 设置创建时间
            healthRecord.setCreateTime(now);
            healthRecordMapper.insert(healthRecord);
        }
    }

    // addBatch方法重写，AI专用
    public void addBatch(List<HealthRecord> healthRecordList, Integer userId) {
        AssertUtils.notNull(healthRecordList, "请填写数据");

        // 校验 userId
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户 ID 无效");
        }

        String now = DateUtil.now();

        for (HealthRecord healthRecord : healthRecordList) {
            AssertUtils.notNull(healthRecord.getHealthItemId(), "健康项未选定，请重试");
            AssertUtils.notNull(healthRecord.getValue(), "记录值不能为空");

            healthRecord.setUserId(userId);
            healthRecord.setCreateTime(now);

            healthRecordMapper.insert(healthRecord);
        }
    }

    public void deleteById(Integer id) {
        healthRecordMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public PageInfo<HealthRecordVO> selectPage(HealthRecordQueryDTO healthRecordQueryDTO, Integer pageNum, Integer pageSize) {
        // 获取当前操作者账号信息
        User user = TokenUtils.getCurrentUser();
        // 判断当前操作者角色，如果不是管理员则无权操作
        AssertUtils.isTrue(Objects.equals(user.getRole(), RoleEnum.ADMIN.getRole()), "无操作权限");
        PageHelper.startPage(pageNum, pageSize);
        List<HealthRecordVO> list =  healthRecordMapper.selectAll(healthRecordQueryDTO);
        return PageInfo.of(list);
    }

    public PageInfo<HealthRecordVO> selectUserPage(HealthRecordQueryDTO healthRecordQueryDTO, Integer pageNum, Integer pageSize) {
        // 获取当前操作者账号信息
        User user = TokenUtils.getCurrentUser();
        // 设置上用户 ID 以做数据隔离
        healthRecordQueryDTO.setUserId(user.getId());
        PageHelper.startPage(pageNum, pageSize);
        List<HealthRecordVO> list =  healthRecordMapper.selectAll(healthRecordQueryDTO);
        return PageInfo.of(list);
    }

    /**
     * 健康数据可视化折线图
     *
     * @param healthRecordQueryDTO 查询参数
     * @return Result 响应结果
     */
    public List<HealthRecordLineChartVO> lineChartListUser(HealthRecordQueryDTO healthRecordQueryDTO) {
        // 参数校验
        AssertUtils.notNull(healthRecordQueryDTO, "查询参数对象不能为空");
        AssertUtils.notNull(healthRecordQueryDTO.getDays(), "查询天数不能为空");
        AssertUtils.notNull(healthRecordQueryDTO.getHealthItemId(), "健康项ID不能为空");
        User user = TokenUtils.getCurrentUser();
        // 设置上用户 ID
        healthRecordQueryDTO.setUserId(user.getId());
        // 处理查询时间
        String endTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String startTime = DateUtil.format(
                DateUtil.offsetDay(new Date(), -healthRecordQueryDTO.getDays()), "yyyy-MM-dd HH:mm:ss"
        );
        healthRecordQueryDTO.setStartTime(startTime);
        healthRecordQueryDTO.setEndTime(endTime);
        return healthRecordMapper.lineChartListUser(healthRecordQueryDTO);
    }

    public List<HealthRecordVO> selectAll(HealthRecordQueryDTO dto) {
        User user = TokenUtils.getCurrentUser();
        dto.setUserId(user.getId()); // 强制隔离
        return healthRecordMapper.selectAll(dto);
    }

    /**
     * 根据特定的健康项和用户 ID 查找出AI报告需要的数据
     *
     * @param dto 生成健康报告需要查找的数据
     * @return List<HealthReportDataDTO> AI报告需要的数据列表
     */
    public List<HealthReportDataDTO> selectRecordForReport(HealthReportQueryDTO dto) {
        return healthRecordMapper.selectRecordForReport(dto);
    }

}
