<template>
  <div class="app-shell">
    <router-view class="view" />
    <nav v-if="showBottomNav" class="bottom-nav">
      <router-link to="/home" class="nav-item">首页</router-link>
      <router-link to="/appointments" class="nav-item">预约</router-link>
      <router-link to="/agreements" class="nav-item">租约</router-link>
      <router-link to="/user" class="nav-item">我的</router-link>
    </nav>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showBottomNav = computed(() => route.path !== '/login')
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: var(--app-bg);
}
.view {
  padding-bottom: 68px;
}
.bottom-nav {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  bottom: 0;
  width: min(960px, 100%);
  height: 60px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  border-top: 1px solid #e6eaf2;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(8px);
  z-index: 100;
}
.nav-item {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #6b7280;
  text-decoration: none;
  font-size: 14px;
  transition: all .2s ease;
}
.nav-item.router-link-active {
  color: var(--app-primary);
  font-weight: 600;
}
</style>
