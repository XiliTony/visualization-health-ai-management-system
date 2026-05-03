package com.noitidart.api.ai.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类：用于注册一个全局的 ChatModelListener Bean。
 * 该监听器用于监听 LangChain4j 中 ChatModel 的请求、响应和错误事件，
 * 便于记录日志、监控调用或进行调试。
 */
@Configuration
@Slf4j
public class ChatModelListenerConfig {

    /**
     * 定义一个 ChatModelListener 的 Spring Bean。
     * 该监听器会在每次调用 ChatModel（如大语言模型）时触发相应的方法，
     * 用于记录请求内容、响应结果或捕获异常信息。
     *
     * @return 配置好的 ChatModelListener 实例
     */
    @Bean
    ChatModelListener chatModelListener() {
        return new ChatModelListener() {
            /**
             * 当 ChatModel 发起请求前调用。
             * 可用于记录发送给模型的原始请求内容（如提示词、参数等）。
             *
             * @param requestContext 包含请求相关信息的上下文对象
             */
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                log.info("onRequest(): {}", requestContext.chatRequest());
            }
            /**
             * 当 ChatModel 成功返回响应后调用。
             * 可用于记录模型返回的完整响应内容（如生成的文本、元数据等）。
             *
             * @param responseContext 包含响应相关信息的上下文对象
             */
            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                log.info("onResponse(): {}", responseContext.chatResponse());
            }
            /**
             * 当 ChatModel 调用过程中发生异常时调用。
             * 可用于记录错误信息，便于排查问题（如网络错误、模型超时、API 限流等）。
             *
             * @param errorContext 包含错误信息的上下文对象
             */
            @Override
            public void onError(ChatModelErrorContext errorContext) {
                log.info("onError(): {}", errorContext.error().getMessage());
            }
        };
    }
}