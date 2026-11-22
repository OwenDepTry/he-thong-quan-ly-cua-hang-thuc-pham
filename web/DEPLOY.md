# Deploy miễn phí: Neon (database) + Render (backend, frontend)

```
Trình duyệt ──► Render Static Site (React)      https://qlcuahang-frontend.onrender.com
     │
     └──────► Render Web Service (Spring Boot)   https://qlcuahang-backend.onrender.com/api
                    │
                    └──► Neon PostgreSQL (SSL)
```

Thời gian: khoảng 20 phút. Cần tài khoản GitHub (repo đã đẩy lên), Neon và Render. Không cần thẻ tín dụng cho gói Free.

---

## 1. Tạo database trên Neon

1. Vào <https://console.neon.tech> → **New Project**.
   - Postgres version: **16** (giống Docker); Region: **AWS Asia Pacific (Singapore)** cho gần Render Singapore.
   - Database name: `qlcuahangthucpham`.
2. Bấm **Connect** → chọn database `qlcuahangthucpham`, **tắt "Connection pooling"** (dùng endpoint trực tiếp, không có `-pooler` trong tên host) → copy chuỗi kết nối dạng:

   ```
   postgresql://<user>:<password>@ep-xxxx.ap-southeast-1.aws.neon.tech/qlcuahangthucpham?sslmode=require
   ```

3. Nạp schema + dữ liệu. Chọn một trong hai cách:

   **Cách A – psql trên máy** (nhanh nhất, chạy ở thư mục gốc repo):

   ```bash
   NEON="postgresql://<user>:<password>@ep-xxxx.ap-southeast-1.aws.neon.tech/qlcuahangthucpham?sslmode=require"
   psql "$NEON" -v ON_ERROR_STOP=1 -f sql/qlcuahangthucpham.sql
   psql "$NEON" -v ON_ERROR_STOP=1 -f sql/zz_du_lieu_mau.sql        # dữ liệu mẫu – bỏ qua nếu không cần
   psql "$NEON" -v ON_ERROR_STOP=1 -f sql/zzz_ma_hoa_mat_khau.sql   # mã hóa mật khẩu BCrypt
   psql "$NEON" -v ON_ERROR_STOP=1 -f sql/zzzz_anh_san_pham.sql     # ảnh sản phẩm mẫu (Wikimedia Commons)
   ```

   Không cài psql? Dùng Docker: `docker run --rm -it -v "$PWD/sql:/sql" postgres:16-alpine psql "$NEON" -f /sql/qlcuahangthucpham.sql` (lặp lại cho các file còn lại).

   **Cách B – SQL Editor của Neon**: mở từng file trong `sql/` theo đúng thứ tự trên, dán toàn bộ nội dung vào SQL Editor và bấm **Run**.

   Neon hỗ trợ sẵn 2 extension mà schema cần là `unaccent` (tìm không dấu) và `pgcrypto` (mã hóa mật khẩu).

4. Đổi chuỗi `postgresql://user:pass@host/db?...` thành **3 biến** cho backend:

   | Biến | Giá trị |
   | --- | --- |
   | `DB_URL` | `jdbc:postgresql://ep-xxxx.ap-southeast-1.aws.neon.tech/qlcuahangthucpham?sslmode=require` |
   | `DB_USER` | `<user>` |
   | `DB_PASSWORD` | `<password>` |

   (Chỉ thêm `jdbc:` ở đầu và tách user/password ra; giữ `?sslmode=require`.)

---

## 2. Deploy lên Render bằng Blueprint (khuyên dùng)

Repo đã có sẵn `render.yaml` ở thư mục gốc, mô tả 2 service: `qlcuahang-backend` (Docker, gói Free) và `qlcuahang-frontend` (Static Site).

1. Vào <https://dashboard.render.com> → **New** → **Blueprint** → kết nối GitHub và chọn repo này.
2. Render hỏi các biến `sync: false`. Điền:
   - `DB_URL`, `DB_USER`, `DB_PASSWORD`: từ bước 1.4.
   - `CORS_ORIGINS`: `https://qlcuahang-frontend.onrender.com` (URL của frontend, sửa lại ở bước 4 nếu Render đặt tên khác).
   - `VITE_API_URL`: `https://qlcuahang-backend.onrender.com` (URL của backend, **không** có `/api` và không có `/` ở cuối).
   - `JWT_SECRET` được Render tự sinh ngẫu nhiên.
   - `VITE_DEMO=true` đã đặt sẵn trong `render.yaml` để trang đăng nhập hiện tài khoản dùng thử. Dùng cho cửa hàng thật thì xóa biến này.
3. Bấm **Apply**. Backend build Docker mất khoảng 5–8 phút lần đầu.
4. Khi xong, mở trang service để xem URL thật. Nếu URL khác với giá trị đã điền:
   - Backend → **Environment** → sửa `CORS_ORIGINS` → **Save, rebuild and deploy**.
   - Frontend → **Environment** → sửa `VITE_API_URL` → **Manual Deploy → Clear build cache & deploy**.
     (Vite ghi địa chỉ API vào file JS **lúc build**, nên đổi biến này bắt buộc phải build lại.)
5. Kiểm tra:
   - `https://<backend>/actuator/health` → `{"status":"UP"}`
   - `https://<backend>/swagger-ui.html` → trang tài liệu API
   - Mở frontend, đăng nhập `admin` / `admin`.
   - Dùng cho cửa hàng thật: **đổi mật khẩu các tài khoản mẫu ngay** ở trang Nhân viên, và tắt Swagger bằng `SWAGGER_ENABLED=false` nếu không cần.

### Tự tạo từng service (không dùng Blueprint)

**Backend** – New → **Web Service** → chọn repo:
- Language: **Docker** · Root Directory: `web/backend` · Dockerfile Path: `./Dockerfile` (nếu Render hỏi đường dẫn từ gốc repo thì điền `web/backend/Dockerfile`)
- Instance type: **Free** · Region: **Singapore** · Health Check Path: `/actuator/health`
- Environment: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (`openssl rand -base64 48`), `CORS_ORIGINS`

**Frontend** – New → **Static Site** → chọn repo:
- Root Directory: `web/frontend` · Build Command: `npm ci && npm run build` · Publish Directory: `dist`
- Environment: `VITE_API_URL=https://<backend>.onrender.com`, `NODE_VERSION=22`
- **Redirects/Rewrites**: Source `/*` → Destination `/index.html` → Action **Rewrite** (để F5 ở `/hoa-don` không bị 404)

---

## 3. Lưu ý với gói Free

- **Render Free tự tắt backend sau 15 phút không có request.** Request đầu tiên sau đó phải chờ khởi động lại (Spring Boot mất khoảng 1 phút), trang web sẽ hiện "Đang tải…" trong lúc chờ. Free web service có 512 MB RAM; Dockerfile đã giới hạn heap Java (`-XX:MaxRAMPercentage=75`).
- **Neon Free tự tạm dừng database khi không dùng** và bật lại sau vài trăm mili-giây ở lần kết nối kế tiếp; HikariCP tự kết nối lại.
- Không đặt `JWT_SECRET` ngắn hơn 32 ký tự – backend sẽ từ chối khởi động.
- Backend tự mã hóa BCrypt mật khẩu còn lưu dạng chữ thường mỗi lần khởi động, nên quên chạy `zzz_ma_hoa_mat_khau.sql` cũng không sao.

## 4. Bản demo công khai (để gắn vào CV)

- Giữ nguyên tài khoản mẫu và `VITE_DEMO=true` để người xem đăng nhập thử được ngay.
- Người xem có thể lập hóa đơn, sửa dữ liệu. Muốn làm mới toàn bộ: trên Neon xóa và tạo lại database (hoặc tạo branch mới), rồi nạp lại các file `sql/` như bước 1.3. Dữ liệu mẫu luôn tính theo ngày nạp, nên biểu đồ và cảnh báo sẽ có số liệu "hôm nay".
- Ghi link frontend vào README ở mục "Xem thử" và vào CV. Lần mở đầu sau một lúc không ai dùng sẽ chậm khoảng 1 phút do Render Free khởi động lại backend.

## 5. App desktop dùng chung database Neon (tùy chọn)

Tạo file `db.properties` cạnh `QuanLyCuaHang.jar`:

```properties
db.url=jdbc:postgresql://ep-xxxx.ap-southeast-1.aws.neon.tech/qlcuahangthucpham?sslmode=require&stringtype=unspecified
db.user=<user>
db.password=<password>
```

Desktop và web khi đó đọc/ghi cùng dữ liệu; mật khẩu BCrypt dùng được ở cả hai.

> Ghi chú: "hôm nay" trên desktop tính theo `CURRENT_DATE` của database. Neon mặc định chạy giờ UTC, nên từ 0h đến 7h sáng giờ Việt Nam desktop có thể tính sang ngày hôm trước. Muốn sửa: chạy `ALTER DATABASE qlcuahangthucpham SET timezone TO 'Asia/Ho_Chi_Minh';` trong SQL Editor của Neon. Bản web luôn tính ngày theo giờ Việt Nam nên không bị ảnh hưởng.
