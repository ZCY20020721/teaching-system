<template>
  <div>
    <h3>我的成绩</h3>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8"><el-statistic title="答题次数" :value="records.length" /></el-col>
      <el-col :span="8"><el-statistic title="平均分" :value="avg" :precision="1" /></el-col>
      <el-col :span="8"><el-statistic title="最高分" :value="max" :precision="1" /></el-col>
    </el-row>
    <el-table :data="records" stripe>
      <el-table-column prop="question" label="题目" show-overflow-tooltip />
      <el-table-column prop="totalScore" label="得分" width="80" />
      <el-table-column prop="logicScore" label="逻辑分" width="80" />
      <el-table-column prop="feedback" label="反馈" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
const records = ref([])
const avg = computed(() => records.value.length ? (records.value.reduce((s, r) => s + (r.totalScore || 0), 0) / records.value.length).toFixed(1) : 0)
const max = computed(() => records.value.length ? Math.max(...records.value.map(r => r.totalScore || 0)).toFixed(1) : 0)
onMounted(async () => {
  const r = await request.get('/student/scores?studentId=' + localStorage.getItem('userId'))
  records.value = r.data || []
})
</script>
