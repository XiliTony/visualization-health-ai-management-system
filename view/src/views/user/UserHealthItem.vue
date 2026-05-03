<script>
import { Message, MessageBox } from 'element-ui'
import {
  healthItemAddService,
  healthItemCheckExistsService,
  healthItemDeleteService,
  healthItemQueryService,
  healthItemUpdateService
} from '@/api/healthItem'
import CommonSearchBar from '@/components/CommonSearchBar.vue'
import Tab from '@/components/Tab.vue'

export default {
  name: 'UserHealthItem',
  components: { CommonSearchBar, Tab },
  data() {
    return {
      name: null,
      tableData: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      formVisible: false,
      form: {},
      ids: [],
      isGlobal: 1,
      // eslint-disable-next-line
      uploadHeaders: { 'token': '' },
      rules: {
        name: [
          { required: true, message: '请输入健康项名称', trigger: 'blur' },
          {
            max: 100,
            message: '健康项名称100个字以内',
            trigger: 'blur'
          },
          { validator: this.checkNameExists, trigger: 'blur' }
        ],
        unit: [
          { required: true, message: '请输入健康项单位', trigger: 'blur' }
        ],
        symbol: [
          { required: true, message: '请输入健康项符号', trigger: 'blur' }
        ],
        normalValue: [
          {
            required: true,
            message: '请输入健康项正常值范围',
            trigger: 'blur'
          },
          {
            pattern: /^\d+(\.\d+)?[,\uFF0C]\d+(\.\d+)?$/,
            message: '格式应为“数字,数字”，例如：60,100 或 3.5，7.8',
            trigger: 'blur'
          }
        ],
        detail: [
          { required: true, message: '请输入健康项简介', trigger: 'blur' },
          {
            max: 200,
            message: '健康项简介100个字以内',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    async load() {
      const res = await healthItemQueryService({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.name,
        isGlobal: this.isGlobal
      })
      this.tableData = res.data.list
      this.total = res.data.total
    },
    reset() {
      this.name = null
      this.load()
    },
    handleAdd() {
      this.formVisible = true
      this.form = {}
    },
    async add() {
      const checkRes = await healthItemCheckExistsService(this.form.name)
      if (checkRes.code === '200' && checkRes.data === true) {
        return Message.warning('同名健康项已存在，请勿重复添加')
      }
      const res = await healthItemAddService(this.form)
      if (res.code === '200') {
        this.formVisible = false
        Message.success('操作成功')
        await this.load()
      } else {
        Message.error(res.msg)
      }
    },
    async update() {
      const res = await healthItemUpdateService(this.form)
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
          const res = await healthItemDeleteService(id)
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
    handleAvatarSuccess(res) {
      this.form.iconUrl = res.data
    },
    selectItem(healthItem) {
      this.$emit('selected', healthItem)
    },
    handleChange(obj) {
      this.isGlobal = obj.value
      this.load()
    },
    async checkNameExists(rule, value, callback) {
      if (!value || this.form.id) return callback() // 编辑模式跳过

      const res = await healthItemCheckExistsService(value)
      if (res.code === '200' && res.data === true) {
        callback(new Error('同名健康项已存在，请勿重复添加'))
      } else {
        callback()
      }
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
      placeholder="请输入健康项名称查询"
      v-model="name"
      @search="load"
      @reset="reset"
    ></CommonSearchBar>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="success" @click="handleAdd">新 增</el-button>
      <div style="float: right">
        <Tab
          :buttons="[
            { label: '公有健康项', value: 1 },
            { label: '私有健康项', value: 0 }
          ]"
          :initial-active="1"
          @change="handleChange"
        ></Tab>
      </div>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="tableData" stripe>
        <el-table-column label="名称" prop="name" />
        <el-table-column label="图标">
          <template #default="scope">
            <img
              v-if="scope.row.iconUrl"
              :src="scope.row.iconUrl"
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
        <el-table-column label="单位" prop="unit" />
        <el-table-column label="符号" prop="symbol" />
        <el-table-column label="简介" prop="detail" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              @click="selectItem(scope.row)"
              type="success"
              icon="el-icon-finished"
              size="small"
              circle
            ></el-button>
            <el-button
              v-if="scope.row.isGlobal === false"
              @click="handleUpdate(scope.row)"
              type="success"
              icon="el-icon-edit"
              size="small"
              circle
            ></el-button>
            <el-button
              v-if="scope.row.isGlobal === false"
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
      title="健康项信息"
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
        <el-form-item label="名称" prop="name">
          <!--!!form.id将数字型转换成布尔型-->
          <el-input
            :disabled="!!form.id"
            v-model="form.name"
            autocomplete="off"
            placeholder="请输入健康项名称，100个字以内"
          />
        </el-form-item>
        <el-form-item label="图标">
          <el-upload
            action="http://localhost:9090/files/upload/icons"
            list-type="picture"
            :on-success="handleAvatarSuccess"
            :headers="uploadHeaders"
          >
            <el-button type="success">上传健康项图标</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input
            v-model="form.unit"
            autocomplete="off"
            placeholder="请输入健康项单位"
          />
        </el-form-item>
        <el-form-item label="符号" prop="symbol">
          <el-input
            v-model="form.symbol"
            autocomplete="off"
            placeholder="请输入健康项符号"
          />
        </el-form-item>
        <el-form-item label="范围" prop="normalValue">
          <el-input
            v-model="form.normalValue"
            autocomplete="off"
            placeholder="请输入健康项正常值范围，格式应为“数字,数字”"
          />
        </el-form-item>
        <el-form-item label="简介" prop="detail">
          <el-input
            v-model="form.detail"
            autocomplete="off"
            placeholder="请输入健康项简介，200个字以内"
            :rows="3"
            type="textarea"
          />
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

/* 修改textarea焦点状态的颜色为绿色 */
:deep(.el-textarea__inner:focus) {
  border-color: #4caf50 !important;
}

/* 修改textarea hover状态的颜色为绿色 */
:deep(.el-textarea__inner:hover) {
  border-color: #4caf50 !important;
}

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
