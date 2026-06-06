<template>
  <div class="login-page">
    <div class="panel">
      <h1>JL 租房平台</h1>
      <el-form label-position="top">
        <el-form-item label="手机号">
          <el-input v-model="phone" maxlength="11" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="code-row">
            <el-input v-model="code" maxlength="6" placeholder="请输入短信验证码" />
            <el-button :disabled="countdown > 0 || !validPhone" @click="onCode">{{ countdown ? `${countdown}s` : '获取验证码'
            }}</el-button>
          </div>
        </el-form-item>
        <el-button type="primary" class="full" :loading="userStore.loading" :disabled="!validForm"
          @click="onLogin">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
  import { computed, ref, onUnmounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { useUserStore } from '../store/user'

  const phone = ref('')
  const code = ref('')
  const countdown = ref(0)
  let timer = null
  const userStore = useUserStore()
  const router = useRouter()

  const validPhone = computed(() => /^1[3-9]\d{9}$/.test(phone.value))
  const validForm = computed(() => validPhone.value && code.value.length === 6)

  const onCode = async () => {
    await userStore.getCode(phone.value)
    ElMessage.success('验证码已发送')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  }

  const onLogin = async () => {
    await userStore.login({ phone: phone.value, code: code.value })
    ElMessage.success('登录成功')
    router.push('/home')
  }

  onUnmounted(() => timer && clearInterval(timer))
</script>

<style scoped>
  .login-page {
    min-height: 100vh;
    display: grid;
    place-items: center;
    background: linear-gradient(140deg, #2563eb, #38bdf8);
  }

  .panel {
    width: min(460px, 92vw);
    background: #fff;
    border-radius: 18px;
    padding: 28px;
    box-shadow: 0 20px 44px rgba(30, 64, 175, .25);
  }

  .code-row {
    display: grid;
    grid-template-columns: 1fr auto;
    gap: 12px;
  }

  .full {
    width: 100%;
  }

  h1 {
    margin: 0 0 18px;
    font-size: 28px;
    color: #0f172a;
  }
</style>
