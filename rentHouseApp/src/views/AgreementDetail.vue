<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="租约详情"></el-page-header>

    <div v-if="detail" v-process-image="detail" class="container">
      <!-- 房间图片 -->
      <div class="image-gallery" v-if="detail.roomGraphVoList?.length">
        <div class="image-container">
          <img :src="detail.roomGraphVoList[0].url" alt="room" class="main-image" />
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="info-header">
          <div>
            <h2 class="room-number">{{ detail.roomNumber || '-' }}</h2>
            <p class="apartment-name">{{ detail.apartmentName || '-' }}</p>
          </div>
          <el-tag :type="getStatusType(detail.status)" size="large">
            {{ getStatusLabel(detail.status) }}
          </el-tag>
        </div>

        <el-divider />

        <!-- 租期信息 -->
        <div class="info-section">
          <h3 class="section-title">租期信息</h3>
          <div class="info-group">
            <div class="info-row">
              <span class="label">开始日期</span>
              <span class="value">{{ formatDate(detail.leaseStartDate) }}</span>
            </div>
            <div class="info-row">
              <span class="label">结束日期</span>
              <span class="value">{{ formatDate(detail.leaseEndDate) }}</span>
            </div>
            <div class="info-row" v-if="detail.leaseTermMonthCount">
              <span class="label">租期</span>
              <span class="value">{{ detail.leaseTermMonthCount || '-' }} {{ detail.leaseTermUnit || '月' }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 费用信息 -->
        <div class="info-section">
          <h3 class="section-title">费用信息</h3>
          <div class="info-group">
            <div class="info-row">
              <span class="label">月租金</span>
              <span class="value highlight">¥{{ detail.rent || '-' }}</span>
            </div>
            <!-- <div class="info-row">
              <span class="label">押金</span>
              <span class="value">¥{{ detail.deposit || '-' }}</span>
            </div> -->
            <div class="info-row" v-if="detail.paymentTypeName">
              <span class="label">支付方式</span>
              <span class="value">{{ detail.paymentTypeName }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 承租人信息 -->
        <div class="info-section">
          <h3 class="section-title">承租人信息</h3>
          <div class="info-group">
            <div class="info-row" v-if="detail.name">
              <span class="label">姓名</span>
              <span class="value">{{ detail.name || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">手机号</span>
              <span class="value">{{ detail.phone || '-' }}</span>
            </div>
            <div class="info-row" v-if="detail.identificationNumber">
              <span class="label">身份证号</span>
              <span class="value">{{ maskIdNumber(detail.identificationNumber) }}</span>
            </div>
          </div>
        </div>

        <el-divider v-if="detail.additionalInfo" />

        <!-- 备注信息 -->
        <div class="info-section" v-if="detail.additionalInfo">
          <h3 class="section-title">备注信息</h3>
          <p class="remark">{{ detail.additionalInfo }}</p>
        </div>

        <el-divider />

        <!-- 操作按钮 -->
        <div class="actions">
          <el-button v-if="detail.status === 'PENDING' || detail.status === 1" type="success"
            @click="changeStatus('SIGNED')" :loading="loading">
            确认签约
          </el-button>
          <el-button v-if="detail.status === 'SIGNED' || detail.status === 2" type="danger"
            @click="changeStatus('WITHDRAWING')" :loading="loading">
            申请退租
          </el-button>
          <el-button v-else disabled>
            租约已结束
          </el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="加载中..." />
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { getAgreementDetail, updateAgreementStatus } from '../api/agreement'

  const route = useRoute()
  const router = useRouter()
  const detail = ref(null)
  const loading = ref(false)

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

  const maskIdNumber = (idNumber) => {
    if (!idNumber || idNumber.length < 8) return idNumber
    return idNumber.substring(0, 6) + '****' + idNumber.substring(idNumber.length - 2)
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

  const isEndStatus = (status) => {
    return status === 3 || status === 4 || status === 'COMPLETED' || status === 'WITHDRAWN'
  }

  const load = async () => {
    try {
      const res = await getAgreementDetail(route.params.id)
      detail.value = res.data
      console.log(detail.value)
    } catch (error) {
      ElMessage.error('加载租约详情失败')
      console.error(error)
    }
  }

  const changeStatus = async (status) => {
    try {
      const action = status === 'SIGNED' ? '确认签约' : '申请退租'
      const confirmMsg = status === 'SIGNED'
        ? '确认签署此租约吗？'
        : '申请退租后将无法恢复，确认吗？'

      await ElMessageBox.confirm(confirmMsg, '确认操作', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: status === 'SIGNED' ? 'warning' : 'error'
      })

      loading.value = true
      await updateAgreementStatus(route.params.id, status)
      ElMessage.success('操作成功')
      await load()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('操作失败，请重试')
        console.error(error)
      }
    } finally {
      loading.value = false
    }
  }

  onMounted(load)
</script>

<style scoped>
  .page {
    padding-bottom: 20px;
  }

  .container {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    margin: 16px 12px;
  }

  .image-gallery {
    position: relative;
  }

  .image-container {
    width: 100%;
    height: 300px;
    background: #f0f0f0;
    overflow: hidden;
  }

  .main-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .info-card {
    padding: 20px;
  }

  .info-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;
  }

  .room-number {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }

  .apartment-name {
    margin: 0;
    font-size: 14px;
    color: #999;
  }

  .info-section {
    margin-bottom: 16px;
  }

  .section-title {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #333;
  }

  .info-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #f5f5f5;
  }

  .info-row:last-child {
    border-bottom: none;
  }

  .label {
    color: #999;
    font-size: 14px;
  }

  .value {
    color: #333;
    font-size: 14px;
    font-weight: 500;
  }

  .value.highlight {
    color: #ff6b6b;
    font-size: 16px;
    font-weight: 600;
  }

  .remark {
    padding: 12px;
    background: #f5f7fa;
    border-radius: 8px;
    color: #666;
    font-size: 14px;
    line-height: 1.6;
    margin: 0;
  }

  .actions {
    display: flex;
    gap: 12px;
    margin-top: 20px;
  }

  .actions :deep(.el-button) {
    flex: 1;
  }
</style>
