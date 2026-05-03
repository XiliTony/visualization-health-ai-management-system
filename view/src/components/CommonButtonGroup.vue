<script>
export default {
  name: 'CommonButtonGroup',
  props: {
    options: {
      type: Array,
      required: true,
      default: () => []
    },
    value: {
      type: [String, Number],
      default: ''
    },
    type: {
      type: String,
      default: 'primary'
    },
    size: {
      type: String,
      default: 'medium'
    },
    // ✅ 新增：悬停颜色主题
    hoverType: {
      type: String,
      default: 'success' // success / primary / danger 等
    }
  },
  computed: {
    selected: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
        this.$emit('change', val)
      }
    },
    // ✅ 根据 hoverType 生成颜色类名
    hoverClass() {
      return `hover-${this.hoverType}`
    }
  }
}
</script>

<template>
  <el-button-group :class="hoverClass">
    <el-button
      v-for="option in options"
      :key="option.value"
      :type="selected === option.value ? type : ''"
      :size="size"
      :style="option.style || {}"
      @click="selected = option.value"
    >
      {{ option.label }}
    </el-button>
  </el-button-group>
</template>

<style scoped>
/* ✅ 默认悬停：绿色主题 */
.hover-success :deep(.el-button:hover) {
  color: #67c23a !important;
  background-color: #f0f9eb !important;
  border-color: #67c23a !important;
}

.hover-success :deep(.el-button--success:hover) {
  color: #fff !important;
  background-color: #85ce61 !important;
  border-color: #85ce61 !important;
}
</style>
