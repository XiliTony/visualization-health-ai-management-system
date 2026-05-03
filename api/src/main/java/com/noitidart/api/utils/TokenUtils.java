package com.noitidart.api.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@Component // 必须要注入容器，否则无法通过@Resource拿到adminService这样一个Bean
public class TokenUtils {

    @Resource
    UserService userService;

    static UserService staticUserService;

    // springboot 工程启动后会加载这段代码
    @PostConstruct
    public void init() {
        staticUserService = userService;
    }

    /**
     * 生成 token
     *
     * @param data 将userId-role保存到token里面，作为载荷
     * @param sign 以password作为token的密钥，HMAC256算法加密
     * @return token
     */
    public static String createToken(String data, String sign) {
        return JWT.create()
                .withAudience(data) // 将userId-role保存到token里面，作为载荷
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 1天后token过期
                .sign(Algorithm.HMAC256(sign)); // 以password作为token的密钥，HMAC256算法加密
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return User 用户信息
     */
    public static User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");  // 获取 token
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        // 拿到 token 的载荷数据
        String audience = JWT.decode(token).getAudience().get(0);// 通过decode解析token，然后拿到里面携带的id和role数据，audience即载荷
        String[] split = audience.split("-");
        String userId = split[0];
        String role = split[1];
        // 根据 token 解析出来的 userId 去对应的表查询用户信息
        return staticUserService.getUserById(Integer.valueOf(userId));
    }

    /**
     * AI功能新增：不依赖 Request，直接解析 token 获取用户
     * 异步线程、Tool 方法、Controller 都能用
     *
     * @param token JWT token 字符串
     * @return 解析成功的 User 对象，失败返回 null
     */
    public static User getUserByToken(String token) {
        // 1. 参数校验
        if (StrUtil.isBlank(token)) {
            return null;
        }

        try {
            // 2. 解析 token 获取 audience（格式：userId-role）
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");

            // 3. 提取 userId 并查询用户
            Integer userId = Integer.valueOf(split[0]);
            return staticUserService.getUserById(userId);

        } catch (Exception e) {
            // 4. 异常处理：token 过期/格式错误/用户不存在等
            System.err.println("解析 token 失败: " + e.getMessage());
            return null;
        }
    }
}
