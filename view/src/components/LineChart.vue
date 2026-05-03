<script>
import * as echarts from 'echarts'
import Tab from '@/components/Tab.vue'

export default {
  name: 'LineChart',
  components: { Tab },
  props: {
    tag: {
      type: String,
      default: '请选择健康项'
    },
    values: {
      type: Array,
      default: () => []
    },
    data: {
      type: Array,
      default: () => []
    },
    height: {
      type: String,
      default: '500px'
    },
    tooltipFormatter: {
      type: Function,
      default: (params) => {
        return `${params[0].axisValue} -> ${params[0].data}`
      }
    }
  },
  data() {
    return {
      chart: null,
      timeOptions: [
        { value: 7, label: '一周内' },
        { value: 30, label: '一个月内' },
        { value: 90, label: '三个月内' },
        { value: 365, label: '一年内' }
      ],
      selectedValueItem: 365,
      resizeTimer: null
    }
  },
  watch: {
    values: {
      handler() {
        this.$nextTick(() => {
          this.updateChart()
        })
      },
      deep: true
    },
    data: {
      handler() {
        this.$nextTick(() => {
          this.updateChart()
        })
      },
      deep: true
    }
  },
  mounted() {
    this.initChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    this.cleanup()
  },
  methods: {
    initChart() {
      if (this.chart) {
        this.chart.dispose()
        this.chart = null
      }

      if (!this.$refs.chart) {
        return
      }

      try {
        this.chart = echarts.init(this.$refs.chart)
        this.updateChart()
      } catch (error) {
        console.error('ECharts 初始化失败:', error)
      }
    },
    updateChart() {
      if (!this.chart) {
        this.initChart()
        return
      }

      if (
        !this.data ||
        this.data.length === 0 ||
        !this.values ||
        this.values.length === 0
      ) {
        this.chart.clear()
        return
      }

      try {
        const option = {
          backgroundColor: 'transparent',
          tooltip: {
            trigger: 'axis',
            formatter: this.tooltipFormatter,
            backgroundColor: 'rgba(50, 50, 50, 0.7)',
            borderColor: '#333',
            textStyle: {
              color: '#fff'
            }
          },
          grid: {
            left: '4%',
            right: '15%', // 保持较大的右边距
            bottom: '12%',
            top: '10%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: true,
            data: this.data,
            min: 0,
            max: this.data.length > 0 ? this.data.length - 1 : 0,
            axisLabel: {
              color: '#666',
              fontSize: 12,
              rotate: 0,
              margin: 10,
              interval: 0,
              formatter: (value, index) => {
                const total = this.data.length
                if (total > 10) {
                  return index % Math.ceil(total / 6) === 0 ? value : ''
                }
                return value
              }
            },
            axisLine: {
              lineStyle: {
                color: '#E4E7ED'
              }
            },
            axisTick: {
              alignWithLabel: true,
              interval: (index) => {
                const total = this.data.length
                if (total > 10) {
                  return index % Math.ceil(total / 6) === 0
                }
                return true
              }
            }
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              color: '#666',
              fontSize: 12,
              margin: 8
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#E4E7ED'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#F0F0F0',
                type: 'dashed'
              }
            }
            // 移除自定义的min/max，让ECharts自动计算合适的等差数列
          },
          series: [
            {
              name: this.tag,
              type: 'line',
              data: this.values,
              smooth: true,
              symbol: 'circle',
              symbolSize: 8,
              lineStyle: {
                width: 3,
                color: '#67C23A'
              },
              itemStyle: {
                color: '#67C23A',
                borderColor: '#fff',
                borderWidth: 2
              },
              areaStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    {
                      offset: 0,
                      color: 'rgba(103, 194, 58, 0.3)'
                    },
                    {
                      offset: 1,
                      color: 'rgba(103, 194, 58, 0.05)'
                    }
                  ]
                }
              },
              emphasis: {
                focus: 'series',
                scale: true,
                itemStyle: {
                  color: '#67C23A',
                  borderColor: '#fff',
                  borderWidth: 3,
                  shadowBlur: 10,
                  shadowColor: 'rgba(103, 194, 58, 0.5)'
                }
              },
              animation: true,
              animationDuration: 1000,
              animationEasing: 'cubicOut'
            }
          ]
        }

        this.chart.setOption(option, true)
      } catch (error) {
        console.error('图表更新失败:', error)
      }
    },
    handleChange(obj) {
      this.selectedValueItem = obj.value
      this.$emit('on-selected', obj.value)
    },
    handleResize() {
      if (this.resizeTimer) {
        clearTimeout(this.resizeTimer)
      }
      this.resizeTimer = setTimeout(() => {
        if (this.chart) {
          this.chart.resize()
        }
      }, 200)
    },
    cleanup() {
      if (this.resizeTimer) {
        clearTimeout(this.resizeTimer)
      }
      if (this.chart) {
        this.chart.dispose()
        this.chart = null
      }
      window.removeEventListener('resize', this.handleResize)
    }
  }
}
</script>

<template>
  <div class="line-chart-container">
    <div class="chart-header">
      <div class="header-left">
        <span class="chart-title">{{ tag }}</span>
      </div>
      <div class="header-right">
        <span class="time-label">时间选择</span>
        <Tab
          :buttons="timeOptions"
          :initial-active="selectedValueItem"
          @change="handleChange"
        />
      </div>
    </div>

    <div
      v-if="!data || data.length === 0 || !values || values.length === 0"
      class="empty-container"
    >
      <el-empty
        :description="tag === '请选择健康项' ? '请先选择健康项' : '暂无数据'"
      ></el-empty>
    </div>

    <div v-else class="chart-wrapper">
      <div ref="chart" class="chart" :style="{ height: height }"></div>
    </div>
  </div>
</template>

<style scoped>
.line-chart-container {
  width: 100%;
  background: transparent;
  padding: 0;
  box-shadow: none;
  min-width: 0;
  overflow: hidden; /* 防止容器溢出 */
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0;
  flex-wrap: wrap;
  gap: 10px;
  min-width: 0;
}

.header-left .chart-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  min-width: 0;
}

.time-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  border: 1px dashed #e4e7ed;
  border-radius: 4px;
  background: #fafafa;
  min-width: 0;
}

.chart-wrapper {
  width: 100%;
  min-height: 300px;
  overflow: hidden;
  min-width: 0;
}

.chart {
  width: 100%;
  min-height: 300px;
  min-width: 0;
}

/* 响应式设计 - 进一步优化小屏幕 */
@media (max-width: 1200px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .header-right {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .header-right {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .chart-title {
    font-size: 16px;
  }

  .empty-container {
    height: 300px;
  }

  .time-label {
    font-size: 13px;
  }
}

/* 超小屏幕特殊处理 */
@media (max-width: 480px) {
  .chart-header {
    margin-bottom: 15px;
  }

  .header-left .chart-title {
    font-size: 16px;
  }

  .empty-container {
    height: 250px;
  }
}
</style>
