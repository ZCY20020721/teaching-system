<template>
  <div style="display:flex;justify-content:center;align-items:center;min-height:100vh;background:#f0f2f5">
    <el-card style="width:420px">
      <h2 style="text-align:center;margin-bottom:20px">智能教学系统</h2>
      <el-radio-group v-model="mode" style="margin-bottom:16px;width:100%;text-align:center">
        <el-radio-button value="login">登录</el-radio-button>
        <el-radio-button value="register">注册</el-radio-button>
      </el-radio-group>
      <el-radio-group v-model="form.role" style="margin-bottom:16px;width:100%;text-align:center">
        <el-radio-button value="student">学生</el-radio-button>
        <el-radio-button value="teacher">教师</el-radio-button>
      </el-radio-group>
      <el-input v-model="form.username" placeholder="用户名" style="margin-bottom:12px" />
      <el-input v-model="form.password" type="password" placeholder="密码" style="margin-bottom:16px"
                @keyup.enter="submit" />
      <el-button type="primary" style="width:100%;background:#07C160;border-color:#07C160" @click="submit" :loading="loading">
        {{ mode === 'login' ? '登录' : '注册' }}
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const mode = ref('login')
const loading = ref(false)
const form = reactive({ username: '', password: '', role: 'student' })

async function submit() {
  if (!form.username || !form.password) return ElMessage.error('请填写用户名和密码')
  loading.value = true
  if (mode.value === 'login') {
    const ok = await store.login(form.username, form.password, form.role)
    if (ok) {
      router.push(form.role === 'teacher' ? '/teacher' : '/student')
    } else {
      ElMessage.error('用户名、密码或角色不匹配')
    }
  } else {
    const err = await store.register(form.username, form.password, form.role)
    err ? ElMessage.error(err) : ElMessage.success('注册成功，请切换到登录')
  }
  loading.value = false
}
</script>
