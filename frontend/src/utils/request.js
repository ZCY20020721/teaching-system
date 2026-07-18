import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({ baseURL: 'http://localhost:8080/api/v1', timeout: 120000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, err => Promise.reject(err))

request.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.clear()
      router.push('/login')
    }
    const msg = err.response?.data?.message || err.message || '请求失败'
    if (err.response?.status === 403) {
      ElMessage.error('权限不足，请重新登录')
      return Promise.reject(err)
    }
    ElMessage.error(msg)
    return Promise.reject(err)
  }
)
export default request
