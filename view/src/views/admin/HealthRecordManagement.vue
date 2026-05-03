<script>
import {
  healthRecordDeleteBatchService,
  healthRecordDeleteService,
  healthRecordQueryService
} from '@/api/healthRecord'
import { Message, MessageBox } from 'element-ui'
import { healthItemOptionsService } from '@/api/healthItem'

export default {
  name: 'HealthRecordManagement',
  data() {
    return {
      userId: null,
      healthItemId: null,
      tableData: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      ids: [],
      healthItemOptions: []
    }
  },
  methods: {
    async load() {
      const res = await healthRecordQueryService({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        userId: this.userId,
        healthItemId: this.healthItemId
      })
      this.tableData = res.data.list
      this.total = res.data.total
    },
    reset() {
      this.userId = null
      this.load()
    },
    del(id) {
      MessageBox.confirm('删除数据后无法恢复，您确认删除吗？', '确认删除', {
        type: 'warning'
      })
        .then(async () => {
          const res = await healthRecordDeleteService(id)
          if (res.code === '200') {
            Message.success('操作成功')
            await this.load()
          } else {
            Message.error(res.msg)
          }
        })
        .catch(() => {})
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
          const res = await healthRecordDeleteBatchService(this.ids)
          if (res.code === '200') {
            Message.success('操作成功')
            await this.load()
          } else {
            Message.error(res.msg)
          }
        })
        .catch(() => {})
    },
    async fetchHealthItemOptions() {
      const { data } = await healthItemOptionsService()
      this.healthItemOptions = data
      this.healthItemOptions.unshift({ value: null, label: '全部' })
    },
    normalValueText(healthRecord) {
      const valueRange = healthRecord.normalValue.split(',')
      // true 正常情况; false 异常情况
      return (
        healthRecord.value > valueRange[0] && healthRecord.value < valueRange[1]
      )
    },
    normalValueRangeText(healthRecord) {
      const valueRange = healthRecord.normalValue.split(',')
      return `下限值: ${valueRange[0]}${healthRecord.healthItemUnit}-上限值: ${valueRange[1]}${healthRecord.healthItemUnit}`
    }
  },
  mounted() {
    this.load()
    this.fetchHealthItemOptions()
  }
}
</script>

<template>
  <div>
    <div style="margin: 15px">
      <div class="item">
        <div><span class="normal"></span>正常</div>
        <div><span class="abnormal"></span>异常</div>
        <div style="float: right">
          <el-select
            @change="load"
            v-model="healthItemId"
            placeholder="请选择健康项"
          >
            <el-option
              v-for="item in healthItemOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
          <el-button type="danger" @click="delBatch" style="margin-left: 16px"
            >批量删除</el-button
          >
        </div>
      </div>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column label="记录者" prop="username" />
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
        <el-table-column label="记录项" prop="healthItemName" />
        <el-table-column label="单位" prop="healthItemUnit" />
        <el-table-column label="指标情况">
          <template #default="scope">
            <div class="item">
              <span v-if="normalValueText(scope.row)" class="normal"></span>
              <span v-else class="abnormal"></span>
              {{ normalValueText(scope.row) ? '正常' : '异常' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="记录值">
          <template #default="scope">
            <div>{{ scope.row.value }}{{ scope.row.healthItemUnit }}</div>
          </template>
        </el-table-column>
        <el-table-column label="范围" prop="normalValue" show-overflow-tooltip>
          <template #default="scope">
            <div>{{ normalValueRangeText(scope.row) }}</div>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" prop="createTime" :sortable="true" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
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
  </div>
</template>

<style scoped>
/* 修改按钮悬停颜色为绿色 */
:deep(.el-button-group .el-button:hover) {
  border-color: #4caf50 !important;
  color: #4caf50 !important;
  background-color: rgba(76, 175, 80, 0.1) !important;
}

/* 修改选中按钮的悬停样式 */
:deep(.el-button-group .el-button--success:hover) {
  border-color: #388e3c !important;
  background-color: #388e3c !important;
  color: white !important;
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

/* 健康项指标情况图标 */
.item {
  display: flex;
  justify-content: left;
  align-items: center;
  gap: 16px;
}
.item .normal {
  display: inline-block;
  height: 10px;
  width: 10px;
  border-radius: 50%;
  background-color: rgb(57, 132, 7);
  margin-right: 5px;
}
.item .abnormal {
  display: inline-block;
  height: 10px;
  width: 10px;
  border-radius: 50%;
  background-color: rgb(227, 74, 32);
  margin-right: 5px;
}
</style>
