import request from '@/utils/request'

// 注册接口
export const userRegisterService = ({ account, password, confirmPassword }) =>
  request.post('/user/register', { account, password, confirmPassword })

// 登录接口
export const userLoginService = ({ account, password }) =>
  request.post('/user/login', { account, password })

// 分页查询接口
export const userQueryService = ({ pageNum, pageSize, username, roleName }) =>
  request.get('/user/selectPage', {
    params: { pageNum, pageSize, username, roleName }
  })

// 新增账号接口
export const userAddService = ({
  account,
  username,
  password,
  avatar,
  roleName
}) =>
  request.post('/user/add', { account, username, password, avatar, roleName })

// 更新账号接口
export const userUpdateService = ({
  id,
  account,
  username,
  password,
  avatar
}) => request.put('/user/update', { id, account, username, password, avatar })

// 删除单个账号接口
export const userDeleteService = (id) =>
  request.delete('/user/deleteById/' + id)

// 批量删除账号接口
export const userDeleteBatchService = (ids) =>
  request.delete('/user/deleteBatch/', { data: ids })

// 修改密码接口
export const userUpdatePasswordService = (userUpdatePasswordDTO) =>
  request.put('/user/updatePassword', userUpdatePasswordDTO)

// 个人中心接口
export const personCenterUpdateService = ({ id, account, username, avatar }) =>
  request.put('/user/personCenterUpdate', { id, account, username, avatar })
