<script>
import ChatMessage from '../../components/ChatMessage.vue'
import ChatInput from '../../components/ChatInput.vue'
import LoadingDots from '../../components/LoadingDots.vue'
import { chatWithSSE } from '../../api/chatApi.js'
import { generateMemoryId } from '../../utils/chatUtils.js'
import { marked } from 'marked'

export default {
  name: 'AIChat',
  components: {
    ChatMessage,
    ChatInput,
    LoadingDots
  },
  data() {
    return {
      messages: [],
      memoryId: null,
      isAiTyping: false,
      isStreaming: false,
      currentAiResponse: '',
      currentEventSource: null,
      connectionError: false
    }
  },
  computed: {
    currentAiResponseRendered() {
      if (!this.currentAiResponse) return ''
      // 配置marked选项
      marked.setOptions({
        breaks: true, // 支持换行
        gfm: true, // 支持GitHub风格的Markdown
        sanitize: false, // 不过滤HTML（根据需要可以开启）
        // eslint-disable-next-line no-unused-vars
        highlight: function (code, lang) {
          // 可以在这里添加代码高亮功能
          return code
        }
      })
      return marked(this.currentAiResponse)
    }
  },
  methods: {
    sendMessage(message) {
      this.addMessage(message, true)
      this.startAiResponse(message)
    },

    addMessage(content, isUser = false) {
      const message = {
        id: Date.now() + Math.random(),
        content,
        isUser,
        timestamp: new Date()
      }
      this.messages.push(message)
      this.scrollToBottom()
    },

    startAiResponse(userMessage) {
      this.isAiTyping = true
      this.isStreaming = true
      this.currentAiResponse = ''
      this.connectionError = false

      // 🔑 关键：先清理旧连接的所有回调，再关闭
      if (this.currentEventSource) {
        this.currentEventSource.onmessage = null // ✅ 断开消息回调
        this.currentEventSource.onerror = null // ✅ 断开错误回调
        this.currentEventSource.onclose = null // ✅ 断开关闭回调
        this.currentEventSource.close() // ✅ 关闭连接
        this.currentEventSource = null // ✅ 清空引用
      }

      // 开始新连接
      this.currentEventSource = chatWithSSE(
        this.memoryId,
        userMessage,
        this.handleAiMessage,
        this.handleAiError,
        this.handleAiClose
      )
    },

    handleAiMessage(event) {
      // 🔑 兼容两种传参：{ data } 或直接字符串
      const data = event?.data || event
      this.currentAiResponse += data
      this.scrollToBottom()
    },

    handleAiError(error) {
      console.error('AI 回复出错:', error)
      this.connectionError = true
      this.finishAiResponse()

      setTimeout(() => {
        this.connectionError = false
      }, 5000)
    },

    handleAiClose() {
      // 🔑 只处理关闭事件，不调用 finishAiResponse（避免死循环）
      console.log('🔵 SSE 连接已关闭')
      // 如果连接异常关闭且还在流式，强制结束
      if (this.isStreaming) {
        this.finishAiResponse()
      }
    },

    finishAiResponse() {
      console.log('🟢 finishAiResponse 执行') // 🔑 调试日志

      // 🔑 先断开 onclose 回调，防止死循环
      if (this.currentEventSource) {
        this.currentEventSource.onclose = null
      }

      // 🔑 重置所有状态
      this.isStreaming = false
      this.isAiTyping = false

      // 添加最终消息
      if (this.currentAiResponse?.trim()) {
        this.addMessage(this.currentAiResponse.trim(), false)
      }

      // 清空临时响应
      this.currentAiResponse = ''
      this.connectionError = false

      // 关闭连接
      if (this.currentEventSource) {
        this.currentEventSource.close()
        this.currentEventSource = null
      }

      // 🔑 强制 Vue 更新（确保 LoadingDots 消失）
      this.$forceUpdate()
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    initializeChat() {
      this.memoryId = generateMemoryId()
      console.log('聊天室ID:', this.memoryId)
    }
  },

  mounted() {
    this.initializeChat()
  },

  beforeUnmount() {
    // 组件销毁前关闭连接
    if (this.currentEventSource) {
      this.currentEventSource.close()
    }
  }
}
</script>

<template>
  <div class="ai-chat-container">
    <!-- 头部标题 -->
    <div class="chat-header">
      <h2 class="chat-title">健康智能助手</h2>
      <div class="chat-subtitle">
        您的健康顾问，随时为您解答健康相关问题。内容由AI生成，请仔细甄别
      </div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-container">
      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-content">
            <div class="welcome-icon">🤖</div>
            <h3>欢迎使用健康智能助手</h3>
            <p>我可以帮助您：</p>
            <ul>
              <li>解答健康相关问题</li>
              <li>添加健康项和健康记录</li>
              <li>分析数据生成健康报告</li>
              <li>分享健康知识</li>
            </ul>
            <p>请随时向我提问吧！</p>
          </div>
        </div>

        <!-- 历史消息 -->
        <ChatMessage
          v-for="message in messages"
          :key="message.id"
          :message="message.content"
          :is-user="message.isUser"
          :timestamp="message.timestamp"
        />

        <!-- AI 正在回复的消息 -->
        <div v-if="isAiTyping" class="chat-message ai-message">
          <div class="message-avatar">
            <div class="avatar ai-avatar">AI</div>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="ai-typing-content">
                <div
                  class="ai-response-text message-markdown"
                  v-html="currentAiResponseRendered"
                ></div>
                <LoadingDots v-if="isStreaming" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <ChatInput
        :disabled="isAiTyping"
        @send-message="sendMessage"
        placeholder="请输入您的健康问题..."
      />
    </div>

    <!-- 连接状态提示 -->
    <div v-if="connectionError" class="connection-error">
      <div class="error-content">
        <span class="error-icon">⚠️</span>
        <span>连接服务器失败，请检查后端服务是否启动</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8f5e8 100%);
  color: #333333;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chat-header {
  background: linear-gradient(to right, #4caf50, #45a049);
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: white;
}

.chat-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.chat-subtitle {
  font-size: 14px;
  margin-top: 5px;
  opacity: 0.9;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  background: linear-gradient(180deg, #f5f5f5 0%, #ffffff 100%);
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.welcome-content {
  text-align: center;
  max-width: 400px;
  color: #666666;
  background: rgba(255, 255, 255, 0.8);
  padding: 30px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid #e0e0e0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.welcome-content h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333333;
}

.welcome-content p {
  margin-bottom: 10px;
  line-height: 1.5;
  color: #666666;
}

.welcome-content ul {
  text-align: left;
  margin: 15px 0;
  padding-left: 20px;
}

.welcome-content li {
  margin-bottom: 8px;
  color: #555555;
  position: relative;
}

.welcome-content li:before {
  content: '•';
  color: #4caf50;
  font-weight: bold;
  position: absolute;
  left: -15px;
}

/* AI 正在回复时的消息样式 */
.chat-message {
  display: flex;
  margin-bottom: 20px;
  padding: 0 20px;
}

.ai-message {
  justify-content: flex-start;
  flex-direction: row;
}

.message-avatar {
  display: flex;
  align-items: flex-start;
  margin: 0 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.ai-avatar {
  background: linear-gradient(135deg, #2196f3 0%, #0b7dda 100%);
}

.message-content {
  max-width: 70%;
  min-width: 100px;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
  word-wrap: break-word;
  word-break: break-word;
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  color: #333333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
}

.ai-typing-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ai-response-text {
  font-size: 14px;
  line-height: 1.5;
  color: #333333;
}

/* AI实时回复的Markdown样式 */
.ai-response-text.message-markdown h1,
.ai-response-text.message-markdown h2,
.ai-response-text.message-markdown h3,
.ai-response-text.message-markdown h4,
.ai-response-text.message-markdown h5,
.ai-response-text.message-markdown h6 {
  margin: 0.5em 0;
  font-weight: bold;
  color: #333333;
}

.ai-response-text.message-markdown h1 {
  font-size: 1.5em;
  color: #2196f3;
}
.ai-response-text.message-markdown h2 {
  font-size: 1.3em;
  color: #4caf50;
}
.ai-response-text.message-markdown h3 {
  font-size: 1.2em;
  color: #ff9800;
}
.ai-response-text.message-markdown h4 {
  font-size: 1.1em;
  color: #ffc107;
}
.ai-response-text.message-markdown h5 {
  font-size: 1em;
  color: #9c27b0;
}
.ai-response-text.message-markdown h6 {
  font-size: 0.9em;
  color: #673ab7;
}

.ai-response-text.message-markdown p {
  margin: 0.5em 0;
  color: #333333;
}

.ai-response-text.message-markdown ul,
.ai-response-text.message-markdown ol {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.ai-response-text.message-markdown li {
  margin: 0.2em 0;
  color: #555555;
}

.ai-response-text.message-markdown code {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #ff9800;
  border: 1px solid #e0e0e0;
}

.ai-response-text.message-markdown pre {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 1em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 0.5em 0;
  border: 1px solid #e0e0e0;
}

.ai-response-text.message-markdown pre code {
  background-color: transparent;
  padding: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #4caf50;
  border: none;
}

.ai-response-text.message-markdown blockquote {
  border-left: 4px solid #2196f3;
  padding-left: 1em;
  margin: 0.5em 0;
  font-style: italic;
  color: #666666;
  background: rgba(33, 150, 243, 0.1);
  border-radius: 0 8px 8px 0;
}

.ai-response-text.message-markdown a {
  color: #2196f3;
  text-decoration: none;
  border-bottom: 1px solid #2196f3;
  transition: all 0.3s ease;
}

.ai-response-text.message-markdown a:hover {
  color: #4caf50;
  border-bottom-color: #4caf50;
}

.ai-response-text.message-markdown table {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5em 0;
  border: 1px solid #e0e0e0;
}

.ai-response-text.message-markdown th,
.ai-response-text.message-markdown td {
  border: 1px solid #e0e0e0;
  padding: 0.5em;
  text-align: left;
  color: #333333;
}

.ai-response-text.message-markdown th {
  background-color: rgba(33, 150, 243, 0.2);
  font-weight: bold;
  color: #333333;
}

.ai-response-text.message-markdown hr {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 1em 0;
}

.connection-error {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  z-index: 1000;
  animation: slideDown 0.3s ease-out;
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.error-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.error-icon {
  font-size: 16px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
}

@keyframes slideDown {
  from {
    transform: translateX(-50%) translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(-50%) translateY(0);
    opacity: 1;
  }
}

/* 滚动条样式 */
.messages-container::-webkit-scrollbar {
  width: 8px;
}

.messages-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #4caf50 0%, #45a049 100%);
  border-radius: 4px;
  border: 2px solid #f5f5f5;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #45a049 0%, #3d8b40 100%);
}

@media (max-width: 768px) {
  .chat-header {
    padding: 15px;
  }

  .chat-title {
    font-size: 20px;
  }

  .messages-container {
    padding: 15px 0;
  }

  .welcome-content {
    padding: 20px 15px;
    margin: 0 10px;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-message {
    padding: 0 10px;
  }

  .welcome-content ul {
    padding-left: 15px;
  }
}
</style>
