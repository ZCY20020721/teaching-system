import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const username = ref(localStorage.getItem('username') || '')
  const userId = ref(localStorage.getItem('userId') || '')

  async function login(uname, pwd, selectedRole) {
    const res = await request.post('/auth/login', { username: uname, password: pwd })
    if (res.code === 200) {
      const d = res.data
      token.value = d.token
      role.value = d.role
      username.value = d.username
      userId.value = d.userId
      localStorage.setItem('token', d.token)
      localStorage.setItem('role', d.role)
      localStorage.setItem('username', d.username)
      localStorage.setItem('userId', d.userId)
      return true
    }
    return false
  }

  async function register(uname, pwd, selectedRole) {
    const res = await request.post('/auth/register', { username: uname, password: pwd, role: selectedRole })
    return res.code === 200 ? null : res.message
  }

  function logout() {
    token.value = role.value = username.value = userId.value = ''
    localStorage.clear()
  }

  return { token, role, username, userId, login, register, logout }
})
