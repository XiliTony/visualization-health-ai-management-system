package com.noitidart.api.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.noitidart.api.mapper.UserMapper;
import com.noitidart.api.pojo.dto.*;
import com.noitidart.api.pojo.em.RoleEnum;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.pojo.vo.TokenResponseVO;
import com.noitidart.api.pojo.vo.UserVO;
import com.noitidart.api.utils.AssertUtils;
import com.noitidart.api.utils.TokenUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户业务逻辑类
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录入参
     * @return tokenResponseVO 登录返回给前端的数据VO类
     */
    public TokenResponseVO login(UserLoginDTO userLoginDTO) {
        AssertUtils.hasText(userLoginDTO.getAccount(), "账号不能为空");
        AssertUtils.hasText(userLoginDTO.getPassword(), "密码不能为空");
        User user = userMapper.getUserByAccount(userLoginDTO.getAccount());
        AssertUtils.isTrue(user != null, "账号不存在");
        AssertUtils.equals(userLoginDTO.getPassword(), user.getPassword(), "账号或密码错误");
        String token = TokenUtils.createToken(user.getId() + "-" + RoleEnum.getNameByRole(user.getRole()), user.getPassword());

        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        tokenResponseVO.setToken(token);
        tokenResponseVO.setId(user.getId());
        tokenResponseVO.setAccount(user.getAccount());
        tokenResponseVO.setUsername(user.getUsername());
        tokenResponseVO.setAvatar(user.getAvatar());
        tokenResponseVO.setRole(RoleEnum.getNameByRole(user.getRole()));

        return tokenResponseVO;
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册入参
     */
    public void register(UserRegisterDTO userRegisterDTO) {
        AssertUtils.equals(userRegisterDTO.getPassword(), userRegisterDTO.getConfirmPassword(), "两次密码不一致");
        User user = userMapper.getUserByAccount(userRegisterDTO.getAccount());
        AssertUtils.isTrue(user == null, "账号已存在，请更换");
        User newUser = new User();
        newUser.setAccount(userRegisterDTO.getAccount());
        newUser.setPassword(userRegisterDTO.getPassword());
        newUser.setUsername(userRegisterDTO.getAccount());
        newUser.setRole(RoleEnum.USER.getRole());
        newUser.setCreateTime(DateUtil.now());

        userMapper.insert(newUser);

    }

    /**
     * 根据用户 ID 查找用户
     *
     * @param id 用户 id
     * @return User 用户实体
     */
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    /**
     * 分页查询
     *
     * @param userQueryDTO 账号查询入参
     * @param pageNum 当前要查询的页码，默认1
     * @param pageSize 每页显示的条数，默认5
     * @return PageInfo<UserVO> 分页结果对象 PageInfo<UserVO>，包含当前页数据、总条数、总页数等信息
     */
    public PageInfo<UserVO> selectPage(UserQueryDTO userQueryDTO, Integer pageNum, Integer pageSize) {
        Integer role = RoleEnum.getRoleByName(userQueryDTO.getRoleName());
        userQueryDTO.setRole(role);
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(userQueryDTO);
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : list) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setAccount(user.getAccount());
            userVO.setPassword(user.getPassword());
            userVO.setUsername(user.getUsername());
            userVO.setAvatar(user.getAvatar());
            userVO.setRoleName(RoleEnum.getNameByRole(user.getRole()));
            userVOList.add(userVO);
        }
        return PageInfo.of(userVOList);
    }

    /**
     * 新增账号
     *
     * @param userAddDTO 新增账号入参
     */
    public void add(UserAddDTO userAddDTO) {
        User user = userMapper.getUserByAccount(userAddDTO.getAccount());
        AssertUtils.isTrue(user == null, "账号已存在，请更换");
        Integer role = RoleEnum.getRoleByName(userAddDTO.getRoleName());
        AssertUtils.notNull(role, "角色不能为空");
        AssertUtils.notNull(userAddDTO.getAccount(), "账号不能为空");
        AssertUtils.notNull(userAddDTO.getPassword(), "密码不能为空");
        User newUser = new User();
        newUser.setAccount(userAddDTO.getAccount());
        newUser.setPassword(userAddDTO.getPassword());
        if (userAddDTO.getUsername() == null) {
            newUser.setUsername(userAddDTO.getAccount());
        } else {
            newUser.setUsername(userAddDTO.getUsername());
        }
        newUser.setAvatar(userAddDTO.getAvatar());
        newUser.setRole(role);
        newUser.setCreateTime(DateUtil.now());

        userMapper.insert(newUser);
    }

    /**
     * 更新账号
     *
     * @param userUpdateDTO 更新账号入参
     */
    public void update(UserUpdateDTO userUpdateDTO) {
        AssertUtils.notNull(userUpdateDTO.getAccount(), "账号不能为空");
        AssertUtils.notNull(userUpdateDTO.getPassword(), "密码不能为空");
        if (userUpdateDTO.getUsername() == null) {
            userUpdateDTO.setUsername(userUpdateDTO.getAccount());
        }
        userMapper.updateById(userUpdateDTO);

    }

    /**
     * 删除账号
     *
     * @param id 要删除的账号 id
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除账号
     *
     * @param ids 要批量删除的账号列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改密码
     *
     * @param userUpdatePasswordDTO 修改密码入参
     */
    public void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO) {
        AssertUtils.notNull(userUpdatePasswordDTO, "参数不能为空");
        AssertUtils.equals(userUpdatePasswordDTO.getNewPassword(), userUpdatePasswordDTO.getConfirmNewPassword(), "两次密码不一致");
        User user = userMapper.getUserById(userUpdatePasswordDTO.getId());
        // 页面传来的原密码跟数据库密码对比，不匹配报错
        AssertUtils.isFalse(!user.getPassword().equals(userUpdatePasswordDTO.getPassword()), "对不起，原密码错误");
        userMapper.updatePassword(userUpdatePasswordDTO);
    }

    /**
     * 个人中心更新账号
     * @param userUpdateDTO 更新入参
     *
     */
    public void personCenterUpdate(UserUpdateDTO userUpdateDTO) {
        AssertUtils.notNull(userUpdateDTO.getUsername(), "昵称不能为空");
        User user = userMapper.getUserByAccount(userUpdateDTO.getAccount());
        userUpdateDTO.setPassword(user.getPassword());
        userMapper.updateById(userUpdateDTO);
    }
}
