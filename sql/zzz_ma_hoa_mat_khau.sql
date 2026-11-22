-- =====================================================================
-- MÃ HÓA MẬT KHẨU NHÂN VIÊN BẰNG BCRYPT
--
-- * Tên file bắt đầu bằng "zzz_" để Docker chạy SAU file tạo bảng và dữ liệu mẫu.
-- * Chỉ mã hóa những mật khẩu còn lưu dạng chữ thường; chạy lại nhiều lần không sao.
-- * Hash sinh ra có dạng $2a$10$... – bản desktop (jBCrypt) và bản web (Spring Security)
--   đều kiểm tra được.
-- * Áp dụng cho database đang chạy (không mất dữ liệu):
--     docker compose exec db psql -U postgres -d qlcuahangthucpham -f /docker-entrypoint-initdb.d/zzz_ma_hoa_mat_khau.sql
--   Trên Neon: mở SQL Editor, dán nội dung file này và chạy.
--
-- Không chạy file này cũng được: bản desktop tự mã hóa mật khẩu của nhân viên ngay lần
-- đăng nhập đúng đầu tiên, và backend web cũng tự mã hóa khi khởi động.
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE NhanVien ALTER COLUMN MatKhau TYPE VARCHAR(100);

UPDATE NhanVien
SET MatKhau = crypt(TRIM(MatKhau), gen_salt('bf', 10))
WHERE MatKhau IS NOT NULL
  AND MatKhau <> ''
  AND MatKhau !~ '^\$2[aby]\$\d{2}\$.{53}$';
