# 租房系统 - 公寓管理模块详细分析

## 📋 文档概览
本文详细分析了rentHouseAdmin和rentHouseApp中的公寓管理功能，包括地区信息绑定、新增编辑流程、图片上传显示等核心业务逻辑。

---

## 📁 核心文件清单

### 后台管理系统（rentHouseAdmin）

#### 1. **API层** - `src/api/apartmentManagement/`

**types.ts** - 类型定义
```typescript
// 公寓查询接口
interface ApartmentListQueryInterface extends ReqPage {
  provinceId?: number | string
  cityId?: number | string
  districtId?: number | string
}

// 公寓信息接口
interface ApartmentInterface {
  id: number | string
  name: string                                    // 公寓名称
  introduction: string                           // 公寓介绍
  
  // 地区信息 - 关键字段
  provinceId: number | string                   // 省份ID
  provinceName: string                          // 省份名称
  cityId: number | string                       // 城市ID
  cityName: string                              // 城市名称
  districtId: number | string                   // 区域ID
  districtName: string                          // 区域名称
  
  // 地址和坐标
  addressDetail: string                         // 详细地址
  latitude: number | string                    // 纬度
  longitude: number | string                   // 经度
  
  // 基础信息
  phone: string                                // 联系电话
  isRelease: string | number                  // 发布状态
  totalRoomCount: number                      // 房间总数
  freeRoomCount: number                       // 空闲房间数
  
  // 关联信息
  facilityInfoIds?: number[]                  // 配套ID数组
  facilityInfoList?: FacilityInfoInterface[]  // 配套详情
  labelIds?: number[]                         // 标签ID数组
  labelInfoList?: LabelInfoInterface[]        // 标签详情
  feeValueIds?: number[]                      // 杂费ID数组
  feeValueVoList?: FeeValueInfoInterface[]    // 杂费详情
  
  // 图片信息 - 重要
  graphVoList?: Array<{ url: string; name: number }> | UploadFile[]
}

// 地区接口
interface RegionInterface {
  id: number
  name: string
}
```

**index.ts** - API函数
```typescript
// 获取公寓分页列表
getApartmentList(params: ApartmentListQueryInterface)
  → GET /admin/apartment/pageItem
  → 参数：current, size, provinceId?, cityId?, districtId?
  → 返回：PageRes<ApartmentInterface[]>

// 地区API
getProvinceList()     → GET /admin/region/province/list
getCityList(provinceId)          → GET /admin/region/city/listByProvinceId?id={id}
getDistrictList(cityId)          → GET /admin/region/district/listByCityId?id={id}

// 公寓操作API
getApartmentById(id)             → 获取公寓详情
saveOrUpdateApartment(params)    → POST /admin/apartment/saveOrUpdate
deleteApartmentById(id)          → DELETE /admin/apartment/removeById?id={id}
updateApartmentReleaseStatus(id, status) → POST 更新发布状态

// 配套/标签/杂费API
getFacilityInfoList(type?)       → GET /admin/facility/list
getLabelInfoList(type?)          → GET /admin/label/list
getFeeInfoList()                 → GET /admin/fee/list
```

#### 2. **列表页** - `src/views/apartmentManagement/apartmentManagement/apartmentManagement.vue`

**核心功能：显示公寓列表，支持地区筛选**

```vue
<ProTable
  :requestApi="getApartmentList"      <!-- 关键：传入API函数 -->
  :initParam="initParam"               <!-- 初始查询参数 -->
  :dataCallback="dataCallback"         <!-- 数据转换 -->
>
  <!-- 搜索模板：省市区三级联动 -->
  <el-select v-model="areaInfo.provinceId"  @change="provinceChangeCallback">
  <el-select v-model="areaInfo.cityId"      @change="cityChangeCallback">
  <el-select v-model="areaInfo.districtId"  @change="districtChangeCallback">
</ProTable>
```

**地区数据流管理：**
```typescript
// 地区响应式状态
const areaInfo = reactive({
  provinceList: [],    // 省份列表数据
  provinceId: '',      // 用户选中的省份ID
  cityList: [],        // 城市列表数据
  cityId: '',          // 用户选中的城市ID
  districtList: [],    // 区域列表数据
  districtId: ''       // 用户选中的区域ID
})

// 初始化：加载省份列表
async function getProvinceListHandle() {
  const { data } = await getProvinceList()  // API调用
  areaInfo.provinceList = data               // 保存到状态
  proTable.value?.enumMap.set('provinceId', areaInfo.provinceList)  // 同步到表格组件
}

// 核心：省份改变回调 - 联动获取城市
const provinceChangeCallback = async () => {
  const provinceId = proTable.value!.searchParam.provinceId
  
  if (provinceId) {
    resetCity()                    // 重置城市（清空选中和列表）
    resetDistrict()                // 重置区域
    await getCityListHandle(provinceId)  // 加载该省份的城市
  }
}

// 城市改变回调
const cityChangeCallback = async () => {
  const cityId = proTable.value!.searchParam.cityId
  
  if (cityId) {
    resetDistrict()
    await getDistrictListHandle(cityId)  // 加载该城市的区域
  }
}

// 数据转换函数
const dataCallback = (data: any) => {
  return {
    list: data?.records,   // 公寓数组
    total: data?.total     // 总记录数
  }
}
```

**表格列配置：**
```typescript
const columns: ColumnProps[] = [
  // 搜索字段（隐藏显示但用于筛选）
  { prop: 'provinceId', label: '省份', isShow: false, search: { el: 'select', ... } },
  { prop: 'cityId',     label: '城市', isShow: false, search: { el: 'select', ... } },
  { prop: 'districtId', label: '区域', isShow: false, search: { el: 'select', ... } },
  
  // 显示字段
  { prop: 'name',           label: '名称' },
  { prop: 'addressDetail',  label: '详细地址' },
  { prop: 'freeRoomCount',  label: '空闲房间数' },
  { prop: 'totalRoomCount', label: '房间总数' },
  { prop: 'provinceName',   label: '所处省份' },     // 显示名称
  { prop: 'cityName',       label: '所处城市' },     // 显示名称
  { prop: 'districtName',   label: '所处区域' },     // 显示名称
  { prop: 'phone',          label: '联系方式' },
  
  // 入住状态（计算属性）
  {
    prop: '入住状态',
    render: ({ row }) => row.freeRoomCount === 0 ? '已满房' : '可入住'
  },
  
  // 发布状态（开关）
  {
    prop: 'isRelease',
    render: ({ row }) => (
      <el-switch
        v-model={row.isRelease}
        onChange={() => updateApartmentReleaseStatus(row.id, row.isRelease)}
      />
    )
  },
  
  { prop: 'operation', label: '操作', fixed: 'right' }
]
```

#### 3. **新增/编辑页** - `src/views/apartmentManagement/apartmentManagement/components/addOrEditApartment.vue`

**关键特性：表单填写、地区同步、图片上传、高德地图集成**

```typescript
// 表单数据结构
const formData = ref({
  id: '',
  name: '',                    // 公寓名称
  introduction: '',            // 介绍
  
  // 地区字段 - 核心
  provinceId: '',
  provinceName: '',            // 初始为空，需要同步
  cityId: '',
  cityName: '',
  districtId: '',
  districtName: '',
  
  // 地址和坐标
  addressDetail: '',           // 详细地址（可从地图获取）
  latitude: '',                // 纬度（从地图获得）
  longitude: '',               // 经度（从地图获得）
  
  phone: '',
  isRelease: ApartmentReleaseStatus.NOT_RELEASED,
  
  // 关联ID数组（而非详情对象）
  facilityInfoIds: [],         // 配套IDs
  labelIds: [],                // 标签IDs
  feeValueIds: [],             // 杂费IDs
  
  // 图片
  graphVoList: []              // 图片数组
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入公寓名称', trigger: 'blur' }],
  introduction: [{ required: true, message: '请输入公寓介绍', trigger: 'blur' }],
  provinceId: [{ required: true, message: '请选择省份', trigger: 'change' }],
  cityId: [{ required: true, message: '请选择城市', trigger: 'change' }],
  districtId: [{ required: true, message: '请选择区域', trigger: 'change' }],
  addressDetail: [{ required: true, message: '请选择详细地址', trigger: 'change' }],
  phone: [{ required: true, message: '请输入公寓前台电话', trigger: 'blur' }],
  graphVoList: [{ required: true, message: '请上传图片', trigger: 'change' }]
}
```

**地区联动逻辑（同列表页）：**
```typescript
// 地区数据
const areaInfo = reactive({
  provinceList: [],
  cityList: [],
  districtList: []
})

// 初始化加载省份
async function getProvinceListHandle() {
  const { data } = await getProvinceList()
  areaInfo.provinceList = data
}

// 省份改变：获取城市列表
const provinceChangeCallback = async () => {
  const provinceId = formData.value.provinceId
  if (provinceId) {
    resetCity()          // 清空城市和区域
    resetDistrict()
    await getCityListHandle(provinceId)  // 加载城市
  }
}

// 城市改变：获取区域列表
const cityChangeCallback = async () => {
  const cityId = formData.value.cityId
  if (cityId) {
    resetDistrict()
    await getDistrictListHandle(cityId)  // 加载区域
  }
}
```

**编辑模式：获取和转换数据**
```typescript
async function getApartmentInfoByIdHandle(id: number | string) {
  const { data } = await getApartmentById(id)  // 获取完整公寓信息
  
  // 关键：将详情列表转换为ID数组供表单使用
  data.facilityInfoIds = data.facilityInfoList?.map(item => item.id)
  delete data.facilityInfoList  // 删除不需要的详情
  
  data.labelIds = data.labelInfoList?.map(item => item.id)
  delete data.labelInfoList
  
  data.feeValueIds = data.feeValueVoList?.map(item => item.id)
  delete data.feeValueVoList
  
  // 地址建议列表（用于地图自动补全）
  addressDetailOptions.value = [{
    label: data.addressDetail,
    value: data.addressDetail,
    location: { lng: data.longitude, lat: data.latitude }
  }]
  
  formData.value = data  // 赋值给表单
  
  // 加载对应的城市和区域（因为API返回的是IDs，需要重新加载列表）
  formData.value.provinceId && getCityListHandle(formData.value.provinceId)
  formData.value.cityId && getDistrictListHandle(formData.value.cityId)
}
```

**高德地图集成 - 详细地址自动补全：**
```typescript
// 用户输入关键词时触发
function remoteMethod(keywords: string) {
  if (!keywords.trim()) {
    addressDetailOptions.value = []
    return
  }
  
  // 构建完整查询关键词：省 + 区 + 用户输入
  const provinceName = areaInfo.provinceList.find(
    item => item.id === formData.value.provinceId
  )?.name || ''
  
  const districtName = areaInfo.districtList.find(
    item => item.id === formData.value.districtId
  )?.name || ''
  
  keywords = provinceName + districtName + keywords
  
  // 调用高德地图API搜索
  AMap.value.plugin('AMap.AutoComplete', function() {
    const autoComplete = new AMap.value.AutoComplete({ city: '全国' })
    
    autoComplete.search(keywords, function(status, result) {
      // 转换搜索结果为下拉选项
      addressDetailOptions.value = result?.tips?.map((item: any) => ({
        label: item.district + item.name + item.address,
        value: item.district + item.name + item.address,
        location: item.location  // 包含经纬度
      })) || []
    })
  })
}

// 用户选择地址后，从选项中提取经纬度
function addressDetailChangeCallback(value: string) {
  const targetObj = addressDetailOptions.value.find(
    item => item.value === value
  )
  
  if (targetObj) {
    formData.value.longitude = targetObj.location?.lng || ''
    formData.value.latitude = targetObj.location?.lat || ''
  }
}
```

**图片上传处理：**
```typescript
// 图片上传成功回调
function uploadSuccessHandle(response, uploadFile, uploadFiles) {
  // 关键：后端返回的图片URL在 response.data 中
  formData.value.graphVoList = uploadFiles?.map((item) => ({
    name: item.name,
    url: item.response.data  // 后端返回的URL
  }))
  
  console.log('图片更新:', formData.value.graphVoList)
}

// uploadImg.vue 组件配置
<upload-img 
  v-model:file-list="formData.graphVoList" 
  :on-success="uploadSuccessHandle"
  list-type="picture-card"
  :limit="5"  <!-- 最多5张 -->
/>
```

**表单提交：**
```typescript
// 表单验证和提交
function submitHandle() {
  apartmentFormRef.value?.validate(async (valid) => {
    if (valid) {
      await addOrUpdateApartmentInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
    }
  })
}

// 真实的提交逻辑
async function addOrUpdateApartmentInfoHandle() {
  try {
    // 发送完整的formData对象到后端
    // 后端负责根据ID判断新增还是更新
    await saveOrUpdateApartment(formData.value)
    
    ElMessage.success('操作成功')
    router.back()  // 返回列表页
  } catch (error) {
    console.error(error)
  }
}

// 初始化
onMounted(() => {
  getProvinceListHandle()        // 加载省份
  getFacilityInfoListHandle()    // 加载配套
  getLabelInfoListHandle()       // 加载标签
  getFeeInfoListHandle()         // 加载杂费
  
  if (route.query?.id) {
    getApartmentInfoByIdHandle(route.query.id)  // 编辑模式
  }
})
```

#### 4. **图片上传组件** - `src/components/uploadImg/uploadImg.vue`

```vue
<template>
  <el-upload
    v-bind="$attrs"
    :action="UPLOAD_IMG_URL"                    <!-- 上传端点 -->
    :on-preview="handlePictureCardPreview"      <!-- 预览回调 -->
    :headers="{ 'access-token': useUserStore().token }"
  >
    <el-icon><Plus /></el-icon>
  </el-upload>

  <!-- 图片预览弹窗 -->
  <el-dialog v-model="dialogVisible">
    <el-image :src="dialogImageUrl" fit="fill" />
  </el-dialog>
</template>

<script setup>
import { UPLOAD_IMG_URL } from '@/api/upload'

const dialogImageUrl = ref('')
const dialogVisible = ref(false)

function handlePictureCardPreview(uploadFile: UploadFile) {
  dialogImageUrl.value = uploadFile.url!
  dialogVisible.value = true
}
</script>
```

#### 5. **上传API配置** - `src/api/upload/index.ts`

```typescript
// 基础URL配置
export const BASE_URL = import.meta.env.PROD
  ? import.meta.env.VITE_APP_BASE_URL
  : ''

// 图片上传端点
export const UPLOAD_IMG_URL = `${BASE_URL}/admin/file/upload`
```

---

### App端应用（rentHouseApp）

#### 1. **公寓详情页** - `src/views/ApartmentDetail.vue`

```vue
<template>
  <el-page-header title="返回" content="公寓详情" @back="router.back()" />
  
  <div v-if="apartment" class="apartment-container">
    <!-- 1. 图片轮播 - graphVoList的使用 -->
    <div class="gallery-section" v-if="apartment.graphVoList?.length">
      <el-carousel height="280px">
        <el-carousel-item v-for="img in apartment.graphVoList" :key="img.id || img.url">
          <!-- 关键：直接显示后端返回的img.url -->
          <img class="carousel-img" :src="img.url" alt="apartment" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 2. 基本信息显示 - 包括地区名称 -->
    <div class="info-section">
      <h2 class="apartment-name">{{ apartment.name }}</h2>
      <p class="address">📍 {{ apartment.addressDetail }}</p>
      <p v-if="apartment.phone" class="phone">📞 {{ apartment.phone }}</p>
      <p v-if="apartment.introductionInfo">{{ apartment.introductionInfo }}</p>
    </div>

    <!-- 3. 标签显示 -->
    <div v-if="apartment.labelInfoList?.length" class="tags-section">
      <h3>公寓标签</h3>
      <el-space wrap>
        <el-tag v-for="label in apartment.labelInfoList" :key="label.id">
          {{ label.name }}
        </el-tag>
      </el-space>
    </div>

    <!-- 4. 房间列表 - 每个房间也有graphVoList -->
    <div class="rooms-section">
      <h3>可租房间 ({{ rooms.length }})</h3>
      <div class="rooms-grid">
        <div v-for="room in rooms" :key="room.id" class="room-item">
          <!-- 房间图片（第一张） -->
          <img :src="room.graphVoList?.[0]?.url || '占位符'" alt="room" />
          <div class="room-details">
            <h4>{{ room.roomNumber }} 号房间</h4>
            <p>¥{{ room.rent }}/月</p>
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
    // API调用获取公寓详情
    apartment.value = (await getApartmentDetail(route.params.id)).data
    
    // 获取该公寓的房间列表
    const roomRes = await getRoomsByApartmentId(route.params.id)
    rooms.value = roomRes.data?.records || []
  } catch (error) {
    ElMessage.error('加载公寓详情失败')
    console.error(error)
  }
})
</script>
```

#### 2. **公寓API** - `src/api/apartment.js`

```javascript
import request from '../utils/request'

// 获取公寓详情 - 返回包含graphVoList的完整apartment对象
export const getApartmentDetail = (id) =>
  request({
    url: '/apartment/getDetailById',
    method: 'get',
    params: { id }
  })

// 获取公寓下的房间列表 - 每个room也包含graphVoList
export const getRoomsByApartmentId = (id, current = 1, size = 100) =>
  request({
    url: '/room/pageItemByApartmentId',
    method: 'get',
    params: { id, current, size }
  })
```

---

## 🔑 关键数据流总结

### 1. 地区信息同步流程

```
后台管理：
选择省份 → API获取城市列表 → 显示城市下拉 
                    ↓
          选择城市 → API获取区域列表 → 显示区域下拉
                    ↓
          选择区域 → 完成地区选择

保存公寓：
formData {
  provinceId, provinceName,
  cityId, cityName,
  districtId, districtName,
  ...其他字段
} → POST /admin/apartment/saveOrUpdate
  ↓
后端保存到数据库
```

### 2. 新增公寓的地区字段问题（待确认）

**问题分析：** 新增时，provinceName/cityName/districtName如何获得？

**可能情况：**
1. 前端在保存前查询RegionInterface获取名称
2. 后端根据ID自动补充名称
3. 需要手动在formData中维护（推荐方案）

**建议修复（如需要）：**
```typescript
// 在submitHandle前添加
async function enrichAreaNames() {
  if (formData.value.districtId) {
    const district = areaInfo.districtList.find(d => d.id === formData.value.districtId)
    if (district) formData.value.districtName = district.name
  }
  
  if (formData.value.cityId) {
    const city = areaInfo.cityList.find(c => c.id === formData.value.cityId)
    if (city) formData.value.cityName = city.name
  }
  
  if (formData.value.provinceId) {
    const province = areaInfo.provinceList.find(p => p.id === formData.value.provinceId)
    if (province) formData.value.provinceName = province.name
  }
}

// 在提交前调用
function submitHandle() {
  apartmentFormRef.value?.validate(async (valid) => {
    if (valid) {
      await enrichAreaNames()  // 补充地区名称
      await addOrUpdateApartmentInfoHandle()
    }
  })
}
```

### 3. 图片上传完整流程

```
后台：
用户选择文件
  ↓
<el-upload> 自动上传到 /admin/file/upload
  ↓
onSuccess回调：uploadSuccessHandle()
  ├─ 解析response.data（URL）
  └─ 更新formData.graphVoList = [{name, url}, ...]
  ↓
提交表单到 /admin/apartment/saveOrUpdate
  ├─ 包含 graphVoList: [{url, name}, ...]
  └─ 后端保存到数据库

App端：
API获取 /apartment/getDetailById
  ↓
返回 apartment.graphVoList = [{url, name}, ...]
  ↓
渲染 <el-carousel>
  └─ <img :src="img.url" />
  ↓
显示公寓图片轮播
```

### 4. 房间图片显示

```
房间API返回：
room.graphVoList = [{url, name}, ...]
  ↓
在公寓详情页显示第一张图片：
<img :src="room.graphVoList?.[0]?.url" />
  ↓
在房间详情页：
完整显示所有graphVoList
```

---

## 📊 API端点完整列表

| 功能模块           | 方法   | 端点                                       | 说明                  |
| ------------------ | ------ | ------------------------------------------ | --------------------- |
| **公寓管理**       |        |                                            |                       |
| 分页查询列表       | GET    | `/admin/apartment/pageItem`                | 支持省市区过滤        |
| 获取详情           | GET    | `/admin/apartment/getDetailById`           | 返回完整信息含名称    |
| 新增/更新          | POST   | `/admin/apartment/saveOrUpdate`            | 自动判断新增还是更新  |
| 删除               | DELETE | `/admin/apartment/removeById`              | 按ID删除              |
| 更新发布状态       | POST   | `/admin/apartment/updateReleaseStatusById` | 切换发布/未发布       |
| **地区管理**       |        |                                            |                       |
| 省份列表           | GET    | `/admin/region/province/list`              | 返回RegionInterface[] |
| 城市列表           | GET    | `/admin/region/city/listByProvinceId`      | 按省份ID查询          |
| 区域列表           | GET    | `/admin/region/district/listByCityId`      | 按城市ID查询          |
| **文件上传**       |        |                                            |                       |
| 上传图片           | POST   | `/admin/file/upload`                       | 返回 {data: URL}      |
| **配套/标签/杂费** |        |                                            |                       |
| 配套列表           | GET    | `/admin/facility/list`                     | 可按type过滤          |
| 标签列表           | GET    | `/admin/label/list`                        | 可按type过滤          |
| 杂费列表           | GET    | `/admin/fee/list`                          | 返回树形结构          |
| **App端**          |        |                                            |                       |
| 公寓详情           | GET    | `/apartment/getDetailById`                 | 返回包含graphVoList   |
| 房间列表           | GET    | `/room/pageItemByApartmentId`              | 房间的graphVoList     |

---

## 🔍 字段映射对照表

### ApartmentInterface 核心字段

| 字段名              | 类型     | 用途           | 来源                 | 显示位置       |
| ------------------- | -------- | -------------- | -------------------- | -------------- |
| id                  | number   | 主键           | 后端生成             | -              |
| name                | string   | 公寓名称       | 用户输入             | 列表、详情     |
| introduction        | string   | 介绍           | 用户输入             | 详情页         |
| **provinceId**      | number   | **省份ID**     | **用户选择**         | **搜索条件**   |
| **provinceName**    | string   | **省份名称**   | **API返回/后端补充** | **列表显示**   |
| **cityId**          | number   | **城市ID**     | **用户选择**         | **搜索条件**   |
| **cityName**        | string   | **城市名称**   | **API返回/后端补充** | **列表显示**   |
| **districtId**      | number   | **区域ID**     | **用户选择**         | **搜索条件**   |
| **districtName**    | string   | **区域名称**   | **API返回/后端补充** | **列表显示**   |
| addressDetail       | string   | 详细地址       | 用户输入/地图选择    | 列表、详情     |
| latitude            | number   | 纬度           | 地图获取             | -              |
| longitude           | number   | 经度           | 地图获取             | -              |
| phone               | string   | 联系电话       | 用户输入             | 列表、详情     |
| isRelease           | number   | 发布状态       | 用户设置             | 列表（开关）   |
| totalRoomCount      | number   | 房间总数       | 后端计算             | 列表           |
| freeRoomCount       | number   | 空闲房间数     | 后端计算             | 列表、详情     |
| **facilityInfoIds** | number[] | **配套ID数组** | **用户选择**         | **表单**       |
| facilityInfoList    | Object[] | 配套详情       | API返回              | 详情页         |
| **labelIds**        | number[] | **标签ID数组** | **用户选择**         | **表单**       |
| labelInfoList       | Object[] | 标签详情       | API返回              | 详情页         |
| **feeValueIds**     | number[] | **杂费ID数组** | **用户选择**         | **表单**       |
| feeValueVoList      | Object[] | 杂费详情       | API返回              | 详情页         |
| **graphVoList**     | Array    | **图片数组**   | **上传获取**         | **轮播、卡片** |

---

## 🛠️ 技术栈和组件使用

### 后台管理（Vue 3 + TypeScript + Element Plus）
- **ProTable** - 高级表格组件（自定义搜索、操作等）
- **el-select** - 下拉选择（级联联动）
- **el-upload** - 文件上传
- **el-form** - 表单验证
- **el-carousel** - 图片轮播（App端）
- **高德地图API** - 地址自动补全

### App端（Vue 3 + Element Plus）
- **el-carousel** - 图片轮播
- **el-tag** - 标签显示
- **el-image** - 图片显示
- **el-button** - 按钮

---

## ⚠️ 关键注意点

### 1. 地区名称同步
- **编辑时**：API返回包含6个字段，直接使用
- **新增时**：需要确保provinceName/cityName/districtName被设置
  - 可能需要前端补充或后端自动补充

### 2. 图片上传
- 上传端点：`/admin/file/upload`
- 返回格式：`{data: "图片URL"}`
- 需要auth token：从`x-access-token`header传递

### 3. 表单数据结构
- 存储时：发送ID数组（facilityInfoIds等）
- 获取时：API返回详情对象列表
- 需要在编辑前进行转换

### 4. ProTable配置
- 搜索字段可设置 `isShow: false` 但仍作为查询条件
- `enumMap.set()` 用于动态更新搜索选项
- `dataCallback` 必须返回 `{list, total}` 格式

### 5. 级联联动逻辑
- 必须在上层改变时清空下层（resetCity/resetDistrict）
- 然后重新加载下层数据
- 重置时要同时清空：选中值、列表数据、ProTable中的参数

---

## 📝 总结

本系统的公寓管理模块采用**三级地区联动**设计，**统一的图片处理方案**，以及**完整的表单验证**流程。核心流程包括：

1. **地区选择** → 联动获取下级列表 → 完成地区选择
2. **公寓新增** → 填写表单 → 上传图片 → 验证保存
3. **公寓编辑** → 加载详情 → 修改字段 → 保存更新
4. **图片同步** → 后台上传获得URL → 保存到数据库 → App端获取显示

数据流清晰，各层职责明确，易于维护和扩展。
