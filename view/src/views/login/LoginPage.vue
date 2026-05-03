<script>
import { userLoginService, userRegisterService } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'LoginPage',
  data() {
    let validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'login',
      isFlipping: false,
      form: {
        account: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        account: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          {
            min: 5,
            max: 10,
            message: '账号必须是 5-10位 的字符',
            trigger: 'blur'
          }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {
            pattern: /^\S{6,15}$/,
            message: '密码必须是 6-15位 的非空字符',
            trigger: 'blur'
          }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' },
          {
            pattern: /^\S{6,15}$/,
            message: '密码必须是 6-15位 的非空字符',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    switchTab(tab) {
      if (this.activeTab === tab || this.isFlipping) return

      this.isFlipping = true
      setTimeout(() => {
        // 在切换标签前清空表单
        this.clearForm()
        this.activeTab = tab
        setTimeout(() => {
          this.isFlipping = false
        }, 300)
      }, 300)
    },
    clearForm() {
      this.form = {
        account: '',
        password: '',
        confirmPassword: ''
      }
    },
    register() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          const res = await userRegisterService(this.form)
          if (res.code === '200') {
            Message.success('注册成功')
            setTimeout(() => {
              this.switchTab('login')
            }, 800)
          } else {
            Message.error(res.msg)
          }
        }
      })
    },
    login() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          const res = await userLoginService(this.form)
          if (res.code === '200') {
            this.$store.commit('user/setUserInfo', res.data)
            Message.success('登录成功')
            await this.$router.push('/')
          } else {
            Message.error(res.msg)
          }
        }
      })
    }
  }
}
</script>

<template>
  <div class="auth-wrapper">
    <div class="auth-container">
      <!-- 网站圆形图标区域 -->
      <div class="logo-container">
        <div class="site-logo">
          <img src="../../assets/logo.jpg" alt="Logo" />
        </div>
      </div>

      <!-- 项目标题 -->
      <div class="title-container">
        <h1 class="system-title">可视化健康管理系统</h1>
      </div>

      <div class="tabs-container">
        <!-- 使用简单的淡入淡出动画 -->
        <transition :name="isFlipping ? 'flip' : 'none'" mode="out-in">
          <!-- 登录表单 -->
          <div v-if="activeTab === 'login'" key="login" class="form-container">
            <h2 class="form-title">欢迎回来</h2>
            <el-form
              label-width="0"
              :model="form"
              :rules="rules"
              ref="formRef"
              autocomplete="off"
            >
              <el-form-item prop="account">
                <div class="custom-input-wrapper">
                  <i class="el-icon-user input-icon"></i>
                  <el-input
                    placeholder="请输入账号"
                    class="custom-input"
                    v-model="form.account"
                  ></el-input>
                </div>
              </el-form-item>
              <el-form-item prop="password">
                <div class="custom-input-wrapper">
                  <i class="el-icon-lock input-icon"></i>
                  <el-input
                    show-password
                    v-model="form.password"
                    type="password"
                    placeholder="请输入密码"
                    class="custom-input"
                  ></el-input>
                </div>
              </el-form-item>
              <el-button type="primary" @click="login">登 录</el-button>
            </el-form>
            <div class="switch-tab">
              没有账号？<span class="switch-link" @click="switchTab('register')"
                >注册</span
              >
            </div>
          </div>

          <!-- 注册表单 -->
          <div v-else key="register" class="form-container">
            <h2 class="form-title">创建账号</h2>
            <el-form
              label-width="0"
              :model="form"
              :rules="rules"
              ref="formRef"
              autocomplete="off"
            >
              <el-form-item prop="account">
                <div class="custom-input-wrapper">
                  <i class="el-icon-user input-icon"></i>
                  <el-input
                    v-model="form.account"
                    placeholder="设置账号"
                    class="custom-input"
                  ></el-input>
                </div>
              </el-form-item>
              <el-form-item prop="password">
                <div class="custom-input-wrapper">
                  <i class="el-icon-lock input-icon"></i>
                  <el-input
                    show-password
                    v-model="form.password"
                    type="password"
                    placeholder="设置密码"
                    class="custom-input"
                  ></el-input>
                </div>
              </el-form-item>
              <el-form-item prop="confirmPassword">
                <div class="custom-input-wrapper">
                  <i class="el-icon-lock input-icon"></i>
                  <el-input
                    show-password
                    v-model="form.confirmPassword"
                    type="password"
                    placeholder="确认密码"
                    class="custom-input"
                  ></el-input>
                </div>
              </el-form-item>
              <el-button type="primary" @click="register">注 册</el-button>
            </el-form>
            <div class="switch-tab">
              已有账号？<span class="switch-link" @click="switchTab('login')"
                >登录</span
              >
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e6f7e6, #c8e6c9);
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.auth-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 100, 0, 0.25);
  width: 420px;
  overflow: hidden;
}

.logo-container {
  background: #4caf50;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.site-logo {
  width: 80px;
  height: 80px;
  background: #81c784;
  border-radius: 50%;
  border: 3px solid white;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
  overflow: hidden; /* 关键：防止图片溢出圆形 */
}

.site-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 关键：让图片填充整个圆形区域 */
  border-radius: 50%; /* 确保图片也是圆形 */
}

.title-container {
  background: #4caf50;
  padding: 15px 0;
  text-align: center;
}

.system-title {
  color: white;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 1px;
}

.tabs-container {
  padding: 30px;
  position: relative;
  min-height: 320px;
}

.form-container {
  padding: 0 10px;
}

.form-title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: #2e7d32;
  margin-bottom: 30px;
}

.el-form-item {
  margin-bottom: 22px;
}

/* 重新设计输入框样式 */
.custom-input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #81c784;
  font-size: 18px;
  z-index: 10;
}

/* 深度选择器来覆盖Element UI样式 */
:deep(.custom-input .el-input__inner) {
  height: 46px;
  border-radius: 6px;
  border: 1px solid #c8e6c9;
  padding-left: 45px !important;
  padding-right: 15px;
  width: 100%;
  font-size: 14px;
}

:deep(.custom-input .el-input__inner:focus) {
  border-color: #81c784;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.25);
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: #999;
}

.el-button {
  background: #4caf50;
  border-color: #43a047;
  color: white;
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 10px;
  border-radius: 6px;
}

.el-button:hover {
  background: #43a047;
  border-color: #388e3c;
}

.el-button:active {
  background: #388e3c;
}

.switch-tab {
  text-align: right;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.switch-link {
  color: #4caf50;
  cursor: pointer;
  text-decoration: underline;
}

.switch-link:hover {
  color: #388e3c;
}

/* 翻转动画 */
.flip-enter-active,
.flip-leave-active {
  transition: all 0.6s ease;
  transform-style: preserve-3d;
  perspective: 1000px;
}

.flip-enter-from {
  opacity: 0;
  transform: rotateY(90deg) scale(0.8);
}

.flip-enter-to {
  opacity: 1;
  transform: rotateY(0deg) scale(1);
}

.flip-leave-from {
  opacity: 1;
  transform: rotateY(0deg) scale(1);
}

.flip-leave-to {
  opacity: 0;
  transform: rotateY(-90deg) scale(0.8);
}
</style>
