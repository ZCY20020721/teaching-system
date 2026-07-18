<template>
  <div style="display:flex;height:calc(100vh - 120px);gap:16px">
    <div style="width:200px;background:white;border-radius:8px;padding:8px;overflow-y:auto">
      <h4>教师列表</h4>
      <div v-for="t in contacts" :key="t.id" @click="selectContact(t)"
           :style="{padding:'8px',cursor:'pointer',borderRadius:'4px',margin:'4px 0',background:selectedId===t.id?'#e6f7e6':'#f5f5f5'}">
        {{ t.username }}
      </div>
    </div>
    <div style="flex:1;background:white;border-radius:8px;display:flex;flex-direction:column">
      <div style="padding:12px;border-bottom:1px solid #eee"><b>{{ selectedName || '选择教师' }}</b></div>
      <div style="flex:1;padding:12px;overflow-y:auto" ref="msgBox">
        <div v-for="m in messages" :key="m.id" :style="{textAlign:m.senderId==userId?'right':'left',margin:'8px 0'}">
          <div style="font-size:11px;color:#999">{{ m.senderName }}</div>
          <span :style="{display:'inline-block',padding:'8px 14px',borderRadius:'8px',maxWidth:'70%',background:m.senderId==userId?'#95EC69':'#fff',border:m.senderId==userId?'none':'1px solid #e4e4e4'}">
            {{ m.content }}
          </span>
        </div>
      </div>
      <div style="padding:12px;border-top:1px solid #eee;display:flex;gap:8px">
        <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="sendMsg" />
        <el-button type="success" @click="sendMsg">send</el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import request from '@/utils/request'
const userId = Number(localStorage.getItem('userId'))
const contacts = ref([]); const messages = ref([])
const selectedId = ref(null); const selectedName = ref(''); const inputText = ref('')
const msgBox = ref(null); let pollTimer = null

onMounted(async () => {
  const r = await request.get('/chat/contacts'); contacts.value = r.data || []
})
async function selectContact(t) {
  selectedId.value = t.id; selectedName.value = t.username
  if (pollTimer) clearInterval(pollTimer)
  await loadMessages()
  pollTimer = setInterval(loadMessages, 2000)
}
async function loadMessages() {
  if (!selectedId.value) return
  const r = await request.get('/chat/messages/' + selectedId.value)
  messages.value = r.data || []
  await nextTick()
  if (msgBox.value) msgBox.value.scrollTop = msgBox.value.scrollHeight
}
async function sendMsg() {
  if (!inputText.value.trim() || !selectedId.value) return
  await request.post('/chat/send', { receiverId: selectedId.value, content: inputText.value })
  inputText.value = ''
  await loadMessages()
}
onBeforeUnmount(() => { if (pollTimer) clearInterval(pollTimer) })
</script>
