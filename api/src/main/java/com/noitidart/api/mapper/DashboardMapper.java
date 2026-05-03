package com.noitidart.api.mapper;

import com.noitidart.api.pojo.vo.StaticCountVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 仪表盘持久化接口
 */
@Mapper
public interface DashboardMapper {

    List<StaticCountVO> staticCount();

}
