<template>
  <div>
    <h3>习题生成</h3>
    <el-button type="success" @click="generate" :loading="genLoading">基于教材生成考题</el-button>
    <el-card v-if="currentQuestion" style="margin-top:16px">
      <h4>{{ currentQuestion.question }}</h4>
      <p>满分：{{ currentQuestion.total_max_score }} 分</p>
      <el-divider />
      <h4>标准答案要点</h4>
      <el-tag v-for="(pt, i) in currentQuestion.standard_answer_points" :key="i" style="margin:4px" type="info">
        {{ pt.tag }}: {{ pt.point }} ({{ pt.max_score }}分)
      </el-tag>
      <div style="margin-top:12px">
        <el-button type="primary" @click="publish">发布此题给学生</el-button>
      </div>
    </el-card>
    <el-divider />
    <h4>已发布习题</h4>
    <el-table :data="exercises" stripe>
      <el-table-column prop="question" label="题目" show-overflow-tooltip />
      <el-table-column prop="totalMaxScore" label="满分" width="80" />
      <el-table-column prop="createdAt" label="发布时间" width="180" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const exercises = ref([])
const currentQuestion = ref(null)
const genLoading = ref(false)
onMounted(async () => { const r = await request.get('/teacher/exercises'); exercises.value = r.data || [] })
async function generate() {
  genLoading.value = true
  const r = await request.post('/teacher/exercises/generate')
  currentQuestion.value = r.data
  genLoading.value = false
}
async function publish() {
  await request.post(`/teacher/exercises?teacherId=${localStorage.getItem('userId')}`, currentQuestion.value)
  currentQuestion.value = null
  const r = await request.get('/teacher/exercises')
  exercises.value = r.data || []
}
</script>
