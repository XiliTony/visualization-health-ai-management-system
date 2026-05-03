import request from '@/utils/request'

// 健康记录批量新增接口
export const healthRecordAddBatchService = (healthRecordList) =>
  request.post('/health-record/addBatch', healthRecordList)

// 健康记录分页查询接口
export const healthRecordQueryService = ({
  pageNum,
  pageSize,
  healthItemId,
  userId
}) =>
  request.get('/health-record/selectPage', {
    params: { pageNum, pageSize, healthItemId, userId }
  })

// 健康记录删除接口
export const healthRecordDeleteService = (id) =>
  request.delete('/health-record/deleteById/' + id)

// 健康记录批量删除接口
export const healthRecordDeleteBatchService = (ids) =>
  request.delete('/health-record/deleteBatch/', { data: ids })

// 用户查询健康记录接口
export const healthRecordUserQueryService = ({
  pageNum,
  pageSize,
  healthItemId,
  userId
}) =>
  request.get('/health-record/selectUserPage', {
    params: { pageNum, pageSize, healthItemId, userId }
  })

// 健康数据可视化折线图
export const lineChartQueryService = (healthRecordLineChartQueryDTO) =>
  request.post(
    '/health-record/lineChartListUser',
    healthRecordLineChartQueryDTO
  )

// 导出用户健康记录接口
export const healthRecordExportUserService = (params) =>
  request({
    url: '/health-record/exportUser',
    method: 'get',
    params,
    responseType: 'blob' // 关键：告诉 axios 这次要接收二进制文件
  })
