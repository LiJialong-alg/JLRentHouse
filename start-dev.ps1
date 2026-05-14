param(
  # MinIO 数据目录（按你的约定默认 D:\minio）
  [string]$MinioDataDir = "D:\minio",
  # （可选）用于手机/局域网访问图片的 MinIO 公网/局域网地址，例如：http://192.168.1.10:9000
  [string]$MinioPublicUrl = ""
)

$ErrorActionPreference = "Stop"

Write-Host "== 租房系统：启动开发环境 ==" -ForegroundColor Cyan

if ($MinioPublicUrl) {
  Write-Host "设置 MINIO_PUBLIC_URL=$MinioPublicUrl"
}

function Start-Cmd($Title, $WorkDir, $Command) {
  $cmd = "cd /d `"$WorkDir`" && $Command"
  Start-Process -FilePath "cmd.exe" -ArgumentList "/k", "title $Title && $cmd"
}

# 1) Redis
Start-Cmd "Redis" $PSScriptRoot "redis-server"

# 2) MinIO
Start-Cmd "MinIO" $PSScriptRoot ("minio server `"$MinioDataDir`"")

# 3) 后端（两个 Spring Boot 应用）
$leaseDir = Join-Path $PSScriptRoot "lease"

# 首次运行/未安装依赖时：先把 common/model 安装到本机 m2 仓库，避免 spring-boot:run 找不到类
Write-Host "构建后端依赖模块（common/model）..." -ForegroundColor Yellow
Push-Location $leaseDir
mvn -pl common,model -am install -DskipTests
Pop-Location

if ($MinioPublicUrl) {
  Start-Cmd "Backend-Admin(8080)" $leaseDir ("set MINIO_PUBLIC_URL=$MinioPublicUrl && mvn -pl web/web-admin spring-boot:run")
  Start-Cmd "Backend-App(8081)" $leaseDir ("set MINIO_PUBLIC_URL=$MinioPublicUrl && mvn -pl web/web-app spring-boot:run")
} else {
  Start-Cmd "Backend-Admin(8080)" $leaseDir "mvn -pl web/web-admin spring-boot:run"
  Start-Cmd "Backend-App(8081)" $leaseDir "mvn -pl web/web-app spring-boot:run"
}

# 4) 前端
$adminDir = Join-Path $PSScriptRoot "rentHouseAdmin"
$appDir = Join-Path $PSScriptRoot "rentHouseApp"

Start-Cmd "FE-Admin(Vite)" $adminDir "npm run dev"
Start-Cmd "FE-App(Vite:5174)" $appDir "npm run dev"

Write-Host ""
Write-Host "已在多个窗口中启动。手机体验：确保手机与电脑同一局域网，用浏览器访问 http://<电脑IP>:5174" -ForegroundColor Green
