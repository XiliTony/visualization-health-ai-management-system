<script>
export default {
  name: 'CommonSearchBar',
  // Vue2选项式props定义
  props: {
    // 是否显示输入框（第三个页面无输入框）
    showInput: {
      type: Boolean,
      default: true
    },
    // 输入框占位符
    placeholder: {
      type: String,
      default: '请输入名称查询'
    },
    // 父组件传递的绑定值（用于双向绑定）
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      // 组件内部维护的搜索值
      searchValue: this.value
    }
  },
  watch: {
    // 监听父组件传递的value变化，同步到组件内部
    value(newVal) {
      this.searchValue = newVal
    },
    // 监听组件内部搜索值变化，同步到父组件
    searchValue(newVal) {
      this.$emit('input', newVal)
    }
  },
  methods: {
    // 点击查询：向父组件触发search事件，传递当前搜索值
    handleSearch() {
      this.$emit('search', this.searchValue)
    },
    // 点击重置：清空输入框，向父组件触发reset事件
    handleReset() {
      this.searchValue = ''
      this.$emit('reset')
    }
  }
}
</script>

<template>
  <div style="margin-bottom: 5px; display: flex; align-items: center">
    <!-- 搜索输入框（支持动态显示/隐藏） -->
    <el-input
      v-if="showInput"
      style="width: 240px; margin-right: 5px"
      v-model="searchValue"
      :placeholder="placeholder"
      prefix-icon="el-icon-search"
    ></el-input>
    <!-- 查询按钮 -->
    <el-button type="success" @click="handleSearch">查 询</el-button>
    <!-- 重置按钮 -->
    <el-button type="warning" @click="handleReset">重 置</el-button>
  </div>
</template>

<style scoped>
/* 组件内通用样式：输入框相关（所有页面复用） */
:deep(.el-input__inner:focus) {
  border-color: #4caf50 !important;
}
:deep(.el-input__inner:hover) {
  border-color: #4caf50 !important;
}
:deep(.el-icon-search:before) {
  color: #4caf50 !important;
}
:deep(.el-input.is-focus .el-input__inner) {
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2) !important;
}

/* 组件内按钮基础 hover 样式（查询/重置按钮通用） */
:deep(.el-button:hover:not(.el-button--success):not(.el-button--warning)) {
  border-color: #4caf50 !important;
  color: #4caf50 !important;
  background-color: rgba(76, 175, 80, 0.1) !important;
}
</style>
