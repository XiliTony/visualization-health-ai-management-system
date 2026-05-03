package com.noitidart.api.mapper;

import com.noitidart.api.pojo.entity.PersistentChat;
import org.apache.ibatis.annotations.*;

/**
 * 聊天记忆持久化接口层
 */
@Mapper
public interface PersistentChatMapper {

    /**
     * 根据会话 ID 查询持久化聊天记录。
     *
     * @param id 会话唯一标识
     * @return 对应的 {@link PersistentChat} 记录，若不存在则返回 {@code null}
     */
    @Select("SELECT id, jsonContent FROM persistent_chat WHERE id = #{id}")
    PersistentChat selectById(Integer id);

    /**
     * 插入一条新的聊天记录。
     *
     * @param record 要插入的 {@link PersistentChat} 对象，必须包含非空的 {@code id} 和 {@code jsonContent}
     */
    @Insert("INSERT INTO persistent_chat (id, jsonContent) VALUES (#{id}, #{jsonContent})")
    void insert(PersistentChat record);

    /**
     * 更新指定会话 ID 的聊天内容。
     *
     * @param record 包含更新后 {@code jsonContent} 的 {@link PersistentChat} 对象，{@code id} 用于定位记录
     */
    @Update("UPDATE persistent_chat SET jsonContent = #{jsonContent} WHERE id = #{id}")
    void update(PersistentChat record);

    /**
     * 根据会话 ID 删除聊天记录。
     *
     * @param id 要删除的会话唯一标识
     */
    @Delete("DELETE FROM persistent_chat WHERE id = #{id}")
    void deleteById(Integer id);
}
