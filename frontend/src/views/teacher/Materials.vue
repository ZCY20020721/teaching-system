<template>
  <div>
    <h3>教材管理</h3>
    <el-card style="margin-top:16px">
      <el-upload drag :before-upload="handleUpload" accept=".pdf" :show-file-list="false">
        <el-icon size="48"><UploadFilled /></el-icon>
        <div>点击或拖拽 PDF 教材到此处上传</div>
      </el-upload>
      <div v-if="uploading" style="margin-top:12px"><el-progress :percentage="100" :indeterminate="true" />正在向量化...</div>
      <el-alert v-if="msg" :title="msg" type="success" style="margin-top:12px" closable />
    </el-card>
    <el-card style="margin-top:16px">
      <h4>已导入教材</h4>
      <el-empty v-if="!pdfs.length" description="暂无教材" />
      <el-tag v-for="f in pdfs" :key="f" style="margin:4px">{{ f }}</el-tag>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
const pdfs = ref([])
const uploading = ref(false)
const msg = ref('')
async function handleUpload(file) {
  const formData = new FormData()
  formData.append('file', file.raw)
  uploading.value = true
  await request.post('/teacher/materials/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
  uploading.value = false
  msg.value = `教材 ${file.name} 已加载成功`
  pdfs.value.push(file.name)
}
</script>
