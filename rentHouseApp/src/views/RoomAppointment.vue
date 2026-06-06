<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="预约看房"></el-page-header>
    <div class="card" v-if="apartmentInfo" v-process-image="apartmentInfo">
      <div class="info-card">
        <h3 class="apartment-name">{{ apartmentInfo.name }}</h3>
        <p class="address">📍 {{ apartmentInfo.addressDetail }}</p>
        <p class="price"><span class="label">最低房租:</span> ¥{{ apartmentInfo.minRent }}/月
        </p>
      </div>
      <el-divider></el-divider>
      <el-form label-position="top" :model="form">
        <el-form-item label="您的姓名" required>
          <el-input v-model="form.name" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="预约时间" required>
          <el-date-picker v-model="form.appointmentTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            style="width:100%" :locale="zhCn" placeholder="选择预约时间" />
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="form.phone" maxlength="11" placeholder="请输入11位手机号" />
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="form.additionalInfo" type="textarea" :rows="3" placeholder="补充说明您的需求" />
        </el-form-item>
      </el-form>
      <el-button type="primary" :loading="loading" @click="submit" style="width:100%">提交预约</el-button>
    </div>
  </div>
</template>

<script setup>
  import { reactive, ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import zhCn from 'element-plus/es/locale/lang/zh-cn'
  import { saveAppointment } from '../api/appointment'
  import { getApartmentDetail } from '../api/apartment'

  const route = useRoute()
  const router = useRouter()
  const loading = ref(false)
  const apartmentInfo = ref(null)

  const form = reactive({
    apartmentId: Number(route.params.id),
    userId: Number(localStorage.getItem('userId')),
    name: '',
    appointmentTime: '',
    phone: '',
    additionalInfo: ''
  })

  const loadApartmentInfo = async () => {
    try {
      const res = await getApartmentDetail(route.params.id)
      apartmentInfo.value = res.data
    } catch (error) {
      console.error('获取公寓信息失败', error)
    }
  }

  const submit = async () => {
    if (!form.name || !form.appointmentTime || !/^1[3-9]\d{9}$/.test(form.phone)) {
      ElMessage.warning('请正确填写姓名、预约时间和手机号')
      return
    }
    loading.value = true
    try {
      await saveAppointment(form)
      ElMessage.success('预约提交成功')
      router.push('/appointments')
    } catch (error) {
      ElMessage.error(error.message || '预约失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    loadApartmentInfo()
  })
</script>

<style scoped>
  .card {
    margin-top: 15px;
  }

  .info-card {
    background: #f5f7fa;
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 16px;
  }

  .apartment-name {
    margin: 0 0 8px 0;
    font-size: 18px;
    color: #333;
  }

  .address {
    margin: 4px 0;
    color: #999;
    font-size: 14px;
  }

  .price {
    margin: 8px 0 0 0;
    color: #ff6b6b;
    font-size: 16px;
    font-weight: bold;
  }

  .label {
    color: #333;
    font-weight: normal;
  }
</style>
