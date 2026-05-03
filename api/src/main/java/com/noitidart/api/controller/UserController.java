package com.noitidart.api.controller;

import com.github.pagehelper.PageInfo;
import com.noitidart.api.common.Result;
import com.noitidart.api.pojo.dto.*;
import com.noitidart.api.pojo.vo.TokenResponseVO;
import com.noitidart.api.pojo.vo.UserVO;
import com.noitidart.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户控制层
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录入参
     * @return Result 响应结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        TokenResponseVO result = userService.login(userLoginDTO);
        return Result.success(result);
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册入参
     * @return Result 响应结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param userQueryDTO 账号查询入参
     * @param pageNum 当前要查询的页码，默认1
     * @param pageSize 每页显示的条数，默认5
     * @return Result 响应结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(UserQueryDTO userQueryDTO,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<UserVO> pageInfo = userService.selectPage(userQueryDTO, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 账号管理添加账号
     *
     * @param userAddDTO 添加入参
     * @return Result 响应结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserAddDTO userAddDTO) {
        userService.add(userAddDTO);
        return Result.success();
    }

    /**
     * 账号管理更新账号
     * @param userUpdateDTO 更新入参
     * @return Result 响应结果
     */
    @PutMapping("/update")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.update(userUpdateDTO);
        return Result.success();
    }

    /**
     * 账号管理单个删除账号
     *
     * @param id 单个删除入参
     * @return Result 响应结果
     */
    @DeleteMapping("/deleteById/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return Result.success();
    }

    /**
     * 账号管理批量删除账号
     *
     * @param ids 删除账号列表入参
     * @return Result 响应结果
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param userUpdatePasswordDTO 修改密码入参
     * @return Result 响应结果
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        userService.updatePassword(userUpdatePasswordDTO);
        return Result.success();
    }

    /**
     * 个人中心更新账号
     * @param userUpdateDTO 更新入参
     * @return Result 响应结果
     */
    @PutMapping("/personCenterUpdate")
    public Result personCenterUpdate(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.personCenterUpdate(userUpdateDTO);
        return Result.success();
    }
}
