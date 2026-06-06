<template>
  <div class="page">
    <!-- 搜索栏 -->
    <div class="hero-header">
      <h2 class="page-title">精选房源</h2>
      <el-input v-model="keyword" placeholder="搜索房号或公寓名..." clearable :prefix-icon="Search" />
      <div class="filter-tags">
        <el-tag v-if="keyword" closable @close="keyword = ''">{{ keyword }}</el-tag>
      </div>
    </div>

    <!-- 房间列表 -->
    <div class="rooms-container">
      <div v-for="room in displayRooms" :key="room.id" v-process-image="room" class="list-card"
        @click="goRoom(room.id)">
        <!-- 图片 -->
        <div class="list-card__img">
          <img :src="room.graphVoList?.[0]?.url || 'https://via.placeholder.com/300x200?text=No+Image'" alt="room" />
          <div class="badge">¥{{ room.rent }}/月</div>
        </div>

        <!-- 信息 -->
        <div class="room-info">
          <h3>{{ room.roomNumber }} 号房间</h3>
          <p class="apartment-name">{{ room.apartmentInfo?.name || room.apartmentName || '未知公寓' }}</p>
          <p class="address">📍 {{ room.apartmentInfo?.addressDetail || '位置待定' }}</p>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && !displayRooms.length" description="暂无房源" style="margin-top: 60px" />

    <!-- 加载更多 -->
    <div class="load-more">
      <el-button :loading="loading" @click="loadMore" :disabled="!hasMore" style="width: 100%">
        {{ loading ? '加载中...' : hasMore ? '加载更多房源' : '已加载全部房源' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
  import { computed, onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { Search } from '@element-plus/icons-vue'
  import { getRoomPage } from '../api/room'

  const router = useRouter()
  const rooms = ref([])
  const page = ref(1)
  const size = 10
  const hasMore = ref(true)
  const loading = ref(false)
  const keyword = ref('')

  const displayRooms = computed(() => rooms.value.filter((x) => {
    const k = keyword.value.trim().toLowerCase()
    if (!k) return true
    const roomNum = String(x.roomNumber || '').toLowerCase()
    const apartmentName = (x.apartmentInfo?.name || x.apartmentName || '').toLowerCase()
    return roomNum.includes(k) || apartmentName.includes(k)
  }))

  const fetchRooms = async () => {
    loading.value = true
    try {
      const res = await getRoomPage({ current: page.value, size })
      const data = res.data || {}
      rooms.value = page.value === 1 ? (data.records || []) : rooms.value.concat(data.records || [])
      hasMore.value = page.value < (data.pages || 0)
    } catch (error) {
      console.error('加载房源失败', error)
    } finally {
      loading.value = false
    }

  }

  const loadMore = async () => {
    if (!hasMore.value || loading.value) return
    page.value += 1
    await fetchRooms()

  }

  const goRoom = (id) => router.push(`/room/${id}`)
  onMounted(fetchRooms)
</script>

<style scoped>
  .filter-tags {
    display: flex;
    gap: 8px;
    margin-top: 12px;
    flex-wrap: wrap;
  }

  :deep(.hero-header .el-input__wrapper) {
    background: rgba(255, 255, 255, 0.9);
  }

  .rooms-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
    padding: 0 12px;
  }

  .room-info {
    flex: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .room-info h3 {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: var(--app-text);
  }

  .apartment-name {
    margin: 0 0 6px 0;
    font-size: 14px;
    color: var(--app-text-2);
    font-weight: 500;
  }

  .address {
    margin: 0;
    font-size: 12px;
    color: var(--app-muted);
  }

  .load-more {
    padding: 16px 12px;
  }
</style>
