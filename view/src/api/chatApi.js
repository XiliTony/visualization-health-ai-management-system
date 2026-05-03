import store from '@/store'

// 配置基础API地址
const API_BASE_URL = 'http://localhost:9090'

/**
 * 使用 SSE 方式调用聊天接口（支持自定义 header，token 不暴露）
 */
export function chatWithSSE(memoryId, message, onMessage, onError, onClose) {
  const userInfo = store.state.user.userInfo || {}
  const token = userInfo.token

  if (!token) {
    console.error('用户未登录，无法调用 AI 接口')
    onError?.(new Error('请先登录'))
    return null
  }

  const params = new URLSearchParams({
    memoryId: memoryId,
    message: message
  })

  return createAuthSSE(
    `${API_BASE_URL}/ai/chat?${params}`,
    { token: token },
    onMessage,
    onError,
    onClose
  )
}

/**
 * 用 fetch + ReadableStream 模拟 SSE（修复版本）
 */
/**
 * 用 fetch + ReadableStream 模拟 SSE（完整修复版）
 */
function createAuthSSE(url, headers, onMessage, onError, onClose) {
  const controller = new AbortController()

  const es = {
    onmessage: onMessage,
    onerror: onError,
    onclose: onClose,
    readyState: 0,
    CONNECTING: 0,
    OPEN: 1,
    CLOSED: 2,
    close: () => {
      controller.abort()
      es.readyState = es.CLOSED
      es.onclose?.()
    }
  }

  fetch(url, {
    method: 'GET',
    headers: {
      Accept: 'text/event-stream',
      'Cache-Control': 'no-cache',
      Connection: 'keep-alive',
      ...headers
    },
    signal: controller.signal
  })
    .then(async (response) => {
      if (!response.ok) throw new Error(`HTTP ${response.status}`)

      es.readyState = es.OPEN
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break

        buffer += decoder.decode(value, { stream: true })

        // 按 \n\n 分割消息
        const parts = buffer.split('\n\n')
        buffer = parts.pop() || ''

        for (const part of parts) {
          if (!part.trim()) continue

          // 🔑 关键修复：正确提取所有数据行并保留格式
          const lines = part.split('\n')
          let fullData = ''

          for (const line of lines) {
            // 匹配 "data: " 开头（注意有空格）
            if (line.startsWith('data: ')) {
              const data = line.substring(6) // 去掉 "data: " 共6个字符
              if (data && data !== '[DONE]') {
                // 🔑 累积数据，保留换行
                if (fullData) {
                  fullData += '\n' + data
                } else {
                  fullData = data
                }
              }
            }
            // 兼容 "data:" 开头（无空格）
            else if (line.startsWith('data:')) {
              const data = line.substring(5).trim()
              if (data && data !== '[DONE]') {
                if (fullData) {
                  fullData += '\n' + data
                } else {
                  fullData = data
                }
              }
            }
          }

          // 一次性发送完整的多行数据
          if (fullData) {
            console.log('📨 发送数据块:', fullData.substring(0, 100))
            es.onmessage?.({ data: fullData })
          }
        }
      }

      es.readyState = es.CLOSED
      es.onclose?.()
    })
    .catch((err) => {
      if (err.name !== 'AbortError') {
        console.error('SSE 错误:', err)
        es.readyState = es.CLOSED
        es.onerror?.(err)
      }
    })

  return es
}
