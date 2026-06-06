<template>
  <div class="page">
    <div class="header hero-header">
      <h2 class="page-title">我的预约</h2>
      <div class="statistic">
        总预约数：{{ list.length }}
      </div>
    </div>

    <div v-if="list.length > 0" class="tabs">
      <el-button v-for="status in statusOptions" :key="status.value"
        :type="currentStatus === status.value ? 'primary' : 'info'" :plain="currentStatus !== status.value" size="small"
        @click="currentStatus = status.value">
        {{ status.label }}
      </el-button>
    </div>

    <div class="list">
      <div v-for="item in filteredList" :key="item.id" v-process-image="item" class="appointment-card list-card"
        @click="router.push(`/appointment/${item.id}`)">

        <!-- 图片 -->
        <div class="card-image list-card__img" v-if="item.graphVoList?.length">
          <img :src="item.graphVoList[0].url" alt="apartment" />
        </div>

        <!-- 内容 -->
        <div class="card-content">
          <div class="card-header">
            <h3>{{ item.apartmentName }}</h3>
            <el-tag :type="getStatusType(item.appointmentStatus)" size="small">
              {{ getStatusLabel(item.appointmentStatus) }}
            </el-tag>
          </div>

          <p class="info-item">
            <span class="icon">🕐</span>
            <span class="label">预约时间：</span>
            <span class="value">{{ formatTime(item.appointmentTime) }}</span>
          </p>

        </div>

        <div class="card-arrow">
          <el-icon>
            <ArrowRight />
          </el-icon>
        </div>
      </div>
    </div>

    <el-empty v-if="!list.length" description="暂无预约记录" style="margin-top: 60px" />
  </div>
</template>

<script setup>
  import { onMounted, ref, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { getAppointmentList } from '../api/appointment'
  import { ArrowRight } from '@element-plus/icons-vue'

  const router = useRouter()
  const list = ref([])
  const currentStatus = ref(null)

  const statusOptions = [
    { value: null, label: '全部预约' },
    { value: 1, label: '待看房' },
    { value: 3, label: '已看房' }
  ]

  const statusMap = {
    1: '待看房',
    3: '已看房'
  }

  const filteredList = computed(() => {
    if (currentStatus.value === null) {
      return list.value
    }
    return list.value.filter(item => item.appointmentStatus === currentStatus.value)
  })

  const formatTime = (time) => {
    if (!time) return '-'
    return new Date(time).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const getStatusLabel = (status) => {
    return statusMap[status] || '未知'
  }

  const getStatusType = (status) => {
    const types = { 1: 'warning', 2: 'info', 3: 'success' }
    return types[status] || 'info'
  }

  onMounted(async () => {
    try {
      const res = await getAppointmentList()
      list.value = res.data || []
      console.log(list.value)
    } catch (error) {
      ElMessage.error('获取预约列表失败')
      console.error(error)
    }
  })
</script>

<style scoped>
  .page {
    padding-bottom: 20px;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .page-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }

  .tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
    padding: 0 12px;
    overflow-x: auto;
  }

  .list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0 12px;
  }

  .appointment-card {
    width: 100%;
  }

  .card-image {
    width: 100px; /* 覆盖全局 list-card__img 默认尺寸 */
    height: 100px;
  }

  .card-content {
    flex: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .card-header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
    flex-wrap: wrap;
  }

  .card-header h3 {
    margin: 0;
    font-size: 16px;
    color: var(--app-text);
    font-weight: 600;
    flex: 1;
  }

  .info-item {
    margin: 4px 0;
    font-size: 13px;
    color: var(--app-text-2);
    display: flex;
    align-items: center;
    flex-wrap: wrap;
  }

  .icon {
    margin-right: 6px;
  }

  .label {
    color: var(--app-muted);
    margin-right: 4px;
  }

  .value {
    color: var(--app-text);
    font-weight: 500;
  }

  .card-arrow {
    display: flex;
    align-items: center;
    padding: 0 12px;
    color: #cbd5e1;
    font-size: 20px;
  }

  :deep(.el-statistic__content) {
    color: white;
    font-size: 16px;
  }

  :deep(.el-statistic__label) {
    color: rgba(255, 255, 255, 0.8);
  }
</style>
