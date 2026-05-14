# 租房管理系统

## 项目结构

- **lease/**: 后端项目
- **rentHouseApp/**: 移动端前端项目
- **rentHouseWeb/**: 网页端前端项目

## 技术栈

### 后端
- Java Spring Boot
- MyBatis-Plus
- MySQL
- Swagger

### 前端
- Vue 3
- Vite
- Element Plus
- Pinia

## 快速开始

> 如果你希望手机在同一局域网下能完整体验（尤其是 MinIO 图片访问），请务必阅读“局域网/真机体验”小节。

### 后端启动
后端分为两个服务：
- 管理端接口（8080）：`lease/web/web-admin`
- App 端接口（8081）：`lease/web/web-app`

启动方式（在 `lease` 目录执行）：
```bash
mvn -pl web/web-admin spring-boot:run
mvn -pl web/web-app spring-boot:run
```
首次运行建议先安装依赖模块到本机 m2 仓库（避免运行时找不到 common 里的类）：
```bash
mvn -pl common,model -am install -DskipTests
```

### 前端启动
1. 进入对应前端目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动开发服务器

## 局域网/真机体验（重点：图片访问 MinIO）

### 1) 启动依赖服务
- Redis：`redis-server`
- MinIO：`minio server D:\minio`

### 2) 配置 MINIO_PUBLIC_URL（推荐）
后端会把 MinIO 图片地址写入数据库/返回给前端，如果返回的是 `127.0.0.1`，手机端会打不开图片。

启动后端前设置环境变量（将 IP 换成你电脑在局域网中的 IP）：
```bash
set MINIO_PUBLIC_URL=http://192.168.1.10:9000
```

> 不设置也可以：App 端已做了 `127.0.0.1/localhost` 的兼容替换，但推荐配置成局域网 IP，链路更清晰。

### 3) 一键启动（Windows）
仓库根目录提供了脚本：
```powershell
# 可选：指定 MinIO 数据目录与对外访问地址
.\start-dev.ps1 -MinioDataDir "D:\minio" -MinioPublicUrl "http://192.168.1.10:9000"
```

### 4) 手机访问
确保手机与电脑在同一 Wi-Fi/局域网内，然后：
- 打开 App 前端（Vite）：`http://<电脑IP>:5174`

## 接口文档

后端接口文档可通过访问 `http://localhost:8081/swagger-ui.html` 查看。
