package com.noitidart.api.ai.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeServerlessIndexConfig;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Pinecone 向量存储配置类
 * 用于配置和管理向量数据库连接
 */
@Configuration
public class EmbeddingStoreConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;

    /**
     * 创建 Pinecone 向量存储 Bean
     * 如果指定的索引不存在，将自动创建新的索引
     *
     * @return EmbeddingStore<TextSegment> 向量存储实例
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 创建 Pinecone 向量存储实例
        PineconeEmbeddingStore embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey("pcsk_YVgTi_2k9NNnUTgkZHVBbrxU63nb7zLbQQ4wZ2MmAGcnqU87ALKFfrXh8fZ2eevJ473R4") // 从环境变量获取 API 密钥
                .index("health-index") // 索引名称（不存在则自动创建）
                .nameSpace("health-namespace") // 命名空间（不存在则自动创建）
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS") // 指定云服务提供商为 AWS
                        .region("us-east-1") // 指定 AWS 区域为 us-east-1
                        .dimension(qwenEmbeddingModel.dimension()) // 设置向量维度与嵌入模型一致
                        .build())
                .build();

        return embeddingStore;
    }
}