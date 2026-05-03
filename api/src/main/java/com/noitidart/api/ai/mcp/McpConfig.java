package com.noitidart.api.ai.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP（Model Context Protocol）客户端配置类。
 * 用于连接大模型平台提供的 MCP 服务（如网页搜索等工具能力），
 * 并将其封装为 LangChain4j 可用的工具提供者（ToolProvider）。
 */
@Configuration
public class McpConfig {

    /**
     * 从 Spring 配置文件中注入大模型平台的 API Key，
     * 用于身份认证，访问 MCP 服务
     */
    @Value("${bigmodel.api-key}")
    private String apiKey;

    /**
     * 创建并注册一个 {@link McpToolProvider} Bean，
     * 该提供者封装了与 MCP 服务（如智谱 BigModel 的 Web 搜索）的通信能力，
     * 可被 LangChain4j 的 Agent 或 Tool 执行器调用。
     *
     * @return 配置完成的 McpToolProvider 实例
     */
    @Bean
    public McpToolProvider mcpToolProvider() {
        // 和 MCP 服务通讯
        McpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl("https://open.bigmodel.cn/api/mcp/web_search/sse?Authorization=" + apiKey)
                .logRequests(true) // 开启日志，查看更多信息
                .logResponses(true)
                .build();
        // 创建 MCP 客户端
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("bankMcpClient")
                .transport(transport)
                .build();
        // 从 MCP 客户端获取工具
        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
        return toolProvider;
    }
}