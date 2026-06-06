@echo off
chcp 65001 >nul
title RentSystem - Init Database

set "ROOT=%~dp0"

echo ============================================================
echo       租房管理系统 - 数据库初始化
echo ============================================================
echo.

:: 检查 MySQL 是否可用
echo [1/3] 检查 MySQL...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo [失败] 未找到 MySQL，请确保已安装并配置了环境变量
    pause
    exit /b 1
)
for /f "delims=" %%i in ('mysql --version') do echo [OK] %%i
echo.

:: 输入数据库密码
set /p DB_PWD=请输入 MySQL 密码（root 用户）:

:: 执行 SQL 脚本
echo [2/3] 正在初始化数据库...
cd /d "%ROOT%sql"

mysql -u root -p%DB_PWD% < "lease.sql"
if errorlevel 1 (
    echo [失败] 数据库初始化失败，请检查密码是否正确
    pause
    exit /b 1
)
echo [OK] 数据库初始化完成
echo.

:: 验证
echo [3/3] 验证数据库...
mysql -u root -p%DB_PWD% -e "SHOW DATABASES LIKE 'lease';" >nul 2>&1
if errorlevel 1 (
    echo [警告] 数据库验证失败，请手动检查
) else (
    echo [OK] 数据库 lease 已创建
)
echo.

echo ============================================================
echo     数据库初始化完成！
echo ============================================================
echo.
echo  数据库名: lease
echo  用户: root
echo.
echo  现在可以双击 start-all.bat 启动项目了
echo.
pause
