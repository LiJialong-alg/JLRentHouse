@echo off
chcp 65001 >nul
title RentSystem - One-Click Start

set "ROOT=%~dp0"

echo ============================================================
echo       Rent House Management System
echo       One-Click Start
echo ============================================================
echo.
echo This script will:
echo   1. Check Java / Node.js environment
echo   2. Start backend services (port 8080, 8081)
echo   3. Start frontend dev servers (port 5173, 5174)
echo      (npm install + npm run dev using source code)
echo.
echo Prerequisites (install first):
echo   - Java 17+       https://adoptium.net/
echo   - Node.js 16+    https://nodejs.org/
echo   - MySQL 8.0      (running on port 3306)
echo   - Redis          (running on port 6379)
echo   - MinIO          (running on port 9000, bucket: lease)
echo.
echo ============================================================
echo.

:: ===== Step 1: Check Java =====
echo [1/4] Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Java not found! Please install Java 17+
    pause
    exit /b 1
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do echo [OK] Java: %%i
echo.

:: ===== Step 2: Check Node.js =====
echo [2/4] Checking Node.js...
node -v >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Node.js not found! Please install Node.js 16+
    pause
    exit /b 1
)
for /f "delims=" %%i in ('node -v') do echo [OK] Node.js: %%i
echo.

:: ===== Step 3: Start Backend =====
echo [3/4] Starting backend services...
cd /d "%ROOT%backend"

echo   Starting Admin API (port 8080)...
start "Rent-Admin" java -Xmx256m -jar web-admin.jar --server.port=8080 --server.servlet.context-path=
timeout /t 3 /nobreak >nul

echo   Starting App API (port 8081)...
start "Rent-App" java -Xmx256m -jar web-app.jar --server.port=8081 --server.servlet.context-path=
timeout /t 3 /nobreak >nul
echo [OK] Backend services started
echo.

:: ===== Step 4: Start Frontend Dev Servers =====
echo [4/4] Starting frontend dev servers...

:: --- Admin Frontend ---
set "ADMIN_DIR=%ROOT%frontend-source\admin"
if not exist "%ADMIN_DIR%\package.json" (
    echo [WARN] Admin frontend not found, skipping...
    goto skip_admin
)
cd /d "%ADMIN_DIR%"
echo   Installing Admin dependencies (first time may take a while)...
call npm install
if errorlevel 1 (
    echo [FAIL] npm install failed for Admin frontend!
    pause
    exit /b 1
)
echo [OK] Admin dependencies installed
start "Rent-Admin-Frontend" cmd /c "title Rent-Admin-Frontend && npm run dev"
timeout /t 3 /nobreak >nul
echo [OK] Admin dev server started (port 5173)
echo.
:skip_admin

:: --- App Frontend ---
set "APP_DIR=%ROOT%frontend-source\app"
if not exist "%APP_DIR%\package.json" (
    echo [WARN] App frontend not found, skipping...
    goto skip_app
)
cd /d "%APP_DIR%"
echo   Installing App dependencies (first time may take a while)...
call npm install
if errorlevel 1 (
    echo [FAIL] npm install failed for App frontend!
    pause
    exit /b 1
)
echo [OK] App dependencies installed
start "Rent-App-Frontend" cmd /c "title Rent-App-Frontend && npm run dev"
timeout /t 3 /nobreak >nul
echo [OK] App dev server started (port 5174)
echo.
:skip_app

cd /d "%ROOT%"

echo ============================================================
echo     All services started!
echo ============================================================
echo.
echo   Admin Panel:  http://localhost:5173
echo   App Portal:   http://localhost:5174
echo.
echo   Default account: admin / admin123
echo.
echo   To stop all services, double-click "stop-all.bat"
echo.
echo ============================================================
echo.
pause


