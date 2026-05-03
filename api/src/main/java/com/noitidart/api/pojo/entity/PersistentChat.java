package com.noitidart.api.pojo.entity;

import lombok.Data;

/**
 * 聊天记忆持久化实体
 */
@Data
public class PersistentChat {
    /**
     * 会话 id ，主键
     */
    private Integer id;

    /**
     * 聊天记忆
     */
    private String jsonContent;

}
