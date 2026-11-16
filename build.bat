@echo off
REM ==========================================================================
REM  Dong goi ung dung thanh 1 file duy nhat: dist\QuanLyCuaHang.jar
REM  Yeu cau: JDK 17 tro len (co lenh javac va jar).
REM  Cach dung: bam dup vao file nay, hoac chay "build.bat" trong terminal.
REM ==========================================================================
setlocal EnableDelayedExpansion
cd /d "%~dp0"

where javac >nul 2>&1
if errorlevel 1 (
    echo [LOI] Khong tim thay javac. Hay cai JDK 17 tro len va them vao PATH.
    goto fail
)

echo [1/4] Don dep thu muc build...
if exist build rmdir /s /q build
mkdir build\classes
if not exist dist mkdir dist

echo [2/4] Bien dich ma nguon...
REM Ghi danh sach file .java (dau / va ngoac kep de chiu duoc duong dan co khoang trang)
(for /r "src" %%f in (*.java) do (set "p=%%f" & echo "!p:\=/!")) > build\sources.txt
(for /r "ui" %%f in (*.java) do (set "p=%%f" & echo "!p:\=/!")) >> build\sources.txt
javac --release 17 -encoding UTF-8 -cp "lib/*" -d build\classes @build\sources.txt
if errorlevel 1 (
    echo [LOI] Bien dich that bai. Xem loi o tren.
    goto fail
)
xcopy /e /i /q /y "src\images" "build\classes\images" >nul
if exist "src\fonts" xcopy /e /i /q /y "src\fonts" "build\classes\fonts" >nul

echo [3/4] Gop cac thu vien trong lib\ vao chung 1 file...
pushd build\classes
for %%j in ("..\..\lib\*.jar") do jar xf "%%~fj"
if exist META-INF\MANIFEST.MF del /q META-INF\MANIFEST.MF
del /q META-INF\*.SF META-INF\*.DSA META-INF\*.RSA 2>nul
if exist module-info.class del /q module-info.class
popd

echo [4/4] Tao file dist\QuanLyCuaHang.jar...
if exist dist\QuanLyCuaHang.jar del /q dist\QuanLyCuaHang.jar
jar --create --file dist\QuanLyCuaHang.jar --main-class main.Main -C build\classes .
if errorlevel 1 (
    echo [LOI] Khong tao duoc file jar.
    goto fail
)

echo.
echo Xong! File chay: dist\QuanLyCuaHang.jar
echo Bam dup chay.bat de khoi dong co so du lieu va mo ung dung.
if /i not "%~1"=="nopause" pause
exit /b 0

:fail
if /i not "%~1"=="nopause" pause
exit /b 1
