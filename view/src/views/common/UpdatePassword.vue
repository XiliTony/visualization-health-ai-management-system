<script>
import { userUpdatePasswordService } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'UpdatePassword',
  data() {
    let validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次确认新密码'))
      } else if (value !== this.userUpdatePasswordDTO.newPassword) {
        callback(new Error('两次输入的新密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      user: this.$store.state.user.userInfo,
      userUpdatePasswordDTO: {
        id: '',
        password: '',
        newPassword: '',
        confirmNewPassword: ''
      },
      rules: {
        password: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          {
            pattern: /^\S{6,15}$/,
            message: '密码必须是 6-15位 的非空字符',
            trigger: 'blur'
          }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          {
            pattern: /^\S{6,15}$/,
            message: '密码必须是 6-15位 的非空字符',
            trigger: 'blur'
          }
        ],
        confirmNewPassword: [
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
    updatePassword() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          this.userUpdatePasswordDTO.id = this.user.id
          const res = await userUpdatePasswordService(
            this.userUpdatePasswordDTO
          )
          if (res.code === '200') {
            Message.success('修改成功')
            this.$store.commit('user/resetUserInfo')
            setTimeout(() => {
              location.href = '/login'
            }, 500)
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
  <div class="card" style="width: 50%; padding: 40px 20px">
    <el-form
      :model="userUpdatePasswordDTO"
      ref="formRef"
      :rules="rules"
      label-width="100px"
      style="padding-right: 40px; padding-top: 20px"
    >
      <el-form-item label="原密码" prop="password">
        <el-input
          show-password
          v-model="userUpdatePasswordDTO.password"
          autocomplete="off"
          placeholder="请输入原密码"
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          show-password
          v-model="userUpdatePasswordDTO.newPassword"
          autocomplete="off"
          placeholder="请输入新密码"
        />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmNewPassword" required>
        <el-input
          show-password
          v-model="userUpdatePasswordDTO.confirmNewPassword"
          autocomplete="off"
          placeholder="请再次确认新密码"
        />
      </el-form-item>
      <div style="text-align: center">
        <el-button
          @click="updatePassword"
          type="success"
          style="padding: 20px 30px"
          >立即修改</el-button
        >
      </div>
    </el-form>
  </div>
</template>

<style scoped>
/* 修改输入框焦点状态的颜色为绿色 */
:deep(.el-input__inner:focus) {
  border-color: #4caf50 !important;
}

/* 修改输入框hover状态的颜色为绿色 */
:deep(.el-input__inner:hover) {
  border-color: #4caf50 !important;
}

/* 修改搜索图标颜色为绿色 */
:deep(.el-icon-search:before) {
  color: #4caf50 !important;
}

/* 修改输入框获得焦点时的阴影效果为绿色 */
:deep(.el-input.is-focus .el-input__inner) {
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2) !important;
}
</style>
