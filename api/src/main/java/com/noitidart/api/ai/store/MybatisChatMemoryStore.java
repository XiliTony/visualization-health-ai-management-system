package com.noitidart.api.ai.store;

import com.noitidart.api.mapper.PersistentChatMapper;
import com.noitidart.api.pojo.entity.PersistentChat;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 MyBatis 的聊天记忆存储实现类。
 * 该类将 {@link ChatMessage} 序列化为 JSON 字符串并持久化到数据库表中，
 * 通过 {@link PersistentChatMapper} 与数据库交互，支持按会话 ID（memoryId）进行消息的
 * 查询、更新和删除操作，满足 LangChain4j 的 {@link ChatMemoryStore} 接口规范。
 */
@Component
public class MybatisChatMemoryStore implements ChatMemoryStore {

    /**
     * 注入用于操作数据库的 MyBatis Mapper，用于持久化聊天消息记录。
     */
    @Autowired
    private PersistentChatMapper mapper;

    /**
     * 根据 memoryId ,获取消息集合
     * @param memoryId 会话id
     * @return 该会话对应的消息列表；若无记录或内容为空，则返回空的不可变列表（{@link List#of()}）
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Integer id = (Integer) memoryId;
        PersistentChat record = mapper.selectById(id);
        // 如果没数据返回空列表
        if (record == null || record.getJsonContent() == null) {
            return List.of();
        }
        return ChatMessageDeserializer.messagesFromJson(record.getJsonContent());
    }

    /**
     * 将指定会话的聊天消息序列化为 JSON 并持久化到数据库。
     * 若对应 memoryId 已存在记录，则执行更新；否则插入新记录。
     *
     * @param memoryId 会话唯一标识（预期为 {@link Integer} 类型）
     * @param messages 当前会话的完整消息列表（包括用户与 AI 的交互）
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        Integer id = (Integer) memoryId;
        String json = ChatMessageSerializer.messagesToJson(messages);
        PersistentChat record = new PersistentChat();
        record.setId(id);
        record.setJsonContent(json);
        // 有 id 就更新，没 id 就插入
        if (mapper.selectById(id) != null) {
            mapper.update(record);
        } else {
            mapper.insert(record);
        }

    }

    /**
     * 根据 memoryId 从数据库中删除对应的聊天记录。
     *
     * @param memoryId 会话唯一标识（预期为 {@link Integer} 类型）
     */
    @Override
    public void deleteMessages(Object memoryId) {
        mapper.deleteById((Integer) memoryId);

    }
}