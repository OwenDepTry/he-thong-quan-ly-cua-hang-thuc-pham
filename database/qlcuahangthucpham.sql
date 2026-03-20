CREATE DATABASE IF NOT EXISTS qlcuahangthucpham
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qlcuahangthucpham;

SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS hoadon_khuyenmai;
DROP TABLE IF EXISTS chitiethoadon;
DROP TABLE IF EXISTS chitietpnhap;
DROP TABLE IF EXISTS hoadon;
DROP TABLE IF EXISTS phieunhaphang;
DROP TABLE IF EXISTS hang;
DROP TABLE IF EXISTS khuyenmai;
DROP TABLE IF EXISTS sanpham;
DROP TABLE IF EXISTS nhacungcap;
DROP TABLE IF EXISTS khachhang;
DROP TABLE IF EXISTS nhanvien;
DROP TABLE IF EXISTS donvi;
DROP TABLE IF EXISTS loai;
DROP TABLE IF EXISTS tinhthanh;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tinhthanh (
    MaTThanh INT PRIMARY KEY AUTO_INCREMENT,
    TenTThanh VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE loai (
    MaLoai INT PRIMARY KEY AUTO_INCREMENT,
    TenLoai VARCHAR(255) NOT NULL,
    MoTa VARCHAR(255)
);

CREATE TABLE donvi (
    MaDonVi INT PRIMARY KEY AUTO_INCREMENT,
    TenDonVi VARCHAR(32) NOT NULL,
    MoTa VARCHAR(255)
);

CREATE TABLE nhanvien (
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

CREATE TABLE khachhang (
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

CREATE TABLE nhacungcap (
    MaNCCap VARCHAR(20) PRIMARY KEY,
    TenNCCap VARCHAR(255) NOT NULL,
    TenLienHe VARCHAR(255),
    SDThoai CHAR(10) NOT NULL,
    TinhThanh INT,
    DiaChi VARCHAR(255),
    TrangThai VARCHAR(10) NOT NULL,
    CONSTRAINT fk_ncc_tinhthanh FOREIGN KEY (TinhThanh) REFERENCES tinhthanh(MaTThanh)
);

CREATE TABLE sanpham (
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

CREATE TABLE hang (
    MaHang VARCHAR(20) PRIMARY KEY,
    MaSP VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL DEFAULT 0,
    NSX DATE,
    GiamGia INT NOT NULL DEFAULT 0,
    TrangThai VARCHAR(20) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_hang_sp FOREIGN KEY (MaSP) REFERENCES sanpham(MaSP)
);

CREATE TABLE khuyenmai (
    MaKM VARCHAR(20) PRIMARY KEY,
    TenKM VARCHAR(255) NOT NULL,
    MoTa VARCHAR(255),
    PhanTramGiam INT NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai VARCHAR(20) NOT NULL
);

CREATE TABLE hoadon (
    MaHD VARCHAR(20) PRIMARY KEY,
    MaKH VARCHAR(20) NOT NULL,
    MaNV VARCHAR(20) NOT NULL,
    TongTien INT NOT NULL DEFAULT 0,
    TienGiam INT NOT NULL DEFAULT 0,
    ThoiGian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hd_kh FOREIGN KEY (MaKH) REFERENCES khachhang(MaKH),
    CONSTRAINT fk_hd_nv FOREIGN KEY (MaNV) REFERENCES nhanvien(MaNV)
);

CREATE TABLE hoadon_khuyenmai (
    MaHD VARCHAR(20) NOT NULL,
    MaKM VARCHAR(20) NOT NULL,
    PRIMARY KEY (MaHD, MaKM),
    CONSTRAINT fk_hdkm_hd FOREIGN KEY (MaHD) REFERENCES hoadon(MaHD) ON DELETE CASCADE,
    CONSTRAINT fk_hdkm_km FOREIGN KEY (MaKM) REFERENCES khuyenmai(MaKM)
);

CREATE TABLE chitiethoadon (
    MaHD VARCHAR(20) NOT NULL,
    MaSP VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL,
    DonGia INT NOT NULL,
    PRIMARY KEY (MaHD, MaSP),
    CONSTRAINT fk_cthd_hd FOREIGN KEY (MaHD) REFERENCES hoadon(MaHD) ON DELETE CASCADE,
    CONSTRAINT fk_cthd_sp FOREIGN KEY (MaSP) REFERENCES sanpham(MaSP)
);

CREATE TABLE phieunhaphang (
    MaPhieu VARCHAR(20) PRIMARY KEY,
    MaNCCap VARCHAR(20) NOT NULL,
    NguoiNhap VARCHAR(20) NOT NULL,
    TongTien INT NOT NULL DEFAULT 0,
    ThoiGian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pn_ncc FOREIGN KEY (MaNCCap) REFERENCES nhacungcap(MaNCCap),
    CONSTRAINT fk_pn_nv FOREIGN KEY (NguoiNhap) REFERENCES nhanvien(MaNV)
);

CREATE TABLE chitietpnhap (
    MaPhieu VARCHAR(20) NOT NULL,
    MaSP VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL,
    DonGia INT NOT NULL,
    PRIMARY KEY (MaPhieu, MaSP),
    CONSTRAINT fk_ctpn_pn FOREIGN KEY (MaPhieu) REFERENCES phieunhaphang(MaPhieu) ON DELETE CASCADE,
    CONSTRAINT fk_ctpn_sp FOREIGN KEY (MaSP) REFERENCES sanpham(MaSP)
);

INSERT INTO tinhthanh (TenTThanh) VALUES
('TP.HCM'),
('Hà Nội'),
('Đà Nẵng'),
('Cần Thơ'),
('Bình Dương');

INSERT INTO loai (TenLoai, MoTa) VALUES
('Rau củ', 'Các loại rau củ quả tươi'),
('Thịt cá', 'Các loại thịt, cá, hải sản'),
('Đồ uống', 'Sữa, nước giải khát, nước uống'),
('Đồ khô', 'Mì, gạo, gia vị, thực phẩm khô');

INSERT INTO donvi (TenDonVi, MoTa) VALUES
('Kg', 'Đơn vị kilogram'),
('Hộp', 'Đơn vị hộp'),
('Chai', 'Đơn vị chai'),
('Gói', 'Đơn vị gói');

INSERT INTO nhanvien (
    MaNV, Ho, TenLot, Ten, Phai, NgaySinh, SDT, TinhThanh, DiaChi, Luong, ChucVu, TrangThai, MatKhau
) VALUES
('admin', 'Phan', 'Tiến', 'Đạt', 'Nam', '2004-01-01', '0900000001', 1, 'TP.HCM', 15000000, 'ADM', 'active', '123456'),
('nv01', 'Nguyễn', 'Văn', 'An', 'Nam', '2003-05-10', '0900000002', 1, 'Thủ Đức', 8000000, 'NV', 'active', '123456'),
('nv02', 'Trần', 'Thị', 'Mai', 'Nữ', '2002-09-21', '0900000003', 2, 'Cầu Giấy', 8500000, 'NV', 'active', '123456');

INSERT INTO khachhang (
    MaKH, Ho, TenLot, Ten, Phai, NgaySinh, SDT, TinhThanh, DiaChi, NgayThamGia, Diem, TrangThai
) VALUES
('KH001', 'Trần', 'Thị', 'Lan', 'Nữ', '2000-03-12', '0911111111', 1, 'Quận 1', '2026-01-01', 20, 'active'),
('KH002', 'Lê', 'Văn', 'Bình', 'Nam', '1998-08-21', '0922222222', 2, 'Ba Đình', '2026-02-15', 10, 'active'),
('KH003', 'Ngô', 'Thanh', 'Hà', 'Nữ', '2001-11-05', '0931234567', 3, 'Hải Châu', '2026-03-05', 15, 'active');

INSERT INTO nhacungcap (
    MaNCCap, TenNCCap, TenLienHe, SDThoai, TinhThanh, DiaChi, TrangThai
) VALUES
('NCC001', 'Công ty Rau Sạch', 'Nguyễn Minh', '0933333333', 1, 'Thủ Đức', 'active'),
('NCC002', 'Thực phẩm An Tâm', 'Lê Hùng', '0944444444', 2, 'Cầu Giấy', 'active'),
('NCC003', 'Kho Đồ Uống Xanh', 'Phạm Long', '0955555555', 3, 'Sơn Trà', 'active');

INSERT INTO sanpham (
    MaSP, TenSP, Loai, DonViTinh, HSDung, MoTa, Gia, SoLuongTon
) VALUES
('SP001', 'Cà chua', 1, 1, 7, 'Cà chua tươi', 30000, 100),
('SP002', 'Thịt bò', 2, 1, 3, 'Thịt bò loại 1', 250000, 50),
('SP003', 'Sữa tươi', 3, 3, 180, 'Sữa tươi không đường', 35000, 80),
('SP004', 'Mì gói', 4, 4, 180, 'Mì gói tiện lợi', 5000, 200),
('SP005', 'Nước cam', 3, 3, 90, 'Nước cam đóng chai', 18000, 120);

INSERT INTO hang (MaHang, MaSP, SoLuong, NSX, GiamGia, TrangThai) VALUES
('H001', 'SP001', 40, '2026-03-01', 0, 'active'),
('H002', 'SP002', 20, '2026-03-18', 5, 'active'),
('H003', 'SP003', 35, '2026-03-10', 10, 'active'),
('H004', 'SP004', 100, '2026-02-25', 0, 'active'),
('H005', 'SP005', 60, '2026-03-14', 5, 'active');

INSERT INTO khuyenmai (MaKM, TenKM, MoTa, PhanTramGiam, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('KM001', 'Khuyến mãi tháng 3', 'Giảm giá toàn cửa hàng tháng 3', 10, '2026-03-01', '2026-03-31', 'active'),
('KM002', 'Giảm giá cuối tuần', 'Áp dụng cuối tuần', 5, '2026-03-20', '2026-03-22', 'active'),
('KM003', 'Khách hàng thân thiết', 'Ưu đãi cho khách mua nhiều', 7, '2026-03-01', '2026-04-15', 'active');

INSERT INTO hoadon (MaHD, MaKH, MaNV, TongTien, TienGiam, ThoiGian) VALUES
('HD001', 'KH001', 'nv01', 54000, 6000, '2026-03-18 09:20:00'),
('HD002', 'KH002', 'nv02', 250000, 0, '2026-03-19 14:35:00'),
('HD003', 'KH003', 'admin', 66600, 7400, '2026-03-20 10:10:00');

INSERT INTO hoadon_khuyenmai (MaHD, MaKM) VALUES
('HD001', 'KM001'),
('HD003', 'KM003');

INSERT INTO chitiethoadon (MaHD, MaSP, SoLuong, DonGia) VALUES
('HD001', 'SP001', 2, 30000),
('HD002', 'SP002', 1, 250000),
('HD003', 'SP003', 2, 35000);

INSERT INTO phieunhaphang (MaPhieu, MaNCCap, NguoiNhap, TongTien, ThoiGian) VALUES
('PN001', 'NCC001', 'nv01', 1200000, '2026-03-16 08:15:00'),
('PN002', 'NCC002', 'admin', 2500000, '2026-03-18 13:45:00'),
('PN003', 'NCC003', 'nv02', 1080000, '2026-03-20 09:00:00');

INSERT INTO chitietpnhap (MaPhieu, MaSP, SoLuong, DonGia) VALUES
('PN001', 'SP001', 40, 20000),
('PN001', 'SP004', 80, 5000),
('PN002', 'SP002', 10, 250000),
('PN003', 'SP005', 60, 18000);