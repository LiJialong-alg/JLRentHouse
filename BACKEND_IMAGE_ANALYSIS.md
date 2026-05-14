# Java后端公寓图片无法显示问题 - 深度分析报告

## 执行时间: 2026-05-07

---

## 📋 问题概述

**现象**: 新上传的公寓图片无法在App端显示  
**预期**: 上传图片后，在公寓详情页应该显示最新的图片列表  
**实际**: 图片列表为空或无法加载

---

## 🏗️ 架构分析

### 模块划分
```
lease/
├── model/               # 数据模型 (公共)
├── web/
│   ├── web-admin/       # 后台管理端 (管理员操作)
│   └── web-app/         # 移动App端 (用户操作)
└── common/              # 公共工具模块
```

### 数据流向
```
前端上传 → FileUploadController → MinIO存储
           ↓
           返回 URL
           ↓
前端提交表单 → ApartmentController.saveOrUpdate() 
           ↓
           ApartmentInfoService.saveOrUpdateApartmentInfo()
           ↓
           保存到 graph_info 表
           ↓
App端查询 → ApartmentController.getDetailById()
           ↓
           查询 graph_info 表
           ↓
           返回 graphVoList
```

---

## 🔍 核心代码详解

### 1️⃣ 图片上传 - FileUploadController

**文件**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/controller/apartment/FileUploadController.java`

```java
@Tag(name = "文件管理")
@RequestMapping("/admin/file")
@RestController
public class FileUploadController {
    
    @Autowired
    private FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("upload")
    public Result<String> upload(@RequestParam MultipartFile file) throws Exception {
        String upload = fileService.upload(file);
        return Result.ok(upload);  // 返回图片URL
    }
}
```

**关键点**:
- ✅ 接收文件并上传到MinIO
- ✅ 返回文件的完整URL
- ⚠️ **注意**: 此步骤只上传文件，不保存到数据库

**返回值示例**:
```
http://minio-server:9000/bucket/20240507/uuid-filename.jpg
```

---

### 2️⃣ 图片上传实现 - FileServiceImpl

**文件**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/service/impl/FileServiceImpl.java`

```java
@Service
public class FileServiceImpl implements FileService {
    
    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws Exception {
        // 1. 检查bucket是否存在
        boolean b = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build()
        );

        // 2. 如果不存在，创建bucket
        if (!b) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build());
            // 设置bucket权限
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(minioProperties.getBucketName())
                .config(createBucketPolicyConfig(minioProperties.getBucketName()))
                .build());
        }

        // 3. 生成文件路径 (按日期分组)
        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) 
                        + "/" + UUID.randomUUID() + "-" 
                        + file.getOriginalFilename();

        // 4. 上传文件
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(minioProperties.getBucketName())
            .stream(file.getInputStream(), file.getSize(), -1)
            .object(filename)
            .contentType(file.getContentType())
            .build());

        // 5. 生成访问URL
        String baseUrl = minioProperties.getExternalUrl() != null 
                       && !minioProperties.getExternalUrl().isEmpty()
                       ? minioProperties.getExternalUrl()
                       : minioProperties.getEndpoint();
        
        String url = baseUrl + "/" + minioProperties.getBucketName() + "/" + filename;
        return url;
    }
}
```

**关键点**:
- ✅ 使用UUID确保文件名唯一性
- ✅ 按日期分组存储
- ✅ 返回完整的访问URL
- ⚠️ **重要**: 文件上传成功，但此时还未关联到公寓！

---

### 3️⃣ 公寓保存 - ApartmentController

**文件**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/controller/apartment/ApartmentController.java`

```java
@Tag(name = "公寓信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService apartmentService;

    @Operation(summary = "保存或更新公寓信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        apartmentService.saveOrUpdateApartmentInfo(apartmentSubmitVo);
        return Result.ok();
    }

    @Operation(summary = "根据ID获取公寓详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo apartmentDetailVo = apartmentService.getDetailById(id);
        return Result.ok(apartmentDetailVo);
    }
}
```

**关键点**:
- ✅ 接收 ApartmentSubmitVo (包含 graphVoList)
- ✅ 调用 saveOrUpdateApartmentInfo 保存
- ✅ 提供 getDetailById 查询详情

---

### 4️⃣ 数据传输对象 - ApartmentSubmitVo

**文件**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/vo/apartment/ApartmentSubmitVo.java`

```java
@Schema(description = "公寓信息")
@Data
public class ApartmentSubmitVo extends ApartmentInfo {

    @Schema(description="公寓配套id")
    private List<Long> facilityInfoIds;

    @Schema(description="公寓标签id")
    private List<Long> labelIds;

    @Schema(description="公寓杂费值id")
    private List<Long> feeValueIds;

    @Schema(description="公寓图片id")
    private List<GraphVo> graphVoList;  // ✅ 关键字段
}
```

**GraphVo 结构**:
```java
@Data
@Schema(description = "图片信息")
public class GraphVo {
    @Schema(description = "图片名称")
    private String name;
    
    @Schema(description = "图片地址")
    private String url;
}
```

**前端提交示例**:
```json
{
  "id": 123,
  "name": "公寓A",
  "graphVoList": [
    {
      "name": "主卧",
      "url": "http://minio/bucket/20240507/uuid-1.jpg"
    },
    {
      "name": "客厅",
      "url": "http://minio/bucket/20240507/uuid-2.jpg"
    }
  ]
}
```

---

### 5️⃣ 公寓保存服务 - ApartmentInfoServiceImpl (web-admin)

**文件**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/service/impl/ApartmentInfoServiceImpl.java`

```java
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {
    
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Override
    public void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo) {
        // 步骤1: 保存或更新公寓基本信息
        Boolean isUpdate = apartmentSubmitVo.getId() != null;
        super.saveOrUpdate(apartmentSubmitVo);
        
        // 如果是更新，删除旧数据
        if (isUpdate) {
            // 1.1 删除旧图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphInfoQueryWrapper);
            
            // 1.2 删除旧配套列表
            LambdaQueryWrapper<ApartmentFacility> apartmentFacilityWrapper = new LambdaQueryWrapper<>();
            apartmentFacilityWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(apartmentFacilityWrapper);
            
            // ... 删除标签、杂费等
        }
        
        // 步骤2: 保存新的图片列表 ⭐ 关键步骤
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setName(graphVo.getName());                          // 图片名称
                graphInfo.setItemType(ItemType.APARTMENT);                    // ✅ 类型: 1 (APARTMENT)
                graphInfo.setItemId(apartmentSubmitVo.getId());               // ✅ 公寓ID
                graphInfo.setUrl(graphVo.getUrl());                           // ✅ 图片URL
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);  // 批量保存到数据库
        }
        
        // 步骤3: 保存配套、标签、杂费等其他关联数据
        // ... (省略相似代码)
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        // 查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        
        // 查询图片列表 ⭐ 调用Mapper查询
        List<GraphVo> graphVoList = graphInfoMapper.selectListById(ItemType.APARTMENT, id);

        // 查询标签、配套、杂费等
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListById(id);
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListById(id);
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListById(id);

        // 构建响应VO
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);      // ✅ 设置图片列表
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return apartmentDetailVo;
    }
}
```

**流程梳理**:
1. ✅ 接收 ApartmentSubmitVo (含 graphVoList)
2. ✅ 保存基本信息并获取ID
3. ✅ 删除旧的图片记录 (WHERE item_type=1 AND item_id=xxx)
4. ✅ 创建 GraphInfo 对象并设置:
   - `itemType = ItemType.APARTMENT` (值为1)
   - `itemId = 公寓ID`
   - `url = 图片URL`
5. ✅ 批量保存到 graph_info 表

---

### 6️⃣ 数据库表 - GraphInfo

**实体类**: `lease/model/src/main/java/org/example/lease/model/entity/GraphInfo.java`

```java
@Schema(description = "图片信息表")
@TableName(value = "graph_info")
@Data
public class GraphInfo extends BaseEntity {
    
    @Schema(description = "图片名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "图片所属对象类型")
    @TableField(value = "item_type")
    private ItemType itemType;  // 1=公寓, 2=房间

    @Schema(description = "图片所有对象id")
    @TableField(value = "item_id")
    private Long itemId;        // 公寓ID或房间ID

    @Schema(description = "图片地址")
    @TableField(value = "url")
    private String url;
}
```

**ItemType 枚举**:
```java
public enum ItemType implements BaseEnum {
    APARTMENT(1, "公寓"),      // ✅ 代码: 1
    ROOM(2, "房间");          // ✅ 代码: 2
    
    @EnumValue
    @JsonValue
    private Integer code;
}
```

**表结构**:
```sql
CREATE TABLE graph_info (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    item_type INT,          -- 1=公寓, 2=房间
    item_id BIGINT,         -- 关联对象的ID
    url VARCHAR(1000),      -- 图片URL
    is_deleted TINYINT,     -- 逻辑删除标志
    create_time DATETIME,
    update_time DATETIME
);

-- 查询公寓123的所有图片:
SELECT * FROM graph_info 
WHERE is_deleted = 0 
AND item_type = 1 
AND item_id = 123;
```

---

### 7️⃣ 图片查询 (后台管理端) - GraphInfoMapper (web-admin)

**Mapper接口**: `lease/web/web-admin/src/main/java/org/example/lease/web/admin/mapper/GraphInfoMapper.java`

```java
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {
    List<GraphVo> selectListById(ItemType itemType, Long id);
}
```

**SQL映射** (`lease/web/web-admin/src/main/resources/mapper/GraphInfoMapper.xml`):

```xml
<mapper namespace="org.example.lease.web.admin.mapper.GraphInfoMapper">
    <select id="selectListById" resultType="org.example.lease.web.admin.vo.graph.GraphVo">
        select name, url from graph_info
        where is_deleted = 0 
        and item_id=#{id} 
        and item_type=#{itemType}
    </select>
</mapper>
```

**查询逻辑**:
- ✅ 只查询未删除的记录
- ✅ 按 item_id 和 item_type 查询
- ✅ 只返回 name 和 url 字段

---

### 8️⃣ App端公寓查询 - ApartmentController (web-app)

**文件**: `lease/web/web-app/src/main/java/org/example/lease/web/app/controller/apartment/ApartmentController.java`

```java
@RestController
@Tag(name = "公寓信息")
@RequestMapping("/app/apartment")
public class ApartmentController {
    
    @Autowired
    private ApartmentInfoService service;

    @Operation(summary = "根据id获取公寓信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo detailById = service.getDetailById(id);
        return Result.ok(detailById);
    }
}
```

**API文档**:
- 路径: `/app/apartment/getDetailById`
- 方法: GET
- 参数: `id` (公寓ID)
- 返回: ApartmentDetailVo

---

### 9️⃣ App端图片查询服务 - ApartmentInfoServiceImpl (web-app)

**文件**: `lease/web/web-app/src/main/java/org/example/lease/web/app/service/impl/ApartmentInfoServiceImpl.java`

```java
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {
    
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        // 1. 查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        
        // 2. 查询图片信息 ⭐ 关键查询
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(
            ItemType.APARTMENT, id
        );
        
        // 3. 查询标签信息
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);
        
        // 4. 查询配套信息
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);
        
        // 5. 查询最小租金
        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        // 构建响应
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);          // ✅ 设置图片列表
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setMinRent(minRent);
        
        return apartmentDetailVo;
    }
}
```

**注意**: 使用的是 `selectListByItemTypeAndId` 方法

---

### 🔟 App端图片查询Mapper (web-app)

**Mapper接口**: `lease/web/web-app/src/main/java/org/example/lease/web/app/mapper/GraphInfoMapper.java`

```java
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {
    List<GraphVo> selectListByItemTypeAndId(ItemType itemType, Long id);
}
```

**SQL映射** (`lease/web/web-app/src/main/resources/mapper/GraphInfoMapper.xml`):

```xml
<mapper namespace="org.example.lease.web.app.mapper.GraphInfoMapper">
    <select id="selectListByItemTypeAndId" resultType="org.example.lease.web.app.vo.graph.GraphVo">
        select name, url from graph_info
        where is_deleted = 0
        and item_type = #{itemType}
        and item_id = #{id}
    </select>
</mapper>
```

**对比 web-admin**:
- web-admin 用: `selectListById(itemType, id)`
- web-app 用: `selectListByItemTypeAndId(itemType, id)`
- SQL 完全相同，只是方法名不同

---

### 1️⃣1️⃣ App端前端展示 - ApartmentDetail.vue

**文件**: `rentHouseApp/src/views/ApartmentDetail.vue`

```vue
<template>
  <div class="page">
    <!-- 图片轮播 -->
    <div class="gallery-section" v-if="apartment.graphVoList?.length">
      <el-carousel height="280px" indicator-position="outside">
        <el-carousel-item v-for="img in apartment.graphVoList" :key="img.id || img.url">
          <img class="carousel-img" :src="img.url" alt="apartment" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 其他信息 -->
    <div class="info-section">
      <h2 class="apartment-name">{{ apartment.name }}</h2>
      <!-- ... -->
    </div>
  </div>
</template>

<script setup>
  import { onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { getApartmentDetail, getRoomsByApartmentId } from '../api/apartment'

  const apartment = ref(null)
  const rooms = ref([])

  onMounted(async () => {
    try {
      apartment.value = (await getApartmentDetail(route.params.id)).data
      const roomRes = await getRoomsByApartmentId(route.params.id)
      rooms.value = roomRes.data?.records || []
    } catch (error) {
      console.error(error)
    }
  })
</script>
```

**前端逻辑**:
1. ✅ 调用 `getApartmentDetail(id)` 获取公寓详情
2. ✅ 检查 `apartment.graphVoList?.length` 是否有图片
3. ✅ 遍历 graphVoList 显示轮播
4. ✅ 使用 `img.url` 显示图片

---

## 🚨 可能的问题排查

### 问题A: 图片是否真的被保存了?

**检查步骤**:

1. **检查保存逻辑是否执行**
   ```java
   // 在 ApartmentInfoServiceImpl.saveOrUpdateApartmentInfo() 中添加日志
   System.out.println("graphVoList: " + graphVoList);
   if(!CollectionUtils.isEmpty(graphVoList)){
       // 这段代码是否执行?
       graphInfoService.saveBatch(graphInfos);
   }
   ```

2. **检查数据库记录**
   ```sql
   -- 查询是否有记录被保存
   SELECT * FROM graph_info 
   WHERE item_type = 1 
   AND item_id = {公寓ID}
   AND is_deleted = 0;
   
   -- 查询所有记录(包括已删除)
   SELECT * FROM graph_info 
   WHERE item_type = 1 
   AND item_id = {公寓ID};
   ```

### 问题B: ItemType是否正确?

**检查**:
```java
// ItemType.APARTMENT 对应数值 1
// 代码中设置: graphInfo.setItemType(ItemType.APARTMENT);
// 数据库中应该存储: 1

// 检查数据库
SELECT DISTINCT item_type FROM graph_info;  -- 应该看到 1, 2
```

### 问题C: item_id是否正确?

**检查**:
```java
// 代码中设置: graphInfo.setItemId(apartmentSubmitVo.getId());
// 这个 apartmentSubmitVo.getId() 值是多少?

// 添加日志验证
System.out.println("Apartment ID: " + apartmentSubmitVo.getId());
System.out.println("GraphInfo list size: " + graphVoList.size());
for (GraphVo g : graphVoList) {
    System.out.println("  - " + g.getName() + ": " + g.getUrl());
}
```

### 问题D: 图片URL是否正确?

**检查**:
```sql
-- 查询存储的URL
SELECT name, url FROM graph_info 
WHERE item_id = {公寓ID} 
AND item_type = 1 
AND is_deleted = 0;

-- URL应该类似:
-- http://minio:9000/bucket/20240507/uuid-xxx.jpg
```

### 问题E: 查询时是否正确过滤?

**检查SQL**:
```sql
-- web-admin查询
SELECT name, url FROM graph_info
WHERE is_deleted = 0 
AND item_id = ? 
AND item_type = ?;

-- web-app查询
SELECT name, url FROM graph_info
WHERE is_deleted = 0
AND item_type = ?
AND item_id = ?;

-- 这两个查询在逻辑上是等价的
-- is_deleted、item_type、item_id 都要匹配
```

### 问题F: 前端是否正确接收?

**浏览器调试**:
```javascript
// 在浏览器控制台检查
// 打开 ApartmentDetail.vue 的公寓详情页
// 在控制台输入
console.log(apartment);  // 查看完整的公寓对象
console.log(apartment.graphVoList);  // 查看图片列表
console.log(apartment.graphVoList?.length);  // 查看长度
```

---

## 🔧 可能的根本原因

### 原因1: graphVoList 在提交时为空或未发送
**症状**: 后端接收到的 graphVoList 为 null 或空列表
**表现**: 数据库中没有图片记录

**检查**:
```java
@PostMapping("saveOrUpdate")
public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
    System.out.println("Received: " + JSON.toJSONString(apartmentSubmitVo));
    // 检查 graphVoList 是否为 null
    if (apartmentSubmitVo.getGraphVoList() == null) {
        System.out.println("WARNING: graphVoList is NULL!");
    }
}
```

### 原因2: 数据库逻辑删除未考虑
**症状**: 旧的图片被标记为逻辑删除
**表现**: 查询时 `is_deleted = 0` 过滤掉了所有记录

**检查**:
```sql
-- 检查所有图片，包括已删除的
SELECT *, is_deleted FROM graph_info 
WHERE item_id = {公寓ID} 
AND item_type = 1;

-- 如果 is_deleted = 1，需要检查为什么被标记为删除
```

### 原因3: 公寓ID获取失败
**症状**: 新增公寓后，ID没有正确赋值
**表现**: item_id 存储为 null 或 0

**检查**:
```java
// 检查是否使用了自增ID
// ApartmentInfo 的 ID 生成策略是什么?
// @TableId(type = IdType.AUTO)  -- 自增
// @TableId(type = IdType.ASSIGN_ID)  -- 雪花算法

// 如果使用自增，需要获取生成的ID:
super.saveOrUpdate(apartmentSubmitVo);  // 之后 apartmentSubmitVo.getId() 应该有值
Long apartmentId = apartmentSubmitVo.getId();
System.out.println("After save, ID: " + apartmentId);
```

### 原因4: 枚举值序列化/反序列化问题
**症状**: ItemType 为 null 或错误的值
**表现**: SQL 条件 `item_type = ?` 不匹配

**检查**:
```java
// 检查 ItemType 的序列化配置
// @EnumValue 注解是否正确
// @JsonValue 注解是否正确

// 数据库中实际存储的值是什么?
SELECT DISTINCT item_type, COUNT(*) FROM graph_info GROUP BY item_type;
```

### 原因5: 并发问题
**症状**: 偶然性的数据不一致
**表现**: 有时有图片，有时没有

**检查**:
- 是否有多个线程同时修改同一个公寓?
- 是否存在数据库锁定问题?
- 是否有消息队列异步处理?

---

## 📊 完整数据流验证清单

```
[ ] 1. 前端上传图片
    ├─ [ ] FileUploadController 接收文件
    ├─ [ ] FileService 上传到 MinIO
    ├─ [ ] 返回 URL 给前端
    └─ [ ] 前端接收到 URL

[ ] 2. 前端构造表单数据
    ├─ [ ] 创建 ApartmentSubmitVo 对象
    ├─ [ ] 设置基本信息 (name, address等)
    ├─ [ ] 构造 GraphVo 列表
    │   ├─ [ ] 设置 name (图片名称)
    │   └─ [ ] 设置 url (图片URL)
    └─ [ ] 设置 graphVoList 属性

[ ] 3. 后端接收数据
    ├─ [ ] ApartmentController.saveOrUpdate() 收到请求
    ├─ [ ] ApartmentSubmitVo 正确反序列化
    ├─ [ ] graphVoList 不为空
    └─ [ ] 每个 GraphVo 都有 url

[ ] 4. 后端保存数据
    ├─ [ ] saveOrUpdate() 保存 ApartmentInfo
    ├─ [ ] 获取 apartmentId
    ├─ [ ] graphVoList 不为空
    ├─ [ ] 遍历 graphVoList
    │   ├─ [ ] 创建 GraphInfo 对象
    │   ├─ [ ] 设置 itemType = ItemType.APARTMENT (值1)
    │   ├─ [ ] 设置 itemId = apartmentId
    │   ├─ [ ] 设置 url = graphVo.url
    │   └─ [ ] 设置 name = graphVo.name
    └─ [ ] graphInfoService.saveBatch() 保存到数据库

[ ] 5. 数据库存储验证
    ├─ [ ] graph_info 表有新记录
    ├─ [ ] item_type = 1
    ├─ [ ] item_id = 公寓ID
    ├─ [ ] url 正确
    ├─ [ ] name 正确
    ├─ [ ] is_deleted = 0
    └─ [ ] create_time 正确

[ ] 6. App端查询
    ├─ [ ] 调用 /app/apartment/getDetailById?id=xxx
    ├─ [ ] ApartmentController 收到请求
    ├─ [ ] ApartmentInfoService.getDetailById() 执行
    ├─ [ ] 查询 ApartmentInfo
    ├─ [ ] 查询 graph_info:
    │   ├─ [ ] 执行 SQL 查询
    │   ├─ [ ] WHERE 条件正确
    │   ├─ [ ] 返回结果不为空
    │   └─ [ ] GraphVo 列表包含 name 和 url
    └─ [ ] 设置 graphVoList 到 ApartmentDetailVo

[ ] 7. 返回给前端
    ├─ [ ] ApartmentDetailVo 包含 graphVoList
    ├─ [ ] graphVoList 不为空
    ├─ [ ] 每个元素都有 url
    └─ [ ] 序列化为 JSON 正确

[ ] 8. 前端显示
    ├─ [ ] apartment.graphVoList 不为空
    ├─ [ ] v-if="apartment.graphVoList?.length" 通过
    ├─ [ ] 遍历每个图片
    ├─ [ ] :src="img.url" 正确
    └─ [ ] 图片显示成功
```

---

## 🎯 建议的调试步骤

### 第1步: 添加日志
```java
// 在 ApartmentInfoServiceImpl.saveOrUpdateApartmentInfo() 中
@Override
public void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo) {
    System.out.println("===== saveOrUpdateApartmentInfo START =====");
    System.out.println("Apartment ID: " + apartmentSubmitVo.getId());
    System.out.println("GraphVoList: " + apartmentSubmitVo.getGraphVoList());
    
    Boolean isUpdate = apartmentSubmitVo.getId() != null;
    super.saveOrUpdate(apartmentSubmitVo);
    
    System.out.println("After save, ID: " + apartmentSubmitVo.getId());
    
    if (isUpdate) {
        // ... 删除旧记录
    }
    
    List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
    if(!CollectionUtils.isEmpty(graphVoList)){
        System.out.println("Saving " + graphVoList.size() + " images");
        ArrayList<GraphInfo> graphInfos = new ArrayList<>();
        for (GraphVo graphVo : graphVoList) {
            System.out.println("  Image: " + graphVo.getName() + " -> " + graphVo.getUrl());
            GraphInfo graphInfo = new GraphInfo();
            graphInfo.setName(graphVo.getName());
            graphInfo.setItemType(ItemType.APARTMENT);
            graphInfo.setItemId(apartmentSubmitVo.getId());
            graphInfo.setUrl(graphVo.getUrl());
            graphInfos.add(graphInfo);
        }
        graphInfoService.saveBatch(graphInfos);
        System.out.println("Images saved successfully");
    } else {
        System.out.println("WARNING: GraphVoList is empty!");
    }
    System.out.println("===== saveOrUpdateApartmentInfo END =====");
}
```

### 第2步: 查询数据库
```sql
-- 查询图片是否被保存
SELECT * FROM graph_info 
WHERE item_type = 1 
AND is_deleted = 0 
ORDER BY create_time DESC 
LIMIT 10;

-- 查询特定公寓的图片
SELECT * FROM graph_info 
WHERE item_id = 123 
AND item_type = 1 
AND is_deleted = 0;
```

### 第3步: 测试查询API
```bash
# 使用 curl 测试
curl "http://localhost:8080/app/apartment/getDetailById?id=123"

# 查看返回的 graphVoList 是否为空
```

### 第4步: 浏览器调试
```javascript
// 在 ApartmentDetail.vue 中添加调试
onMounted(async () => {
    try {
        const response = await getApartmentDetail(route.params.id);
        console.log("API Response:", response);
        console.log("GraphVoList:", response.data?.graphVoList);
        apartment.value = response.data;
    } catch (error) {
        console.error(error);
    }
});
```

---

## 📝 总结

### 代码流程正确性评估
| 步骤          | 状态 | 评估                           |
| ------------- | ---- | ------------------------------ |
| 文件上传      | ✅    | 正确，返回URL                  |
| VO 构造       | ✅    | 正确，包含 graphVoList         |
| 数据保存      | ✅    | 代码逻辑正确                   |
| itemType 设置 | ✅    | 使用 ItemType.APARTMENT (值1)  |
| itemId 设置   | ✅    | 使用 apartmentSubmitVo.getId() |
| 数据库查询    | ✅    | SQL 正确，条件完整             |
| 响应构造      | ✅    | 正确设置 graphVoList           |
| 前端展示      | ✅    | 逻辑正确                       |

### 可能的实际问题
1. **前端提交问题**: graphVoList 在提交时为空或未正确构造
2. **数据库问题**: is_deleted 标志被错误设置
3. **ID获取问题**: 新增公寓后 ID 获取失败
4. **并发问题**: 多次保存导致数据不一致
5. **URL问题**: 图片URL 格式不正确或无法访问

### 建议行动
1. 添加详细的系统日志
2. 查看数据库实际存储的数据
3. 使用 Postman 测试 API
4. 查看浏览器网络请求和响应
5. 检查后端和前端的异常日志

