@echo off
REM ==========================================================================
REM  Chay ung dung bang 1 lan bam dup:
REM    1. Dong goi neu chua co dist\QuanLyCuaHang.jar
REM    2. Bat co so du lieu PostgreSQL bang Docker va cho san sang
REM    3. Mo ung dung
REM  Yeu cau: Docker Desktop dang mo, JDK 17 tro len.
REM ==========================================================================
setlocal
cd /d "%~dp0"

if not exist "dist\QuanLyCuaHang.jar" (
    echo Chua co file dist\QuanLyCuaHang.jar - dang dong goi lan dau...
    call build.bat nopause
    if errorlevel 1 goto fail
)

where docker >nul 2>&1
if errorlevel 1 (
    echo [LOI] Khong tim thay Docker. Hay cai va mo Docker Desktop.
    goto fail
)

echo Dang khoi dong co so du lieu (Docker)...
docker compose up -d
if errorlevel 1 (
    echo [LOI] Docker chua chay. Hay mo Docker Desktop, cho no khoi dong xong roi chay lai file nay.
    goto fail
)

echo Dang cho co so du lieu san sang...
set /a tries=0
:wait
docker compose exec -T db pg_isready -U postgres -d qlcuahangthucpham >nul 2>&1
if not errorlevel 1 goto ready
set /a tries+=1
if %tries% geq 45 (
    echo [LOI] Co so du lieu khoi dong qua lau. Thu lai, hoac xem: docker compose logs db
    goto fail
)
timeout /t 2 /nobreak >nul
goto wait

:ready
echo Dang mo ung dung...
start "" javaw -jar "dist\QuanLyCuaHang.jar"
exit /b 0

:fail
pause
exit /b 1
