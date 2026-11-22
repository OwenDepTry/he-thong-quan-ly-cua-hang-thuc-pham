package vn.qlcuahang.web.hoadon;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import vn.qlcuahang.web.hoadon.HoaDonDtos.ChiTiet;
import vn.qlcuahang.web.hoadon.HoaDonDtos.HoaDonTomTat;

@Repository
public class HoaDonRepository {

    /** Thông tin sản phẩm dùng khi bán. */
    public record SanPhamBan(String maSanPham, String tenSanPham, String donViTinh, double gia, int soLuongTon) {
    }

    private static final String SELECT_TOM_TAT = """
            SELECT hd.MaHoaDon, hd.MaKhachHang,
                   TRIM(COALESCE(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten), '')) AS TenKhachHang,
                   hd.MaNhanVien,
                   TRIM(COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '')) AS TenNhanVien,
                   COALESCE(hd.TongTien, 0) AS TongTien, COALESCE(hd.TienGiam, 0) AS TienGiam,
                   hd.ThoiGian, hd.MaKhuyenMai
            FROM HoaDon hd
            LEFT JOIN KhachHang kh ON kh.MaKhachHang = hd.MaKhachHang
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien
            """;

    private static final String ORDER_BY = """
             ORDER BY hd.ThoiGian DESC NULLS LAST,
                      CAST(NULLIF(REGEXP_REPLACE(SUBSTRING(hd.MaHoaDon FROM 3), '[^0-9]', '', 'g'), '') AS BIGINT)
                          DESC NULLS LAST,
                      hd.MaHoaDon DESC
            """;

    private final JdbcClient jdbc;

    public HoaDonRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Đọc ----------

    /**
     * @param maNhanVien null = mọi nhân viên (quản lý), ngược lại chỉ hóa đơn của nhân viên đó
     */
    public List<HoaDonTomTat> search(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay,
                                     int limit, int offset) {
        Filter f = filter(maNhanVien, keyword, tuNgay, denNgay);
        List<Object> params = new ArrayList<>(f.params);
        params.add(limit);
        params.add(offset);
        return jdbc.sql(SELECT_TOM_TAT + f.where + ORDER_BY + " LIMIT ? OFFSET ?")
                .params(params)
                .query(HoaDonRepository::mapTomTat)
                .list();
    }

    public long count(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay) {
        Filter f = filter(maNhanVien, keyword, tuNgay, denNgay);
        return jdbc.sql("SELECT COUNT(*) FROM HoaDon hd " + f.where)
                .params(f.params)
                .query(Long.class).single();
    }

    public Optional<HoaDonTomTat> findTomTat(String maHoaDon) {
        return jdbc.sql(SELECT_TOM_TAT + " WHERE hd.MaHoaDon = ?")
                .param(maHoaDon)
                .query(HoaDonRepository::mapTomTat)
                .optional();
    }

    public List<ChiTiet> findChiTiet(String maHoaDon) {
        return jdbc.sql("""
                SELECT ct.MaSanPham, sp.TenSanPham, sp.DonViTinh, ct.SoLuong, ct.DonGia
                FROM ChiTietHoaDon ct
                JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham
                WHERE ct.MaHoaDon = ?
                ORDER BY ct.MaSanPham
                """)
                .param(maHoaDon)
                .query((rs, i) -> {
                    int sl = rs.getInt("SoLuong");
                    double gia = rs.getDouble("DonGia");
                    return new ChiTiet(rs.getString("MaSanPham"), rs.getString("TenSanPham"),
                            rs.getString("DonViTinh"), sl, gia, sl * gia);
                })
                .list();
    }

    public Optional<SanPhamBan> findSanPham(String ma, boolean forUpdate) {
        return jdbc.sql("SELECT MaSanPham, TenSanPham, DonViTinh, COALESCE(Gia, 0) AS Gia, "
                        + "COALESCE(SoLuongTon, 0) AS SoLuongTon FROM SanPham WHERE MaSanPham = ?"
                        + (forUpdate ? " FOR UPDATE" : ""))
                .param(ma)
                .query((rs, i) -> new SanPhamBan(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getDouble(4), rs.getInt(5)))
                .optional();
    }

    public Optional<KhuyenMai> findKhuyenMai(String ma) {
        return jdbc.sql("SELECT MaKhuyenMai, TenKhuyenMai, DieuKien, PhanTramGiam, NgayBatDau, NgayKetThuc, "
                        + "TrangThai FROM KhuyenMai WHERE MaKhuyenMai = ?")
                .param(ma)
                .query(HoaDonRepository::mapKhuyenMai)
                .optional();
    }

    /** Khuyến mãi đang hoạt động và còn hiệu lực trong ngày. */
    public List<KhuyenMai> findKhuyenMaiHieuLuc(LocalDate ngay) {
        return jdbc.sql("SELECT MaKhuyenMai, TenKhuyenMai, DieuKien, PhanTramGiam, NgayBatDau, NgayKetThuc, "
                        + "TrangThai FROM KhuyenMai WHERE TrangThai = 'active' "
                        + "AND (NgayBatDau IS NULL OR NgayBatDau <= ?) AND (NgayKetThuc IS NULL OR NgayKetThuc >= ?) "
                        + "ORDER BY MaKhuyenMai")
                .params(ngay, ngay)
                .query(HoaDonRepository::mapKhuyenMai)
                .list();
    }

    public boolean khachHangTonTai(String ma) {
        return jdbc.sql("SELECT COUNT(*) FROM KhachHang WHERE MaKhachHang = ?").param(ma)
                .query(Integer.class).single() > 0;
    }

    // ---------- Ghi ----------

    /**
     * Sinh mã "HD{n+1}" giống app desktop. Khóa advisory theo transaction để 2 người lập hóa đơn
     * cùng lúc trên web không lấy trùng mã.
     */
    public String nextMaHoaDon() {
        jdbc.sql("SELECT pg_advisory_xact_lock(hashtext('qlcuahang.HoaDon.MaHoaDon'))")
                .query((rs, i) -> Boolean.TRUE).single();
        Long max = jdbc.sql("""
                SELECT COALESCE(MAX(CAST(SUBSTRING(UPPER(TRIM(MaHoaDon)) FROM 3) AS BIGINT)), 0)
                FROM HoaDon WHERE UPPER(TRIM(MaHoaDon)) ~ '^HD[0-9]{1,18}$'
                """).query(Long.class).single();
        return "HD" + (max + 1);
    }

    public void insertHoaDon(String ma, String maKH, String maNV, double tongTien, double tienGiam, LocalDate ngay,
                             String maKM) {
        jdbc.sql("""
                INSERT INTO HoaDon (MaHoaDon, MaKhachHang, MaNhanVien, TongTien, TienGiam, ThoiGian, MaKhuyenMai)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)
                .params(ma, maKH, maNV, tongTien, tienGiam, ngay, maKM)
                .update();
    }

    public void insertChiTiet(String maHoaDon, String maSP, int soLuong, double donGia) {
        jdbc.sql("INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia) VALUES (?, ?, ?, ?)")
                .params(maHoaDon, maSP, soLuong, donGia)
                .update();
    }

    /** Trừ tồn kho; trả về false nếu không đủ hàng. */
    public boolean truTonKho(String maSP, int soLuong) {
        return jdbc.sql("UPDATE SanPham SET SoLuongTon = SoLuongTon - ? WHERE MaSanPham = ? AND SoLuongTon >= ?")
                .params(soLuong, maSP, soLuong)
                .update() > 0;
    }

    // ---------- Tiện ích ----------

    private record Filter(String where, List<Object> params) {
    }

    private static Filter filter(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (maNhanVien != null) {
            where.append(" AND hd.MaNhanVien = ?");
            params.add(maNhanVien);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (hd.MaHoaDon ILIKE ? OR COALESCE(hd.MaKhachHang, '') ILIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        if (tuNgay != null) {
            where.append(" AND hd.ThoiGian >= ?");
            params.add(tuNgay);
        }
        if (denNgay != null) {
            where.append(" AND hd.ThoiGian <= ?");
            params.add(denNgay);
        }
        return new Filter(where.toString(), params);
    }

    private static HoaDonTomTat mapTomTat(ResultSet rs, int i) throws SQLException {
        double tong = rs.getDouble("TongTien");
        double giam = rs.getDouble("TienGiam");
        Date d = rs.getDate("ThoiGian");
        return new HoaDonTomTat(rs.getString("MaHoaDon"), rs.getString("MaKhachHang"),
                rs.getString("TenKhachHang"), rs.getString("MaNhanVien"), rs.getString("TenNhanVien"),
                tong, giam, tong - giam, d == null ? null : d.toLocalDate(), rs.getString("MaKhuyenMai"));
    }

    private static KhuyenMai mapKhuyenMai(ResultSet rs, int i) throws SQLException {
        Date bd = rs.getDate("NgayBatDau");
        Date kt = rs.getDate("NgayKetThuc");
        return new KhuyenMai(rs.getString("MaKhuyenMai"), rs.getString("TenKhuyenMai"), rs.getString("DieuKien"),
                rs.getDouble("PhanTramGiam"), bd == null ? null : bd.toLocalDate(),
                kt == null ? null : kt.toLocalDate(), rs.getString("TrangThai"));
    }
}
