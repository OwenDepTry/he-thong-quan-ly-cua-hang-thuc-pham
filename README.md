# 🛒 HỆ THỐNG QUẢN LÝ CỬA HÀNG THỰC PHẨM

## 📌 Giới thiệu

Dự án xây dựng hệ thống quản lý cửa hàng thực phẩm nhằm hỗ trợ việc quản lý bán hàng, kho hàng và nhân sự một cách hiệu quả, chính xác và nhanh chóng.

Hệ thống giúp giảm thiểu sai sót trong quản lý thủ công và tối ưu hóa quy trình vận hành cửa hàng.

---

## 👨‍💻 Thành viên nhóm

| Họ tên              | MSSV       |
| ------------------- | ---------- |
| Nguyễn Trọng Nguyễn | 3124410242 |
| Phạm Thị Hải Yến    | 3124410418 |
| Lê Hữu Nam          | 3123410227 |
| Trần Ngọc Thái Sơn  | 3122410361 |
| Lê Hoàng Thuận      | 3124560088 |
| Phan Tiến Đạt       | 3124410066 |

---

## ⚙️ Công nghệ sử dụng

* Java (JDK 17)
* Java Swing (Giao diện)
* PostgreSQL 16 chạy bằng Docker (Cơ sở dữ liệu)
* JDBC (Kết nối database)
* iText (Xuất file PDF)

---

## 🚀 Chức năng chính

* Quản lý sản phẩm
* Quản lý loại sản phẩm
* Quản lý hàng hóa / kho
* Quản lý khách hàng
* Quản lý nhân viên
* Quản lý hóa đơn
* Quản lý phiếu nhập
* Quản lý khuyến mãi
* Thống kê & báo cáo
* Xuất hóa đơn PDF

---

## 🗂️ Cấu trúc thư mục

```
📦 he-thong-quan-ly-cua-hang-thuc-pham
 ┣ 📂 BaoCao
 ┃ ┣ 📄 BaoCao.docx
 ┃ ┗ 📄 BaoCao.pdf
 ┣ 📂 ChuongTrinh
 ┃ ┣ 📂 src (source code)
 ┃ ┣ 📂 database (file SQL)
 ┃ ┣ 📂 lib (thư viện .jar)
 ┃ ┗ 📄 file chạy (nếu có)
 ┗ 📄 README.md
```

---

## 🛠️ Hướng dẫn cài đặt

### 1. Clone project

```bash
git clone https://github.com/OwenDepTry/he-thong-quan-ly-cua-hang-thuc-pham
```

### 2. Khởi động database (PostgreSQL bằng Docker)

Mở Docker Desktop, rồi ở thư mục gốc project chạy:

```bash
docker compose up -d
```

Lần đầu, Docker tự tạo database `qlcuahangthucpham` và nạp dữ liệu mẫu từ `sql/qlcuahangthucpham.sql`.

* Tắt database: `docker compose stop`
* Xóa sạch dữ liệu, nạp lại từ đầu: `docker compose down -v` rồi `docker compose up -d`

### 3. Thêm thư viện JDBC của PostgreSQL

Tải `postgresql-42.7.4.jar` tại https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
và chép vào thư mục `lib/` (có thể xóa `mysql-connector-j-*.jar` vì không còn dùng).

Thông tin kết nối mặc định đã khớp với `docker-compose.yml` (user `postgres`, mật khẩu `postgres`, cổng `5432`).
Muốn đổi thì sao chép `db.properties.example` thành `db.properties` và sửa.

### 4. Chạy chương trình

* Mở project bằng VS Code / IntelliJ / NetBeans / Eclipse
* Run file `Main.java`

---

## 🔑 Tài khoản demo

| Vai trò   | Tài khoản | Mật khẩu |
| --------- | --------- | -------- |
| Admin     | admin     | admin    |
| Nhân viên | nhanvien  | 123      |

---

## 📊 Yêu cầu hệ thống

* JDK 17 trở lên
* Docker Desktop (để chạy PostgreSQL)
* IDE Java (IntelliJ / NetBeans / Eclipse)

---

## 📌 Ghi chú

* Cần import database trước khi chạy
* Nếu lỗi kết nối → kiểm tra DBConnection
* Nếu lỗi PDF → kiểm tra thư viện iText đã add chưa

---

## 📚 Tài liệu tham khảo

* Java Swing UI
* JDBC Documentation
* PostgreSQL Documentation
* iText PDF Library

---

## 🎯 Định hướng phát triển

* Xây dựng phiên bản web
* Thêm phân quyền chi tiết hơn
* Tích hợp thanh toán online
* Cải thiện UI/UX

---

⭐ Nếu thấy project hữu ích hãy để lại 1 star nhé!

---

## 🎨 Giao diện (khuyến nghị cài FlatLaf)

Ứng dụng tự nhận **FlatLaf** nếu có file jar trong thư mục `lib/` (bo góc, ô nhập có placeholder, tab/thanh cuộn hiện đại, nét trên màn hình HiDPI).
Không có FlatLaf thì app vẫn chạy bình thường với giao diện mặc định.

1. Tải `flatlaf-3.5.4.jar` tại: https://repo1.maven.org/maven2/com/formdev/flatlaf/3.5.4/flatlaf-3.5.4.jar
2. Chép vào thư mục `lib/` (VS Code đã cấu hình sẵn `lib/**/*.jar`).
3. Chạy lại `Main`.

Tùy chọn: nhúng font **Be Vietnam Pro** bằng cách đặt `BeVietnamPro-Regular.ttf` và `BeVietnamPro-Bold.ttf` vào `src/fonts/`.

Màu sắc, font dùng chung nằm ở `ui/AppTheme.java`; icon ở `ui/AppIcons.java`; định dạng bảng (tiền tệ, ngày, dòng xen kẽ) ở `ui/AppTable.java`.
