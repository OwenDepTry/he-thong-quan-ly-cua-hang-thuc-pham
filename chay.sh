#!/usr/bin/env bash
# Bật CSDL bằng Docker, chờ sẵn sàng rồi mở ứng dụng. Dùng cho macOS / Linux.
set -euo pipefail
cd "$(dirname "$0")"
[ -f dist/QuanLyCuaHang.jar ] || ./build.sh
docker compose up -d
echo "Đang chờ cơ sở dữ liệu sẵn sàng..."
for i in $(seq 1 45); do
  docker compose exec -T db pg_isready -U postgres -d qlcuahangthucpham >/dev/null 2>&1 && break
  [ "$i" = 45 ] && { echo "[LỖI] CSDL khởi động quá lâu – xem: docker compose logs db"; exit 1; }
  sleep 2
done
java -jar dist/QuanLyCuaHang.jar
