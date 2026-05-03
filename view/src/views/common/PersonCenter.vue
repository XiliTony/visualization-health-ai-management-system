<script>
import { personCenterUpdateService } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'PersonCenter',
  data() {
    return {
      user: this.$store.state.user.userInfo,
      form: {},
      uploadHeaders: { token: '' },
      rules: {
        username: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async updateUser() {
      const res = await personCenterUpdateService(this.form)
      if (res.code === '200') {
        Message.success('更新成功')
        // 更新缓存数据
        this.$store.commit('user/setUserInfo', this.form)
        // 触发父级从缓存里面取到最新的数据
        this.$emit('updateUser')
      } else {
        Message.error(res.msg)
      }
    },
    handleAvatarSuccess(res) {
      this.form.avatar = res.data
    }
  },
  mounted() {
    this.form = JSON.parse(JSON.stringify(this.user))
    this.uploadHeaders.token = this.user.token
  }
}
</script>

<template>
  <div class="card" style="width: 50%; padding: 40px 20px">
    <el-form
      :model="form"
      ref="formRef"
      :rules="rules"
      label-width="80px"
      style="padding-right: 40px; padding-top: 20px"
    >
      <div
        style="
          width: 100%;
          display: flex;
          justify-content: center;
          margin-bottom: 20px;
        "
      >
        <el-upload
          class="avatar-uploader"
          action="http://localhost:9090/files/upload/avatars"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :headers="uploadHeaders"
        >
          <img v-if="form.avatar" :src="form.avatar" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </div>
      <el-form-item label="账号">
        <el-input
          disabled
          v-model="form.account"
          autocomplete="off"
          placeholder="请输入账号"
        />
      </el-form-item>
      <el-form-item label="昵称" prop="username">
        <el-input
          v-model="form.username"
          autocomplete="off"
          placeholder="请输入昵称"
        />
      </el-form-item>
      <div style="text-align: center">
        <el-button @click="updateUser" type="success" style="padding: 20px 30px"
          >更新个人信息</el-button
        >
      </div>
    </el-form>
  </div>
</template>

<style scoped>
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}

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
