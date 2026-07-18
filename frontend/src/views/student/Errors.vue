<template>
  <div>
    <h3>错题集</h3>
    <div ref="chartRef" style="height:350px"></div>
    <el-divider />
    <h4>错题回顾</h4>
    <el-empty v-if="!weakRecords.length" description="暂无错题" />
    <el-collapse v-else>
      <el-collapse-item v-for="(r, i) in weakRecords" :key="i" :title="'题' + (i+1)">
        <p><b>题目：</b>{{ r.question }}</p>
        <p><b>你的答案：</b>{{ r.studentAnswer }}</p>
        <p><b>得分：</b>{{ r.totalScore }}</p>
        <p><b>反馈：</b>{{ r.feedback }}</p>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
const errorStats = ref([])
const weakRecords = ref([])
const chartRef = ref(null)
onMounted(async () => {
  const uid = localStorage.getItem('userId')
  const [r1, r2] = await Promise.all([
    request.get('/student/errors?studentId=' + uid),
    request.get('/student/scores?studentId=' + uid)
  ])
  errorStats.value = r1.data || []
  const allRecords = r2.data || []
  weakRecords.value = allRecords.filter(r => r.weakTags && r.weakTags !== '[]')
  await nextTick()
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      title: { text: '个人薄弱知识点' },
      xAxis: { type: 'category', data: errorStats.value.map(e => e.tag) },
      yAxis: { type: 'value' },
      series: [{ data: errorStats.value.map(e => e.count), type: 'bar', itemStyle: { color: '#e74c3c' } }],
      tooltip: { trigger: 'axis' }
    })
  }
})
</script>
