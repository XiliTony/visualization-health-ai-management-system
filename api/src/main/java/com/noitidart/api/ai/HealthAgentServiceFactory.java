package com.noitidart.api.ai;

import com.noitidart.api.ai.provider.MyChatMemoryProvider;
import com.noitidart.api.ai.tools.HealthDataTools;
import com.noitidart.api.ai.tools.HealthReportTools;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 健康管理智能体工厂配置类
 */
@Configuration
public class HealthAgentServiceFactory {

    @Resource
    private ChatModel myQwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private MyChatMemoryProvider myChatMemoryProvider;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private HealthDataTools healthDataTools;

    @Resource
    private HealthReportTools healthReportTools;


    /**
     * 创建并注册 {@link HealthAgentService} 的代理实现 Bean
     *
     * @return 配置完整的 {@link HealthAgentService} 代理实例
     */
    @Bean
    public HealthAgentService HealthAgentService() {
        // 构造AI Service
        HealthAgentService HealthAgentService = AiServices.builder(HealthAgentService.class)
                .chatModel(myQwenChatModel)
                .streamingChatModel(qwenStreamingChatModel) // 流式输出模型
                .contentRetriever(contentRetriever) // RAG 检索增强生成
                .chatMemoryProvider(myChatMemoryProvider) // MySQL 持久化存储，每个会话独立存储
                .toolProvider(mcpToolProvider) // MCP 工具调用
                .tools(healthDataTools, healthReportTools) // FunctionCalling 添加健康项和健康记录、健康报告功能
                .build();
        return HealthAgentService;
    }

}
