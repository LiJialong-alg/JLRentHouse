import axios from 'axios'
import { ElMessage } from 'element-plus'

// 获取当前访问的主机名和端口
const getServerBaseURL = () => {

  // // 如果是本地开发环境
  // if (location.hostname === 'localhost' || location.hostname === '127.0.0.1') {
  //   // 开发环境下使用 /app 相对路径
  //   return '/app'
  // }

  // 生产环境或真机测试：使用当前页面的主机地址
  const protocol = location.protocol
  const host = location.hostname
  const port = '8081' // 后端应用端口
  return `${protocol}//${host}:${port}/app`
}

const request = axios.create({
  baseURL: getServerBaseURL(),
  timeout: 5000
})

request.interceptors.request.use((config) => {
  // 登录相关接口不需要添加token
  if (!config.url.includes('/login')) {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res?.code === 200) return res

    if (res?.code === 501) {
      localStorage.removeItem('token')
      if (location.pathname !== '/login' && location.pathname !== '/') location.href = '/login'
      ElMessage.error('会话过期，请重新登录')
      return Promise.reject(new Error('UNAUTHORIZED'))
    }

    // 处理403禁止访问（账号被停用）
    if (res?.code === 403) {
      localStorage.removeItem('token')
      if (location.pathname !== '/login' && location.pathname !== '/') location.href = '/login'
      ElMessage.error(res?.message || '你的账号已被禁止登录，请联系管理员')
      return Promise.reject(new Error('FORBIDDEN'))
    }

    ElMessage.error(res?.message || 'Request failed')
    return Promise.reject(new Error(res?.message || 'REQUEST_FAILED'))
  },
  (error) => {
    // 处理HTTP状态码403
    if (error.response?.status === 403) {
      localStorage.removeItem('token')
      if (location.pathname !== '/login' && location.pathname !== '/') location.href = '/login'
      const errorMsg = error.response?.data?.message || '你的账号已被禁止登录，请联系管理员'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error('FORBIDDEN'))
    }
    ElMessage.error(error.message || 'Network error')
    return Promise.reject(error)
  }
)

export default request
