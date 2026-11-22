# Bản web – Hệ thống quản lý cửa hàng thực phẩm

Bản web dùng **chung database PostgreSQL** với app desktop: hóa đơn lập trên web hiện ngay trên desktop và ngược lại.

| Phần | Công nghệ | Thư mục |
| --- | --- | --- |
| Backend | Spring Boot 3.5 (Java 17), Spring Security + JWT, JdbcTemplate/JdbcClient | `web/backend` |
| Frontend | React 19 + Vite 8 + TypeScript, React Router | `web/frontend` |
| Database | PostgreSQL 16 (schema có sẵn trong `sql/`) | `sql/` |

## Chức năng (bản đầu)

- **Đăng nhập JWT**, phân quyền theo `NhanVien.ChucVu`: `QL` = quản lý, `NV` = nhân viên.
- **Sản phẩm**: ai cũng xem/tìm được (không phân biệt dấu); chỉ quản lý được thêm/sửa/xóa. Không xóa được sản phẩm đã có trong hóa đơn, phiếu nhập hoặc lô hàng.
- **Lập hóa đơn**: chọn sản phẩm, khách hàng, khuyến mãi. Máy chủ tính lại tiền theo giá trong DB, kiểm tra khuyến mãi (đang chạy, đúng ngày, đạt điều kiện), trừ tồn kho trong cùng 1 transaction (lỗi là hủy cả hóa đơn). Nhân viên chỉ xem được hóa đơn mình lập.
- **Tổng quan**: doanh thu hôm nay / tháng này (chỉ quản lý), số hóa đơn, sản phẩm sắp hết hàng (tồn ≤ 30), lô sắp hết hạn (trong 7 ngày tới hoặc vừa hết hạn ≤ 30 ngày). Giống Trang chủ của desktop.

## Mật khẩu BCrypt – app desktop vẫn đăng nhập được

- Mật khẩu được lưu dạng BCrypt `$2a$10$…`. Desktop (jBCrypt, nhúng sẵn ở `src/org/mindrot/jbcrypt`) và web (Spring Security) đọc chung định dạng này.
- Dữ liệu cũ còn lưu chữ thường **vẫn đăng nhập được** ở cả hai bản, và được mã hóa ngay sau lần đăng nhập đúng đầu tiên.
- Backend cũng mã hóa hết mật khẩu chữ thường khi khởi động (tắt bằng `MIGRATE_PLAINTEXT_PASSWORDS=false`).
- Muốn mã hóa ngay trong DB: chạy `sql/zzz_ma_hoa_mat_khau.sql` (cần extension `pgcrypto`; chạy lại nhiều lần không sao). Database tạo mới bằng Docker sẽ tự chạy file này.
- Trên desktop: đổi mật khẩu, thêm/sửa nhân viên đều lưu dạng BCrypt; ô mật khẩu khi sửa nhân viên để trống nghĩa là giữ nguyên.

## Chạy trên máy

### Cách 1 – Docker, chạy cả 3 phần

```bash
docker compose --profile web up -d --build
```

- Web: <http://localhost:8081> (nginx chuyển `/api` sang backend)
- API: <http://localhost:8080/api> · health: <http://localhost:8080/actuator/health>
- `docker compose up -d` (không có `--profile web`) vẫn chỉ bật PostgreSQL như trước, cho app desktop.

Đã có database cũ từ trước (volume `db-data`)? File `sql/` chỉ tự chạy khi tạo DB lần đầu; backend sẽ tự mã hóa mật khẩu khi khởi động, hoặc chạy:

```bash
docker compose exec db psql -U postgres -d qlcuahangthucpham -f /docker-entrypoint-initdb.d/zzz_ma_hoa_mat_khau.sql
```

### Cách 2 – Chạy từng phần để phát triển

```bash
docker compose up -d                      # PostgreSQL ở cổng 55432

cd web/backend && mvn spring-boot:run     # http://localhost:8080

cd web/frontend && npm install && npm run dev   # http://localhost:5173 (Vite chuyển /api sang 8080)
```

Tài khoản mẫu: `admin` / `admin` (quản lý), `nhanvien` / `123` (nhân viên), `NV01`…`NV04` / `123`.

## Biến môi trường của backend

| Biến | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:55432/qlcuahangthucpham` | JDBC URL |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `postgres` | Tài khoản DB |
| `JWT_SECRET` | chuỗi dev | **Bắt buộc đổi khi deploy**, ≥ 32 ký tự (`openssl rand -base64 48`) |
| `JWT_TTL` | `PT8H` | Thời hạn token (ISO-8601) |
| `CORS_ORIGINS` | `http://localhost:5173,http://localhost:8081` | Domain frontend được gọi API, phân tách bằng dấu phẩy |
| `MIGRATE_PLAINTEXT_PASSWORDS` | `true` | Mã hóa mật khẩu chữ thường khi khởi động |
| `PORT` | `8080` | Cổng HTTP (Render tự đặt) |

"Hôm nay" / "tháng này" luôn tính theo giờ Việt Nam (`Asia/Ho_Chi_Minh`) dù database đặt ở múi giờ nào.

## API

Mọi API (trừ đăng nhập và health) cần header `Authorization: Bearer <token>`. Lỗi trả về dạng `{"message": "...", "errors": {"truong": "..."}}`.

| Method | Đường dẫn | Quyền | Mô tả |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | – | `{maNhanVien, matKhau}` → `{token, expiresAt, user}` |
| GET | `/api/auth/me` | đăng nhập | Thông tin người dùng hiện tại |
| GET | `/api/dashboard` | đăng nhập | Số liệu tổng quan (doanh thu = `null` với nhân viên) |
| GET | `/api/san-pham?q=&loai=` | đăng nhập | Danh sách / tìm sản phẩm |
| GET | `/api/san-pham/{ma}` | đăng nhập | Chi tiết sản phẩm |
| POST / PUT / DELETE | `/api/san-pham[/{ma}]` | QL | Thêm / sửa / xóa |
| GET | `/api/loai-san-pham` | đăng nhập | Loại sản phẩm |
| GET | `/api/khach-hang?q=` | đăng nhập | Khách hàng đang hoạt động |
| GET | `/api/khuyen-mai/hieu-luc` | đăng nhập | Khuyến mãi áp dụng được hôm nay |
| GET | `/api/hoa-don?q=&tuNgay=&denNgay=&page=&size=` | đăng nhập | QL: mọi hóa đơn; NV: của mình |
| GET | `/api/hoa-don/{ma}` | đăng nhập | Chi tiết (NV chỉ xem của mình) |
| POST | `/api/hoa-don/xem-truoc` | đăng nhập | Tính tiền + kiểm tra, không lưu |
| POST | `/api/hoa-don` | đăng nhập | `{maKhachHang?, maKhuyenMai?, items:[{maSanPham, soLuong}]}` |

## Kiểm thử

```bash
cd web/backend && mvn verify      # JUnit + Testcontainers (cần Docker)
cd web/frontend && npm test       # Vitest + Testing Library
```

- Test backend dựng `postgres:16-alpine` bằng Testcontainers và nạp toàn bộ `sql/` – giống hệt docker-compose.
- Máy không có Docker: trỏ test vào một database **dùng để thử** đã nạp `sql/`:
  `TEST_DB_URL=jdbc:postgresql://localhost:55432/qlch_test mvn verify` (test sẽ ghi dữ liệu vào DB này).
- GitHub Actions (`.github/workflows/ci.yml`) chạy: test backend, test + build frontend, đóng gói desktop + nạp `sql/` + thử đăng nhập desktop bằng BCrypt, build Docker image.

## Deploy miễn phí

Xem [DEPLOY.md](DEPLOY.md) (Neon + Render).

## Ghi chú thiết kế

- Không dùng JPA để giữ nguyên schema cũ (bảng không có khóa tự tăng, mã dạng `HD123`); câu SQL viết giống các DAO của desktop.
- Mã hóa đơn `HD{n+1}` sinh giống desktop, có `pg_advisory_xact_lock` để 2 người lập hóa đơn cùng lúc trên web không bị trùng mã. Desktop không giữ khóa này; nếu web và desktop lập hóa đơn đúng cùng lúc, một bên sẽ báo lỗi trùng mã và chỉ cần bấm lại.
- Đơn giá trên hóa đơn lấy từ DB, không tin giá trình duyệt gửi lên. Tiền giảm làm tròn tới đồng.
