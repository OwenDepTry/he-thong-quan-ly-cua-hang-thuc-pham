package vn.qlcuahang.web.baocao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.config.AppProperties;

/**
 * Báo cáo doanh thu cho quản lý: tổng hợp kỳ này so với kỳ trước, doanh thu theo ngày/tháng,
 * sản phẩm bán chạy và doanh thu theo nhân viên. Doanh thu = TongTien - TienGiam (khách thực trả).
 */
@RestController
@Tag(name = "Báo cáo", description = "Chỉ quản lý")
public class BaoCaoController {

    /** Khoảng thời gian dài nhất cho một báo cáo. */
    static final int MAX_DAYS = 3 * 366;

    public record TongHop(double doanhThu, double tienGiam, int soHoaDon, double giaTriTrungBinh,
                          double tienNhap, int soPhieuNhap) {
    }

    /** {@code ky} là ngày (nhom=ngay) hoặc ngày đầu tháng (nhom=thang). */
    public record DiemDoanhThu(LocalDate ky, double doanhThu, int soHoaDon) {
    }

    public record SanPhamBanChay(String maSanPham, String tenSanPham, String loai, String donViTinh,
                                 long soLuong, double doanhThu) {
    }

    public record NhanVienDoanhThu(String maNhanVien, String hoTen, int soHoaDon, double doanhThu) {
    }

    public record BaoCao(LocalDate tuNgay, LocalDate denNgay, String nhom, TongHop tongHop, TongHop kyTruoc,
                         List<DiemDoanhThu> theoKy, List<SanPhamBanChay> sanPhamBanChay,
                         List<NhanVienDoanhThu> theoNhanVien) {
    }

    private static final String DOANH_THU = "COALESCE(SUM(hd.TongTien - COALESCE(hd.TienGiam, 0)), 0)";

    private final JdbcClient jdbc;
    private final AppProperties props;

    public BaoCaoController(JdbcClient jdbc, AppProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    @GetMapping("/api/bao-cao")
    @PreAuthorize("hasRole('QL')")
    @Transactional(readOnly = true)
    @Operation(summary = "Báo cáo doanh thu",
            description = "Mặc định 30 ngày gần nhất. nhom = ngay | thang; bỏ trống thì tự chọn theo độ dài khoảng.")
    public BaoCao baoCao(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                         LocalDate tuNgay,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                         LocalDate denNgay,
                         @RequestParam(required = false) String nhom,
                         @RequestParam(defaultValue = "10") int top) {
        LocalDate den = denNgay != null ? denNgay : LocalDate.now(props.zoneId());
        LocalDate tu = tuNgay != null ? tuNgay : den.minusDays(29);
        if (tu.isAfter(den)) {
            throw ApiException.badRequest("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        }
        long soNgay = ChronoUnit.DAYS.between(tu, den) + 1;
        if (soNgay > MAX_DAYS) {
            throw ApiException.badRequest("Mỗi báo cáo tối đa 3 năm.");
        }
        String n = nhom == null || nhom.isBlank() ? (soNgay > 62 ? "thang" : "ngay") : nhom.trim();
        if (!n.equals("ngay") && !n.equals("thang")) {
            throw ApiException.badRequest("nhom chỉ nhận ngay hoặc thang.");
        }
        int limit = Math.min(Math.max(top, 1), 50);

        LocalDate tuTruoc = tu.minusDays(soNgay);
        LocalDate denTruoc = tu.minusDays(1);
        return new BaoCao(tu, den, n, tongHop(tu, den), tongHop(tuTruoc, denTruoc), theoKy(tu, den, n),
                sanPhamBanChay(tu, den, limit), theoNhanVien(tu, den));
    }

    private TongHop tongHop(LocalDate tu, LocalDate den) {
        Object[] ban = jdbc.sql("SELECT " + DOANH_THU + ", COALESCE(SUM(COALESCE(hd.TienGiam, 0)), 0), COUNT(*) "
                        + "FROM HoaDon hd WHERE hd.ThoiGian BETWEEN ? AND ?")
                .params(tu, den)
                .query((rs, i) -> new Object[]{rs.getDouble(1), rs.getDouble(2), rs.getInt(3)})
                .single();
        Object[] nhap = jdbc.sql("SELECT COALESCE(SUM(TongTien), 0), COUNT(*) FROM PhieuNhap "
                        + "WHERE ThoiGian BETWEEN ? AND ?")
                .params(tu, den)
                .query((rs, i) -> new Object[]{rs.getDouble(1), rs.getInt(2)})
                .single();
        double doanhThu = (Double) ban[0];
        int soHoaDon = (Integer) ban[2];
        return new TongHop(doanhThu, (Double) ban[1], soHoaDon, soHoaDon == 0 ? 0 : doanhThu / soHoaDon,
                (Double) nhap[0], (Integer) nhap[1]);
    }

    /** Đủ mọi ngày/tháng trong khoảng, kỳ không có hóa đơn trả về 0 để vẽ biểu đồ liền mạch. */
    private List<DiemDoanhThu> theoKy(LocalDate tu, LocalDate den, String nhom) {
        boolean thang = nhom.equals("thang");
        String ky = thang ? "CAST(DATE_TRUNC('month', hd.ThoiGian) AS DATE)" : "hd.ThoiGian";
        Map<LocalDate, DiemDoanhThu> data = new HashMap<>();
        jdbc.sql("SELECT " + ky + " AS Ky, " + DOANH_THU + ", COUNT(*) FROM HoaDon hd "
                        + "WHERE hd.ThoiGian BETWEEN ? AND ? GROUP BY 1")
                .params(tu, den)
                .query((rs, i) -> new DiemDoanhThu(rs.getDate(1).toLocalDate(), rs.getDouble(2), rs.getInt(3)))
                .list()
                .forEach(d -> data.put(d.ky(), d));

        List<DiemDoanhThu> out = new ArrayList<>();
        LocalDate d = thang ? tu.withDayOfMonth(1) : tu;
        while (!d.isAfter(den)) {
            out.add(data.getOrDefault(d, new DiemDoanhThu(d, 0, 0)));
            d = thang ? d.plusMonths(1) : d.plusDays(1);
        }
        return out;
    }

    private List<SanPhamBanChay> sanPhamBanChay(LocalDate tu, LocalDate den, int limit) {
        return jdbc.sql("""
                SELECT ct.MaSanPham, sp.TenSanPham, sp.Loai, sp.DonViTinh,
                       SUM(ct.SoLuong) AS SoLuong, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu
                FROM ChiTietHoaDon ct
                JOIN HoaDon hd ON hd.MaHoaDon = ct.MaHoaDon
                JOIN SanPham sp ON sp.MaSanPham = ct.MaSanPham
                WHERE hd.ThoiGian BETWEEN ? AND ?
                GROUP BY ct.MaSanPham, sp.TenSanPham, sp.Loai, sp.DonViTinh
                ORDER BY DoanhThu DESC, ct.MaSanPham
                LIMIT ?
                """)
                .params(tu, den, limit)
                .query((rs, i) -> new SanPhamBanChay(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getLong(5), rs.getDouble(6)))
                .list();
    }

    private List<NhanVienDoanhThu> theoNhanVien(LocalDate tu, LocalDate den) {
        return jdbc.sql("SELECT hd.MaNhanVien, "
                        + "TRIM(COALESCE(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten), '')), "
                        + "COUNT(*), " + DOANH_THU + " AS DoanhThu "
                        + "FROM HoaDon hd LEFT JOIN NhanVien nv ON nv.MaNhanVien = hd.MaNhanVien "
                        + "WHERE hd.ThoiGian BETWEEN ? AND ? "
                        + "GROUP BY hd.MaNhanVien, nv.Ho, nv.TenLot, nv.Ten ORDER BY DoanhThu DESC")
                .params(tu, den)
                .query((rs, i) -> new NhanVienDoanhThu(rs.getString(1), rs.getString(2), rs.getInt(3),
                        rs.getDouble(4)))
                .list();
    }
}
