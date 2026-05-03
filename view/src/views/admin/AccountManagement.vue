<script>
import {
  userAddService,
  userDeleteBatchService,
  userDeleteService,
  userQueryService,
  userUpdateService
} from '@/api/user'
import { Message, MessageBox } from 'element-ui'
import CommonSearchBar from '@/components/CommonSearchBar.vue'
import Tab from '@/components/Tab.vue'

export default {
  name: 'AccountManagement',
  components: { Tab, CommonSearchBar },
  data() {
    return {
      username: null,
      tableData: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      formVisible: false,
      form: {},
      ids: [],
      roleName: null,
      // eslint-disable-next-line
      uploadHeaders: { 'token': '' },
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
        roleName: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  methods: {
    async load() {
      const res = await userQueryService({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        username: this.username,
        roleName: this.roleName
      })
      this.tableData = res.data.list
      this.total = res.data.total
    },
    reset() {
      this.username = null
      this.load()
    },
    handleAdd() {
      this.formVisible = true
      this.form = {} // 重置表单数据
      // 清除验证状态，避免新增时显示请选择角色错误
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.clearValidate()
        }
      })
    },
    async add() {
      const res = await userAddService(this.form)
      if (res.code === '200') {
        this.formVisible = false
        Message.success('操作成功')
        await this.load()
      } else {
        Message.error(res.msg)
      }
    },
    async update() {
      const res = await userUpdateService(this.form)
      if (res.code === '200') {
        this.formVisible = false
        Message.success('操作成功')
        await this.load()
      } else {
        Message.error(res.msg)
      }
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          // 在一个保存方法中做两种操作，一个是新增，一个是编辑
          this.form.id ? this.update() : this.add()
        }
      })
    },
    del(id) {
      MessageBox.confirm('删除数据后无法恢复，您确认删除吗？', '确认删除', {
        type: 'warning'
      })
        .then(async () => {
          const res = await userDeleteService(id)
          if (res.code === '200') {
            Message.success('操作成功')
            await this.load()
          } else {
            Message.error(res.msg)
          }
        })
        .catch(() => {})
    },
    handleUpdate(row) {
      this.form = JSON.parse(JSON.stringify(row)) // 深拷贝一个新的对象，用于编辑，这样就不会影响行对象
      this.formVisible = true
    },
    handleSelectionChange(rows) {
      //返回所有选中的行对象数组
      // console.log(rows);
      //从选中的行数组里面取出所有行的id组成一个新的数组
      this.ids = rows.map((row) => row.id)
    },
    delBatch() {
      if (this.ids.length === 0) {
        Message.warning('请选择数据')
        return
      }
      MessageBox.confirm('删除数据后无法恢复，您确认删除吗？', '确认删除', {
        type: 'warning'
      })
        .then(async () => {
          const res = await userDeleteBatchService(this.ids)
          if (res.code === '200') {
            Message.success('操作成功')
            await this.load()
          } else {
            Message.error(res.msg)
          }
        })
        .catch(() => {})
    },
    handleChange(obj) {
      this.roleName = obj.value
      this.load()
    },
    handleAvatarSuccess(res) {
      this.form.avatar = res.data
    }
  },
  mounted() {
    this.load()
    const user = this.$store.state.user.userInfo || {}
    this.uploadHeaders.token = user.token
  }
}
</script>

<template>
  <div>
    <CommonSearchBar
      placeholder="请输入昵称查询"
      v-model="username"
      @search="load"
      @reset="reset"
    ></CommonSearchBar>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="success" @click="handleAdd">新 增</el-button>
      <el-button type="danger" @click="delBatch">批量删除</el-button>
      <div style="float: right">
        <Tab
          :buttons="[
            { label: '全部', value: null },
            { label: '管理员', value: '管理员' },
            { label: '用户', value: '用户' }
          ]"
          :initial-active="null"
          @change="handleChange"
        ></Tab>
      </div>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column label="账号" prop="account" />
        <el-table-column label="头像">
          <template #default="scope">
            <img
              v-if="scope.row.avatar"
              :src="scope.row.avatar"
              alt=""
              style="
                display: block;
                width: 40px;
                height: 40px;
                border-radius: 50%;
              "
            />
          </template>
        </el-table-column>
        <el-table-column label="昵称" prop="username" />
        <el-table-column label="角色" prop="roleName" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
              @click="handleUpdate(scope.row)"
              type="success"
              icon="el-icon-edit"
              size="small"
              circle
            ></el-button>
            <el-button
              @click="del(scope.row.id)"
              type="danger"
              icon="el-icon-delete"
              size="small"
              circle
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="margin-top: 15px">
      <el-pagination
        @size-change="load"
        @current-change="load"
        :current-page.sync="pageNum"
        :page-size.sync="pageSize"
        :page-sizes="[5, 10, 15, 20]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <el-dialog
      title="账号信息"
      :visible.sync="formVisible"
      width="500"
      destroy-on-close
    >
      <el-form
        :model="form"
        ref="formRef"
        :rules="rules"
        label-width="80px"
        style="padding-right: 40px; padding-top: 20px"
      >
        <el-form-item label="账号" prop="account">
          <!--!!form.id将数字型转换成布尔型-->
          <el-input
            :disabled="!!form.id"
            v-model="form.account"
            autocomplete="off"
            placeholder="请输入账号"
          />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            action="http://localhost:9090/files/upload/avatars"
            list-type="picture"
            :on-success="handleAvatarSuccess"
            :headers="uploadHeaders"
          >
            <el-button type="success">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            show-password
            v-model="form.password"
            autocomplete="off"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="form.username"
            autocomplete="off"
            placeholder="请输入昵称"
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleName">
          <el-radio-group :disabled="!!form.id" v-model="form.roleName">
            <el-radio value="管理员" label="管理员">管理员</el-radio>
            <el-radio value="用户" label="用户">用户</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="formVisible = false">取 消</el-button>
          <el-button type="success" @click="save">保 存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 修改对话框中的取消按钮 */
:deep(.el-dialog .el-button:not(.el-button--success):hover) {
  border-color: #4caf50 !important;
  color: #4caf50 !important;
  background-color: #fff !important;
}

/* 修改对话框关闭按钮悬停颜色 */
:deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #2e7d32 !important;
}
</style>
