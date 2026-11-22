package vn.qlcuahang.web.nhapkho;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.DongNhap;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.NhaCungCap;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.PhieuNhapTomTat;

@Repository
public class PhieuNhapRepository {

    private static final String SELECT_TOM_TAT = """
            SELECT pn.MaPhieuNhap, pn.MaNhaCungCap, ncc.Ten AS TenNhaCungCap, pn.MaNhanVien,
                   TRIM(COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '')) AS TenNhanVien,
                   COALESCE(pn.TongTien, 0) AS TongTien, pn.ThoiGian,
                   (SELECT COUNT(*) FROM ChiTietPhieuNhap ct WHERE ct.MaPhieuNhap = pn.MaPhieuNhap) AS SoMatHang
            FROM PhieuNhap pn
            LEFT JOIN NhaCungCap ncc ON ncc.MaNhaCungCap = pn.MaNhaCungCap
            LEFT JOIN NhanVien nv ON nv.MaNhanVien = pn.MaNhanVien
            """;

    private static final String ORDER_BY = """
             ORDER BY pn.ThoiGian DESC NULLS LAST,
                      CAST(NULLIF(REGEXP_REPLACE(SUBSTRING(pn.MaPhieuNhap FROM 3), '[^0-9]', '', 'g'), '') AS BIGINT)
                          DESC NULLS LAST,
                      pn.MaPhieuNhap DESC
            """;

    private final JdbcClient jdbc;

    public PhieuNhapRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<NhaCungCap> findNhaCungCap() {
        return jdbc.sql("""
                SELECT MaNhaCungCap, Ten, TenLienHe, SoDienThoai FROM NhaCungCap
                WHERE COALESCE(TrangThai, 'active') = 'active'
                ORDER BY Ten
                """)
                .query((rs, i) -> new NhaCungCap(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)))
                .list();
    }

    public boolean nhaCungCapHoatDong(String ma) {
        return jdbc.sql("SELECT COUNT(*) FROM NhaCungCap WHERE MaNhaCungCap = ? "
                        + "AND COALESCE(TrangThai, 'active') = 'active'")
                .param(ma).query(Integer.class).single() > 0;
    }

    public List<PhieuNhapTomTat> search(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay,
                                        int limit, int offset) {
        Filter f = filter(maNhanVien, keyword, tuNgay, denNgay);
        List<Object> params = new ArrayList<>(f.params);
        params.add(limit);
        params.add(offset);
        return jdbc.sql(SELECT_TOM_TAT + f.where + ORDER_BY + " LIMIT ? OFFSET ?")
                .params(params)
                .query(PhieuNhapRepository::mapTomTat)
                .list();
    }

    public long count(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay) {
        Filter f = filter(maNhanVien, keyword, tuNgay, denNgay);
        return jdbc.sql("SELECT COUNT(*) FROM PhieuNhap pn LEFT JOIN NhaCungCap ncc "
                        + "ON ncc.MaNhaCungCap = pn.MaNhaCungCap" + f.where)
                .params(f.params)
                .query(Long.class).single();
    }

    public Optional<PhieuNhapTomTat> findTomTat(String ma) {
        return jdbc.sql(SELECT_TOM_TAT + " WHERE pn.MaPhieuNhap = ?")
                .param(ma)
                .query(PhieuNhapRepository::mapTomTat)
                .optional();
    }

    public List<DongNhap> findChiTiet(String ma) {
        return jdbc.sql("""
                SELECT ct.MaSanPham, sp.TenSanPham, sp.DonViTinh, ct.SoLuong, ct.DonGia
                FROM ChiTietPhieuNhap ct
                JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham
                WHERE ct.MaPhieuNhap = ?
                ORDER BY ct.MaSanPham
                """)
                .param(ma)
                .query((rs, i) -> {
                    int sl = rs.getInt("SoLuong");
                    double gia = rs.getDouble("DonGia");
                    return new DongNhap(rs.getString("MaSanPham"), rs.getString("TenSanPham"),
                            rs.getString("DonViTinh"), sl, gia, sl * gia);
                })
                .list();
    }

    /** Khóa dòng sản phẩm trong transaction, trả về tên sản phẩm nếu tồn tại. */
    public Optional<String> lockSanPham(String ma) {
        return jdbc.sql("SELECT TenSanPham FROM SanPham WHERE MaSanPham = ? FOR UPDATE")
                .param(ma).query(String.class).optional();
    }

    public void insertPhieuNhap(String ma, String maNCC, String maNV, double tongTien, LocalDate ngay) {
        jdbc.sql("INSERT INTO PhieuNhap (MaPhieuNhap, MaNhaCungCap, MaNhanVien, TongTien, ThoiGian) "
                        + "VALUES (?, ?, ?, ?, ?)")
                .params(ma, maNCC, maNV, tongTien, ngay)
                .update();
    }

    public void insertChiTiet(String ma, String maSP, int soLuong, double donGia) {
        jdbc.sql("INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGia) VALUES (?, ?, ?, ?)")
                .params(ma, maSP, soLuong, donGia)
                .update();
    }

    public void congTonKho(String maSP, int soLuong) {
        jdbc.sql("UPDATE SanPham SET SoLuongTon = COALESCE(SoLuongTon, 0) + ? WHERE MaSanPham = ?")
                .params(soLuong, maSP)
                .update();
    }

    public void insertLoHang(String maHang, String maSP, int soLuong, LocalDate ngayNhap, LocalDate hanSuDung) {
        jdbc.sql("INSERT INTO HangHoa (MaHang, MaSanPham, SoLuong, NgayNhap, HanSuDung) VALUES (?, ?, ?, ?, ?)")
                .params(maHang, maSP, soLuong, ngayNhap, hanSuDung)
                .update();
    }

    private record Filter(String where, List<Object> params) {
    }

    private static Filter filter(String maNhanVien, String keyword, LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (maNhanVien != null) {
            where.append(" AND pn.MaNhanVien = ?");
            params.add(maNhanVien);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (pn.MaPhieuNhap ILIKE ? OR unaccent(COALESCE(ncc.Ten, '')) ILIKE unaccent(?))");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        if (tuNgay != null) {
            where.append(" AND pn.ThoiGian >= ?");
            params.add(tuNgay);
        }
        if (denNgay != null) {
            where.append(" AND pn.ThoiGian <= ?");
            params.add(denNgay);
        }
        return new Filter(where.toString(), params);
    }

    private static PhieuNhapTomTat mapTomTat(ResultSet rs, int i) throws SQLException {
        Date d = rs.getDate("ThoiGian");
        return new PhieuNhapTomTat(rs.getString("MaPhieuNhap"), rs.getString("MaNhaCungCap"),
                rs.getString("TenNhaCungCap"), rs.getString("MaNhanVien"), rs.getString("TenNhanVien"),
                rs.getDouble("TongTien"), d == null ? null : d.toLocalDate(), rs.getInt("SoMatHang"));
    }
}
