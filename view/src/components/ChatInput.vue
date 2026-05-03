<script>
export default {
  name: 'ChatInput',
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '请输入您的问题...'
    }
  },
  data() {
    return {
      inputMessage: ''
    }
  },
  methods: {
    sendMessage() {
      if (this.inputMessage.trim() && !this.disabled) {
        this.$emit('send-message', this.inputMessage.trim())
        this.inputMessage = ''
        this.adjustHeight()
      }
    },
    handleKeyDown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },
    adjustHeight() {
      this.$nextTick(() => {
        const textarea = this.$refs.inputRef
        textarea.style.height = 'auto'
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
      })
    },
    focus() {
      this.$refs.inputRef.focus()
    }
  },
  mounted() {
    this.adjustHeight()
  }
}
</script>

<template>
  <div class="chat-input">
    <div class="input-container">
      <textarea
          ref="inputRef"
          v-model="inputMessage"
          :placeholder="placeholder"
          :disabled="disabled"
          class="input-textarea"
          rows="1"
          @keydown="handleKeyDown"
          @input="adjustHeight"
      />
      <button
          :disabled="disabled || !inputMessage.trim()"
          @click="sendMessage"
          class="send-button"
      >
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M2 21l21-9L2 3v7l15 2-15 2v7z" fill="currentColor"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.chat-input {
  padding: 20px;
  background: linear-gradient(to top, #f5f5f5 0%, #e0e0e0 100%);
  border-top: 1px solid #d0d0d0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  max-width: 800px;
  margin: 0 auto;
}

.input-textarea {
  flex: 1;
  padding: 14px 18px;
  border: 1px solid #d0d0d0;
  border-radius: 24px;
  font-size: 14px;
  line-height: 1.4;
  resize: none;
  outline: none;
  transition: all 0.3s ease;
  min-height: 46px;
  max-height: 120px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.8);
  color: #333333;
  backdrop-filter: blur(10px);
}

.input-textarea::placeholder {
  color: #888888;
}

.input-textarea:focus {
  border-color: #4caf50;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.input-textarea:disabled {
  background: rgba(255, 255, 255, 0.6);
  color: #666666;
  cursor: not-allowed;
}

.send-button {
  width: 46px;
  height: 46px;
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.send-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #45a049 0%, #3d8b40 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.send-button:active:not(:disabled) {
  transform: translateY(0);
}

.send-button:disabled {
  background: linear-gradient(135deg, #cccccc 0%, #b0b0b0 100%);
  cursor: not-allowed;
  box-shadow: none;
}

@media (max-width: 768px) {
  .chat-input {
    padding: 15px;
  }

  .input-container {
    gap: 8px;
  }

  .input-textarea {
    font-size: 16px;
    padding: 12px 16px;
  }

  .send-button {
    width: 44px;
    height: 44px;
  }
}
</style>
