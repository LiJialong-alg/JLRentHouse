# 📋 软件物料清单（SBOM）— JL租房管理系统

> **项目名称**：JL租房管理系统  
> **版本**：1.0  
> **日期**：2026-06

---

## 一、项目概述

本项目为前后端分离的租房管理系统，包含：
- **管理后台**（Vue3 + Element Plus）：供管理员使用
- **App端**（Vue3 + Vant）：供租客使用  
- **后端**（Spring Boot）：提供REST API

---

## 二、文件组织结构

```
Project/
├── rentHouseAdmin/                    # 前端管理后台
│   ├── src/
│   │   ├── api/                      # API接口封装（自研JS逻辑）
│   │   ├── components/               # 通用组件（AI生成）
│   │   ├── views/                   # 页面视图（JS逻辑自研）
│   │   ├── styles/                  # 样式文件（AI生成）
│   │   ├── router/                  # 路由配置（自研）
│   │   ├── store/                   # 状态管理（自研）
│   │   └── utils/                   # 工具函数（AI生成+人工修改）
│   └── package.json
├── rentHouseApp/                      # 前端App端
├── lease/                            # 后端Spring Boot
│   ├── model/                        # 数据模型（框架生成）
│   ├── common/                      # 公共模块（框架生成）
│   └── web/                         # Web接口（具体逻辑自研）
└── lease.sql                        # 数据库脚本
```

---

## 三、前端管理后台文件清单

### 3.1 API接口层 `src/api/`（🔧 自研JS逻辑）

| 文件路径                           | 来源   | 用途说明                         |
| ---------------------------------- | ------ | -------------------------------- |
| `api/user/index.ts`                | 🔧 自研 | 用户登录、注册、认证接口封装     |
| `api/user/types.ts`                | 🔧 自研 | 用户模块TypeScript类型定义       |
| `api/rentManagement/index.ts`      | 🔧 自研 | 租赁管理接口（预约、合同）       |
| `api/rentManagement/types.ts`      | 🔧 自研 | 租赁管理类型定义                 |
| `api/apartmentManagement/index.ts` | 🔧 自研 | 公寓管理接口                     |
| `api/apartmentManagement/types.ts` | 🔧 自研 | 公寓管理类型定义                 |
| `api/system/index.ts`              | 🔧 自研 | 系统管理接口（用户、岗位、字典） |
| `api/system/types.ts`              | 🔧 自研 | 系统管理类型定义                 |
| `api/upload/index.ts`              | 🔧 自研 | 文件上传接口                     |

**自研说明**：所有API封装的JS逻辑均为开发者手写，根据后端接口文档封装请求参数、处理响应数据、错误处理等。

---

### 3.2 工具函数层 `src/utils/`（🤖 AI生成+人工修改）

| 文件路径              | 来源              | 用途说明                                      |
| --------------------- | ----------------- | --------------------------------------------- |
| `utils/http/index.ts` | 🤖 AI生成+人工修改 | Axios封装，含请求拦截、Token注入、401自动跳转 |

**AI生成提示词**：
```
帮我写一个Vue3项目的Axios封装类，要求：
1. 基于Axios封装统一请求方法
2. 请求拦截器：自动添加Authorization头（从localStorage取Token）
3. 响应拦截器：处理401状态码（Token过期）时跳转到登录页
4. 统一错误处理
```

**人工加工**：
- 添加了 `Token` 过期自动跳转登录页面的逻辑
- 修改了错误提示方式，使用 `ElMessage` 替代原生 `alert`
- 添加了请求超时配置

---

### 3.3 样式层 `src/styles/`（🤖 AI生成）

| 文件路径               | 来源     | 用途说明                 |
| ---------------------- | -------- | ------------------------ |
| `styles/variable.scss` | 🤖 AI生成 | 主题变量定义（浅蓝主题） |
| `styles/element.scss`  | 🤖 AI生成 | Element Plus组件样式覆盖 |
| `styles/global.scss`   | 🤖 AI生成 | 全局样式、滚动条等       |
| `styles/index.scss`    | 🔧 自研   | 样式入口文件             |

**AI生成提示词**：
```
请帮我创建一个Vue3 + Element Plus项目的主题样式文件，要求：
1. 主题色为浅蓝色（#1890ff或类似）
2. 覆盖按钮、表格、表单、菜单等组件的主题色
3. 统一样式变量，便于维护
```

---

### 3.4 视图层 `src/views/`（JS逻辑🔧 自研，样式🤖 AI生成）

| 文件路径                                     | JS逻辑来源 | 样式来源 | 用途说明     |
| -------------------------------------------- | ---------- | -------- | ------------ |
| `views/login/index.vue`                      | 🔧 自研     | 🤖 AI生成 | 登录页面     |
| `views/login/index.scss`                     | -          | 🤖 AI生成 | 登录页面样式 |
| `views/home/index.vue`                       | 🔧 自研     | 🤖 AI生成 | 首页/仪表盘  |
| `views/apartmentManagement/index.vue`        | 🔧 自研     | 🤖 AI生成 | 公寓管理页面 |
| `views/roomManagement/index.vue`             | 🔧 自研     | 🤖 AI生成 | 房间管理页面 |
| `views/rentManagement/appointment/index.vue` | 🔧 自研     | 🤖 AI生成 | 看房预约管理 |
| `views/rentManagement/agreement/index.vue`   | 🔧 自研     | 🤖 AI生成 | 租赁合同管理 |
| `views/system/userManagement/index.vue`      | 🔧 自研     | 🤖 AI生成 | 用户管理     |
| `views/system/postManagement/index.vue`      | 🔧 自研     | 🤖 AI生成 | 岗位管理     |
| `views/system/roleManagement/index.vue`      | 🔧 自研     | 🤖 AI生成 | 角色管理     |
| `views/system/dictionary/index.vue`          | 🔧 自研     | 🤖 AI生成 | 字典管理     |

**自研说明**：所有页面的JS逻辑（数据请求、状态管理、业务交互）均为开发者手写。

---

### 3.5 组件层 `src/components/`（🤖 AI生成）

| 文件路径                                   | 来源     | 用途说明         |
| ------------------------------------------ | -------- | ---------------- |
| `components/ProTable/index.vue`            | 🤖 AI生成 | 通用表格组件     |
| `components/SearchForm/index.vue`          | 🤖 AI生成 | 通用搜索表单组件 |
| `components/uploadImg/index.vue`           | 🤖 AI生成 | 图片上传组件     |
| `components/SvgIcon/index.vue`             | 🔧 自研   | SVG图标组件      |
| `components/DynamicComponentBox/index.vue` | 🔧 自研   | 动态组件渲染     |

---

### 3.6 路由与状态管理（🔧 自研）

| 文件路径                    | 来源   | 用途说明           |
| --------------------------- | ------ | ------------------ |
| `router/index.ts`           | 🔧 自研 | 路由配置、路由守卫 |
| `router/constantRoutes.ts`  | 🔧 自研 | 静态路由配置       |
| `store/modules/user.ts`     | 🔧 自研 | 用户状态管理       |
| `store/modules/settings.ts` | 🔧 自研 | 系统设置状态       |

---

## 四、后端文件清单

### 4.1 数据模型层 `lease/model/`（📦 框架生成）

| 文件            | 来源       | 用途说明                 |
| --------------- | ---------- | ------------------------ |
| `entity/*.java` | 📦 框架生成 | 数据库实体映射（36张表） |
| `enums/*.java`  | 🔧 自研     | 业务枚举类               |

---

### 4.2 公共模块 `lease/common/`（📦 框架生成）

| 文件                          | 来源       | 用途说明       |
| ----------------------------- | ---------- | -------------- |
| `Result.java`                 | 📦 框架生成 | 统一返回结果类 |
| `GlobalExceptionHandler.java` | 📦 框架生成 | 全局异常处理   |
| `JWTUtil.java`                | 🔧 自研     | JWT工具类      |
| `LoginUserHolder.java`        | 🔧 自研     | 用户上下文传递 |

---

### 4.3 Web接口层 `lease/web/`（🔧 自研接口逻辑）

#### 管理后台API `lease/web/web-admin/`（端口8080）

| 文件                       | 来源       | 用途说明 |
| -------------------------- | ---------- | -------- |
| `WebAdminApplication.java` | 📦 框架生成 | 启动类   |
| `application.yml`          | 📦 框架生成 | 配置文件 |

**Controller层**（🔧 自研接口逻辑）：

| 文件                             | 用途说明     |
| -------------------------------- | ------------ |
| `ApartmentController.java`       | 公寓CRUD接口 |
| `RoomController.java`            | 房间CRUD接口 |
| `ViewAppointmentController.java` | 看房预约接口 |
| `LeaseAgreementController.java`  | 租赁合同接口 |
| `SystemUserController.java`      | 用户管理接口 |
| `SystemPostController.java`      | 岗位管理接口 |
| `DictionaryController.java`      | 数据字典接口 |
| `UploadController.java`          | 文件上传接口 |
| `LoginController.java`           | 登录认证接口 |

**Service层**（🔧 自研业务逻辑）：

| 文件                          | 用途说明     |
| ----------------------------- | ------------ |
| `ApartmentService.java`       | 公寓业务逻辑 |
| `RoomService.java`            | 房间业务逻辑 |
| `ViewAppointmentService.java` | 预约业务逻辑 |
| `LeaseAgreementService.java`  | 合同业务逻辑 |
| `SystemUserService.java`      | 用户业务逻辑 |

**Mapper层**（📦 框架生成）：

| 文件            | 用途说明               |
| --------------- | ---------------------- |
| `*.Mapper.java` | MyBatis-Plus数据访问层 |

#### App端API `lease/web/web-app/`（端口8081）

| 文件                     | 来源       | 用途说明 |
| ------------------------ | ---------- | -------- |
| `WebAppApplication.java` | 📦 框架生成 | 启动类   |
| `application.yml`        | 📦 框架生成 | 配置文件 |

**Controller层**（🔧 自研接口逻辑）：

| 文件                             | 用途说明     |
| -------------------------------- | ------------ |
| `UserInfoController.java`        | 用户信息管理 |
| `ApartmentBrowseController.java` | 公寓浏览接口 |
| `RoomBrowseController.java`      | 房间浏览接口 |
| `ViewAppointmentController.java` | 看房预约接口 |
| `LeaseAgreementController.java`  | 租赁合同接口 |

---

## 五、开源依赖清单

### 5.1 前端依赖

| 依赖         | 版本  | 用途       | 许可证 |
| ------------ | ----- | ---------- | ------ |
| Vue          | 3.4.x | 前端框架   | MIT    |
| Vue Router   | 4.x   | 路由管理   | MIT    |
| Pinia        | 2.x   | 状态管理   | MIT    |
| Element Plus | 2.6.x | UI组件库   | MIT    |
| Axios        | 1.6.x | HTTP客户端 | MIT    |
| Vite         | 5.x   | 构建工具   | MIT    |

### 5.2 后端依赖

| 依赖            | 版本   | 用途       | 许可证     |
| --------------- | ------ | ---------- | ---------- |
| Spring Boot     | 3.2.x  | 后端框架   | Apache 2.0 |
| MyBatis-Plus    | 3.5.x  | ORM框架    | Apache 2.0 |
| MySQL Connector | 8.x    | 数据库驱动 | GPL        |
| Lombok          | 1.18.x | 代码生成   | MIT        |
| JJWT            | 0.11.x | JWT认证    | Apache 2.0 |

---

## 六、数据库清单

| 表名               | 说明         |
| ------------------ | ------------ |
| `system_user`      | 管理员用户表 |
| `system_post`      | 岗位表       |
| `apartment_info`   | 公寓信息表   |
| `room_info`        | 房间信息表   |
| `view_appointment` | 看房预约表   |
| `lease_agreement`  | 租赁合同表   |
| `user_info`        | 租客用户表   |
| `graph_info`       | 图片表       |

---

## 七、自研核心代码注释说明

### 7.1 前端自研JS逻辑

| 文件                          | 核心注释                                          |
| ----------------------------- | ------------------------------------------------- |
| `api/user/index.ts`           | `// 用户登录API：POST请求，返回Token`             |
| `api/rentManagement/index.ts` | `// 预约删除：调用DELETE /admin/appointment/{id}` |
| `router/index.ts`             | `// 路由守卫：检查Token，未登录跳转登录页`        |
| `store/modules/user.ts`       | `// 用户状态管理：保存Token和用户信息`            |

### 7.2 后端自研接口逻辑

| 文件                             | 核心注释                                               |
| -------------------------------- | ------------------------------------------------------ |
| `ViewAppointmentController.java` | `// 预约删除接口：RESTful DELETE，@PathVariable获取id` |
| `LoginController.java`           | `// 登录认证：验证用户名密码，生成JWT Token`           |
| `ApartmentService.java`          | `// 公寓CRUD业务逻辑处理`                              |
| `SystemUserService.java`         | `// 用户管理业务逻辑`                                  |

---

## 八、代码来源汇总

### 前端代码来源

| 来源类型   | 说明                                           | 占比 |
| ---------- | ---------------------------------------------- | ---- |
| 🔧 自研     | JS/TS逻辑（API封装、路由、状态管理、页面逻辑） | ~60% |
| 🤖 AI生成   | 样式文件、通用组件、页面模板                   | ~30% |
| 📦 开源框架 | Vue、Element Plus等                            | ~10% |

### 后端代码来源

| 来源类型   | 说明                                | 占比 |
| ---------- | ----------------------------------- | ---- |
| 🔧 自研     | Controller接口逻辑、Service业务逻辑 | ~40% |
| 📦 框架生成 | 实体类、配置文件、Mapper层          | ~50% |
| 📦 开源框架 | Spring Boot、MyBatis-Plus等         | ~10% |

---

**编制说明**：
- 前端JS逻辑（API封装、路由、状态管理、页面逻辑）均为自研
- 前端样式、组件等由AI生成
- 后端仅接口具体逻辑（Controller、Service）为自研
- 后端框架、实体类、配置等为框架生成