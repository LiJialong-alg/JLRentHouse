@echo off
chcp 65001 >nul
title RentSystem - Stop All Services

echo ============================================================
echo       Rent System - Stop All Services
echo ============================================================
echo.

echo [1/3] Stopping all services...

:: Kill backend Java processes (by window title)
echo   Stopping backend services...
taskkill /F /FI "WINDOWTITLE eq Rent-Admin*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Rent-App*" >nul 2>&1
echo   [OK]

:: Kill frontend node processes (by window title)
echo   Stopping frontend services...
taskkill /F /FI "WINDOWTITLE eq Rent-Admin-Frontend*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Rent-App-Frontend*" >nul 2>&1
echo   [OK]
echo   All rent system services stopped
echo.

echo [2/3] Checking ports...
for %%p in (8080 8081 5173 5174) do (
    netstat -ano 2>nul | findstr ":%%p " >nul 2>&1
    if errorlevel 1 (
        echo [OK] Port %%p: Released
    ) else (
        echo [!] Port %%p: Still in use (may be other app)
    )
)
echo.

echo [3/3] Done
echo.
echo ============================================================
echo     All services stopped
echo ============================================================
echo.
echo Note: MySQL, Redis, MinIO are NOT stopped by this script.
echo To stop them manually:
echo   - MySQL:  net stop MySQL80
echo   - Redis:  redis-cli shutdown
echo   - MinIO:  press Ctrl+C in its window
echo.
timeout /t 5 /nobreak >nul

