<script>
import { getDashBoardService, getLineChartService } from '@/api/dashboard'
import LineChart from '@/components/LineChart.vue'

export default {
  name: 'DashboardPage',
  components: { LineChart },
  data() {
    return {
      staticCount: {
        userCount: '',
        itemCount: ''
      },
      tag: '',
      dataList: [],
      values: [],
      days: 365 // 默认查询一年的数据
    }
  },
  created() {
    this.fetchStaticCount()
    this.fetchItemInfo()
  },
  methods: {
    async fetchStaticCount() {
      try {
        const { data } = await getDashBoardService()
        this.staticCount = data[0]
      } catch (error) {
        console.log('仪表盘查询静态数据异常: ', error)
      }
    },
    onSelected(days) {
      this.days = days
      this.fetchItemInfo()
    },
    customTooltip(params) {
      return `
        <div style="padding: 8px;">
          <div>${params[0].axisValue}</div>
          <div>当天收录健康项数: ${params[0].data}</div>
        </div>`
    },
    async fetchItemInfo() {
      try {
        const { data } = await getLineChartService(this.days)
        this.values = data.map((entity) => entity.count)
        this.dataList = data.map((entity) => entity.name)
      } catch (error) {
        console.log('折线图查询健康项收录情况异常: ', error)
      }
    }
  }
}
</script>

<template>
  <div class="dashboard-home">
    <!-- 顶部仪表盘区域 -->
    <div class="dashboard-top">
      <div class="dashboard-header">
        <h2 class="section-title">系统仪表盘</h2>
      </div>
      <div class="stats-card">
        <div class="stat-item">
          <div class="stat-icon user-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-value">{{ staticCount.userCount }}</div>
          <div class="stat-label">存量用户</div>
          <div class="stat-desc">存在的用户总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-icon health-icon">
            <i class="el-icon-notebook-2"></i>
          </div>
          <div class="stat-value">{{ staticCount.itemCount }}</div>
          <div class="stat-label">收录健康项</div>
          <div class="stat-desc">平台累计健康项数目</div>
        </div>
      </div>
    </div>

    <!-- 间距分隔区 -->
    <div class="dashboard-separator"></div>

    <!-- 底部折线图区域 -->
    <div class="dashboard-bottom">
      <h2 class="section-title">健康项收录情况</h2>
      <!-- 去掉时间范围选择框 -->
      <div class="chart-placeholder">
        <LineChart
          @on-selected="onSelected"
          :tag="tag"
          :tooltip-formatter="customTooltip"
          :data="dataList"
          :values="values"
        ></LineChart>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 基础容器样式 */
.dashboard-home {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
  min-height: 100vh;
}

/* 统一标题样式 - 字体大小一致 */
.section-title {
  font-size: 22px; /* 统一两个标题字体大小 */
  font-weight: 600;
  color: #2e7d32;
  margin: 0 0 20px 0; /* 增加标题下方间距 */
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

/* 顶部仪表盘区域 */
.dashboard-top {
  padding: 25px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  flex: 1;
}

.dashboard-header {
  padding-bottom: 15px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

/* 统计卡片容器 */
.stats-card {
  display: flex;
  gap: 40px;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  padding: 20px 0;
  flex: 1;
}

/* 统计项样式 - 放大卡片填充空间 */
.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px; /* 进一步放大卡片内边距 */
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(76, 175, 80, 0.18);
  background: white;
  min-width: 280px; /* 增大卡片最小宽度 */
  max-width: 360px; /* 增大卡片最大宽度 */
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
}

/* 图标样式 - 放大图标 */
.stat-icon {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 35px;
  font-size: 48px;
  color: white;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.25);
}

.user-icon {
  background: linear-gradient(135deg, #4caf50, #66bb6a);
}

.health-icon {
  background: linear-gradient(135deg, #81c784, #a5d6a7);
}

/* 数值样式 */
.stat-value {
  font-size: 60px; /* 增大数值字体 */
  font-weight: 700;
  color: #2e7d32;
  margin-bottom: 20px;
  line-height: 1;
}

/* 标签样式 */
.stat-label {
  font-size: 22px; /* 增大标签字体 */
  color: #333;
  line-height: 1.4;
  font-weight: 600;
  margin-bottom: 10px;
}

/* 描述文本 */
.stat-desc {
  font-size: 15px;
  color: #888;
  line-height: 1.5;
  text-align: center;
  max-width: 220px;
}

/* 区域分隔间距 */
.dashboard-separator {
  height: 30px;
}

/* 底部折线图区域 */
.dashboard-bottom {
  padding: 25px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  flex: 2;
}

/* 折线图容器 - 扩大尺寸填充空间 */
.chart-placeholder {
  width: 100%;
  flex: 1;
  min-height: 500px; /* 增加图表高度填充空间 */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px; /* 增加内边距 */
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-home {
    padding: 15px;
  }

  .dashboard-top,
  .dashboard-bottom {
    padding: 20px;
  }

  .stats-card {
    flex-direction: column;
    gap: 25px;
  }

  .stat-item {
    min-width: auto;
    width: 100%;
    padding: 50px 30px;
  }

  .stat-icon {
    width: 90px;
    height: 90px;
    font-size: 40px;
  }

  .stat-value {
    font-size: 52px;
  }

  .chart-placeholder {
    min-height: 400px;
  }
}

@media (max-width: 480px) {
  .stat-item {
    padding: 40px 20px;
  }

  .stat-icon {
    width: 80px;
    height: 80px;
    font-size: 36px;
  }

  .stat-value {
    font-size: 46px;
  }

  .chart-placeholder {
    min-height: 350px;
  }
}
</style>
