# 公寓图片管理 - 代码结构和数据流详解

## 📐 类关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端 (Vue.js)                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  ApartmentDetail.vue                                     │   │
│  │  - 调用 getApartmentDetail(id)                           │   │
│  │  - 显示 apartment.graphVoList                            │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────┬──────────────────────────────────────────────┘
                  │ HTTP API
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    后端 API (web-app)                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  ApartmentController (/app/apartment/getDetailById)      │   │
│  │  - @GetMapping("getDetailById")                          │   │
│  │  - 调用 ApartmentInfoService.getDetailById(id)           │   │
│  │  - 返回 ApartmentDetailVo                                │   │
│  └───────────────────┬────────────────────────────────────┘    │
│                      │                                          │
│  ┌───────────────────▼────────────────────────────────────┐    │
│  │  ApartmentInfoServiceImpl (web-app)                     │    │
│  │  - getDetailById(Long id)                              │    │
│  │  - 调用 graphInfoMapper.selectListByItemTypeAndId()    │    │
│  │  - 调用 labelInfoMapper.selectListByApartmentId()      │    │
│  │  - 调用 facilityInfoMapper.selectListByApartmentId()   │    │
│  └──────────────────┬──────────────────────────────────┘     │
│                     │                                          │
│  ┌──────────────────▼──────────────────────────────────┐      │
│  │  GraphInfoMapper (web-app)                          │      │
│  │  - selectListByItemTypeAndId(ItemType, Long)        │      │
│  │  SQL: SELECT name, url FROM graph_info              │      │
│  │       WHERE is_deleted = 0                          │      │
│  │       AND item_type = #{itemType}                   │      │
│  │       AND item_id = #{id}                           │      │
│  └──────────────────┬──────────────────────────────────┘      │
└─────────────────────┼────────────────────────────────────────┘
                      │ SQL 查询
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                        MySQL 数据库                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  graph_info 表                                           │   │
│  │  ┌─────────┬──────────┬───────────┬─────────────────┐   │   │
│  │  │ item_id │item_type │   name    │      url        │   │   │
│  │  ├─────────┼──────────┼───────────┼─────────────────┤   │   │
│  │  │   123   │    1     │   主卧    │  http://...jpg  │   │   │
│  │  │   123   │    1     │   客厅    │  http://...jpg  │   │   │
│  │  │   123   │    1     │   厨房    │  http://...jpg  │   │   │
│  │  └─────────┴──────────┴───────────┴─────────────────┘   │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 数据流序列图

```
客户端                   后端                   数据库
   │                     │                       │
   │──GET /app/apartment/getDetailById?id=123──→│
   │                     │                       │
   │                 ┌───┴─────────┐             │
   │                 │ new          │             │
   │                 │ ApartmentDetailVo         │
   │                 └───┬─────────┘             │
   │                     │                       │
   │                     ├──SELECT * FROM        │
   │                     │  apartment_info───────→│
   │                     │  WHERE id = 123       │
   │                     │                       │
   │                     │←─ ApartmentInfo ──────│
   │                     │  {id: 123, name...}   │
   │                     │                       │
   │                     │──SELECT name, url     │
   │                     │  FROM graph_info──────→│
   │                     │  WHERE item_type=1    │
   │                     │  AND item_id=123      │
   │                     │                       │
   │                     │←─ GraphVo[] ─────────│
   │                     │  [{name: "主卧", url: "http://..."}]
   │                     │                       │
   │                 ┌───┴──────────────┐        │
   │                 │ copy properties  │        │
   │                 │ set graphVoList  │        │
   │                 └───┬──────────────┘        │
   │                     │                       │
   │←─ ApartmentDetailVo ─────────────────────  │
   │  {id: 123, name: "公寓A",                  │
   │   graphVoList: [{name: "主卧",            │
   │   url: "http://..."}], ...}               │
   │
   ├─ 前端解析 graphVoList
   ├─ v-if="apartment.graphVoList?.length" 通过
   ├─ 遍历每个图片
   ├─ <img :src="img.url" />
   └─ 图片显示 ✅
```

---

## 📦 数据对象转换

### 1. 前端 → 后端 (保存图片)

**前端构造**:
```javascript
const formData = {
  id: 123,                        // 公寓ID
  name: "公寓A",
  phone: "138xxxxx",
  graphVoList: [
    {
      name: "主卧",
      url: "http://minio:9000/bucket/20240507/uuid-1.jpg"
    },
    {
      name: "客厅",
      url: "http://minio:9000/bucket/20240507/uuid-2.jpg"
    }
  ],
  facilityInfoIds: [1, 2, 3],
  labelIds: [4, 5],
  feeValueIds: [6, 7]
}
```

**JSON 序列化**:
```json
{
  "id": 123,
  "name": "公寓A",
  "phone": "138xxxxx",
  "graphVoList": [
    {
      "name": "主卧",
      "url": "http://minio:9000/bucket/20240507/uuid-1.jpg"
    },
    {
      "name": "客厅",
      "url": "http://minio:9000/bucket/20240507/uuid-2.jpg"
    }
  ],
  "facilityInfoIds": [1, 2, 3],
  "labelIds": [4, 5],
  "feeValueIds": [6, 7]
}
```

**后端反序列化为**:
```java
ApartmentSubmitVo {
  id: 123L,
  name: "公寓A",
  phone: "138xxxxx",
  graphVoList: [
    GraphVo { name: "主卧", url: "http://minio:9000/bucket/20240507/uuid-1.jpg" },
    GraphVo { name: "客厅", url: "http://minio:9000/bucket/20240507/uuid-2.jpg" }
  ],
  facilityInfoIds: [1L, 2L, 3L],
  labelIds: [4L, 5L],
  feeValueIds: [6L, 7L]
}
```

**转换为 GraphInfo**:
```java
for (GraphVo graphVo : graphVoList) {
  GraphInfo graphInfo = new GraphInfo();
  graphInfo.setName("主卧");                           // 来自 graphVo.name
  graphInfo.setItemType(ItemType.APARTMENT);          // 枚举: 值为 1
  graphInfo.setItemId(123L);                          // 来自 apartmentSubmitVo.id
  graphInfo.setUrl("http://minio:9000/bucket/20240507/uuid-1.jpg");
}
```

**保存到数据库**:
```sql
INSERT INTO graph_info 
(id, name, item_type, item_id, url, is_deleted, create_time, update_time)
VALUES
(nextval('seq_graph_info'), '主卧', 1, 123, 'http://...', 0, NOW(), NOW()),
(nextval('seq_graph_info'), '客厅', 1, 123, 'http://...', 0, NOW(), NOW());
```

---

### 2. 数据库 → 前端 (查询图片)

**数据库查询**:
```sql
SELECT id, name, url FROM graph_info
WHERE is_deleted = 0
AND item_type = 1
AND item_id = 123;
```

**结果集**:
```
┌────┬────────┬────────────────────────────────────┐
│ id │ name   │ url                                │
├────┼────────┼────────────────────────────────────┤
│ 1  │ 主卧   │ http://minio:9000/bucket/.../1.jpg│
│ 2  │ 客厅   │ http://minio:9000/bucket/.../2.jpg│
└────┴────────┴────────────────────────────────────┘
```

**Mapper 返回 GraphVo**:
```java
List<GraphVo> graphVoList = [
  GraphVo { name: "主卧", url: "http://minio:9000/bucket/.../1.jpg" },
  GraphVo { name: "客厅", url: "http://minio:9000/bucket/.../2.jpg" }
];
```

**Service 构建 ApartmentDetailVo**:
```java
ApartmentDetailVo {
  id: 123L,
  name: "公寓A",
  graphVoList: [
    GraphVo { name: "主卧", url: "http://minio:9000/bucket/.../1.jpg" },
    GraphVo { name: "客厅", url: "http://minio:9000/bucket/.../2.jpg" }
  ],
  labelInfoList: [...],
  facilityInfoList: [...],
  minRent: 2000.00
}
```

**JSON 序列化**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 123,
    "name": "公寓A",
    "graphVoList": [
      {
        "name": "主卧",
        "url": "http://minio:9000/bucket/20240507/uuid-1.jpg"
      },
      {
        "name": "客厅",
        "url": "http://minio:9000/bucket/20240507/uuid-2.jpg"
      }
    ],
    "labelInfoList": [...],
    "facilityInfoList": [...],
    "minRent": 2000.00
  }
}
```

**前端解析**:
```javascript
const apartment = response.data;  // 获取 ApartmentDetailVo
console.log(apartment.graphVoList);  // 数组
apartment.graphVoList.forEach(img => {
  console.log(img.name, img.url);
});
```

**前端 Vue 渲染**:
```vue
<el-carousel v-if="apartment.graphVoList?.length">
  <el-carousel-item v-for="img in apartment.graphVoList" :key="img.url">
    <img :src="img.url" />
  </el-carousel-item>
</el-carousel>
```

---

## 🔗 关键类的核心方法

### ApartmentInfoServiceImpl (web-admin)

```java
public void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo) {
    // 步骤1: 保存基本信息
    Boolean isUpdate = apartmentSubmitVo.getId() != null;
    super.saveOrUpdate(apartmentSubmitVo);
    
    // 此时 apartmentSubmitVo.getId() 已被赋值 (如果是新增)
    Long apartmentId = apartmentSubmitVo.getId();
    
    // 步骤2: 如果是更新，删除旧关联数据
    if (isUpdate) {
        LambdaQueryWrapper<GraphInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT)
               .eq(GraphInfo::getItemId, apartmentId);
        graphInfoService.remove(wrapper);  // DELETE FROM graph_info WHERE ...
    }
    
    // 步骤3: 新增图片列表
    List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
    if(!CollectionUtils.isEmpty(graphVoList)){
        ArrayList<GraphInfo> graphInfos = new ArrayList<>();
        
        for (GraphVo graphVo : graphVoList) {
            GraphInfo graphInfo = new GraphInfo();
            graphInfo.setName(graphVo.getName());
            graphInfo.setItemType(ItemType.APARTMENT);      // 关键: ItemType.APARTMENT
            graphInfo.setItemId(apartmentId);               // 关键: 从 apartmentSubmitVo 获取
            graphInfo.setUrl(graphVo.getUrl());
            graphInfos.add(graphInfo);
        }
        
        graphInfoService.saveBatch(graphInfos);  // 批量插入
    }
}

public ApartmentDetailVo getDetailById(Long id) {
    // 查询基本信息
    ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
    
    // 查询图片列表 ⭐ 关键查询
    List<GraphVo> graphVoList = graphInfoMapper.selectListById(
        ItemType.APARTMENT,  // 关键: 指定类型为APARTMENT (值1)
        id                   // 关键: 指定公寓ID
    );
    
    // 构建响应
    ApartmentDetailVo dto = new ApartmentDetailVo();
    BeanUtils.copyProperties(apartmentInfo, dto);
    dto.setGraphVoList(graphVoList);  // 设置图片列表
    
    return dto;
}
```

### ApartmentInfoServiceImpl (web-app)

```java
public ApartmentDetailVo getDetailById(Long id) {
    // 查询基本信息
    ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
    
    // 查询图片列表 ⭐ 关键查询
    // 注意: web-app 使用不同的 Mapper 方法名
    List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(
        ItemType.APARTMENT,  // 关键: 指定类型为APARTMENT
        id                   // 关键: 指定公寓ID
    );
    
    // 查询其他关联信息
    List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);
    List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);
    BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);
    
    // 构建响应
    ApartmentDetailVo dto = new ApartmentDetailVo();
    BeanUtils.copyProperties(apartmentInfo, dto);
    dto.setGraphVoList(graphVoList);              // 设置图片列表
    dto.setLabelInfoList(labelInfoList);
    dto.setFacilityInfoList(facilityInfoList);
    dto.setMinRent(minRent);
    
    return dto;
}
```

---

## 💾 ItemType 枚举详解

```java
public enum ItemType implements BaseEnum {
    APARTMENT(1, "公寓"),      // code = 1, 用于标识公寓相关的图片/评价/等
    ROOM(2, "房间");          // code = 2, 用于标识房间相关的图片/评价/等
    
    @EnumValue      // MyBatis 序列化值
    @JsonValue      // JSON 序列化值
    private Integer code;
    
    private String name;
}
```

**数据库中的值**:
```
ItemType.APARTMENT → 数据库存储为 1
ItemType.ROOM → 数据库存储为 2
```

**查询示例**:
```sql
-- 查询所有公寓的图片
WHERE item_type = 1 AND item_id = 123

-- 查询所有房间的图片
WHERE item_type = 2 AND item_id = 456
```

---

## 🔍 SQL 查询语句

### 保存图片

```sql
-- 插入公寓 123 的图片
INSERT INTO graph_info (
  id,           -- 自增或UUID生成
  name,         -- "主卧"
  item_type,    -- 1 (ItemType.APARTMENT)
  item_id,      -- 123 (公寓ID)
  url,          -- "http://minio:9000/bucket/..."
  is_deleted,   -- 0
  create_time,  -- NOW()
  update_time   -- NOW()
) VALUES (
  NEXT_VAL('seq_graph_info'),
  '主卧',
  1,
  123,
  'http://minio:9000/bucket/20240507/uuid-1.jpg',
  0,
  NOW(),
  NOW()
);
```

### 查询图片

```sql
-- web-admin 查询 (selectListById)
SELECT name, url FROM graph_info
WHERE is_deleted = 0
AND item_id = 123
AND item_type = 1;

-- web-app 查询 (selectListByItemTypeAndId)
SELECT name, url FROM graph_info
WHERE is_deleted = 0
AND item_type = 1
AND item_id = 123;

-- 逻辑完全相同，只是参数顺序不同
```

### 删除图片

```sql
-- 更新时删除旧图片
UPDATE graph_info
SET is_deleted = 1, update_time = NOW()
WHERE item_type = 1
AND item_id = 123
AND is_deleted = 0;

-- 或硬删除
DELETE FROM graph_info
WHERE item_type = 1
AND item_id = 123;
```

---

## 📋 关键参数对照表

| 参数        | 值           | 来源                      | 用途             |
| ----------- | ------------ | ------------------------- | ---------------- |
| itemType    | 1            | ItemType.APARTMENT        | 标识是公寓的图片 |
| itemId      | 123          | apartmentSubmitVo.getId() | 关联到特定公寓   |
| name        | "主卧"       | graphVo.getName()         | 图片的描述名称   |
| url         | "http://..." | graphVo.getUrl()          | 图片的访问地址   |
| is_deleted  | 0            | 新增时                    | 标记为未删除     |
| create_time | NOW()        | 数据库                    | 创建时间         |
| update_time | NOW()        | 数据库                    | 更新时间         |

---

## 🔧 常见问题排查对照表

| 问题现象       | 可能原因          | 排查方法                      |
| -------------- | ----------------- | ----------------------------- |
| 图片无法显示   | graphVoList 为空  | 检查SQL查询结果               |
| 图片无法显示   | item_type 错误    | 检查数据库中的item_type值     |
| 图片无法显示   | item_id 错误      | 检查数据库中的item_id值       |
| 图片无法显示   | is_deleted = 1    | 检查逻辑删除标志              |
| 图片无法显示   | URL 无效          | 检查URL格式和访问权限         |
| 更新后丢失图片 | 删除旧图片失败    | 检查删除逻辑                  |
| 新增公寓无ID   | saveOrUpdate 失败 | 检查ApartmentInfo的ID生成策略 |
| 图片URL 重复   | UUID 冲突         | 非常罕见，检查UUID生成器      |

---

## 🎯 验证清单

- [ ] 后端收到 graphVoList 不为空
- [ ] graphVoList 中的每个对象都有 url
- [ ] itemType 被正确设置为 ItemType.APARTMENT
- [ ] itemId 被正确设置为公寓ID
- [ ] 数据库 graph_info 表有新记录
- [ ] is_deleted 字段值为 0
- [ ] SQL 查询条件包括 is_deleted = 0
- [ ] SQL 查询条件包括 item_type = 1
- [ ] SQL 查询条件包括 item_id = 公寓ID
- [ ] 返回的 graphVoList 不为空
- [ ] 前端接收到 graphVoList
- [ ] 前端 v-if 条件通过
- [ ] 图片 URL 可正常访问
- [ ] 图片显示在App端

