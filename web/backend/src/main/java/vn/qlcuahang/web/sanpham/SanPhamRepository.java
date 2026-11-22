package vn.qlcuahang.web.sanpham;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import vn.qlcuahang.web.sanpham.SanPhamDtos.LoaiSanPham;
import vn.qlcuahang.web.sanpham.SanPhamDtos.SanPham;
import vn.qlcuahang.web.sanpham.SanPhamDtos.SanPhamRequest;

@Repository
public class SanPhamRepository {

    private static final String COLUMNS =
            "MaSanPham, MaNhom, TenSanPham, Loai, DonViTinh, HanSuDung, MoTa, Gia, SoLuongTon";

    private final JdbcClient jdbc;

    public SanPhamRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Tìm theo mã hoặc tên, không phân biệt hoa thường và dấu (giống app desktop). */
    public List<SanPham> search(String keyword, String loai) {
        String k = keyword == null ? "" : keyword.trim();
        String l = loai == null ? "" : loai.trim();
        return jdbc.sql("SELECT " + COLUMNS + " FROM SanPham "
                        + "WHERE (? = '' OR unaccent(MaSanPham || ' ' || COALESCE(TenSanPham, '')) ILIKE unaccent(?)) "
                        + "AND (? = '' OR Loai = ?) "
                        + "ORDER BY MaSanPham")
                .params(k, "%" + k + "%", l, l)
                .query(SanPhamRepository::map)
                .list();
    }

    public Optional<SanPham> findById(String ma) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM SanPham WHERE MaSanPham = ?")
                .param(ma)
                .query(SanPhamRepository::map)
                .optional();
    }

    public boolean exists(String ma) {
        return jdbc.sql("SELECT COUNT(*) FROM SanPham WHERE MaSanPham = ?").param(ma)
                .query(Integer.class).single() > 0;
    }

    public void insert(String ma, SanPhamRequest r) {
        jdbc.sql("INSERT INTO SanPham (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(ma, blankToNull(r.maNhom()), r.tenSanPham().trim(), blankToNull(r.loai()),
                        r.donViTinh().trim(), blankToNull(r.hanSuDung()), blankToNull(r.moTa()),
                        r.gia(), r.soLuongTon())
                .update();
    }

    public boolean update(String ma, SanPhamRequest r) {
        return jdbc.sql("""
                UPDATE SanPham SET MaNhom = ?, TenSanPham = ?, Loai = ?, DonViTinh = ?, HanSuDung = ?,
                                   MoTa = ?, Gia = ?, SoLuongTon = ?
                WHERE MaSanPham = ?
                """)
                .params(blankToNull(r.maNhom()), r.tenSanPham().trim(), blankToNull(r.loai()),
                        r.donViTinh().trim(), blankToNull(r.hanSuDung()), blankToNull(r.moTa()),
                        r.gia(), r.soLuongTon(), ma)
                .update() > 0;
    }

    /** Số bản ghi khác đang tham chiếu tới sản phẩm (hóa đơn, phiếu nhập, lô hàng). */
    public int countReferences(String ma) {
        return jdbc.sql("""
                SELECT (SELECT COUNT(*) FROM ChiTietHoaDon WHERE MaSanPham = ?)
                     + (SELECT COUNT(*) FROM ChiTietPhieuNhap WHERE MaSanPham = ?)
                     + (SELECT COUNT(*) FROM HangHoa WHERE MaSanPham = ?)
                """)
                .params(ma, ma, ma)
                .query(Integer.class).single();
    }

    public boolean delete(String ma) {
        return jdbc.sql("DELETE FROM SanPham WHERE MaSanPham = ?").param(ma).update() > 0;
    }

    public List<LoaiSanPham> findLoai() {
        return jdbc.sql("SELECT MaLoai, TenLoai FROM LoaiSanPham ORDER BY MaLoai")
                .query((rs, i) -> new LoaiSanPham(rs.getString(1), rs.getString(2)))
                .list();
    }

    private static SanPham map(ResultSet rs, int rowNum) throws SQLException {
        return new SanPham(
                rs.getString("MaSanPham"),
                rs.getString("MaNhom"),
                rs.getString("TenSanPham"),
                rs.getString("Loai"),
                rs.getString("DonViTinh"),
                rs.getString("HanSuDung"),
                rs.getString("MoTa"),
                rs.getDouble("Gia"),
                rs.getInt("SoLuongTon"));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
