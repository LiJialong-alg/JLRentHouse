<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="预约详情"></el-page-header>
    <div v-if="detail" v-process-image="detail" class="detail-container">

      <!-- 公寓图片和基本信息 -->
      <div class="apartment-section" v-process-image="detail.apartmentItemVo">
        <el-carousel height="240px" v-if="detail.apartmentItemVo?.graphVoList?.length">
          <el-carousel-item v-for="img in detail.apartmentItemVo.graphVoList" :key="img.id || img.url">
            <img class="carousel-img" :src="img.url" alt="apartment" />
          </el-carousel-item>
        </el-carousel>

        <div class="apartment-info">
          <h2>{{ detail.apartmentItemVo?.name }}</h2>
          <p class="address">📍 {{ detail.apartmentItemVo?.addressDetail }}</p>
          <p v-if="detail.apartmentItemVo?.phone" class="phone">📞 {{ detail.apartmentItemVo?.phone }}</p>
        </div>
      </div>

      <!-- 预约信息 -->
      <div class="section">
        <h3 class="section-title">预约信息</h3>
        <div class="info-list">
          <div class="info-item">
            <span class="label">预约用户:</span>
            <span class="value">{{ detail.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">预约状态:</span>
            <el-tag :type="getStatusType(detail.appointmentStatus)">
              {{ getStatusLabel(detail.appointmentStatus) }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">预约时间:</span>
            <span class="value">{{ formatTime(detail.appointmentTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话:</span>
            <span class="value">{{ detail.phone }}</span>
          </div>
          <div v-if="detail.additionalInfo" class="info-item">
            <span class="label">备注说明:</span>
            <span class="value">{{ detail.additionalInfo }}</span>
          </div>
        </div>
      </div>

      <!-- 公寓详情 -->
      <div v-if="detail.apartmentItemVo" class="section">
        <h3 class="section-title">公寓详情</h3>
        <div class="apartment-details">
          <p v-if="detail.apartmentItemVo.minRent">
            <span class="label">租金范围:</span>
            <span class="price">¥{{ detail.apartmentItemVo.minRent }}-¥{{ detail.apartmentItemVo.maxRent }}/月</span>
          </p>
          <p v-if="detail.apartmentItemVo.introductionInfo">
            <span class="label">公寓介绍:</span>
            <span class="value">{{ detail.apartmentItemVo.introductionInfo }}</span>
          </p>
        </div>
      </div>

      <!-- 公寓标签 -->
      <div v-if="detail.apartmentItemVo?.labelInfoList?.length" class="section">
        <h3 class="section-title">公寓标签</h3>
        <el-space wrap>
          <el-tag v-for="label in detail.apartmentItemVo.labelInfoList" :key="label.id || label.name" type="info"
            effect="light">
            {{ label.name }}
          </el-tag>
        </el-space>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="viewApartment" style="flex: 1">查看公寓详情</el-button>
        <el-button @click="router.back()" style="flex: 1">返回</el-button>
      </div>
    </div>

    <el-empty v-else description="加载中..." />
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { getAppointmentDetail } from '../api/appointment'

  const route = useRoute()
  const router = useRouter()
  const detail = ref(null)

  const statusMap = {
    1: '待看房',
    2: '已取消',
    3: '已看房'
  }

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

  const viewApartment = () => {
    const id = detail.value?.apartmentItemVo?.id || detail.value?.apartmentId
    if (id) {
      router.push(`/apartment/${id}`)
    }
  }

  onMounted(async () => {
    try {
      const res = await getAppointmentDetail(route.params.id)
      detail.value = res.data
    } catch (error) {
      console.error('获取预约详情失败', error)
    }
  })
</script>

<style scoped>
  .page {
    padding-bottom: 20px;
  }

  .detail-container {
    margin-top: 12px;
  }

  .apartment-section {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  .carousel-img {
    width: 100%;
    height: 240px;
    object-fit: cover;
    display: block;
  }

  .apartment-info {
    padding: 16px;
  }

  .apartment-info h2 {
    margin: 0 0 8px 0;
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }

  .address {
    margin: 4px 0;
    color: #666;
    font-size: 14px;
  }

  .phone {
    margin: 4px 0;
    color: #666;
    font-size: 14px;
  }

  .section {
    background: white;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  .section-title {
    margin: 0 0 16px 0;
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }

  .info-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .info-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
  }

  .info-item:last-child {
    border-bottom: none;
  }

  .label {
    min-width: 80px;
    color: #999;
    font-size: 14px;
    font-weight: 500;
  }

  .value {
    flex: 1;
    color: #333;
    font-size: 14px;
    word-break: break-all;
  }

  .price {
    color: #ff6b6b;
    font-weight: 600;
    font-size: 16px;
  }

  .apartment-details {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .apartment-details p {
    margin: 0;
    font-size: 14px;
    display: flex;
    align-items: flex-start;
  }

  .action-buttons {
    display: flex;
    gap: 12px;
    padding: 0 12px;
  }

  :deep(.el-tag) {
    border-radius: 20px;
  }
</style>
