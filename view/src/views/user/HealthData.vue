<script>
import UserHealthRecord from '@/views/user/UserHealthRecord.vue'
import { lineChartQueryService } from '@/api/healthRecord'
import { Message } from 'element-ui'
import LineChart from '@/components/LineChart.vue'
import { heathItemQueryCountService } from '@/api/healthItem'

export default {
  components: { UserHealthRecord, LineChart },
  name: 'HealthData',
  data() {
    return {
      values: [],
      dateList: [],
      tag: '请选择健康项',
      height: '400px',
      itemOptions: [],
      selectedHealthItem: {},
      healthRecordLineChartQueryDTO: {
        days: 365,
        healthItemId: null
      },
      selectedHealthItemId: null, // 新增：单独绑定下拉框的值
      itemCount: {}
    }
  },
  methods: {
    async lineChartListUser() {
      // 检查是否有健康项ID
      if (!this.healthRecordLineChartQueryDTO.healthItemId) {
        this.values = []
        this.dateList = []
        return
      }

      try {
        const res = await lineChartQueryService(
          this.healthRecordLineChartQueryDTO
        )
        if (res.code === '200') {
          this.values = res.data.map((entity) => entity.value)
          this.dateList = res.data.map((entity) => entity.createTime)
        } else {
          Message.error(res.msg)
          this.values = []
          this.dateList = []
        }
      } catch (error) {
        Message.error('请求失败')
        this.values = []
        this.dateList = []
      }
    },
    customTooltip(params) {
      return `
        <div style="padding: 8px;">
          <div>记录于: ${params[0].axisValue}</div>
          <div>记录值: ${params[0].data}${
        this.selectedHealthItem.unit || ''
      }</div>
        </div>`
    },
    onSelected(days) {
      this.healthRecordLineChartQueryDTO.days = days
      if (this.healthRecordLineChartQueryDTO.healthItemId) {
        this.lineChartListUser()
      }
    },
    listenerItemOptions(data) {
      this.itemOptions = data
      if (data.length > 0) {
        this.selectedHealthItemId = data[0].value
        this.healthRecordLineChartQueryDTO.healthItemId = data[0].value
        this.selectedItem()
      }
    },
    selectedItem() {
      if (!this.selectedHealthItemId) {
        this.tag = '请选择健康项'
        this.values = []
        this.dateList = []
        return
      }

      const resultItem = this.itemOptions.find(
        (item) => item.value === this.selectedHealthItemId
      )

      if (resultItem) {
        this.selectedHealthItem = resultItem
        this.tag = resultItem.label
        this.healthRecordLineChartQueryDTO.healthItemId = resultItem.value
        this.lineChartListUser()
      } else {
        this.tag = '请选择健康项'
        this.values = []
        this.dateList = []
      }
    },
    async fetchItemCount() {
      const { data } = await heathItemQueryCountService()
      this.itemCount = data
    }
  },
  mounted() {
    // 初始加载，等待健康项数据
    this.fetchItemCount()
  }
}
</script>

<template>
  <div class="health-data-container">
    <div class="left">
      <div class="chart-section">
        <div class="health-item-selector">
          <div class="detail">
            <i class="el-icon-chat-dot-round"></i>
            {{ selectedHealthItem.detail }}
          </div>
          <el-select
            @change="selectedItem"
            v-model="selectedHealthItemId"
            placeholder="请选择健康项"
            style="min-width: 250px"
          >
            <el-option
              v-for="item in itemOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <LineChart
          @on-selected="onSelected"
          :tooltip-formatter="customTooltip"
          :tag="tag"
          :height="height"
          :values="values"
          :data="dateList"
        ></LineChart>
      </div>
      <UserHealthRecord
        @listenerItemOptions="listenerItemOptions"
      ></UserHealthRecord>
    </div>
    <div class="right">
      <!-- 健康项统计区域 -->
      <div class="card">
        <div class="title">健康项统计</div>
        <div class="value">
          <div>
            <div class="count">{{ itemCount.globalItemCount }}</div>
            <div class="model">公有健康项</div>
          </div>
          <div>
            <div class="count">{{ itemCount.privateItemCount }}</div>
            <div class="model">私有健康项</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.health-data-container {
  display: flex;
  padding: 20px;
  gap: 20px;
}

.health-data-container .left {
  width: 70%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.health-data-container .right {
  width: 30%;
  border-radius: 8px;
  padding: 20px;
}

.chart-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.health-item-selector {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 20px;
}

.health-item-selector .detail {
  font-size: 14px;
  color: #666;
}

.right .card .title {
  font-size: 20px;
  margin-bottom: 20px;
  font-weight: 600;
  border-bottom: 1px solid rgba(76, 175, 80, 0.2);
  padding-bottom: 10px;
  color: rgb(51, 51, 51);
}

.right .card .value {
  display: flex;
  justify-content: space-evenly;
  background-color: rgba(76, 175, 80, 0.2);
  padding: 10px;
  border-radius: 5px;
}

.right .card .value .count {
  font-size: 24px;
  font-weight: 600;
  color: #2e7d32;
}
.right .card .value .model {
  font-size: 14px;
  color: #2e7d32;
}

/* 修改输入框焦点状态的颜色为绿色 */
:deep(.el-input__inner:focus) {
  border-color: #4caf50 !important;
}

/* 修改输入框获得焦点时的阴影效果为绿色 */
:deep(.el-input.is-focus .el-input__inner) {
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2) !important;
}
</style>
