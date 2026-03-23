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
* MySQL (Cơ sở dữ liệu)
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

### 2. Import database

* Mở MySQL
* Chạy file `.sql` trong thư mục `database`

### 3. Cấu hình kết nối

* Mở file `DBConnection.java`
* Chỉnh lại:

  * URL
  * Username
  * Password

### 4. Chạy chương trình

* Mở project bằng IntelliJ / NetBeans / Eclipse
* Run file `Main.java`

---

## 🔑 Tài khoản demo

| Vai trò   | Tài khoản | Mật khẩu |
| --------- | --------- | -------- |
| Admin     | admin     | 123      |
| Nhân viên | staff     | 123      |

---

## 📊 Yêu cầu hệ thống

* JDK 17 trở lên
* MySQL Server
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
* MySQL Documentation
* iText PDF Library

---

## 🎯 Định hướng phát triển

* Xây dựng phiên bản web
* Thêm phân quyền chi tiết hơn
* Tích hợp thanh toán online
* Cải thiện UI/UX

---

⭐ Nếu thấy project hữu ích hãy để lại 1 star nhé!
