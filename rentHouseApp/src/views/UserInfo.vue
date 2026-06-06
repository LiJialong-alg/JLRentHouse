<template>
  <div class="page">
    <div class="header-section">
      <div class="user-profile">
        <div class="avatar">
          <el-icon>
            <User />
          </el-icon>
        </div>
        <div class="user-info">
          <h2>{{ info.nickname || '用户' }}</h2>
          <!-- <p>ID: {{ info.id || '未知' }}</p> -->
        </div>
      </div>
    </div>

    <div class="menu-section">
      <h3 class="section-title">我的信息</h3>
      <div class="menu-grid">
        <div class="menu-item" @click="router.push('/agreements')">
          <div class="menu-icon rent">📋</div>
          <div class="menu-text">
            <p class="menu-title">我的租约</p>
            <p class="menu-desc">查看租赁协议</p>
          </div>
          <el-icon class="arrow">
            <ArrowRight />
          </el-icon>
        </div>
        <div class="menu-item" @click="router.push('/appointments')">
          <div class="menu-icon appointment">🕐</div>
          <div class="menu-text">
            <p class="menu-title">我的预约</p>
            <p class="menu-desc">查看看房预约</p>
          </div>
          <el-icon class="arrow">
            <ArrowRight />
          </el-icon>
        </div>
        <div class="menu-item" @click="router.push('/history')">
          <div class="menu-icon history">📜</div>
          <div class="menu-text">
            <p class="menu-title">浏览历史</p>
            <p class="menu-desc">查看浏览记录</p>
          </div>
          <el-icon class="arrow">
            <ArrowRight />
          </el-icon>
        </div>
        <div class="menu-item" @click="router.push('/settings')">
          <div class="menu-icon settings">⚙️</div>
          <div class="menu-text">
            <p class="menu-title">设置</p>
            <p class="menu-desc">应用设置</p>
          </div>
          <el-icon class="arrow">
            <ArrowRight />
          </el-icon>
        </div>
      </div>
    </div>

    <div class="logout-section">
      <el-button type="danger" @click="logout" style="width: 100%" size="large">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
  import { computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { User, ArrowRight } from '@element-plus/icons-vue'
  import { useUserStore } from '../store/user'

  const userStore = useUserStore()
  const router = useRouter()
  const info = computed(() => userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const logout = () => {
    userStore.logout()
    router.push('/login')
  }

  onMounted(async () => {
    if (!userStore.userInfo) {
      try { await userStore.fetchUserInfo() } catch (_) { }
    }
  })
</script>

<style scoped>
  .header-section {
    /* 渐变天蓝色 */
    background: #7ab5f8;
    color: white;
    padding: 24px 16px;
    margin: -12px -12px 0 -12px;
    border-radius: 0 0 12px 12px;
  }

  .user-profile {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
  }

  .user-info h2 {
    margin: 0 0 4px 0;
    font-size: 20px;
    font-weight: 600;
  }

  .user-info p {
    margin: 0;
    font-size: 12px;
    opacity: 0.8;
  }

  .menu-section {
    padding: 16px 12px;
  }

  .section-title {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .menu-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: white;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #f0f0f0;
  }

  .menu-item:active {
    transform: scale(0.98);
    background: #f5f7fa;
  }

  .menu-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    font-size: 20px;
  }

  .menu-icon.rent {
    background: #fef0f0;
  }

  .menu-icon.appointment {
    background: #fef3f0;
  }

  .menu-icon.history {
    background: #f0f3ff;
  }

  .menu-icon.settings {
    background: #f0fff4;
  }

  .menu-text {
    flex: 1;
  }

  .menu-title {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: #333;
  }

  .menu-desc {
    margin: 4px 0 0 0;
    font-size: 12px;
    color: #999;
  }

  .arrow {
    color: #ddd;
    font-size: 18px;
  }

  .logout-section {
    padding: 16px 12px;
  }
</style>