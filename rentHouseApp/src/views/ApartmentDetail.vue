<template>
  <div class="page">
    <el-page-header title="返回" @back="router.back()" content="公寓详情"></el-page-header>

    <div v-if="apartment" v-process-image="apartment" class="apartment-container">
      <!-- 图片轮播 -->
      <div class="gallery-section card" v-if="apartment.graphVoList?.length">
        <el-carousel height="280px" indicator-position="outside">
          <el-carousel-item v-for="img in apartment.graphVoList" :key="img.id || img.url">
            <img class="carousel-img" :src="img.url" alt="apartment" />
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 基本信息 -->
      <div class="info-section card">
        <h2 class="apartment-name">{{ apartment.name }}</h2>
        <p class="address">📍 {{ apartment.addressDetail }}</p>
        <p v-if="apartment.phone" class="phone">📞 {{ apartment.phone }}</p>

        <div class="price-range" v-if="apartment.minRent">
          <span class="label">最低房租:</span>
          <span class="price">¥{{ apartment.minRent }}/月</span>
        </div>

        <p v-if="apartment.introductionInfo" class="introduction">{{ apartment.introductionInfo }}</p>
      </div>

      <!-- 标签 -->
      <div v-if="apartment.labelInfoList?.length" class="tags-section card">
        <h3 class="section-title">公寓标签</h3>
        <el-space wrap>
          <el-tag v-for="label in apartment.labelInfoList" :key="label.id || label.name" type="info" effect="light">{{
            label.name }}</el-tag>
        </el-space>
      </div>

      <!-- 房间列表 -->
      <div class="rooms-section card">
        <h3 class="section-title">可租房间 ({{ rooms.length }})</h3>
        <div class="rooms-grid">
          <div v-for="room in rooms" :key="room.id" v-process-image="room" class="room-item"
            @click="router.push(`/room/${room.id}`)">
            <div class="room-image">
              <img :src="room.graphVoList?.[0]?.url || 'https://via.placeholder.com/200x150?text=No+Image'"
                alt="room" />
              <div class="rent-badge">¥{{ room.rent }}/月</div>
            </div>
            <div class="room-details">
              <h4>{{ room.roomNumber }} 号房间</h4>
              <p v-if="room.attrValueVoList?.length" class="attrs">
                <span v-for="(attr, i) in room.attrValueVoList.slice(0, 2)" :key="attr.id || i">
                  {{ attr.name }}<span v-if="i < room.attrValueVoList.length - 1"> · </span>
                </span>
              </p>
              <el-button link type="primary" size="small">查看详情 →</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { getApartmentDetail, getRoomsByApartmentId } from '../api/apartment'
  import { ElMessage } from 'element-plus'

  const route = useRoute()
  const router = useRouter()
  const apartment = ref(null)
  const rooms = ref([])

  onMounted(async () => {
    try {
      apartment.value = (await getApartmentDetail(route.params.id)).data
      const roomRes = await getRoomsByApartmentId(route.params.id)
      rooms.value = roomRes.data?.records || []
    } catch (error) {
      ElMessage.error('加载公寓详情失败')
      console.error(error)
    }
  })
</script>

<style scoped>
  .apartment-container {
    margin-top: 12px;
  }

  .gallery-section {
    overflow: hidden;
    margin-bottom: 12px;
    padding: 0;
  }

  .carousel-img {
    width: 100%;
    height: 280px;
    object-fit: cover;
    display: block;
  }

  .info-section {
    margin-bottom: 12px;
  }

  .apartment-name {
    margin: 0 0 8px 0;
    font-size: 22px;
    font-weight: 600;
    color: var(--app-text);
  }

  .address {
    margin: 4px 0;
    color: var(--app-text-2);
    font-size: 14px;
  }

  .phone {
    margin: 4px 0;
    color: var(--app-text-2);
    font-size: 14px;
  }

  .price-range {
    margin: 12px 0;
    padding: 8px 0;
    border-top: 1px solid var(--app-border);
    border-bottom: 1px solid var(--app-border);
  }

  .price-range .label {
    color: var(--app-muted);
    font-size: 14px;
    margin-right: 8px;
  }

  .price {
    color: var(--app-primary);
    font-weight: 600;
    font-size: 16px;
  }

  .introduction {
    margin: 12px 0 0 0;
    color: var(--app-text-2);
    font-size: 14px;
    line-height: 1.6;
  }

  .tags-section,
  .rooms-section {
    margin-bottom: 12px;
  }

  .section-title {
    margin: 0 0 16px 0;
    font-size: 16px;
    font-weight: 600;
    color: var(--app-text);
  }

  .rooms-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .room-item {
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid var(--app-border);
    transition: all 0.3s ease;
  }

  .room-item:active {
    transform: scale(0.98);
  }

  .room-image {
    position: relative;
    width: 100%;
    padding-bottom: 75%;
    overflow: hidden;
    background: #eef2f7;
  }

  .room-image img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .rent-badge {
    position: absolute;
    bottom: 6px;
    right: 6px;
    background: rgba(37, 99, 235, 0.92);
    color: white;
    padding: 3px 6px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 600;
  }

  .room-details {
    padding: 8px;
  }

  .room-details h4 {
    margin: 0 0 6px 0;
    font-size: 14px;
    font-weight: 600;
    color: var(--app-text);
  }

  .attrs {
    margin: 0 0 8px 0;
    font-size: 12px;
    color: var(--app-muted);
  }

  :deep(.el-tag) {
    border-radius: 20px;
  }
</style>
