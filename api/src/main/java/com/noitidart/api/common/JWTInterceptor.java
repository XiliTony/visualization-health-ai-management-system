package com.noitidart.api.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.noitidart.api.exception.CustomException;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    UserService userService;

    /**
     * 请求预处理
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler 当前请求的处理器
     * @return true 放行
     * @throws Exception 抛出自定义异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            // 如果没拿到，从参数里再拿一次
            token = request.getParameter("token");
        }
        // 开始执行认证
        if (StrUtil.isEmpty(token)) {
            throw new CustomException("401", "您无权限操作");
        }
        User user = null;
        try {
            // 拿到 token 的载荷数据
            String audience = JWT.decode(token).getAudience().get(0);// 通过decode解析token，然后拿到里面携带的id和role数据，audience即载荷
            String[] split = audience.split("-");
            String userId = split[0];
            String role = split[1];
            // 根据 token 解析出来的 userId 去对应的表查询用户信息
            user = userService.getUserById(Integer.valueOf(userId));
        } catch (Exception e) {
            throw new CustomException("401", "您无权限操作");
        }
        if (user == null) {
            throw new CustomException("401", "您无权限操作");
        }
        try {
            // 用户密码加签认证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomException("401", "您无权限操作");
        }
        return true;
    }
}
