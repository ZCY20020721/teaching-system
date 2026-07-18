<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <div style="color:white;padding:20px;text-align:center;font-size:18px">学生端</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#07C160">
        <el-menu-item index="/student/answer"><el-icon><EditPen /></el-icon>习题作答</el-menu-item>
        <el-menu-item index="/student/scores"><el-icon><Trophy /></el-icon>我的成绩</el-menu-item>
        <el-menu-item index="/student/errors"><el-icon><Warning /></el-icon>错题集</el-menu-item>
        <el-menu-item index="/student/chat"><el-icon><ChatDotRound /></el-icon>师生聊天</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:white;border-bottom:1px solid #e4e4e4;display:flex;align-items:center;justify-content:flex-end">
        <span style="margin-right:16px">{{ store.username }}</span>
        <el-button @click="logout" type="danger" size="small">退出</el-button>
      </el-header>
      <el-main style="background:#f0f2f5"><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
const route = useRoute(); const router = useRouter(); const store = useUserStore()
function logout() { store.logout(); router.push('/login') }
</script>
