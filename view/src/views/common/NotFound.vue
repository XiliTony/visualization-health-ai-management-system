<script>
export default {
  name: 'NotFound',
  methods: {
    goHome() {
      const userInfo = this.$store.state.user.userInfo
      // 如果未登录 或 userInfo 不存在，跳转到登录页
      if (!userInfo || !userInfo.role) {
        this.$router.push('/login')
        return
      }

      // 已登录，根据角色跳转
      if (userInfo.role === '管理员') {
        this.$router.push('/dashboardPage')
      } else {
        this.$router.push('/healthData')
      }
    }
  }
}
</script>

<template>
  <div class="not-found-container">
    <div class="content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #e8f5e9; /* 浅绿色背景，与你的主题一致 */
  text-align: center;
}

.content {
  padding: 40px 60px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2); /* 绿色系阴影 */
}

.content h1 {
  font-size: 72px;
  color: #4caf50; /* 主绿色 */
  margin-bottom: 16px;
  font-weight: 700;
}

.content p {
  font-size: 18px;
  color: #555;
  margin-bottom: 28px;
}

/* 按钮样式：使用绿色主题 */
:deep(.el-button--primary) {
  background-color: #4caf50 !important;
  border-color: #4caf50 !important;
}

:deep(.el-button--primary:hover) {
  background-color: #43a047 !important;
  border-color: #43a047 !important;
}

:deep(.el-button--primary:focus) {
  background-color: #388e3c !important;
  border-color: #388e3c !important;
}
</style>
