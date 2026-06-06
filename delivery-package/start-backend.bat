@echo off
chcp 65001 >nul
title RentSystem - Start Backend

echo ============================================================
echo       Rent System - Backend Service Starter
echo ============================================================
echo.

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Java not found! Please install Java 17+
    pause
    exit /b 1
)
echo [OK] Java found
echo.

:: Start backend services
cd /d "%~dp0backend"

echo [1/2] Starting Admin API (port 8080)...
start "Rent-Admin" java -Xmx256m -jar web-admin.jar --server.port=8080 --server.servlet.context-path=
timeout /t 3 /nobreak >nul

echo [2/2] Starting App API (port 8081)...
start "Rent-App" java -Xmx256m -jar web-app.jar --server.port=8081 --server.servlet.context-path=
echo.
echo [OK] Backend services started
echo.
echo   Admin API: http://localhost:8080
echo   App API:   http://localhost:8081
echo.
echo Make sure MySQL, Redis, MinIO are running.
echo.
pause

