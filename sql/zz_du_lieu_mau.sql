-- =====================================================================
-- DỮ LIỆU MẪU cho demo / kiểm thử
--
-- * Tên file bắt đầu bằng "zz_" để Docker chạy SAU file tạo bảng (chạy theo thứ tự tên).
-- * Ngày tháng tính theo ngày chạy script: hóa đơn trải đều 12 tháng gần nhất tới HÔM NAY,
--   nên Trang chủ và Thống kê có số ngay; có sẵn sản phẩm sắp hết hàng và lô sắp hết hạn.
-- * Chạy lại nhiều lần cũng không bị nhân đôi; không xóa dữ liệu bạn đã nhập.
--
-- Nạp vào database đang chạy (không mất dữ liệu cũ): bấm đúp nap-du-lieu-mau.bat, hoặc
--     docker compose exec db psql -U postgres -d qlcuahangthucpham -f /docker-entrypoint-initdb.d/zz_du_lieu_mau.sql
-- Tạo lại database từ đầu (tự nạp cả file này):
--     docker compose down -v  rồi  docker compose up -d
-- =====================================================================

-- ---------- Nhân viên (mật khẩu đều là 123) ----------
INSERT INTO NhanVien (MaNhanVien, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh, DiaChi, Luong, ChucVu, TrangThai, MatKhau) VALUES
('NV01', 'Nguyễn', 'Thị', 'Hoa',  'nữ',  '1999-03-12', '0905123401', 'TP. Hồ Chí Minh', '12 Nguyễn Trãi, Quận 5',        8500000, 'NV', 'active',   '123'),
('NV02', 'Trần',   'Minh', 'Khoa', 'nam', '1997-07-25', '0905123402', 'TP. Hồ Chí Minh', '88 Lê Văn Sỹ, Quận 3',          9000000, 'NV', 'active',   '123'),
('NV03', 'Lê',     'Thu',  'Trang','nữ',  '2001-11-02', '0905123403', 'Bình Dương',      '5 Đại lộ Bình Dương, Thủ Dầu Một',8000000, 'NV', 'active',   '123'),
('NV04', 'Phạm',   'Quốc', 'Bảo',  'nam', '1994-01-18', '0905123404', 'TP. Hồ Chí Minh', '210 Cách Mạng Tháng 8, Quận 10', 12000000, 'QL', 'active',   '123'),
('NV05', 'Võ',     'Ngọc', 'Lan',  'nữ',  '2000-05-30', '0905123405', 'Long An',         '33 Hùng Vương, Tân An',          7500000, 'NV', 'inactive', '123')
ON CONFLICT (MaNhanVien) DO NOTHING;

-- ---------- Khách hàng ----------
INSERT INTO KhachHang (MaKhachHang, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh, NgayThamGia, Diem, TrangThai) VALUES
('KH003', 'Lê',      'Văn',   'Cường', 'nam', '1990-04-21', '0912000003', 'TP. Hồ Chí Minh', CURRENT_DATE - 700, 0, 'active'),
('KH004', 'Phạm',    'Thị',   'Dung',  'nữ',  '1988-09-09', '0912000004', 'TP. Hồ Chí Minh', CURRENT_DATE - 650, 0, 'active'),
('KH005', 'Hoàng',   'Minh',  'Đức',   'nam', '1995-12-01', '0912000005', 'Bình Dương',      CURRENT_DATE - 600, 0, 'active'),
('KH006', 'Huỳnh',   'Thị',   'Mai',   'nữ',  '1992-02-14', '0912000006', 'Đồng Nai',        CURRENT_DATE - 560, 0, 'active'),
('KH007', 'Phan',    'Văn',   'Hùng',  'nam', '1985-06-30', '0912000007', 'TP. Hồ Chí Minh', CURRENT_DATE - 520, 0, 'active'),
('KH008', 'Vũ',      'Thị',   'Hạnh',  'nữ',  '1998-08-18', '0912000008', 'Long An',         CURRENT_DATE - 480, 0, 'active'),
('KH009', 'Võ',      'Thanh', 'Tâm',   'nam', '1993-03-03', '0912000009', 'TP. Hồ Chí Minh', CURRENT_DATE - 450, 0, 'active'),
('KH010', 'Đặng',    'Thị',   'Ngọc',  'nữ',  '2000-10-10', '0912000010', 'Tây Ninh',        CURRENT_DATE - 420, 0, 'active'),
('KH011', 'Bùi',     'Quang', 'Huy',   'nam', '1991-01-27', '0912000011', 'TP. Hồ Chí Minh', CURRENT_DATE - 390, 0, 'active'),
('KH012', 'Đỗ',      'Thị',   'Lan',   'nữ',  '1987-05-05', '0912000012', 'Bà Rịa - Vũng Tàu', CURRENT_DATE - 360, 0, 'active'),
('KH013', 'Hồ',      'Văn',   'Phúc',  'nam', '1996-07-07', '0912000013', 'TP. Hồ Chí Minh', CURRENT_DATE - 330, 0, 'active'),
('KH014', 'Ngô',     'Thị',   'Thảo',  'nữ',  '1999-09-19', '0912000014', 'Cần Thơ',         CURRENT_DATE - 300, 0, 'active'),
('KH015', 'Dương',   'Minh',  'Tuấn',  'nam', '1989-11-11', '0912000015', 'TP. Hồ Chí Minh', CURRENT_DATE - 270, 0, 'active'),
('KH016', 'Lý',      'Thị',   'Kim',   'nữ',  '1994-04-04', '0912000016', 'Bình Dương',      CURRENT_DATE - 240, 0, 'active'),
('KH017', 'Trịnh',   'Văn',   'Nam',   'nam', '1997-02-22', '0912000017', 'TP. Hồ Chí Minh', CURRENT_DATE - 210, 0, 'active'),
('KH018', 'Mai',     'Thị',   'Yến',   'nữ',  '2002-06-16', '0912000018', 'Đồng Nai',        CURRENT_DATE - 180, 0, 'active'),
('KH019', 'Cao',     'Văn',   'Sơn',   'nam', '1986-12-25', '0912000019', 'TP. Hồ Chí Minh', CURRENT_DATE - 150, 0, 'active'),
('KH020', 'Lâm',     'Thị',   'Vy',    'nữ',  '2001-03-08', '0912000020', 'Long An',         CURRENT_DATE - 120, 0, 'active'),
('KH021', 'Tô',      'Hoàng', 'Long',  'nam', '1993-08-08', '0912000021', 'TP. Hồ Chí Minh', CURRENT_DATE - 90,  0, 'active'),
('KH022', 'Châu',    'Thị',   'Hương', 'nữ',  '1990-10-20', '0912000022', 'Tiền Giang',      CURRENT_DATE - 75,  0, 'active'),
('KH023', 'La',      'Văn',   'Tài',   'nam', '1995-05-15', '0912000023', 'TP. Hồ Chí Minh', CURRENT_DATE - 60,  0, 'active'),
('KH024', 'Kiều',    'Thị',   'Oanh',  'nữ',  '1998-01-01', '0912000024', 'Bình Dương',      CURRENT_DATE - 45,  0, 'active'),
('KH025', 'Quách',   'Văn',   'Thịnh', 'nam', '1992-07-17', '0912000025', 'TP. Hồ Chí Minh', CURRENT_DATE - 30,  0, 'active'),
('KH026', 'Thái',    'Thị',   'Nhung', 'nữ',  '1999-12-12', '0912000026', 'Đồng Nai',        CURRENT_DATE - 20,  0, 'active'),
('KH027', 'Đinh',    'Công',  'Vinh',  'nam', '1988-03-28', '0912000027', 'TP. Hồ Chí Minh', CURRENT_DATE - 12,  0, 'active'),
('KH028', 'Lưu',     'Thị',   'Bích',  'nữ',  '2003-09-09', '0912000028', 'TP. Hồ Chí Minh', CURRENT_DATE - 6,   0, 'active'),
('KH029', 'Tăng',    'Văn',   'Lộc',   'nam', '1991-06-06', '0912000029', 'Tây Ninh',        CURRENT_DATE - 3,   0, 'active'),
('KH030', 'Âu',      'Thị',   'Quyên', 'nữ',  '1996-02-02', '0912000030', 'TP. Hồ Chí Minh', CURRENT_DATE - 1,   0, 'inactive')
ON CONFLICT (MaKhachHang) DO NOTHING;

-- ---------- Nhà cung cấp ----------
INSERT INTO NhaCungCap (MaNhaCungCap, Ten, TenLienHe, SoDienThoai, Tinh, DiaChi, TrangThai) VALUES
('NCC03', 'Công ty TNHH Thực phẩm Sạch Xanh',   'Anh Tùng', '02838123456', 'TP. Hồ Chí Minh', '45 Phan Văn Trị, Gò Vấp',        'active'),
('NCC04', 'Hợp tác xã Rau an toàn Củ Chi',      'Chị Liên', '02837901234', 'TP. Hồ Chí Minh', 'Ấp 3, Tân Thạnh Tây, Củ Chi',    'active'),
('NCC05', 'Công ty CP Sữa và Đồ uống Miền Nam', 'Anh Hải',  '02839445566', 'Bình Dương',      'KCN Sóng Thần, Dĩ An',           'active'),
('NCC06', 'Đại lý Hàng khô Kim Phát',           'Cô Phát',  '02838556677', 'TP. Hồ Chí Minh', '120 Hải Thượng Lãn Ông, Quận 5', 'active'),
('NCC07', 'Vựa Trái cây Miệt Vườn',             'Anh Tư',   '02733887766', 'Tiền Giang',      'QL1A, Cái Bè',                   'inactive')
ON CONFLICT (MaNhaCungCap) DO NOTHING;

-- ---------- Loại sản phẩm ----------
INSERT INTO LoaiSanPham (MaLoai, TenLoai) VALUES
('L04', 'Sữa và đồ uống'),
('L05', 'Đồ khô và gia vị'),
('L06', 'Bánh kẹo'),
('L07', 'Trứng và đậu')
ON CONFLICT (MaLoai) DO NOTHING;

-- ---------- Sản phẩm (HanSuDung = số ngày bảo quản, giống dữ liệu gốc) ----------
-- Một số sản phẩm cố ý có tồn kho thấp để Trang chủ hiện cảnh báo "Sắp hết hàng".
INSERT INTO SanPham (MaSanPham, MaNhom, TenSanPham, Loai, DonViTinh, HanSuDung, MoTa, Gia, SoLuongTon) VALUES
('SP011', 'SU01', 'Sữa tươi Vinamilk 1L',        'Sữa và đồ uống',  'Hộp',  '10',  'Sữa tươi tiệt trùng có đường', 32000, 12),
('SP012', 'SU02', 'Sữa chua Vinamilk (lốc 4)',   'Sữa và đồ uống',  'Lốc',  '20',  'Sữa chua ăn có đường',         26000, 85),
('SP013', 'NU01', 'Nước suối Aquafina 500ml',    'Sữa và đồ uống',  'Chai', '365', 'Nước tinh khiết',               5000, 340),
('SP014', 'NU02', 'Cà phê G7 3in1 (hộp 18 gói)', 'Sữa và đồ uống',  'Hộp',  '365', 'Cà phê hòa tan',               58000, 6),
('SP015', 'DK01', 'Gạo ST25 túi 5kg',            'Đồ khô và gia vị','Túi',  '365', 'Gạo thơm Sóc Trăng',          185000, 42),
('SP016', 'DK02', 'Nước mắm Nam Ngư 500ml',      'Đồ khô và gia vị','Chai', '720', 'Nước mắm cá cơm',              38000, 0),
('SP017', 'DK03', 'Dầu ăn Neptune 1L',           'Đồ khô và gia vị','Chai', '540', 'Dầu ăn tinh luyện',            62000, 25),
('SP018', 'DK04', 'Mì Hảo Hảo tôm chua cay',     'Đồ khô và gia vị','Gói',  '180', 'Mì ăn liền',                    4500, 520),
('SP019', 'DK05', 'Đường trắng Biên Hòa 1kg',    'Đồ khô và gia vị','Túi',  '720', 'Đường tinh luyện',             28000, 96),
('SP020', 'BK01', 'Bánh mì sandwich',            'Bánh kẹo',        'Gói',  '5',   'Bánh mì lát mềm',              22000, 18),
('SP021', 'BK02', 'Bánh quy Cosy (hộp)',         'Bánh kẹo',        'Hộp',  '270', 'Bánh quy bơ',                  45000, 64),
('SP022', 'TR01', 'Trứng gà (vỉ 10 quả)',        'Trứng và đậu',    'Vỉ',   '20',  'Trứng gà tươi',                35000, 9),
('SP023', 'TR02', 'Đậu phụ trắng',               'Trứng và đậu',    'Miếng','3',   'Đậu phụ non',                   6000, 120),
('SP024', 'RC04', 'Khoai tây',                   'Rau củ',          'Kg',   '14',  'Khoai tây Đà Lạt',             28000, 150),
('SP025', 'RC05', 'Cà chua',                     'Rau củ',          'Kg',   '7',   'Cà chua Đà Lạt',               25000, 28),
('SP026', 'TC04', 'Chuối già Nam Mỹ',            'Trái cây',        'Nải',  '5',   'Chuối chín vàng',              30000, 70),
('SP027', 'TC05', 'Xoài cát Hòa Lộc',            'Trái cây',        'Kg',   '6',   'Xoài ngọt thơm',               65000, 45),
('SP028', 'TH03', 'Ức gà phi lê',                'Thịt và hải sản', 'Kg',   '3',   'Ức gà tươi',                   85000, 60),
('SP029', 'CA02', 'Cá basa phi lê',              'Thịt và hải sản', 'Kg',   '3',   'Cá basa làm sạch',             70000, 38),
('SP030', 'TT02', 'Mực ống',                     'Thịt và hải sản', 'Kg',   '3',   'Mực ống tươi',                220000, 22)
ON CONFLICT (MaSanPham) DO NOTHING;

-- ---------- Khuyến mãi (theo ngày hiện tại) ----------
INSERT INTO KhuyenMai (MaKhuyenMai, TenKhuyenMai, DieuKien, PhanTramGiam, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('KM004', 'Cuối tháng giảm giá',     '>=200000', 10, CURRENT_DATE - 10,  CURRENT_DATE + 20,  'active'),
('KM005', 'Tri ân khách hàng thân thiết', '>=500000', 15, CURRENT_DATE - 5, CURRENT_DATE + 25, 'active'),
('KM006', 'Khai trương chi nhánh',   '>=150000', 12, CURRENT_DATE - 200, CURRENT_DATE - 170, 'inactive'),
('KM007', 'Tết sum vầy',             '>=300000', 8,  CURRENT_DATE + 60,  CURRENT_DATE + 90,  'active')
ON CONFLICT (MaKhuyenMai) DO NOTHING;

-- ---------- Lô hàng trong kho (hạn sử dụng tính theo hôm nay) ----------
-- Có lô đã hết hạn, hết hạn hôm nay, còn vài ngày… để Trang chủ hiện cảnh báo "Lô hàng sắp hết hạn".
INSERT INTO HangHoa (MaHang, MaSanPham, SoLuong, NgayNhap, HanSuDung) VALUES
('HH101', 'SP002', 40,  CURRENT_DATE - 5,  CURRENT_DATE - 2),
('HH102', 'SP020', 15,  CURRENT_DATE - 4,  CURRENT_DATE),
('HH103', 'SP012', 60,  CURRENT_DATE - 18, CURRENT_DATE + 2),
('HH104', 'SP007', 30,  CURRENT_DATE - 1,  CURRENT_DATE + 3),
('HH105', 'SP023', 25,  CURRENT_DATE - 1,  CURRENT_DATE + 4),
('HH106', 'SP029', 18,  CURRENT_DATE - 1,  CURRENT_DATE + 6),
('HH107', 'SP011', 48,  CURRENT_DATE - 2,  CURRENT_DATE + 12),
('HH108', 'SP015', 80,  CURRENT_DATE - 30, CURRENT_DATE + 330),
('HH109', 'SP018', 500, CURRENT_DATE - 20, CURRENT_DATE + 160),
('HH110', 'SP027', 45,  CURRENT_DATE - 1,  CURRENT_DATE + 9),
('HH111', 'SP001', 120, CURRENT_DATE - 1,  CURRENT_DATE + 8),
('HH112', 'SP013', 340, CURRENT_DATE - 40, CURRENT_DATE + 320)
ON CONFLICT (MaHang) DO NOTHING;

-- ---------- Hóa đơn + phiếu nhập trong 12 tháng gần nhất ----------
DO $$
DECLARE
    v_day        DATE;
    v_n          INT;
    v_i          INT;
    v_items      INT;
    v_hd_no      INT;
    v_pn_no      INT;
    v_ma         TEXT;
    v_kh         TEXT;
    v_nv         TEXT;
    v_sp         RECORD;
    v_qty        INT;
    v_total      DOUBLE PRECISION;
    v_km         TEXT;
    v_giam       DOUBLE PRECISION;
    v_gia_nhap   DOUBLE PRECISION;
    v_customers  TEXT[];
    v_staff      TEXT[] := ARRAY['NV01', 'NV02', 'NV03', 'NV04', 'nhanvien', 'admin'];
    v_ncc        TEXT[] := ARRAY['NCC01', 'NCC02', 'NCC03', 'NCC04', 'NCC05', 'NCC06'];
BEGIN
    -- Đã nạp rồi thì thôi (chạy lại không bị nhân đôi)
    IF EXISTS (SELECT 1 FROM HoaDon WHERE MaNhanVien = 'NV01') THEN
        RAISE NOTICE 'Dữ liệu mẫu đã có sẵn – bỏ qua phần hóa đơn/phiếu nhập.';
        RETURN;
    END IF;

    PERFORM setseed(0.2026);   -- cố định ngẫu nhiên để lần nào nạp cũng ra cùng bộ số liệu
    SELECT array_agg(MaKhachHang ORDER BY MaKhachHang) INTO v_customers
    FROM KhachHang WHERE TrangThai = 'active';

    -- Đánh số tiếp nối mã lớn nhất đang có (app sinh mã mới cũng theo cách này)
    SELECT COALESCE(MAX(CAST(SUBSTRING(MaHoaDon FROM 3) AS INT)), 0) INTO v_hd_no
    FROM HoaDon WHERE MaHoaDon ~ '^HD[0-9]+$';
    SELECT COALESCE(MAX(CAST(SUBSTRING(MaPhieuNhap FROM 3) AS INT)), 0) INTO v_pn_no
    FROM PhieuNhap WHERE MaPhieuNhap ~ '^PN[0-9]+$';

    FOR v_day IN SELECT d::date FROM generate_series(CURRENT_DATE - 364, CURRENT_DATE, INTERVAL '1 day') d LOOP
        -- Số hóa đơn mỗi ngày: tăng dần theo thời gian (cửa hàng đông khách hơn), cuối tuần đông hơn
        v_n := 2 + floor(random() * 4)::INT
               + CASE WHEN EXTRACT(ISODOW FROM v_day) >= 6 THEN 3 ELSE 0 END
               + floor((365 - (CURRENT_DATE - v_day)) / 120.0)::INT;
        IF v_day = CURRENT_DATE THEN
            v_n := GREATEST(v_n, 8);
        END IF;

        FOR v_i IN 1..v_n LOOP
            v_hd_no := v_hd_no + 1;
            v_ma := 'HD' || v_hd_no;
            v_kh := v_customers[1 + floor(random() * array_length(v_customers, 1))::INT];
            v_nv := v_staff[1 + floor(random() * array_length(v_staff, 1))::INT];

            INSERT INTO HoaDon (MaHoaDon, MaKhachHang, MaNhanVien, TongTien, TienGiam, ThoiGian, MaKhuyenMai)
            VALUES (v_ma, v_kh, v_nv, 0, 0, v_day, NULL);

            v_items := 1 + floor(random() * 4)::INT;
            v_total := 0;
            FOR v_sp IN SELECT MaSanPham, Gia FROM SanPham ORDER BY random() LIMIT v_items LOOP
                v_qty := 1 + floor(random() * 4)::INT;
                INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia)
                VALUES (v_ma, v_sp.MaSanPham, v_qty, v_sp.Gia);
                v_total := v_total + v_qty * v_sp.Gia;
            END LOOP;

            -- Áp khuyến mãi đang chạy nếu đủ điều kiện (giống logic khi lập hóa đơn trong app)
            v_km := NULL;
            v_giam := 0;
            IF v_day BETWEEN CURRENT_DATE - 5 AND CURRENT_DATE AND v_total >= 500000 THEN
                v_km := 'KM005'; v_giam := v_total * 0.15;
            ELSIF v_day BETWEEN CURRENT_DATE - 10 AND CURRENT_DATE AND v_total >= 200000 THEN
                v_km := 'KM004'; v_giam := v_total * 0.10;
            ELSIF v_day BETWEEN CURRENT_DATE - 200 AND CURRENT_DATE - 170 AND v_total >= 150000 THEN
                v_km := 'KM006'; v_giam := v_total * 0.12;
            END IF;

            UPDATE HoaDon SET TongTien = v_total, TienGiam = round(v_giam::numeric, 0), MaKhuyenMai = v_km
            WHERE MaHoaDon = v_ma;
        END LOOP;

        -- Nhập hàng: khoảng 2 lần mỗi tuần
        IF EXTRACT(ISODOW FROM v_day) IN (1, 4) THEN
            v_pn_no := v_pn_no + 1;
            v_ma := 'PN' || v_pn_no;
            INSERT INTO PhieuNhap (MaPhieuNhap, MaNhaCungCap, MaNhanVien, TongTien, ThoiGian)
            VALUES (v_ma, v_ncc[1 + floor(random() * array_length(v_ncc, 1))::INT],
                    CASE WHEN random() < 0.5 THEN 'NV04' ELSE 'admin' END, 0, v_day);
            v_total := 0;
            FOR v_sp IN SELECT MaSanPham, Gia FROM SanPham ORDER BY random() LIMIT 2 + floor(random() * 4)::INT LOOP
                v_qty := (5 + floor(random() * 26)::INT) * 10;          -- 50 – 300
                v_gia_nhap := round((v_sp.Gia * (0.6 + random() * 0.2))::numeric, -2);   -- giá nhập 60–80% giá bán
                INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGia)
                VALUES (v_ma, v_sp.MaSanPham, v_qty, v_gia_nhap);
                v_total := v_total + v_qty * v_gia_nhap;
            END LOOP;
            UPDATE PhieuNhap SET TongTien = v_total WHERE MaPhieuNhap = v_ma;
        END IF;
    END LOOP;

    -- Điểm tích lũy: 1 điểm cho mỗi 10.000 đ đã mua (chỉ khách mẫu)
    UPDATE KhachHang k SET Diem = sub.diem
    FROM (SELECT MaKhachHang, floor(SUM(TongTien - TienGiam) / 10000)::INT AS diem
          FROM HoaDon GROUP BY MaKhachHang) sub
    WHERE k.MaKhachHang = sub.MaKhachHang AND k.MaKhachHang >= 'KH003';

    RAISE NOTICE 'Đã nạp % hóa đơn và % phiếu nhập mẫu.',
        (SELECT COUNT(*) FROM HoaDon WHERE MaNhanVien IN ('NV01','NV02','NV03','NV04') OR ThoiGian > CURRENT_DATE - 365),
        (SELECT COUNT(*) FROM PhieuNhap WHERE ThoiGian > CURRENT_DATE - 365);
END $$;
