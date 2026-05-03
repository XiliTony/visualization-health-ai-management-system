import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'
import store from '@/store'

const request = axios.create({
  baseURL: 'http://localhost:9090', // 后台基础地址
  timeout: 30000 // 后台接口超时时间
})

// request拦截器
// 可以自请求发送前对请求做一些处理
// config 就是这次请求的所有配置信息，包括 URL、方法、 headers、参数、超时时间等。
// headers['Content-Type'] 里的方括号不是“数组”，而是 JavaScript 访问对象属性的“字符串键”写法，用来绕开变量名语法限制。
request.interceptors.request.use(
  (config) => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8' // 设置统一的数据传输格式为json,设置统一的数据传输编码为UTF-8
    let user = store.state.user.userInfo || {}
    config.headers['token'] = user.token // 在请求头里添加token
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// response拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  (response) => {
    let res = response.data
    // 兼容服务器返回的字符串数据
    // 拿回数据判断是不是string，如果是转换为json
    if (typeof res === 'string') {
      res = res ? JSON.parse(res) : res
    }
    if (res.code === '401') {
      Message.error(res.msg)
      router.push('/login')
    } else {
      return res
    }
  },
  (error) => {
    // 安全检查：先判断 error.response 是否存在
    if (error.response) {
      // 有响应，说明是 HTTP 错误（如 401, 403, 500）
      if (error.response.status === 404) {
        Message.error('未找到请求接口')
      } else if (error.response.status === 500) {
        Message.error('系统异常，请查看后端控制台报错')
      } else {
        Message.error(`请求失败: ${error.response.status}`)
      }
    } else {
      // 无响应，说明是网络错误或超时等
      console.error('网络错误或请求超时:', error.message)
      Message.error('网络错误或请求超时，请检查网络连接')
    }
  }
)

export default request
