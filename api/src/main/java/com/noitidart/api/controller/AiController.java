package com.noitidart.api.controller;

import com.noitidart.api.ai.HealthAgentService;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.utils.TokenUtils;
import dev.langchain4j.invocation.InvocationParameters;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Ai 控制器
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private HealthAgentService healthAgentService;

    /**
     * 流式聊天接口
     *
     * @param memoryId 会话 id
     * @param message 用户输入的问题
     * @return {@link ServerSentEvent} 的响应流，每个事件包含一段生成的文本
     */
    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(@RequestParam("memoryId")int memoryId,
                                              @RequestParam("message")String message,
                                              @RequestHeader(value = "token", required = false) String token) {

        // 用 token 解析真实用户
        User user = TokenUtils.getUserByToken(token);
        if (user == null) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error").data("认证失败").build());
        }

        // 构造 InvocationParameters，放入 userId
        InvocationParameters parameters = InvocationParameters.from(Map.of("userId", user.getId()));

        return healthAgentService.chatStream(memoryId, message, parameters)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
