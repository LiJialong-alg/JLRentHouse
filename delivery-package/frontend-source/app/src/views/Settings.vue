<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="设置"></el-page-header>

    <div class="settings-container">
      <!-- 通知设置 -->
      <div class="setting-section">
        <h3 class="section-title">通知设置</h3>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">消息通知</p>
            <p class="setting-desc">接收预约、租约等重要消息</p>
          </div>
          <el-switch v-model="notify" />
        </div>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">邮件通知</p>
            <p class="setting-desc">邮件接收通知提醒</p>
          </div>
          <el-switch v-model="emailNotify" />
        </div>
      </div>

      <!-- 登录设置 -->
      <div class="setting-section">
        <h3 class="section-title">登录设置</h3>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">自动登录</p>
            <p class="setting-desc">下次打开时自动登录</p>
          </div>
          <el-switch v-model="autoLogin" />
        </div>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">记住密码</p>
            <p class="setting-desc">保存账户密码便于快速登录</p>
          </div>
          <el-switch v-model="rememberPassword" />
        </div>
      </div>

      <!-- 系统设置 -->
      <div class="setting-section">
        <h3 class="section-title">系统设置</h3>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">深色模式</p>
            <p class="setting-desc">使用深色主题</p>
          </div>
          <el-switch v-model="darkMode" />
        </div>
        <div class="setting-item">
          <div class="setting-content">
            <p class="setting-label">缓存图片</p>
            <p class="setting-desc">自动缓存已浏览的图片</p>
          </div>
          <el-switch v-model="cacheImages" />
        </div>
      </div>

      <!-- 信息提示 -->
      <div class="info-box">
        <el-alert title="所有设置仅保存在当前浏览器本地" type="info" :closable="false" description="清空浏览器缓存会重置所有设置" />
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button @click="resetSettings">恢复默认设置</el-button>
        <el-button type="primary" @click="saveSettings">保存设置</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'

  const router = useRouter()
  const notify = ref(true)
  const emailNotify = ref(false)
  const autoLogin = ref(true)
  const rememberPassword = ref(false)
  const darkMode = ref(false)
  const cacheImages = ref(true)

  const saveSettings = () => {
    const settings = {
      notify: notify.value,
      emailNotify: emailNotify.value,
      autoLogin: autoLogin.value,
      rememberPassword: rememberPassword.value,
      darkMode: darkMode.value,
      cacheImages: cacheImages.value
    }
    localStorage.setItem('appSettings', JSON.stringify(settings))
    ElMessage.success('设置已保存')
  }

  const resetSettings = () => {
    notify.value = true
    emailNotify.value = false
    autoLogin.value = true
    rememberPassword.value = false
    darkMode.value = false
    cacheImages.value = true
    ElMessage.info('已恢复默认设置')
  }
</script>

<style scoped>
  .settings-container {
    margin-top: 12px;
  }

  .setting-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  .section-title {
    margin: 0;
    padding: 12px 16px;
    font-size: 14px;
    font-weight: 600;
    color: #999;
    background: #fafafa;
    border-bottom: 1px solid #f0f0f0;
  }

  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
  }

  .setting-item:last-child {
    border-bottom: none;
  }

  .setting-content {
    flex: 1;
  }

  .setting-label {
    margin: 0;
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }

  .setting-desc {
    margin: 4px 0 0 0;
    font-size: 12px;
    color: #999;
  }

  .info-box {
    padding: 12px;
    margin: 16px 12px;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    padding: 0 12px 16px 12px;
  }

  .action-buttons .el-button {
    flex: 1;
  }
</style>
