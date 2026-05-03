<script>
export default {
  name: 'SlidingButtonGroup',
  props: {
    buttons: {
      type: Array,
      default: () => [],
      validator: (buttons) => buttons.every((b) => 'value' in b && 'label' in b)
    },
    initialActive: {
      type: [Number, String], // 支持多种类型的value
      default: null
    }
  },
  data() {
    return {
      activeValue: this.initialActive
    }
  },
  computed: {
    activeButtonIndex() {
      return this.buttons.findIndex((b) => b.value === this.activeValue)
    },
    sliderStyle() {
      if (this.buttons.length === 0) return {}
      const index = this.activeButtonIndex // 这里去掉括号，不是函数调用
      return {
        width: `${100 / this.buttons.length}%`,
        transform: `translateX(${index * 100}%)`, // 使用 index 而不是函数调用
        transition: 'transform 0.3s ease'
      }
    }
  },
  methods: {
    selectButton(button) {
      if (this.activeValue !== button.value) {
        this.activeValue = button.value
        this.$emit('change', button)
      }
    }
  }
}
</script>

<template>
  <div class="slidingButtonGroup">
    <div class="buttons-container">
      <div class="slider" :style="sliderStyle"></div>
      <button
        v-for="(button, index) in buttons"
        :key="index"
        :class="{ active: activeValue === button.value }"
        @click="selectButton(button)"
      >
        {{ button.label }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.slidingButtonGroup {
  width: auto;
  display: inline-block;
}

.buttons-container {
  display: inline-flex;
  position: relative;
  background: #f5f7fa;
  border-radius: 4px;
  padding: 2px;
  border: 1px solid #dcdfe6;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  height: 32px;
}

.buttons-container button {
  flex: 1;
  border: none;
  background: transparent;
  padding: 0 20px;
  cursor: pointer;
  position: relative;
  z-index: 2; /* 确保按钮在滑动条上方 */
  transition: color 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  min-width: 70px;
  border-radius: 3px;
  margin: 0;
  outline: none;
  height: 100%;
  line-height: 28px;
  white-space: nowrap;
}

/* 滑动条样式 - 放在按钮后面 */
.slider {
  position: absolute;
  top: 2px;
  bottom: 2px;
  background: #67c23a;
  border-radius: 3px;
  box-shadow: 0 1px 3px rgba(103, 194, 58, 0.3);
  z-index: 1; /* 在按钮下方 */
  transition: transform 0.3s ease;
}

/* 激活状态的按钮文字颜色 */
.buttons-container button.active {
  color: #fff; /* 白色文字在绿色背景上更清晰 */
  font-weight: 600;
}

/* 悬停状态 */
.buttons-container button:hover {
  color: #67c23a;
}

/* 激活状态的按钮悬停 */
.buttons-container button.active:hover {
  color: #fff;
}

/* 第一个按钮的圆角 */
.buttons-container button:first-child {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

/* 中间按钮的圆角 */
.buttons-container button:not(:first-child):not(:last-child) {
  border-radius: 0;
}

/* 最后一个按钮的圆角 */
.buttons-container button:last-child {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

/* 按钮之间的分隔线 */
.buttons-container button:not(:last-child)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 50%;
  background: #dcdfe6;
  z-index: 3;
}

/* 激活状态的按钮移除分隔线 */
.buttons-container button.active::after,
.buttons-container button.active + button::after {
  display: none;
}
</style>
