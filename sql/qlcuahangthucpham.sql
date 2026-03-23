    DROP DATABASE IF EXISTS qlcuahangthucpham;
    CREATE DATABASE qlcuahangthucpham;
    USE qlcuahangthucpham;

    -- =========================
    -- 1. BẢNG NHÂN VIÊN
    -- =========================
    CREATE TABLE NhanVien (
        MaNhanVien VARCHAR(50) PRIMARY KEY,
        Ho VARCHAR(50),
        TenLot VARCHAR(50),
        Ten VARCHAR(50),
        Phai VARCHAR(10),
        NgaySinh DATE,
        SoDienThoai VARCHAR(20),
        Tinh VARCHAR(100),
        DiaChi VARCHAR(255),
        Luong DOUBLE,
        ChucVu VARCHAR(10),
        TrangThai VARCHAR(20),
        MatKhau VARCHAR(100)
    );

    INSERT INTO NhanVien (
        MaNhanVien, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai,
        Tinh, DiaChi, Luong, ChucVu, TrangThai, MatKhau
    ) VALUES
    ('admin', 'Siêu', 'Quản', 'Lý', 'nam', '1995-01-01', '1234567890', 'Quảng Ngãi', 'Đại học ...', 10000000, 'QL', 'active', 'admin'),
    ('nhanvien', 'Người', 'Nhân', 'Viên', 'nam', '1998-01-01', '0987654321', 'Quảng Ngãi', 'Đại học ...', 8000000, 'NV', 'active', '123');

    -- =========================
    -- 2. BẢNG KHÁCH HÀNG
    -- =========================
    CREATE TABLE KhachHang (
        MaKhachHang VARCHAR(50) PRIMARY KEY,
        Ho VARCHAR(50),
        TenLot VARCHAR(50),
        Ten VARCHAR(50),
        Phai VARCHAR(10),
        NgaySinh DATE,
        SoDienThoai VARCHAR(20),
        Tinh VARCHAR(100),
        NgayThamGia DATE,
        Diem INT,
        TrangThai VARCHAR(20)
    );

    INSERT INTO KhachHang (
        MaKhachHang, Ho, TenLot, Ten, Phai, NgaySinh,
        SoDienThoai, Tinh, NgayThamGia, Diem, TrangThai
    ) VALUES
    ('KH001', 'Nguyễn', 'Văn', 'A', 'nam', '2000-01-01', '11111111', 'Hà Nội', '2025-01-01', 0, 'active'),
    ('KH002', 'Trần', 'Thị', 'B', 'nữ', '2001-02-10', '22222222', 'Đà Nẵng', '2025-02-15', 10, 'active');

    -- =========================
    -- 3. BẢNG NHÀ CUNG CẤP
    -- =========================
    CREATE TABLE NhaCungCap (
        MaNhaCungCap VARCHAR(50) PRIMARY KEY,
        Ten VARCHAR(100),
        TenLienHe VARCHAR(100),
        SoDienThoai VARCHAR(20),
        Tinh VARCHAR(100),
        DiaChi VARCHAR(255),
        TrangThai VARCHAR(20)
    );

    INSERT INTO NhaCungCap (
        MaNhaCungCap, Ten, TenLienHe, SoDienThoai, Tinh, DiaChi, TrangThai
    ) VALUES
    ('NCC01', 'Nhà cung cấp A', 'Anh B', '0900000000', 'TP.HCM', 'Quận 1', 'active'),
    ('NCC02', 'Nhà cung cấp B', 'Chị C', '0911111111', 'Hà Nội', 'Cầu Giấy', 'active');

    -- =========================
    -- 4. BẢNG SẢN PHẨM
    -- =========================
    CREATE TABLE SanPham (
        MaSanPham VARCHAR(50) PRIMARY KEY,
        MaNhom VARCHAR(50),
        TenSanPham VARCHAR(100),
        Loai VARCHAR(100),
        DonViTinh VARCHAR(50),
        HanSuDung VARCHAR(50),
        MoTa VARCHAR(255),
        Gia DOUBLE,
        SoLuongTon INT
    );

    INSERT INTO SanPham (
        MaSanPham, MaNhom, TenSanPham, Loai, DonViTinh,
        HanSuDung, MoTa, Gia, SoLuongTon
    ) VALUES
    ('SP001', 'TH01', 'Thịt bò', 'Thịt và hải sản', 'Kg', '3', 'Thịt bò tươi', 30000, 560),
    ('SP002', 'TH02', 'Thịt heo', 'Thịt và hải sản', 'Kg', '3', 'Thịt heo tươi', 12000, 676),
    ('SP003', 'CA01', 'Cá hồi', 'Thịt và hải sản', 'Kg', '7', 'Cá hồi nhập khẩu', 300000, 499),
    ('SP004', 'TT01', 'Tôm tươi', 'Thịt và hải sản', 'Kg', '5', 'Tôm tươi sống', 100000, 400),
    ('SP005', 'RC01', 'Cà rốt', 'Rau củ', 'Kg', '7', 'Cà rốt tươi', 10000, 320),
    ('SP006', 'RC02', 'Rau muống', 'Rau củ', 'Kg', '2', 'Rau muống tươi', 15000, 210),
    ('SP007', 'RC03', 'Cải ngọt', 'Rau củ', 'Kg', '2', 'Cải ngọt tươi', 22000, 180),
    ('SP008', 'TC01', 'Táo', 'Trái cây', 'Kg', '10', 'Táo đỏ tươi', 40000, 280),
    ('SP009', 'TC02', 'Cam', 'Trái cây', 'Kg', '10', 'Cam tươi', 35000, 260),
    ('SP010', 'TC03', 'Nho', 'Trái cây', 'Kg', '7', 'Nho xanh tươi', 45000, 190);

    -- =========================
    -- 5. BẢNG KHUYẾN MÃI
    -- =========================
    CREATE TABLE KhuyenMai (
        MaKhuyenMai VARCHAR(50) PRIMARY KEY,
        TenKhuyenMai VARCHAR(100),
        DieuKien VARCHAR(100),
        PhanTramGiam DOUBLE,
        NgayBatDau DATE,
        NgayKetThuc DATE,
        TrangThai VARCHAR(20)
    );

    INSERT INTO KhuyenMai (
        MaKhuyenMai, TenKhuyenMai, DieuKien, PhanTramGiam,
        NgayBatDau, NgayKetThuc, TrangThai
    ) VALUES
    ('KM001', 'thitbongon', '>=30000', 20, '2025-04-01', '2025-05-01', 'active'),
    ('KM002', 'raucuoi', '>=100000', 10, '2025-04-15', '2025-06-01', 'active'),
    ('KM003', 'sieusale', '>=500000', 15, '2025-03-10', '2025-04-30', 'inactive');

    -- =========================
    -- 6. BẢNG HÓA ĐƠN
    -- =========================
    CREATE TABLE HoaDon (
        MaHoaDon VARCHAR(50) PRIMARY KEY,
        MaKhachHang VARCHAR(50),
        MaNhanVien VARCHAR(50),
        TongTien DOUBLE,
        TienGiam DOUBLE,
        ThoiGian DATE,
        MaKhuyenMai VARCHAR(50),
        FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
        FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
        FOREIGN KEY (MaKhuyenMai) REFERENCES KhuyenMai(MaKhuyenMai)
    );

    INSERT INTO HoaDon (
        MaHoaDon, MaKhachHang, MaNhanVien, TongTien, TienGiam, ThoiGian, MaKhuyenMai
    ) VALUES
    ('HD1', 'KH001', 'nhanvien', 30000, 6000, '2025-04-28', 'KM001'),
    ('HD2', 'KH002', 'nhanvien', 420000, 0, '2025-04-28', NULL);

    -- =========================
    -- 7. BẢNG CHI TIẾT HÓA ĐƠN
    -- =========================
    CREATE TABLE ChiTietHoaDon (
        MaHoaDon VARCHAR(50),
        MaSanPham VARCHAR(50),
        SoLuong INT,
        DonGia DOUBLE,
        PRIMARY KEY (MaHoaDon, MaSanPham),
        FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon),
        FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
    );

    INSERT INTO ChiTietHoaDon (
        MaHoaDon, MaSanPham, SoLuong, DonGia
    ) VALUES
    ('HD1', 'SP001', 1, 30000),
    ('HD2', 'SP004', 2, 100000),
    ('HD2', 'SP008', 3, 40000);

    -- =========================
    -- 8. BẢNG PHIẾU NHẬP
    -- =========================
    CREATE TABLE PhieuNhap (
        MaPhieuNhap VARCHAR(50) PRIMARY KEY,
        MaNhaCungCap VARCHAR(50),
        MaNhanVien VARCHAR(50),
        TongTien DOUBLE,
        ThoiGian DATE,
        FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
        FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
    );

    INSERT INTO PhieuNhap (
        MaPhieuNhap, MaNhaCungCap, MaNhanVien, TongTien, ThoiGian
    ) VALUES
    ('PN1', 'NCC01', 'nhanvien', 204000000, '2025-04-28');

    -- =========================
    -- 9. BẢNG CHI TIẾT PHIẾU NHẬP
    -- =========================
    CREATE TABLE ChiTietPhieuNhap (
        MaPhieuNhap VARCHAR(50),
        MaSanPham VARCHAR(50),
        SoLuong INT,
        DonGia DOUBLE,
        PRIMARY KEY (MaPhieuNhap, MaSanPham),
        FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap),
        FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
    );

    INSERT INTO ChiTietPhieuNhap (
        MaPhieuNhap, MaSanPham, SoLuong, DonGia
    ) VALUES
    ('PN1', 'SP001', 500, 20000),
    ('PN1', 'SP002', 500, 10000);

    JTabbedPane tabs = new JTabbedPane();

tabs.addTab("Quản lý sản phẩm", panelSanPhamCu);
tabs.addTab("Quản lý loại sản phẩm", new LoaiSanPhamPanel());
tabs.addTab("Quản lý hàng hóa", new HangHoaPanel());

setLayout(new BorderLayout());
add(tabs, BorderLayout.CENTER);

-- =========================
-- 10. BẢNG LOẠI SẢN PHẨM
-- =========================
CREATE TABLE LoaiSanPham (
    MaLoai VARCHAR(50) PRIMARY KEY,
    TenLoai VARCHAR(100) NOT NULL
);

INSERT INTO LoaiSanPham (MaLoai, TenLoai) VALUES
('L01', 'Thịt và hải sản'),
('L02', 'Rau củ'),
('L03', 'Trái cây');

-- =========================
-- 11. BẢNG HÀNG HÓA
-- =========================
CREATE TABLE HangHoa (
    MaHang VARCHAR(50) PRIMARY KEY,
    MaSanPham VARCHAR(50),
    SoLuong INT,
    NgayNhap DATE,
    HanSuDung DATE,
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

INSERT INTO HangHoa (MaHang, MaSanPham, SoLuong, NgayNhap, HanSuDung) VALUES
('HH001', 'SP001', 200, '2025-04-01', '2025-04-04'),
('HH002', 'SP002', 250, '2025-04-01', '2025-04-04'),
('HH003', 'SP003', 100, '2025-04-05', '2025-04-12'),
('HH004', 'SP004', 150, '2025-04-05', '2025-04-10'),
('HH005', 'SP005', 180, '2025-04-02', '2025-04-09'),
('HH006', 'SP006', 140, '2025-04-03', '2025-04-05'),
('HH007', 'SP008', 120, '2025-04-06', '2025-04-16');