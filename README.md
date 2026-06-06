# 🏠 JL租房管理系统 — Lease Management System

> **一个基于 Spring Boot 3 + Vue 3 的现代化公寓租赁管理平台，面向长租公寓场景，提供租客端 APP 与后台管理端双端支持。**

---

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue)](https://adoptium.net/)
[![Vue](https://img.shields.io/badge/Vue-3.3-4FC08D)](https://vuejs.org/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.3-red)](https://baomidou.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

---

## 📋 目录

- [🏠 JL租房管理系统 — Lease Management System](#-jl租房管理系统--lease-management-system)
  - [📋 目录](#-目录)
  - [项目简介](#项目简介)
  - [核心功能](#核心功能)
    - [🔹 租客端 (APP)](#-租客端-app)
    - [🔹 管理后台 (Admin)](#-管理后台-admin)
  - [技术架构](#技术架构)
    - [后端技术栈](#后端技术栈)
    - [前端技术栈](#前端技术栈)
    - [基础设施依赖](#基础设施依赖)
  - [项目结构](#项目结构)
    - [后端模块结构](#后端模块结构)
      - [admin 模块 Controller 层](#admin-模块-controller-层)
      - [app 模块 Controller 层](#app-模块-controller-层)
    - [前端模块结构](#前端模块结构)
  - [快速开始](#快速开始)
    - [环境要求](#环境要求)
    - [前置服务安装与启动](#前置服务安装与启动)
      - [1️⃣ MySQL](#1️⃣-mysql)
      - [2️⃣ Redis](#2️⃣-redis)
      - [3️⃣ MinIO（图片存储）](#3️⃣-minio图片存储)
    - [数据库初始化](#数据库初始化)
    - [启动项目（一键启动）](#启动项目一键启动)
    - [手动分步启动](#手动分步启动)
      - [后端（方式一：Maven）](#后端方式一maven)
      - [后端（方式二：IDE 直接运行）](#后端方式二ide-直接运行)
      - [前端](#前端)
  - [API 文档](#api-文档)
    - [管理后台 API 分组](#管理后台-api-分组)
    - [租客端 API 分组](#租客端-api-分组)
  - [核心业务流程](#核心业务流程)
    - [租客端（APP）流程](#租客端app流程)
    - [管理端流程](#管理端流程)
  - [数据模型](#数据模型)
    - [核心实体清单](#核心实体清单)
    - [枚举定义](#枚举定义)
  - [安全设计](#安全设计)
    - [🔐 身份认证](#-身份认证)
    - [🔑 JWT 设计](#-jwt-设计)
    - [🔒 拦截器机制](#-拦截器机制)
    - [📋 用户上下文](#-用户上下文)
    - [🛡️ 其他安全措施](#️-其他安全措施)
  - [关键设计说明](#关键设计说明)
    - [统一响应与异常处理](#统一响应与异常处理)
    - [图片存储方案](#图片存储方案)
    - [短信验证码](#短信验证码)
    - [定时任务](#定时任务)
  - [部署与运维](#部署与运维)
    - [生产环境构建](#生产环境构建)
    - [生产部署](#生产部署)
    - [配置说明](#配置说明)
  - [开发指南](#开发指南)
    - [环境准备](#环境准备)
    - [本地开发](#本地开发)
    - [代码扩展建议](#代码扩展建议)
  - [常见问题](#常见问题)
    - [Q: 启动后端时提示数据库连接失败？](#q-启动后端时提示数据库连接失败)
    - [Q: 图片上传后无法显示？](#q-图片上传后无法显示)
    - [Q: 前端页面无法访问？](#q-前端页面无法访问)
    - [Q: 登录提示验证码错误？](#q-登录提示验证码错误)
    - [Q: 如何修改默认端口？](#q-如何修改默认端口)
  - [许可证](#许可证)

---

## 项目简介

睿居租房管理系统（Lease Management System）是一套完整的**长租公寓租赁解决方案**，采用 **B/S 架构**，面向两类用户群体：

- **租客端 (APP)**：租客通过手机/浏览器在线浏览公寓、搜索房源、预约看房、在线签约、管理租约。
- **管理后台 (Admin)**：公寓管理员通过后台系统管理公寓信息、房间资源、租约合同、用户账号、系统岗位等。

系统设计遵循 RESTful API 规范，前后端完全分离，具备良好的可扩展性和维护性。

---

## 核心功能

### 🔹 租客端 (APP)

| 功能模块       | 功能描述                                   |
| -------------- | ------------------------------------------ |
| 📱 **登录注册** | 手机号 + 短信验证码登录，新用户自动注册    |
| 🏢 **公寓浏览** | 查看公寓详情、配套设施、费用信息、图片展示 |
| 🛏️ **房源搜索** | 多条件分页检索房间（按区域、价格、户型等） |
| 📅 **预约看房** | 在线提交看房预约，查看预约状态             |
| 📄 **在线签约** | 发起签约、确认签约、续约、退租操作         |
| 📖 **浏览历史** | 自动记录最近浏览的房源信息                 |
| 👤 **个人信息** | 头像、昵称等基础信息维护                   |

### 🔹 管理后台 (Admin)

| 功能模块       | 功能描述                                                   |
| -------------- | ---------------------------------------------------------- |
| 🔐 **系统登录** | 图形验证码 + 用户名密码登录，JWT 鉴权                      |
| 🏠 **公寓管理** | 公寓信息的增删改查、发布/下架管理                          |
| 🚪 **房间管理** | 房间信息的增删改查、发布/下架、按公寓分组                  |
| 🏷️ **属性管理** | 配套设施、费用项目、标签、支付方式、租期、房间基础属性维护 |
| 📋 **租约管理** | 租约的创建、编辑、审核、状态变更（签约/退租/续约）         |
| 📅 **预约管理** | 看房预约的查询、审核、状态更新                             |
| 👥 **用户管理** | 租客信息查询、账号状态管理（启用/禁用）                    |
| 👮 **系统管理** | 后台用户管理、岗位管理、权限控制                           |
| 🗺️ **地区管理** | 省/市/区三级联动数据                                       |
| 🖼️ **文件上传** | 基于 MinIO 的对象存储，支持图片上传与访问                  |

---

## 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                    前端展示层                              │
│  ┌─────────────────────┐  ┌───────────────────────────┐ │
│  │  租客端 (Vue 3)     │  │  管理后台 (Vue 3 + TS)    │ │
│  │  Vite + Pinia       │  │  Vite + Element Plus      │ │
│  │  端口: 5174         │  │  端口: 5173               │ │
│  └─────────┬───────────┘  └───────────┬───────────────┘ │
└────────────┼───────────────────────────┼─────────────────┘
             │          HTTP/REST         │
┌────────────┴───────────────────────────┴─────────────────┐
│                    网关层/API                             │
│     CORS 跨域支持 ─── JWT 身份认证拦截器                    │
└────────────┬───────────────────────────┬─────────────────┘
             │                           │
┌────────────┴───────────┐   ┌───────────┴───────────────┐
│   APP API (端口 8081)  │   │  Admin API (端口 8080)    │
│   Spring Boot 3        │   │  Spring Boot 3            │
│   Knife4j API 文档     │   │  Knife4j API 文档         │
└────────────┬───────────┘   └───────────┬───────────────┘
             │                           │
┌────────────┴───────────────────────────┴─────────────────┐
│                   公共服务层 (common)                      │
│  ┌────────┐ ┌──────────┐ ┌─────────┐ ┌──────────────┐   │
│  │ 统一   │ │ 全局     │ │ MinIO   │ │ JWT          │   │
│  │ 响应   │ │ 异常处理  │ │ 文件存储│ │ 令牌工具     │   │
│  └────────┘ └──────────┘ └─────────┘ └──────────────┘   │
└───────────────────────────────────────────────────────────┘
                           │
┌──────────────────────────────────────────────────────────┐
│                    基础设施层                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │  MySQL   │  │  Redis   │  │  MinIO   │  │ 阿里云  │ │
│  │  数据库  │  │  缓存    │  │ 对象存储 │  │  SMS    │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
└──────────────────────────────────────────────────────────┘
```

### 后端技术栈

| 技术             | 版本    | 用途说明                       |
| ---------------- | ------- | ------------------------------ |
| **Java**         | 17      | 编程语言 (LTS)                 |
| **Spring Boot**  | 3.0.5   | 应用框架                       |
| **MyBatis-Plus** | 3.5.3.1 | ORM 框架，简化数据库操作       |
| **MySQL**        | 8.0     | 关系型数据库                   |
| **Redis**        | 7+      | 缓存（验证码存储、会话管理）   |
| **MinIO**        | 最新    | 对象存储（图片等非结构化数据） |
| **Knife4j**      | 4.1.0   | API 文档 (OpenAPI 3)           |
| **JWT** (jjwt)   | 0.11.2  | 无状态身份认证                 |
| **EasyCaptcha**  | 1.6.2   | 图形验证码                     |
| **Hutool**       | 5.x     | 工具类库                       |
| **阿里云 SMS**   | 2.0.23  | 短信验证码发送                 |
| **Lombok**       | 1.18.38 | 减少样板代码                   |
| **HikariCP**     | 内嵌    | 高性能数据库连接池             |

### 前端技术栈

| 模块             | 技术                                                         | 说明             |
| ---------------- | ------------------------------------------------------------ | ---------------- |
| **租客端 (APP)** | Vue 3 + Vite 8 + Pinia + Vue Router 5 + Element Plus         | 轻量、响应式     |
| **管理后台**     | Vue 3 + Vite 4 + TypeScript + Pinia + Element Plus + ECharts | 企业级中后台方案 |

### 基础设施依赖

| 组件          | 用途                   | 默认端口 |
| ------------- | ---------------------- | -------- |
| **MySQL 8.0** | 持久化存储核心业务数据 | 3306     |
| **Redis**     | 验证码缓存、用户会话   | 6379     |
| **MinIO**     | 图片等非结构化文件存储 | 9000     |

---

## 项目结构

### 后端模块结构

```
lease/
├── pom.xml                        # 父 POM（聚合工程）
├── model/                         # 数据模型模块
│   ├── src/main/java/.../entity/  #   数据表实体类（30+ 实体）
│   └── src/main/java/.../enums/   #   枚举定义（状态、类型等）
├── common/                        # 公共基础设施模块
│   ├── constant/                  #   常量定义（Redis key 等）
│   ├── exception/                 #   自定义异常 + 全局异常处理
│   ├── login/                     #   登录用户上下文 (ThreadLocal)
│   ├── minio/                     #   MinIO 客户端配置
│   ├── mybatisplus/               #   MyBatis-Plus 配置（分页、自动填充）
│   ├── result/                    #   统一响应体 + 状态码枚举
│   └── utils/                     #   JWT 工具类
├── web/                           # Web 层聚合模块
│   ├── web-admin/                 #   管理后台 API（端口 8080）
│   │   ├── controller/            #     Controller 层
│   │   ├── service/               #     Service 层
│   │   ├── mapper/                #     Mapper 层
│   │   ├── custom/                #     自定义配置（拦截器、转换器、Knife4j）
│   │   ├── vo/                    #     视图对象
│   │   └── schedule/              #     定时任务
│   └── web-app/                   #   租客端 API（端口 8081）
│       ├── controller/            #     Controller 层
│       ├── service/               #     Service 层
│       ├── mapper/                #     Mapper 层
│       ├── custom/                #     自定义配置（拦截器、Knife4j）
│       └── vo/                    #     视图对象
└── docs/                          # 文档目录（可研报告、需求说明、设计文档等）
```

#### admin 模块 Controller 层

| Controller                  | 路由前缀             | 功能                       |
| --------------------------- | -------------------- | -------------------------- |
| `LoginController`           | `/admin/login`       | 登录、验证码、获取用户信息 |
| `ApartmentController`       | `/admin/apartment`   | 公寓 CRUD、发布状态管理    |
| `RoomController`            | `/admin/room`        | 房间 CRUD、发布状态管理    |
| `AttrController`            | `/admin/attr`        | 房间属性管理               |
| `FacilityController`        | `/admin/facility`    | 配套设施管理               |
| `FeeController`             | `/admin/fee`         | 费用项目管理               |
| `LabelController`           | `/admin/label`       | 标签管理                   |
| `PaymentTypeController`     | `/admin/payment`     | 支付方式管理               |
| `LeaseTermController`       | `/admin/term`        | 租期管理                   |
| `RegionInfoController`      | `/admin/region`      | 地区信息管理               |
| `FileUploadController`      | `/admin/file`        | 文件上传（MinIO）          |
| `LeaseAgreementController`  | `/admin/agreement`   | 租约管理                   |
| `ViewAppointmentController` | `/admin/appointment` | 预约管理                   |
| `UserInfoController`        | `/admin/user`        | 租客用户管理               |
| `SystemUserController`      | `/admin/system/user` | 后台用户管理               |
| `SystemPostController`      | `/admin/system/post` | 岗位管理                   |

#### app 模块 Controller 层

| Controller                  | 路由前缀           | 功能                             |
| --------------------------- | ------------------ | -------------------------------- |
| `LoginController`           | `/app/`            | 短信验证码、登录、用户信息       |
| `ApartmentController`       | `/app/apartment`   | 公寓详情                         |
| `RoomController`            | `/app/room`        | 房间搜索、分页、详情             |
| `RegionController`          | `/app/region`      | 省/市/区三级联动                 |
| `PaymentTypeController`     | `/app/payment`     | 支付方式查询                     |
| `LeaseTermController`       | `/app/term/`       | 租期查询                         |
| `LeaseAgreementController`  | `/app/agreement`   | 租约管理（列表、详情、状态更新） |
| `ViewAppointmentController` | `/app/appointment` | 看房预约（CRUD）                 |
| `BrowsingHistoryController` | `/app/history`     | 浏览历史                         |

### 前端模块结构

```
frontend-source/
├── admin/                         # 管理后台前端（Vue 3 + TypeScript）
│   ├── src/
│   │   ├── api/                   #   接口封装
│   │   ├── views/                 #   页面视图
│   │   │   ├── apartmentManagement/  # 公寓/房间/属性管理
│   │   │   ├── rentManagement/       # 租约/预约管理
│   │   │   ├── userManagement/       # 用户管理
│   │   │   ├── system/               # 系统管理（用户/岗位）
│   │   │   ├── login/                # 登录页
│   │   │   └── home/                 # 首页仪表盘
│   │   ├── components/            #   通用组件 (ProTable 等)
│   │   ├── store/                 #   Pinia 状态管理
│   │   ├── router/                #   路由管理
│   │   ├── utils/                 #   工具类（封装 Axios 等）
│   │   └── styles/                #   全局样式
│   └── ...                        #   ESLint、Prettier、commitlint 配置
└── app/                           # 租客端前端（Vue 3）
    └── src/
        ├── api/                   #   接口封装
        ├── views/                 #   页面视图（首页、详情、签约、预约等）
        ├── store/                 #   Pinia 状态管理
        ├── router/                #   路由管理
        └── utils/                 #   工具类
```

---

## 快速开始

### 环境要求

| 软件        | 版本要求       | 下载地址                               |
| ----------- | -------------- | -------------------------------------- |
| **Java**    | 17+            | https://adoptium.net/                  |
| **Node.js** | 16+ (推荐 18+) | https://nodejs.org/                    |
| **MySQL**   | 8.0+           | https://dev.mysql.com/downloads/mysql/ |
| **Redis**   | 6.x / 7.x      | https://redis.io/download/             |
| **MinIO**   | 最新           | https://min.io/download                |

### 前置服务安装与启动

#### 1️⃣ MySQL

```bash
# 确保 MySQL 服务已启动，端口 3306 可连接
# Windows 通常在"服务"中启动 MySQL80 服务
net start MySQL80
```

#### 2️⃣ Redis

```bash
# 启动 Redis 服务端
redis-server
# 默认端口 6379
```

#### 3️⃣ MinIO（图片存储）

```bash
# 创建存储目录并启动 MinIO 服务
minio server D:\minio
# 默认端口 9000（API），控制台端口 9001
# 启动后访问 http://127.0.0.1:9001 创建 bucket 并命名为 "lease"
```

### 数据库初始化

```bash
# 在 delivery-package/ 目录下执行
# 输入 MySQL 密码即可自动创建数据库和所有表
init-database.bat
```

> **或手动导入**：将 `sql/lease.sql` 导入 MySQL

### 启动项目（一键启动）

```bash
# 在 delivery-package/ 目录下双击
start-all.bat
```

该脚本将自动：
1. ✅ 检查 Java 与 Node.js 环境
2. ✅ 启动 Admin 后端服务（端口 8080）
3. ✅ 启动 APP 后端服务（端口 8081）
4. ✅ 安装 Admin 前端依赖并启动开发服务器（端口 5173）
5. ✅ 安装 APP 前端依赖并启动开发服务器（端口 5174）

**启动后访问：**

| 模块       | 地址                  |
| ---------- | --------------------- |
| 管理后台   | http://localhost:5173 |
| 租客端 APP | http://localhost:5174 |
| Admin API  | http://localhost:8080 |
| APP API    | http://localhost:8081 |

**默认管理账号：** `admin` / `admin123`

> **停止服务：** 双击 `stop-all.bat`

### 手动分步启动

#### 后端（方式一：Maven）

```bash
# 编译打包全部模块
cd lease
mvn clean package -DskipTests

# 启动管理后端
java -jar web/web-admin/target/web-admin-1.0-SNAPSHOT.jar

# 启动租客端后端（新开终端）
java -jar web/web-app/target/web-app-1.0-SNAPSHOT.jar
```

#### 后端（方式二：IDE 直接运行）

- 启动 `AdminWebApplication`（端口 8080）
- 启动 `AppWebApplication`（端口 8081）

#### 前端

```bash
# 管理后台
cd frontend-source/admin
npm install
npm run dev

# 租客端 APP（新开终端）
cd frontend-source/app
npm install
npm run dev
```

---

## API 文档

系统集成了 **Knife4j**（Swagger 增强），启动后端后可直接访问：

| 模块               | API 文档地址                   |
| ------------------ | ------------------------------ |
| **管理后台 API**   | http://localhost:8080/doc.html |
| **租客端 APP API** | http://localhost:8081/doc.html |

> Knife4j 将接口按业务分组展示，支持在线调试。

### 管理后台 API 分组

| 分组         | 包含接口                                    |
| ------------ | ------------------------------------------- |
| 后台登录管理 | 登录、验证码、用户信息                      |
| 公寓信息管理 | 公寓/房间/标签/设施/费用/属性/文件上传/地区 |
| 租赁信息管理 | 预约看房、租约管理                          |
| 平台用户管理 | 租客用户管理                                |
| 系统信息管理 | 后台用户管理、岗位管理                      |

### 租客端 API 分组

| 分组     | 包含接口                         |
| -------- | -------------------------------- |
| 登录信息 | 登录、短信验证码、用户信息       |
| 找房信息 | 公寓、房间、支付方式、地区、租期 |
| 个人信息 | 浏览历史、预约、租约             |

---

## 核心业务流程

### 租客端（APP）流程

```
                            ┌─────────────┐
                            │  手机号登录  │
                            │ （短信验证码）│
                            └──────┬──────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
               ┌────┴────┐   ┌────┴────┐   ┌────┴────┐
               │ 浏览公寓 │   │ 浏览房间 │   │ 浏览历史 │
               └────┬────┘   └────┬────┘   └─────────┘
                    │              │
               ┌────┴────┐   ┌────┴────┐
               │公寓详情  │   │房间详情  │
               │(设施/费用)│   │(图片/租金)│
               └────┬────┘   └────┬────┘
                    │              │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  预约看房    │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  在线签约    │
                    │ (选择租期/  │
                    │  支付方式)  │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  租约管理    │
                    │(签约/续约/  │
                    │  退租/到期) │
                    └─────────────┘
```

### 管理端流程

```
    ┌─────────────┐
    │  管理员登录  │
    │(验证码+JWT) │
    └──────┬──────┘
           │
    ┌──────┴──────────────────────────────────────┐
    │              管理后台主页                    │
    └──────┬──────────┬──────────┬──────────┬─────┘
           │          │          │          │
    ┌──────┴──┐ ┌─────┴────┐ ┌───┴────┐ ┌──┴──────┐
    │公寓管理 │ │房间管理  │ │属性管理 │ │系统管理  │
    │(CRUD/  │ │(CRUD/   │ │(设施/  │ │(用户/   │
    │ 发布)  │ │ 发布)   │ │ 费用/  │ │ 岗位)   │
    └─────────┘ └──────────┘ │ 标签/  │ └─────────┘
                             │ 支付/  │
                    ┌────────┤ 租期)  ├────────┐
                    │       └────────┘        │
              ┌─────┴─────┐            ┌──────┴──────┐
              │ 租约管理    │            │ 用户管理     │
              │(创建/审核/ │            │(查询/禁用)  │
              │ 状态变更)  │            └─────────────┘
              └───────────┘
```

---

## 数据模型

系统主要数据实体关系概览：

```
ProvinceInfo (省份) 1 ──── * CityInfo (城市) 1 ──── * DistrictInfo (区县)
                                                            │
                                                    1       │
                                            ApartmentInfo ──┘ (公寓)
                                                │
                                                │ 1
                                                │
                                           RoomInfo (房间)
                                           ────┬────
                                          │    │     │
                                     ┌────┴┐ ┌┴───┐ ┌┴─────┐
                                     │标签 │ │设施│ │属性/ │
                                     │关联 │ │关联│ │费用等│
                                     └─────┘ └────┘ └──────┘

UserInfo (租客) 1 ──── * LeaseAgreement (租约) * ──── 1 RoomInfo
                         │
                     * ──┴── 1
                   LeaseTerm (租期)
                   PaymentType (支付方式)

UserInfo 1 ──── * ViewAppointment (看房预约) * ──── 1 RoomInfo
```

### 核心实体清单

| 实体                    | 表名                      | 说明                                    |
| ----------------------- | ------------------------- | --------------------------------------- |
| `ProvinceInfo`          | `province_info`           | 省份信息                                |
| `CityInfo`              | `city_info`               | 城市信息                                |
| `DistrictInfo`          | `district_info`           | 区县信息                                |
| `ApartmentInfo`         | `apartment_info`          | 公寓信息（含地址、联系方式、发布状态）  |
| `RoomInfo`              | `room_info`               | 房间信息（房间号、租金、所属公寓）      |
| `FacilityInfo`          | `facility_info`           | 配套设施（如电梯、停车场）              |
| `LabelInfo`             | `label_info`              | 标签信息（如整租、近地铁）              |
| `FeeKey` / `FeeValue`   | `fee_key` / `fee_value`   | 费用项目（键值对结构，如电费=0.8元/度） |
| `AttrKey` / `AttrValue` | `attr_key` / `attr_value` | 房间属性（键值对，如朝向=南）           |
| `GraphInfo`             | `graph_info`              | 图片信息（关联到公寓或房间）            |
| `PaymentType`           | `payment_type`            | 支付方式（月付、季付、年付）            |
| `LeaseTerm`             | `lease_term`              | 租期（如 3个月、6个月、12个月）         |
| `LeaseAgreement`        | `lease_agreement`         | 租约合同（核心业务实体）                |
| `ViewAppointment`       | `view_appointment`        | 看房预约                                |
| `UserInfo`              | `user_info`               | 租客用户信息                            |
| `SystemUser`            | `system_user`             | 后台管理员账号                          |
| `SystemPost`            | `system_post`             | 后台岗位                                |
| `BrowsingHistory`       | `browsing_history`        | 浏览历史记录                            |

### 枚举定义

| 枚举                | 用途         | 值                                                                                      |
| ------------------- | ------------ | --------------------------------------------------------------------------------------- |
| `LeaseStatus`       | 租约状态     | 签约待确认(1)、已签约(2)、已取消(3)、已到期(4)、退租待确认(5)、已退租(6)、续约待确认(7) |
| `AppointmentStatus` | 预约状态     | 待看房、已看房、已取消                                                                  |
| `BaseStatus`        | 基础状态     | 启用/禁用                                                                               |
| `ReleaseStatus`     | 发布状态     | 已发布/未发布                                                                           |
| `ItemType`          | 图片关联类型 | 公寓(1)、房间(2)                                                                        |
| `LeaseSourceType`   | 租约来源     | —                                                                                       |
| `SystemUserType`    | 管理员类型   | —                                                                                       |

---

## 安全设计

### 🔐 身份认证

| 模块         | 认证方式                   | Token 格式                                     |
| ------------ | -------------------------- | ---------------------------------------------- |
| **管理后台** | 用户名 + 密码 + 图形验证码 | JWT，通过 `access-token` Header 传递           |
| **租客端**   | 手机号 + 短信验证码        | JWT，通过 `Authorization: Bearer <token>` 传递 |

### 🔑 JWT 设计

- 使用 `jjwt` 库生成和解析 JSON Web Token
- Token 载荷包含 `userId` 和 `username`
- 无状态设计，服务端不存储会话

### 🔒 拦截器机制

- **管理后台**：`AuthenInterceptor` 拦截 `/admin/**`（排除登录和文件上传），从 Header 提取 JWT 并解析用户信息
- **租客端**：`AuthenticationInterceptor` 拦截 `/app/**`（排除登录接口），同时校验用户账号是否被禁用

### 📋 用户上下文

- 采用 `ThreadLocal` + `LoginUserHolder` 存储当前请求的用户信息
- 请求完成后在 `afterCompletion` 中清除上下文，防止内存泄漏

### 🛡️ 其他安全措施

- 后端密码存储使用 MD5 哈希（`DigestUtils.md5Hex`）
- CORS 支持跨域访问（移动端、多 IP 场景）
- 全局异常处理避免信息泄露

---

## 关键设计说明

### 统一响应与异常处理

系统的 Controller 层统一返回 `Result<T>` 对象，确保前后端交互格式一致：

```json
{
  "code": 200,
  "message": "成功",
  "data": { ... }
}
```

- 成功：`Result.ok(data)`
- 失败：`Result.fail(code, message)`
- 业务异常：抛出 `LeaseException`，由 `GlobalExceptionHandler` 统一捕获并格式化返回

### 图片存储方案

采用 **MinIO** 作为对象存储解决方案：

- **优势**：兼容 S3 API，轻量级，支持私有化部署
- **配置**：通过 `application.yml` 中的 `minio` 配置项设置连接信息
- **外部访问**：通过 `external-url` 配置确保移动端可正常加载图片（避免 `127.0.0.1` 被手机解析为本机）

### 短信验证码

- 租客端登录使用手机号 + 短信验证码方式
- 使用 **阿里云 SMS SDK** (dysmsapi20170525) 发送验证码
- 验证码存储在 Redis 中，设置过期时间（10 分钟）和重发间隔（60 秒）

### 定时任务

系统在管理后台模块中启用了 Spring 定时任务（`@EnableScheduling`），实现租约状态的自动更新：

```java
@Scheduled(cron = "0 0 0 * * *")  // 每日凌晨执行
public void checkLeaseStatus() {
    // 将已到期的租约自动标记为 EXPIRED
}
```

---

## 部署与运维

### 生产环境构建

```bash
# 构建后端
cd lease
mvn clean package -DskipTests -Pproduction

# 构建管理后台前端
cd frontend-source/admin
npm run build:prod   # 输出在 dist/ 目录

# 构建租客端前端
cd frontend-source/app
npm run build        # 输出在 dist/ 目录
```

### 生产部署

1. 将后端 jar 包部署到服务器，使用 Systemd 或 Supervisor 管理
2. 将前端构建产物部署到 Nginx / Caddy 等 Web 服务器
3. 配置反向代理将 `/api/admin` 和 `/api/app` 转发到对应后端端口

```nginx
# Nginx 配置示例
server {
    listen 80;
    server_name your-domain.com;

    # 管理后台前端
    location /admin/ {
        root /path/to/admin/dist;
        try_files $uri $uri/ /admin/index.html;
    }

    # 租客端前端
    location /app/ {
        root /path/to/app/dist;
        try_files $uri $uri/ /app/index.html;
    }

    # 后端 API 代理
    location /admin-api/ {
        proxy_pass http://127.0.0.1:8080/;
    }

    location /app-api/ {
        proxy_pass http://127.0.0.1:8081/;
    }
}
```

### 配置说明

| 配置项                | 说明                                  |
| --------------------- | ------------------------------------- |
| `spring.datasource.*` | 数据库连接配置（URL、用户名、密码）   |
| `spring.data.redis.*` | Redis 连接配置                        |
| `minio.*`             | MinIO 对象存储配置                    |
| `MINIO_PUBLIC_URL`    | 环境变量，用于覆盖 MinIO 外部访问地址 |

> **⚠️ 重要**：默认配置中包含示例密码 `wdsjk666`，生产环境部署前请务必修改！

---

## 开发指南

### 环境准备

```bash
# 克隆项目
git clone <repository-url>
cd lease
```

### 本地开发

```bash
# 启动基础设施（确保已安装 Docker 或本地服务）
# MySQL, Redis, MinIO

# 导入数据库
mysql -u root -p < delivery-package/sql/lease.sql

# 启动后端（IDE 中分别运行两个 Application 类）
# AdminWebApplication (端口 8080)
# AppWebApplication  (端口 8081)

# 启动前端
cd delivery-package/frontend-source/admin
npm install && npm run dev    # 管理后台 -> localhost:5173

cd delivery-package/frontend-source/app
npm install && npm run dev    # 租客端 -> localhost:5174
```

### 代码扩展建议

1. **新增功能模块**：在对应模块下创建 Controller → Service → Mapper 三层结构
2. **新增实体**：在 `model` 模块中添加实体类，继承 `BaseEntity`
3. **新增枚举**：在 `model/enums/` 中添加枚举，实现 `BaseEnum` 接口
4. **新增公共能力**：在 `common` 模块中添加基础设施代码

---

## 常见问题

### Q: 启动后端时提示数据库连接失败？

> 确认 MySQL 已启动，端口 3306 未被占用。检查 `application.yml` 中的用户名密码是否正确。

### Q: 图片上传后无法显示？

> 确认 MinIO 已启动并创建了名为 `lease` 的 bucket。修改 `minio.external-url` 为实际可访问的地址（局域网 IP 或域名），避免使用 `127.0.0.1`。

### Q: 前端页面无法访问？

> 确认 Node.js 版本符合要求（16+），在对应前端目录下执行 `npm install` 安装依赖。检查 Vite 开发服务器端口是否被占用。

### Q: 登录提示验证码错误？

> Redis 必须处于运行状态，验证码存储于 Redis 中。确认 Redis 端口 6379 可连接。

### Q: 如何修改默认端口？

> 修改对应模块 `application.yml` 中的 `server.port`，以及前端 Vite 配置中的代理设置。

---

## 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE) 文件。

---

<p align="center">
  <strong>睿居租房管理系统</strong> · 为长租公寓打造的一站式数字化租赁管理平台
</p>
