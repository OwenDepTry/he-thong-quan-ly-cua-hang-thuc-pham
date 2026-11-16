#!/usr/bin/env bash
# Đóng gói ứng dụng thành 1 file: dist/QuanLyCuaHang.jar (cần JDK 17+). Dùng cho macOS / Linux.
set -euo pipefail
cd "$(dirname "$0")"
command -v javac >/dev/null || { echo "[LỖI] Không tìm thấy javac – hãy cài JDK 17 trở lên."; exit 1; }

echo "[1/4] Dọn dẹp thư mục build..."
rm -rf build && mkdir -p build/classes dist

echo "[2/4] Biên dịch mã nguồn..."
find src ui -name '*.java' | sed 's/.*/"&"/' > build/sources.txt
javac --release 17 -encoding UTF-8 -cp "lib/*" -d build/classes @build/sources.txt
cp -r src/images build/classes/
[ -d src/fonts ] && cp -r src/fonts build/classes/ || true

echo "[3/4] Gộp các thư viện trong lib/..."
( cd build/classes && for j in ../../lib/*.jar; do jar xf "$j"; done
  rm -f META-INF/MANIFEST.MF META-INF/*.SF META-INF/*.DSA META-INF/*.RSA module-info.class )

echo "[4/4] Tạo dist/QuanLyCuaHang.jar..."
rm -f dist/QuanLyCuaHang.jar
jar --create --file dist/QuanLyCuaHang.jar --main-class main.Main -C build/classes .
echo "Xong! Chạy: ./chay.sh  (hoặc java -jar dist/QuanLyCuaHang.jar)"
