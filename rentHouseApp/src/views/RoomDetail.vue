<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="房源详情"></el-page-header>
    <div class="card" v-if="room" v-process-image="room">
      <!-- 图片轮播 -->
      <el-carousel height="280px" indicator-position="outside">
        <el-carousel-item v-for="img in room.graphVoList || []" :key="img.id || img.url">
          <img class="full" :src="img.url" alt="room" />
        </el-carousel-item>
      </el-carousel>

      <!-- 基本信息 -->
      <div class="info-header">

        <div>
          <h2 class="room-title">{{ room.roomNumber }} 号房间</h2>
          <p class="apartment-name">{{ room.apartmentItemVo?.name }}</p>
        </div>

        <div class="price-box">
          <span class="price">¥ {{ room.rent }}</span>
          <span class="unit">/月</span>
        </div>
      </div>
      <p class="address">📍 {{ room.apartmentItemVo?.addressDetail }}</p>




      <!-- 标签 -->
      <div v-if="room.labelInfoList?.length" class="section">
        <el-space wrap>
          <el-tag v-for="label in room.labelInfoList" :key="label.id || label.name" type="info">
            {{ label.name }}
          </el-tag>
        </el-space>
      </div>

      <!-- 房间属性 -->
      <div v-if="room.attrValueVoList?.length" class="section">
        <h3 class="section-title">房间特性</h3>
        <div class="attr-grid">
          <div v-for="attr in room.attrValueVoList" :key="attr.id || attr.attrKeyName" class="attr-item">
            <span class="attr-label">{{ attr.attrKeyName }}</span>
            <span class="attr-value">{{ attr.name }}</span>
          </div>
        </div>
      </div>

      <!-- 配套设施 -->
      <div v-if="room.facilityInfoList?.length" class="section">
        <h3 class="section-title">配套设施</h3>
        <el-space wrap>
          <el-tag v-for="facility in room.facilityInfoList" :key="facility.id || facility.name" type="success"
            effect="light">
            🏠 {{ facility.name }}
          </el-tag>
        </el-space>
      </div>

      <!-- 租期选项 -->
      <div v-if="room.leaseTermList?.length" class="section">
        <h3 class="section-title">租期选择</h3>
        <el-space wrap>
          <el-tag v-for="term in room.leaseTermList" :key="term.id || term.monthCount" type="warning">
            {{ term.monthCount }}个月
          </el-tag>
        </el-space>
      </div>

      <!-- 杂费信息 -->
      <div v-if="room.feeValueVoList?.length" class="section">
        <h3 class="section-title">其他费用</h3>
        <div class="fee-list">
          <div v-for="fee in room.feeValueVoList" :key="fee.feeKeyId" class="fee-item">
            <span>{{ fee.feeKeyName }}</span>
            <span class="fee-value">¥ {{ fee.name }} {{ fee.unit }}</span>
          </div>
        </div>
      </div>

      <!-- 支付方式 -->
      <div v-if="room.paymentTypeList?.length" class="section">
        <h3 class="section-title">支付方式</h3>
        <el-space wrap>
          <el-tag v-for="payment in room.paymentTypeList" :key="payment.id || payment.name">
            💳 {{ payment.name }}
          </el-tag>
        </el-space>
      </div>

      <!-- 按钮 -->
      <div class="button-group">
        <el-button type="primary" size="large"
          @click="router.push(`/room/appointment/${room.apartmentItemVo?.id || room.apartmentId}`)"
          style="flex: 1">立即预约看房</el-button>
        <el-button size="large" @click="toApartment" style="flex: 1">查看公寓</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { getRoomDetail } from '../api/room'

  const route = useRoute()
  const router = useRouter()
  const room = ref(null)

  const toApartment = () => {
    const id = room.value?.apartmentItemVo?.id || room.value?.apartmentId
    if (id) router.push(`/apartment/${id}`)
  }

  onMounted(async () => {
    try {
      const res = await getRoomDetail(route.params.id)
      room.value = res.data
      console.log(room.value)
    } catch (error) {
      console.error('获取房间详情失败', error)
    }
  })
</script>

<style scoped>
  .card {
    margin-top: 12px;
    padding-bottom: 20px;
  }

  .full {
    width: 100%;
    height: 280px;
    object-fit: cover;
    display: block;
  }

  .info-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 16px;
    background: rgba(15, 23, 42, 0.03);
    margin-top: 8px;
    border-radius: 8px;
  }


  .room-title {
    color: var(--app-primary);
    margin: 0 0 8px 0;
    font-size: 22px;
    font-weight: 600;
  }

  .apartment-name {
    margin: 0 0 4px 0;
    font-size: 14px;
    color: var(--app-text-2);
    font-weight: 500;
  }

  .address {
    margin: 4px 0;
    color: var(--app-muted);
    font-size: 13px;
  }

  .price-box {
    text-align: right;
  }

  .price {
    font-size: 28px;
    color: var(--app-primary);
    font-weight: bold;
    display: inline;
  }

  .unit {
    color: var(--app-muted);
    font-size: 14px;
    margin-left: 4px;
  }

  .section {
    padding: 16px;
    border-bottom: 1px solid var(--app-border);
  }

  .section-title {
    margin: 0 0 12px 0;
    font-size: 16px;
    font-weight: 600;
    color: var(--app-text);
  }

  .attr-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .attr-item {
    display: flex;
    flex-direction: column;
    padding: 12px;
    background: rgba(15, 23, 42, 0.03);
    border-radius: 8px;
    text-align: center;
  }

  .attr-label {
    font-size: 12px;
    color: var(--app-muted);
    margin-bottom: 6px;
  }

  .attr-value {
    font-size: 14px;
    font-weight: 600;
    color: var(--app-text);
  }

  .fee-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .fee-item {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background: rgba(15, 23, 42, 0.03);
    border-radius: 6px;
    font-size: 14px;
  }

  .fee-value {
    color: var(--app-primary);
    font-weight: 600;
  }

  .button-group {
    display: flex;
    gap: 12px;
    padding: 0 16px;
    margin-top: 16px;
  }

  :deep(.el-tag) {
    border-radius: 20px;
  }
</style>
