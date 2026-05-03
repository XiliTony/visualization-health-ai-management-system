import request from '@/utils/request'

// 仪表盘接口
export const getDashBoardService = () => request.get('/dashboard/staticCount')

// 折线图接口
export const getLineChartService = (days) =>
  request.get(`/dashboard/itemInfo/${days}`)
