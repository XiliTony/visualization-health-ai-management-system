package com.noitidart.api.ai.provider;

import com.noitidart.api.ai.store.MybatisChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义聊天记忆提供者（ChatMemoryProvider）实现类。
 * 用于根据会话 ID（memoryId）动态创建并管理具有有限消息窗口的聊天记忆实例，
 * 并将消息持久化到基于 MyBatis 的 {@link MybatisChatMemoryStore} 中。
 */
@Component
public class MyChatMemoryProvider implements ChatMemoryProvider {

    /**
     * 注入自定义的聊天记忆存储实现，用于持久化和加载聊天消息。
     */
    @Autowired
    private MybatisChatMemoryStore chatMemoryStore;

    /**
     * 根据指定的 memoryId 创建一个 {@link ChatMemory} 实例。
     * 使用滑动窗口策略，最多保留最近 10 条消息，并通过 {@link MybatisChatMemoryStore}
     * 实现消息的持久化存储与恢复，支持跨请求的会话记忆。
     *
     * @param memoryId 会话唯一标识（如用户 ID、会话 ID），用于区分不同会话的记忆上下文
     * @return 配置好的 ChatMemory 实例
     */
    @Override
    public ChatMemory get(Object memoryId) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}
