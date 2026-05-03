import request from '@/utils/request'

// 健康项新增接口
export const healthItemAddService = ({
  name,
  detail,
  unit,
  symbol,
  normalValue,
  iconUrl
}) =>
  request.post('/health-item/add', {
    name,
    detail,
    unit,
    symbol,
    normalValue,
    iconUrl
  })

// 健康项分页查询接口
export const healthItemQueryService = ({
  pageNum,
  pageSize,
  name,
  userId,
  isGlobal
}) =>
  request.get('/health-item/selectPage', {
    params: { pageNum, pageSize, name, userId, isGlobal }
  })

// 健康项更新接口
export const healthItemUpdateService = ({
  id,
  name,
  detail,
  unit,
  symbol,
  normalValue,
  iconUrl
}) =>
  request.put('/health-item/update', {
    id,
    name,
    detail,
    unit,
    symbol,
    normalValue,
    iconUrl
  })

// 健康项删除接口
export const healthItemDeleteService = (id) =>
  request.delete('/health-item/deleteById/' + id)

// 健康项批量删除接口
export const healthItemDeleteBatchService = (ids) =>
  request.delete('/health-item/deleteBatch/', { data: ids })

// 公有健康项选择器接口
export const healthItemOptionsService = () =>
  request.get('/health-item/options')

// 公有健康项及用户的私有健康项选择器接口
export const healthItemOptionsUserService = () =>
  request.get('/health-item/optionsUser')

// 查询健康项统计
export const heathItemQueryCountService = () =>
  request.get('/health-item/itemCount')

export const healthItemCheckExistsService = (name) =>
  request.get('/health-item/checkExists', { params: { name } })
