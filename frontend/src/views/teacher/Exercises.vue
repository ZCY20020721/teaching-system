<template>
  <div>
    <h3>习题生成</h3>
    <el-card style="margin-top:16px">
      <el-form label-width="90px">
        <el-form-item label="教材来源">
          <el-select v-model="selectedMaterialId" placeholder="选择已导入的教材（可选）" clearable style="width:100%"
            @change="loadMaterials">
            <el-option v-for="m in materials" :key="m.id" :label="m.originalName + ' (' + m.chunks + '块)'" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目要求">
          <el-input v-model="requirement" type="textarea" :rows="3"
            placeholder="自定义题目要求（可选），例如：出3道选择题，每题4个选项，考察第3章核心概念" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="generate" :loading="genLoading">
            {{ requirement ? '按需生成考题' : '基于教材生成考题' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

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
        <el-button @click="generate">重新生成</el-button>
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
const materials = ref([])
const currentQuestion = ref(null)
const genLoading = ref(false)
const selectedMaterialId = ref(null)
const requirement = ref('')

onMounted(async () => {
  const [exRes, matRes] = await Promise.all([
    request.get('/teacher/exercises'),
    request.get('/teacher/materials')
  ])
  exercises.value = exRes.data || []
  materials.value = matRes.data || []
})

async function loadMaterials() {
  const res = await request.get('/teacher/materials')
  materials.value = res.data || []
}

async function generate() {
  genLoading.value = true
  const body = {}
  if (selectedMaterialId.value) body.materialId = selectedMaterialId.value
  if (requirement.value.trim()) body.requirement = requirement.value.trim()
  const r = await request.post('/teacher/exercises/generate', body)
  currentQuestion.value = r.data
  genLoading.value = false
}

async function publish() {
  if (!currentQuestion.value) return
  await request.post('/teacher/exercises', currentQuestion.value)
  currentQuestion.value = null
  const r = await request.get('/teacher/exercises')
  exercises.value = r.data || []
}
</script>
