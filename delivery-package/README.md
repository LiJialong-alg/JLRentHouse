# 租房管理系统 - 部署说明
此项目是 B/S 架构的 Web 系统，跟传统单机软件不同。有独立的后端服务（Java）和前端页面（Vue），依赖 MySQL（数据库）、Redis（缓存）、MinIO（图片存储）这些服务组件，没办法像传统软件那样打成一个 .exe 一键装好所有东西。

所以我的交付方式是这样：

backend/ — 后端 jar 包（相当于服务器端安装包）
frontend-source/ — 前端源码（相当于前端安装包）
sql/ — 数据库脚本
start-all.bat
 — 一键启动脚本
用户电脑只要有 Java、MySQL 这些环境，双击启动脚本就能跑起来，这已经是 Web 项目最简化的一键部署方式了

## 1. 安装环境

| 软件        | 下载地址                                      | 说明                |
| ----------- | --------------------------------------------- | ------------------- |
| Java 17+    | https://adoptium.net/                         | 运行后端            |
| Node.js 16+ | https://nodejs.org/                           | 运行前端            |
| MySQL 8.0   | https://dev.mysql.com/downloads/mysql/        | 数据库，端口 3306   |
| Redis       | https://github.com/tporadowski/redis/releases | 缓存，端口 6379     |
| MinIO       | https://min.io/download#/windows              | 图片存储，端口 9000 |

## 2. 启动前置服务

### MySQL
启动 MySQL 服务，确保端口 3306 可连接。

### Redis
启动 redis-server，确保端口 6379 可连接。

### MinIO

## 3. 初始化数据库

双击 **`init-database.bat`**，输入 MySQL 密码即可自动创建数据库和所有表。

## 4. 启动项目

