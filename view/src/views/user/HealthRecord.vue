<script>
import { Message } from 'element-ui'
import UserHealthItem from '@/views/user/UserHealthItem.vue'
import { healthRecordAddBatchService } from '@/api/healthRecord'
export default {
  name: 'HealthRecord',
  components: { UserHealthItem },
  data() {
    return {
      itemList: [] // 选中的健康项
    }
  },
  methods: {
    itemSelected(healthItem) {
      const newItem = JSON.parse(JSON.stringify(healthItem))
      if (!this.itemList.some((item) => item.id === newItem.id)) {
        this.itemList.push(newItem)
      } else {
        Message.warning('当前健康项列表已存在')
      }
    },
    async handleAddRecord() {
      const healthRecordList = this.itemList.map((healthItem) => {
        return {
          healthItemId: healthItem.id,
          value: healthItem.value
        }
      })
      const res = await healthRecordAddBatchService(healthRecordList)
      if (res.code === '200') {
        Message.success('健康记录添加成功')
      } else {
        Message.error(res.msg)
      }
    }
  }
}
</script>

<template>
  <div class="detail-container">
    <div class="content left-section">
      <div class="component-wrapper">
        <UserHealthItem @selected="itemSelected" />
      </div>
    </div>
    <div class="content right-section">
      <div class="panel-header">
        <div class="title">记录面板</div>
        <div class="right" @click="itemList = []">
          <el-tooltip effect="dark" content="清空选中项" placement="bottom">
            <i class="el-icon-refresh"></i>
          </el-tooltip>
        </div>
      </div>
      <div class="panel-content">
        <div v-if="!itemList.length" class="empty-state">
          <el-empty description="请选中健康项"></el-empty>
        </div>
        <div v-else class="record-list">
          <div
            class="record-item"
            v-for="(healthItem, index) in itemList"
            :key="index"
          >
            <div class="item-header">
              <span class="item-name">{{ healthItem.name }}</span>
              <el-tooltip
                effect="dark"
                :content="healthItem.detail"
                placement="top"
              >
                <i class="el-icon-question question-icon"></i>
              </el-tooltip>
            </div>
            <div class="item-input">
              <el-input
                v-model="healthItem.value"
                placeholder="请输入数值"
                size="medium"
              >
                <template #append>{{ healthItem.unit || '' }}</template>
              </el-input>
            </div>
          </div>
          <div class="action-section">
            <el-button
              type="success"
              class="add-record-btn"
              @click="handleAddRecord"
              icon="el-icon-plus"
            >
              新增记录
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  display: flex;
  height: 100%; /* ← 关键：不再用 vh */
  padding: 20px;
  gap: 20px;
  overflow: hidden;
  background: #fff;
  box-sizing: border-box;
}

.detail-container .content {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.left-section {
  width: 60%;
  min-width: 0;
}

.right-section {
  width: 40%;
  min-width: 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
  height: 40px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
  line-height: 1;
}

.right {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.right:hover {
  background-color: #f5f7fa;
  color: #67c23a;
}

.component-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.component-wrapper > div {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.panel-content {
  flex: 1;
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.empty-state /deep/ .el-empty {
  min-height: auto !important;
  padding: 0 !important;
  margin: 0 !important;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
  padding-right: 4px;
}

.record-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.question-icon {
  color: #909399;
  cursor: pointer;
  font-size: 16px;
}

.question-icon:hover {
  color: #409eff;
}

.item-input /deep/ .el-input-group__append {
  background-color: #f5f7fa;
  color: #606266;
  min-width: 60px;
}

.action-section {
  margin-top: 20px;
  text-align: center;
  flex-shrink: 0;
  padding-top: 10px;
}

.add-record-btn {
  width: 200px;
  height: 40px;
  font-size: 16px;
}

.record-list::-webkit-scrollbar {
  width: 4px;
}
.record-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}
.record-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
.record-list:not(:hover)::-webkit-scrollbar-thumb {
  background: transparent;
}
.item-input /deep/ .el-input.is-active .el-input__inner,
.item-input /deep/ .el-input__inner:focus {
  border-color: #67c23a !important;
  box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.2) !important;
}

.item-input /deep/ .el-input__inner:hover {
  border-color: #67c23a !important;
}

.item-input /deep/ .el-input-group__append {
  background-color: #67c23a !important;
  color: #fff !important;
  border-color: #67c23a !important;
  min-width: 60px;
}
/* 修改问号图标悬停颜色为绿色 */
:deep(.question-icon:hover) {
  color: #67c23a !important;
}
</style>
