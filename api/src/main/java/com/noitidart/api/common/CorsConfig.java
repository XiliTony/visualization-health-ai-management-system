package com.noitidart.api.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域相关配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");   // 1. 允许任何来源
        corsConfiguration.addAllowedHeader("*");   // 2. 允许任何请求头
        corsConfiguration.addAllowedMethod("*");   // 3. 允许任何 HTTP 方法（GET/POST/PUT/DELETE...）
        source.registerCorsConfiguration("/**", corsConfiguration); // 4. 对所有接口生效

        return new CorsFilter(source);
    }
}