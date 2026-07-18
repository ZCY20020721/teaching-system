import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  {
    path: '/teacher',
    component: () => import('@/views/teacher/TeacherLayout.vue'),
    meta: { role: 'teacher' },
    children: [
      { path: '', redirect: '/teacher/materials' },
      { path: 'materials', name: 'TeacherMaterials', component: () => import('@/views/teacher/Materials.vue') },
      { path: 'exercises', name: 'TeacherExercises', component: () => import('@/views/teacher/Exercises.vue') },
      { path: 'scores', name: 'TeacherScores', component: () => import('@/views/teacher/Scores.vue') },
      { path: 'chat', name: 'TeacherChat', component: () => import('@/views/teacher/Chat.vue') },
    ]
  },
  {
    path: '/student',
    component: () => import('@/views/student/StudentLayout.vue'),
    meta: { role: 'student' },
    children: [
      { path: '', redirect: '/student/answer' },
      { path: 'answer', name: 'StudentAnswer', component: () => import('@/views/student/Answer.vue') },
      { path: 'scores', name: 'StudentScores', component: () => import('@/views/student/Scores.vue') },
      { path: 'errors', name: 'StudentErrors', component: () => import('@/views/student/Errors.vue') },
      { path: 'chat', name: 'StudentChat', component: () => import('@/views/student/Chat.vue') },
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (!token && to.path !== '/login') return next('/login')
  if (token && to.path === '/login') return next('/')
  if (to.meta.role && to.meta.role !== role) {
    return next(role === 'teacher' ? '/teacher' : '/student')
  }
  next()
})
export default router
