package com.noitidart.api.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SpringBoot 拦截器
 */
@Configuration  // 表示配置类
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/files/download/**", "/ai/**");
    }

    @Bean
    public JWTInterceptor jwtInterceptor() {
        return new JWTInterceptor();
    }
}
