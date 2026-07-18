<template>
  <div>
    <h3>习题作答</h3>
    <el-select v-model="selectedId" placeholder="选择习题" style="width:100%;margin-bottom:16px" @change="selectExercise">
      <el-option v-for="ex in exercises" :key="ex.id" :label="'#' + ex.id + ' ' + (ex.question||'').substring(0,60)" :value="ex.id" />
    </el-select>
    <el-card v-if="exercise">
      <h4>{{ exercise.question }}</h4>
      <p>满分：{{ exercise.totalMaxScore }} 分</p>
      <el-input v-model="answer" type="textarea" :rows="5" placeholder="在此作答..." style="margin-top:8px" />
      <el-button type="primary" style="margin-top:12px" @click="submit" :loading="submitting">提交批改</el-button>
    </el-card>
    <el-card v-if="result" style="margin-top:16px">
      <el-row :gutter="16">
        <el-col :span="8"><el-statistic title="总分" :value="result.total_score" /></el-col>
        <el-col :span="8"><el-statistic title="逻辑分" :value="result.logic_score" /></el-col>
        <el-col :span="8"><el-statistic title="要点分" :value="calcPoints(result.step_scores)" /></el-col>
      </el-row>
      <el-divider />
      <h4>逐点评分</h4>
      <div v-for="(s, i) in result.step_scores" :key="i" style="margin:8px 0;padding:8px;background:#f5f5f5;border-radius:4px">
        <el-tag :type="s.score>=4?'success':s.score>=2?'warning':'danger'">要点{{ i+1 }}: {{ s.score }}/5</el-tag>
        <p style="margin-top:4px">{{ s.comment }}</p>
      </div>
      <el-alert :title="result.feedback" type="info" style="margin-top:12px" />
      <el-tag v-for="t in result.weak_tags" :key="t" type="warning" style="margin:4px">{{ t }}</el-tag>
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const exercises = ref([]); const selectedId = ref(null); const exercise = ref(null)
const answer = ref(''); const result = ref(null); const submitting = ref(false)
const userId = localStorage.getItem('userId')

onMounted(async () => { const r = await request.get('/student/exercises'); exercises.value = r.data || [] })
function selectExercise() {
  exercise.value = exercises.value.find(e => e.id === selectedId.value)
  result.value = null
}
async function submit() {
  if (!answer.value.trim()) return
  submitting.value = true
  const r = await request.post('/student/answers/' + selectedId.value, { studentAnswer: answer.value, studentId: userId })
  result.value = r.data
  submitting.value = false
}
function calcPoints(scores) { return (scores || []).reduce((s, x) => s + (x.score || 0), 0) }
</script>
