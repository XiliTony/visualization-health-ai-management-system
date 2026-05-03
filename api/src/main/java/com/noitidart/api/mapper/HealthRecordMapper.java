package com.noitidart.api.mapper;

import com.noitidart.api.pojo.dto.HealthRecordQueryDTO;
import com.noitidart.api.pojo.dto.HealthReportDataDTO;
import com.noitidart.api.pojo.dto.HealthReportQueryDTO;
import com.noitidart.api.pojo.entity.HealthRecord;
import com.noitidart.api.pojo.vo.HealthRecordLineChartVO;
import com.noitidart.api.pojo.vo.HealthRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 健康记录持久化接口
 */
@Mapper
public interface HealthRecordMapper {

    void insert(HealthRecord healthRecord);

    void deleteById(Integer id);

    List<HealthRecordVO> selectAll(HealthRecordQueryDTO healthRecordQueryDTO);

    List<HealthRecordLineChartVO> lineChartListUser(HealthRecordQueryDTO healthRecordQueryDTO);

    List<HealthReportDataDTO> selectRecordForReport(HealthReportQueryDTO healthReportQueryDTO);
}
