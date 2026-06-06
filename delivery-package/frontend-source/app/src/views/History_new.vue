<template>
    <div class="page">
        <div class="header">
            <h2 class="page-title">浏览历史</h2>
            <!-- <el-button v-if="list.length" link @click="clearHistory">清空历史</el-button> -->
        </div>

        <div class="list">
            <div v-for="item in list" :key="item.id" class="history-card" @click="goRoom(item.id)">
                <div class="card-image" v-if="item.graphVoList?.length">
                    <img :src="item.graphVoList[0].url" alt="room" />
                </div>
                <div class="card-content">
                    <h3>{{ item.roomNumber }} 号房间</h3>
                    <p class="apartment">{{ item.apartmentInfo?.name }}</p>
                    <p class="price">¥{{ item.rent }}/月</p>
                    <p class="time">{{ formatTime(item.viewTime) }}</p>
                </div>
                <el-icon class="arrow">
                    <ArrowRight />
                </el-icon>
            </div>
        </div>

        <el-empty v-if="!list.length" description="暂无浏览历史" style="margin-top: 60px" />
    </div>
</template>

<script setup>
    import { onMounted, ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    import { ArrowRight } from '@element-plus/icons-vue'
    import { getHistoryPage } from '../api/history'

    const router = useRouter()
    const list = ref([])

    const formatTime = (time) => {
        if (!time) return ''
        const date = new Date(time)
        return date.toLocaleDateString('zh-CN', {
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    const goRoom = (id) => {
        router.push(`/room/${id}`)
    }

    // const clearHistory = () => {
    //     ElMessageBox.confirm('确定要清空所有浏览历史吗？', '提示', {
    //         confirmButtonText: '确定',
    //         cancelButtonText: '取消',
    //         type: 'warning'
    //     }).then(() => {
    //         list.value = []
    //         localStorage.removeItem('roomHistory')
    //         ElMessage.success('浏览历史已清空')
    //     }).catch(() => { })
    // }

    onMounted(async () => {
        try {
            const res = await getHistoryPage(1, 50)
            list.value = res.data?.records || []
        } catch (_) {
            list.value = []
        }
    })
</script>

<style scoped>
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 12px;
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

    .list {
        display: flex;
        flex-direction: column;
        gap: 12px;
        padding: 0 12px;
    }

    .history-card {
        display: flex;
        gap: 12px;
        background: white;
        border-radius: 12px;
        overflow: hidden;
        cursor: pointer;
        border: 1px solid #f0f0f0;
        transition: all 0.3s ease;
    }

    .history-card:active {
        transform: scale(0.98);
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

    .card-content h3 {
        margin: 0 0 6px 0;
        font-size: 16px;
        color: #333;
    }

    .apartment {
        margin: 0;
        font-size: 12px;
        color: #999;
    }

    .price {
        margin: 6px 0 0 0;
        font-size: 14px;
        color: #ff6b6b;
        font-weight: 600;
    }

    .time {
        margin: 4px 0 0 0;
        font-size: 12px;
        color: #ccc;
    }

    .arrow {
        display: flex;
        align-items: center;
        padding: 0 12px;
        color: #ddd;
    }
</style>
