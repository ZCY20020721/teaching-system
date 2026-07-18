<template>
  <div>
    <h3>教材管理</h3>
    <el-card style="margin-top:16px">
      <el-upload drag :http-request="handleUpload" accept=".pdf" :show-file-list="false">
        <el-icon size="48"><UploadFilled /></el-icon>
        <div>点击或拖拽 PDF 教材到此处上传</div>
      </el-upload>
      <div v-if="uploading" style="margin-top:12px">
        <el-progress :percentage="100" :indeterminate="true" />正在向量化...
      </div>
      <el-alert v-if="msg" :title="msg" type="success" style="margin-top:12px" closable @close="msg=''" />
      <el-alert v-if="errMsg" :title="errMsg" type="error" style="margin-top:12px" closable @close="errMsg=''" />
    </el-card>

    <el-card style="margin-top:16px">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <h4>已导入教材</h4>
        <el-button size="small" @click="loadMaterials" :loading="loading">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
      <el-empty v-if="!materials.length && !loading" description="暂无教材，请上传 PDF" />
      <el-table v-else :data="materials" stripe style="margin-top:8px">
        <el-table-column prop="originalName" label="文件名" show-overflow-tooltip />
        <el-table-column prop="chunks" label="文本块数" width="100" />
        <el-table-column prop="createdAt" label="导入时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const materials = ref([])
const uploading = ref(false)
const loading = ref(false)
const msg = ref('')
const errMsg = ref('')

onMounted(() => loadMaterials())

async function loadMaterials() {
  loading.value = true
  try {
    const res = await request.get('/teacher/materials')
    materials.value = res.data || []
  } catch (e) { /* ignore */ }
  loading.value = false
}

async function handleUpload(options) {
  const formData = new FormData()
  formData.append('file', options.file)
  uploading.value = true
  msg.value = ''
  errMsg.value = ''
  try {
    const res = await request.post('/teacher/materials/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 300000
    })
    if (res.code === 200) {
      const c = res.data.chunks || '?'
      msg.value = options.file.name + ' 加载成功（' + c + ' 个文本块）'
      await loadMaterials()
    } else {
      errMsg.value = res.message || '上传失败'
    }
  } catch (e) {
    errMsg.value = '请求失败：' + (e.message || '网络错误')
  }
  uploading.value = false
}
</script>
