import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
  { path: '/home', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: '首页', requiresAuth: true } },
  { path: '/room/:id', name: 'RoomDetail', component: () => import('../views/RoomDetail.vue'), meta: { title: '房源详情', requiresAuth: true } },
  { path: '/room/appointment/:id', name: 'RoomAppointment', component: () => import('../views/RoomAppointment.vue'), meta: { title: '预约看房', requiresAuth: true } },
  { path: '/apartment/:id', name: 'ApartmentDetail', component: () => import('../views/ApartmentDetail.vue'), meta: { title: '公寓详情', requiresAuth: true } },
  { path: '/appointments', name: 'Appointments', component: () => import('../views/Appointments.vue'), meta: { title: '我的预约', requiresAuth: true } },
  { path: '/appointment/:id', name: 'AppointmentDetail', component: () => import('../views/AppointmentDetail.vue'), meta: { title: '预约详情', requiresAuth: true } },
  { path: '/history', name: 'History', component: () => import('../views/History_new.vue'), meta: { title: '浏览历史', requiresAuth: true } },
  { path: '/agreements', name: 'Agreements', component: () => import('../views/Agreements.vue'), meta: { title: '我的租约', requiresAuth: true } },
  { path: '/agreement/:id', name: 'AgreementDetail', component: () => import('../views/AgreementDetail.vue'), meta: { title: '租约详情', requiresAuth: true } },
  { path: '/user', name: 'UserInfo', component: () => import('../views/UserInfo.vue'), meta: { title: '个人中心', requiresAuth: true } },
  { path: '/settings', name: 'Settings', component: () => import('../views/Settings.vue'), meta: { title: '设置', requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) return '/login'
  if (to.path === '/login' && token) return '/home'
  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '租房平台'} - 租房平台`
})

export default router
