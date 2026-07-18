<template>
  <div>
    <h3>学生成绩</h3>
    <el-row :gutter="16">
      <el-col :span="6"><el-statistic title="总答题次数" :value="total" /></el-col>
      <el-col :span="6"><el-statistic title="平均分" :value="avg" :precision="1" /></el-col>
    </el-row>
    <el-divider />
    <el-table :data="records" stripe>
      <el-table-column prop="studentId" label="学生ID" width="80" />
      <el-table-column prop="question" label="题目" show-overflow-tooltip />
      <el-table-column prop="totalScore" label="得分" width="80" />
      <el-table-column prop="feedback" label="反馈" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
    <el-divider />
    <h4>知识点错误统计</h4>
    <div ref="chartRef" style="height:350px"></div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
const records = ref([])
const errorStats = ref([])
const chartRef = ref(null)
const total = computed(() => records.value.length)
const avg = computed(() => records.value.length ? (records.value.reduce((s, r) => s + (r.totalScore || 0), 0) / records.value.length).toFixed(1) : 0)
onMounted(async () => {
  const [r1, r2] = await Promise.all([request.get('/teacher/scores/students'), request.get('/teacher/scores/statistics')])
  records.value = r1.data || []; errorStats.value = r2.data || []
  await nextTick()
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      xAxis: { type: 'category', data: errorStats.value.map(e => e.tag) },
      yAxis: { type: 'value' },
      series: [{ data: errorStats.value.map(e => e.count), type: 'bar', itemStyle: { color: '#e74c3c' } }],
      tooltip: { trigger: 'axis' }
    })
  }
})
</script>
