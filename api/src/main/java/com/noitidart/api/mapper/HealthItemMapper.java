package com.noitidart.api.mapper;

import com.noitidart.api.pojo.dto.HealthItemQueryDTO;
import com.noitidart.api.pojo.entity.HealthItem;
import com.noitidart.api.pojo.vo.HealthItemCountVO;
import com.noitidart.api.pojo.vo.HealthItemToolTipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 健康项持久化接口
 */
@Mapper
public interface HealthItemMapper {

    void insert(HealthItem healthItem);

    void updateById(HealthItem healthItem);

    void deleteById(Integer id);

    List<HealthItem> selectAll(HealthItemQueryDTO healthItemQueryDTO);

    List<HealthItemToolTipVO> options(@Param(value = "isGlobal")Integer isGlobal,
                                      @Param(value = "userId")Integer userId);

    HealthItemCountVO itemCount(@Param(value = "isGlobal")Integer isGlobal,
                                      @Param(value = "userId")Integer userId);

    boolean hasHealthItem(@Param(value = "name") String name,
                          @Param(value = "userId") Integer userId);

    Integer getIdByName(@Param("name") String name, @Param("userId") int userId);
}
