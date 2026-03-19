CREATE DATABASE IF NOT EXISTS qlcuahangthucpham
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlcuahangthucpham;

-- =========================
-- BẢNG TỈNH THÀNH
-- =========================
CREATE TABLE IF NOT EXISTS tinhthanh (
    MaTThanh INT PRIMARY KEY AUTO_INCREMENT,
    TenTThanh VARCHAR(255) NOT NULL
);

-- =========================
-- BẢNG LOẠI
-- =========================
CREATE TABLE IF NOT EXISTS loai (
    MaLoai INT PRIMARY KEY AUTO_INCREMENT,
    TenLoai VARCHAR(255) NOT NULL,
    MoTa VARCHAR(255)
);

-- =========================
-- BẢNG ĐƠN VỊ
-- =========================
CREATE TABLE IF NOT EXISTS donvi (
    MaDonVi INT PRIMARY KEY AUTO_INCREMENT,
    TenDonVi VARCHAR(32) NOT NULL,
    MoTa VARCHAR(255)
);

-- =========================
-- BẢNG NHÂN VIÊN
-- đăng nhập luôn bằng MaNV + MatKhau
-- ChucVu: ADM = admin, NV = nhân viên
-- =========================
CREATE TABLE IF NOT EXISTS nhanvien (
    MaNV VARCHAR(20) PRIMARY KEY,
    Ho VARCHAR(20) NOT NULL,
    TenLot VARCHAR(20),
    Ten VARCHAR(20) NOT NULL,
    Phai VARCHAR(10),
    NgaySinh DATE,
    SDT CHAR(10) NOT NULL,
    TinhThanh INT,
    DiaChi VARCHAR(255),
    Luong INT,
    ChucVu CHAR(3) NOT NULL,
    TrangThai VARCHAR(10) NOT NULL,
    MatKhau VARCHAR(255) NOT NULL,
    CONSTRAINT fk_nv_tinhthanh FOREIGN KEY (TinhThanh) REFERENCES tinhthanh(MaTThanh)
);

-- =========================
-- BẢNG KHÁCH HÀNG
-- =========================
CREATE TABLE IF NOT EXISTS khachhang (
    MaKH VARCHAR(20) PRIMARY KEY,
    Ho VARCHAR(20) NOT NULL,
    TenLot VARCHAR(20),
    Ten VARCHAR(20) NOT NULL,
    Phai VARCHAR(10),
    NgaySinh DATE,
    SDT CHAR(10) NOT NULL UNIQUE,
    TinhThanh INT,
    DiaChi VARCHAR(255),
    NgayThamGia DATE,
    Diem INT DEFAULT 0,
    TrangThai VARCHAR(10) NOT NULL,
    CONSTRAINT fk_kh_tinhthanh FOREIGN KEY (TinhThanh) REFERENCES tinhthanh(MaTThanh)
);

-- =========================
-- BẢNG NHÀ CUNG CẤP
-- =========================
CREATE TABLE IF NOT EXISTS nhacungcap (
    MaNCCap VARCHAR(20) PRIMARY KEY,
    TenNCCap VARCHAR(255) NOT NULL,
    TenLienHe VARCHAR(255),
    SDThoai CHAR(10) NOT NULL,
    TinhThanh INT,
    DiaChi VARCHAR(255),
    TrangThai VARCHAR(10) NOT NULL,
    CONSTRAINT fk_ncc_tinhthanh FOREIGN KEY (TinhThanh) REFERENCES tinhthanh(MaTThanh)
);

-- =========================
-- BẢNG SẢN PHẨM
-- =========================
CREATE TABLE IF NOT EXISTS sanpham (
    MaSP VARCHAR(20) PRIMARY KEY,
    TenSP VARCHAR(255) NOT NULL,
    Loai INT NOT NULL,
    DonViTinh INT NOT NULL,
    HSDung INT,
    MoTa VARCHAR(255),
    Gia INT NOT NULL,
    SoLuongTon INT DEFAULT 0,
    CONSTRAINT fk_sp_loai FOREIGN KEY (Loai) REFERENCES loai(MaLoai),
    CONSTRAINT fk_sp_donvi FOREIGN KEY (DonViTinh) REFERENCES donvi(MaDonVi)
);

-- =========================
-- BẢNG KHUYẾN MÃI
-- =========================
CREATE TABLE IF NOT EXISTS khuyenmai (
    MaKM VARCHAR(20) PRIMARY KEY,
    TenKM VARCHAR(255) NOT NULL,
    PhanTramGiam INT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai VARCHAR(20) NOT NULL
);

-- =========================
-- BẢNG HÓA ĐƠN
-- =========================
CREATE TABLE IF NOT EXISTS hoadon (
    MaHD VARCHAR(20) PRIMARY KEY,
    MaKH VARCHAR(20) NOT NULL,
    MaNV VARCHAR(20) NOT NULL,
    TongTien INT NOT NULL DEFAULT 0,
    ThoiGian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hd_kh FOREIGN KEY (MaKH) REFERENCES khachhang(MaKH),
    CONSTRAINT fk_hd_nv FOREIGN KEY (MaNV) REFERENCES nhanvien(MaNV)
);

-- =========================
-- BẢNG CHI TIẾT HÓA ĐƠN
-- =========================
CREATE TABLE IF NOT EXISTS chitiethoadon (
    MaHD VARCHAR(20) NOT NULL,
    MaSP VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL,
    DonGia INT NOT NULL,
    PRIMARY KEY (MaHD, MaSP),
    CONSTRAINT fk_cthd_hd FOREIGN KEY (MaHD) REFERENCES hoadon(MaHD) ON DELETE CASCADE,
    CONSTRAINT fk_cthd_sp FOREIGN KEY (MaSP) REFERENCES sanpham(MaSP)
);

-- =========================
-- BẢNG PHIẾU NHẬP HÀNG
-- =========================
CREATE TABLE IF NOT EXISTS phieunhaphang (
    MaPhieu VARCHAR(20) PRIMARY KEY,
    MaNCCap VARCHAR(20) NOT NULL,
    NguoiNhap VARCHAR(20) NOT NULL,
    TongTien INT NOT NULL DEFAULT 0,
    ThoiGian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pn_ncc FOREIGN KEY (MaNCCap) REFERENCES nhacungcap(MaNCCap),
    CONSTRAINT fk_pn_nv FOREIGN KEY (NguoiNhap) REFERENCES nhanvien(MaNV)
);

-- =========================
-- BẢNG CHI TIẾT PHIẾU NHẬP
-- =========================
CREATE TABLE IF NOT EXISTS chitietpnhap (
    MaPhieu VARCHAR(20) NOT NULL,
    MaSP VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL,
    DonGia INT NOT NULL,
    PRIMARY KEY (MaPhieu, MaSP),
    CONSTRAINT fk_ctpn_pn FOREIGN KEY (MaPhieu) REFERENCES phieunhaphang(MaPhieu) ON DELETE CASCADE,
    CONSTRAINT fk_ctpn_sp FOREIGN KEY (MaSP) REFERENCES sanpham(MaSP)
);

-- =========================
-- DỮ LIỆU MẪU
-- =========================
INSERT INTO tinhthanh (TenTThanh) VALUES
('TP.HCM'),
('Hà Nội'),
('Đà Nẵng')
ON DUPLICATE KEY UPDATE TenTThanh = VALUES(TenTThanh);

INSERT INTO loai (TenLoai, MoTa) VALUES
('Rau củ', 'Các loại rau củ'),
('Thịt cá', 'Các loại thịt cá'),
('Đồ uống', 'Nước uống, sữa')
ON DUPLICATE KEY UPDATE TenLoai = VALUES(TenLoai);

INSERT INTO donvi (TenDonVi, MoTa) VALUES
('Kg', 'Kilogram'),
('Hộp', 'Đơn vị hộp'),
('Chai', 'Đơn vị chai')
ON DUPLICATE KEY UPDATE TenDonVi = VALUES(TenDonVi);

INSERT INTO nhanvien (
    MaNV, Ho, TenLot, Ten, Phai, NgaySinh, SDT, TinhThanh,
    DiaChi, Luong, ChucVu, TrangThai, MatKhau
) VALUES
('admin', 'Phan', 'Tien', 'Dat', 'Nam', '2004-01-01', '0900000001', 1,
 'TP.HCM', 15000000, 'ADM', 'active', '123456'),
('nv01', 'Nguyen', 'Van', 'A', 'Nam', '2003-05-10', '0900000002', 1,
 'TP.HCM', 8000000, 'NV', 'active', '123456')
ON DUPLICATE KEY UPDATE MatKhau = VALUES(MatKhau);

INSERT INTO sanpham (
    MaSP, TenSP, Loai, DonViTinh, HSDung, MoTa, Gia, SoLuongTon
) VALUES
('SP001', 'Cà chua', 1, 1, 7, 'Cà chua tươi', 30000, 100),
('SP002', 'Thịt bò', 2, 1, 3, 'Thịt bò loại 1', 250000, 50),
('SP003', 'Sữa tươi', 3, 3, 180, 'Sữa tươi không đường', 35000, 80)
ON DUPLICATE KEY UPDATE TenSP = VALUES(TenSP);

INSERT INTO khuyenmai (MaKM, TenKM, PhanTramGiam, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('KM001', 'Khuyến mãi tháng 3', 10, '2026-03-01', '2026-03-31', 'active'),
('KM002', 'Giảm giá cuối tuần', 5, '2026-03-20', '2026-03-22', 'active')
ON DUPLICATE KEY UPDATE TenKM = VALUES(TenKM);