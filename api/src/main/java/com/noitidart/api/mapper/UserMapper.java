package com.noitidart.api.mapper;

import com.noitidart.api.pojo.dto.UserQueryDTO;
import com.noitidart.api.pojo.dto.UserUpdateDTO;
import com.noitidart.api.pojo.dto.UserUpdatePasswordDTO;
import com.noitidart.api.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户持久化接口
 */
@Mapper
public interface UserMapper {

    User getUserByAccount(String account);

    void insert(User user);

    User getUserById(Integer id);

    List<User> selectAll(UserQueryDTO userQueryDTO);

    void updateById(UserUpdateDTO userUpdateDTO);

    void deleteById(Integer id);

    void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO);
}
