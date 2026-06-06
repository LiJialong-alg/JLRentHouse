<template>
  <div class="page">
    <div class="header">
      <h2 class="page-title">我的租约</h2>
      <div class="statistic">
        总租约数：{{ list.length }}
      </div>
    </div>

    <div class="list">
      <div v-for="item in list" :key="item.id" v-process-image="item" class="agreement-card"
        @click="router.push(`/agreement/${item.id}`)">

        <!-- 图片 -->
        <div class="card-image" v-if="item.roomGraphVoList?.length">
          <img :src="item.roomGraphVoList[0].url" alt="room" />
        </div>

        <!-- 内容 -->
        <div class="card-content">
          <div class="card-header">
            <h3>{{ item.roomNumber || '-' }}</h3>
            <el-tag :type="getStatusType(item.leaseStatus)" size="small">
              {{ getStatusLabel(item.leaseStatus) }}
            </el-tag>
          </div>

          <p class="info-item">
            <span class="label">公寓：</span>
            <span class="value">{{ item.apartmentName || '-' }}</span>
          </p>

          <p class="info-item">
            <span class="icon">📅</span>
            <span class="label">租期：</span>
            <span class="value">{{ formatDate(item.leaseStartDate) }} ~ {{ formatDate(item.leaseEndDate) }}</span>
          </p>

          <p class="info-item">
            <span class="icon">💰</span>
            <span class="label">租金：</span>
            <span class="value">¥{{ item.rent || '-' }}/月</span>
          </p>
        </div>

        <div class="card-arrow">
          <el-icon>
            <ArrowRight />
          </el-icon>
        </div>
      </div>
    </div>

    <el-empty v-if="!list.length" description="暂无租约记录" style="margin-top: 60px" />
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { getAgreementList } from '../api/agreement'
  import { ArrowRight } from '@element-plus/icons-vue'

  const router = useRouter()
  const list = ref([])
  const statusMap = {
    1: '待签约',
    2: '已签约',
    5: '退租中',
    6: '已退租',
  }


  const formatDate = (date) => {
    if (!date) return '-'
    const d = new Date(date)
    return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
  }

  const getStatusLabel = (status) => {
    return statusMap[status] || status || '未知'
  }

  const getStatusType = (status) => {
    const types = {
      1: 'info', PENDING: 'info',
      2: 'success', SIGNED: 'success',
      3: 'success', COMPLETED: 'success',
      4: 'info', WITHDRAWING: 'warning',
      WITHDRAWN: 'info'
    }
    return types[status] || 'info'
  }

  onMounted(async () => {
    try {
      const res = await getAgreementList()
      list.value = res.data || []
      console.log(list.value)
    } catch (error) {
      ElMessage.error('获取租约列表失败')
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
    padding: 16px;
    background: #79b5fa;
    color: white;
    margin: -12px -12px 16px -12px;
    border-radius: 0 0 12px 12px;
  }

  .page-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }

  .statistic {
    font-size: 14px;
    opacity: 0.9;
  }

  .list {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0 12px;
  }

  .agreement-card {
    display: flex;
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #f0f0f0;
  }

  .agreement-card:active {
    transform: scale(0.98);
    box-shadow: 0 1px 6px rgba(0, 0, 0, 0.1);
  }

  .card-image {
    width: 100px;
    height: 100px;
    flex-shrink: 0;
    overflow: hidden;
    background: #f0f0f0;
  }

  .card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .card-content {
    flex: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
  }

  .card-header h3 {
    margin: 0;
    font-size: 16px;
    color: #333;
    flex: 1;
  }

  .info-item {
    margin: 4px 0;
    font-size: 13px;
    color: #666;
    display: flex;
    align-items: center;
  }

  .icon {
    margin-right: 4px;
  }

  .label {
    color: #999;
    min-width: 60px;
  }

  .value {
    color: #333;
    flex: 1;
  }

  .card-arrow {
    display: flex;
    align-items: center;
    padding-right: 12px;
    color: #999;
  }
</style>
