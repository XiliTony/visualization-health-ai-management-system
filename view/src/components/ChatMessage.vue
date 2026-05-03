<script>
import { formatTime } from '../utils/chatUtils.js'
import { marked } from 'marked'

export default {
  name: 'ChatMessage',
  props: {
    message: {
      type: String,
      required: true
    },
    isUser: {
      type: Boolean,
      default: false
    },
    timestamp: {
      type: Date,
      default: () => new Date()
    }
  },
  computed: {
    renderedMessage() {
      if (this.isUser) {
        return this.message
      }
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
      return marked(this.message)
    }
  },
  methods: {
    formatTime
  }
}
</script>

<template>
  <div
    class="chat-message"
    :class="{ 'user-message': isUser, 'ai-message': !isUser }"
  >
    <div class="message-avatar">
      <div
        class="avatar"
        :class="{ 'user-avatar': isUser, 'ai-avatar': !isUser }"
      >
        {{ isUser ? '我' : 'AI' }}
      </div>
    </div>
    <div class="message-content">
      <div class="message-bubble">
        <!-- 用户消息使用普通文本 -->
        <pre v-if="isUser" class="message-text">{{ message }}</pre>
        <!-- AI回复使用Markdown渲染 -->
        <div v-else class="message-markdown" v-html="renderedMessage"></div>
      </div>
      <div class="message-time">{{ formatTime(timestamp) }}</div>
    </div>
  </div>
</template>
<style scoped>
.chat-message {
  display: flex;
  margin-bottom: 20px;
  padding: 0 20px;
}

.user-message {
  justify-content: flex-end;
  flex-direction: row;
}

.user-message .message-avatar {
  order: 2;
}

.user-message .message-content {
  order: 1;
}

.ai-message {
  justify-content: flex-start;
  flex-direction: row;
}

.ai-message .message-avatar {
  order: 1;
}

.ai-message .message-content {
  order: 2;
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

.user-avatar {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message .message-bubble {
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  color: #333333;
  border-bottom-left-radius: 4px;
}

.message-text {
  font-family: inherit;
  font-size: 14px;
  line-height: 1.4;
  white-space: pre-wrap;
  margin: 0;
  color: #ffffff;
}

/* Markdown样式 */
.message-markdown {
  font-family: inherit;
  font-size: 14px;
  line-height: 1.5;
  color: #333333;
}

.message-markdown h1,
.message-markdown h2,
.message-markdown h3,
.message-markdown h4,
.message-markdown h5,
.message-markdown h6 {
  margin: 0.5em 0;
  font-weight: bold;
  color: #333333;
}

.message-markdown h1 {
  font-size: 1.5em;
  color: #2196f3;
}
.message-markdown h2 {
  font-size: 1.3em;
  color: #4caf50;
}
.message-markdown h3 {
  font-size: 1.2em;
  color: #ff9800;
}
.message-markdown h4 {
  font-size: 1.1em;
  color: #ffc107;
}
.message-markdown h5 {
  font-size: 1em;
  color: #9c27b0;
}
.message-markdown h6 {
  font-size: 0.9em;
  color: #673ab7;
}

.message-markdown p {
  margin: 0.5em 0;
  color: #333333;
}

.message-markdown ul,
.message-markdown ol {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.message-markdown li {
  margin: 0.2em 0;
  color: #555555;
}

.user-message .message-markdown li {
  color: rgba(255, 255, 255, 0.9);
}

.message-markdown code {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 0.2em 0.4em;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #ff9800;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.user-message .message-markdown code {
  background-color: rgba(255, 255, 255, 0.2);
  color: #ffd8a8;
  border-color: rgba(255, 255, 255, 0.2);
}

.message-markdown pre {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 1em;
  border-radius: 8px;
  overflow-x: auto;
  margin: 0.5em 0;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.user-message .message-markdown pre {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.message-markdown pre code {
  background-color: transparent;
  padding: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9em;
  color: #4caf50;
  border: none;
}

.user-message .message-markdown pre code {
  color: #b2f2bb;
}

.message-markdown blockquote {
  border-left: 4px solid #2196f3;
  padding-left: 1em;
  margin: 0.5em 0;
  font-style: italic;
  color: #666666;
  background: rgba(33, 150, 243, 0.1);
  border-radius: 0 8px 8px 0;
}

.user-message .message-markdown blockquote {
  border-left-color: rgba(255, 255, 255, 0.5);
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
}

.message-markdown a {
  color: #2196f3;
  text-decoration: none;
  border-bottom: 1px solid #2196f3;
  transition: all 0.3s ease;
}

.message-markdown a:hover {
  color: #4caf50;
  border-bottom-color: #4caf50;
}

.user-message .message-markdown a {
  color: #ffd8a8;
  border-bottom-color: #ffd8a8;
}

.user-message .message-markdown a:hover {
  color: #ffffff;
  border-bottom-color: #ffffff;
}

.message-markdown table {
  border-collapse: collapse;
  width: 100%;
  margin: 0.5em 0;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.message-markdown th,
.message-markdown td {
  border: 1px solid rgba(0, 0, 0, 0.1);
  padding: 0.5em;
  text-align: left;
  color: #333333;
}

.message-markdown th {
  background-color: rgba(33, 150, 243, 0.2);
  font-weight: bold;
  color: #333333;
}

.user-message .message-markdown th {
  background-color: rgba(255, 255, 255, 0.2);
}

.message-markdown hr {
  border: none;
  border-top: 1px solid rgba(0, 0, 0, 0.2);
  margin: 1em 0;
}

.user-message .message-markdown hr {
  border-top-color: rgba(255, 255, 255, 0.3);
}

.message-time {
  font-size: 12px;
  color: #888888;
  margin-top: 4px;
  padding: 0 4px;
  font-family: 'Consolas', monospace;
}

.user-message .message-time {
  text-align: right;
  color: #a8ffa8;
}

.ai-message .message-time {
  text-align: left;
  color: #a5d8ff;
}

@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }

  .chat-message {
    padding: 0 10px;
  }
}
</style>
