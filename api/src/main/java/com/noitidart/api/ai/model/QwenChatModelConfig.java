package com.noitidart.api.ai.model;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Qwen 大模型配置类
 * 实现可观测性
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.community.dashscope.chat-model")
@Data
public class QwenChatModelConfig {

    // 用get/set方法从配置文件langchain4j.community.dashscope.chat-model中读取
    private String modelName;

    private String apiKey;

    /**
     * 注入由 {@link com.noitidart.api.ai.listener.ChatModelListenerConfig} 定义的监听器 Bean，
     * 用于在模型调用过程中记录请求、响应和错误日志，实现可观测性。
     */
    @Resource
    private ChatModelListener chatModelListener;

    /**
     * 创建并注册一个基于通义千问（Qwen）的 ChatModel Bean。
     * 该模型使用从配置文件中读取的 apiKey 和 modelName，
     * 并绑定监听器以支持调用过程的监控与日志记录。
     *
     * @return 配置完成的 QwenChatModel 实例，作为 Spring 容器中的 ChatModel Bean
     */
    @Bean
    public ChatModel myQwenChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .listeners(List.of(chatModelListener))
                .build();
    }
}