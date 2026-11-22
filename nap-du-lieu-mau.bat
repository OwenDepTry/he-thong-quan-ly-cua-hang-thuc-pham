@echo off
REM ==========================================================================
REM  Nap du lieu mau (khach hang, san pham, ~2000 hoa don trong 12 thang qua...)
REM  vao co so du lieu dang chay. KHONG xoa du lieu ban da nhap.
REM  Chay lai nhieu lan cung khong bi nhan doi.
REM ==========================================================================
setlocal
chcp 65001 >nul
cd /d "%~dp0"

where docker >nul 2>&1
if errorlevel 1 (
    echo [LOI] Khong tim thay Docker. Hay cai va mo Docker Desktop.
    goto end
)

docker compose up -d
if errorlevel 1 (
    echo [LOI] Docker chua chay. Hay mo Docker Desktop roi chay lai file nay.
    goto end
)

echo Dang cho co so du lieu san sang...
set /a tries=0
:wait
docker compose exec -T db pg_isready -U postgres -d qlcuahangthucpham >nul 2>&1
if not errorlevel 1 goto ready
set /a tries+=1
if %tries% geq 45 (
    echo [LOI] Co so du lieu khoi dong qua lau.
    goto end
)
timeout /t 2 /nobreak >nul
goto wait

:ready
echo Dang nap du lieu mau...
docker compose exec -T db psql -U postgres -d qlcuahangthucpham -v ON_ERROR_STOP=1 -q -f /docker-entrypoint-initdb.d/zz_du_lieu_mau.sql
if errorlevel 1 (
    echo [LOI] Nap du lieu that bai. Xem thong bao o tren.
    goto end
)
REM Ma hoa BCrypt mat khau cua nhan vien mau vua them (chay lai nhieu lan khong sao)
docker compose exec -T db psql -U postgres -d qlcuahangthucpham -v ON_ERROR_STOP=1 -q -f /docker-entrypoint-initdb.d/zzz_ma_hoa_mat_khau.sql
echo.
echo Xong! Mo lai ung dung (hoac bam vao Trang chu) de xem du lieu moi.

:end
pause
