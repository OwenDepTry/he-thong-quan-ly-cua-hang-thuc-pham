package vn.qlcuahang.web.dashboard;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.config.AppProperties;
import vn.qlcuahang.web.security.CurrentUser;

/**
 * Trang tổng quan – cùng số liệu với DashboardDAO của app desktop.
 * Doanh thu chỉ trả về cho quản lý; nhân viên thấy số hóa đơn (giống Trang chủ desktop).
 */
@RestController
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tổng quan")
public class DashboardController {

    public record SapHetHang(String maSanPham, String tenSanPham, String donViTinh, int soLuongTon) {
    }

    public record SapHetHan(String maHang, String maSanPham, String tenSanPham, int soLuong, LocalDate hanSuDung,
                            long soNgayConLai) {
    }

    public record Dashboard(
            LocalDate ngay,
            Double doanhThuHomNay,
            int hoaDonHomNay,
            Double doanhThuThangNay,
            int hoaDonThangNay,
            int tongSanPham,
            int tongKhachHang,
            int nguongSapHetHang,
            int soSapHetHang,
            List<SapHetHang> sapHetHang,
            int soNgayCanhBaoHetHan,
            int soLoSapHetHan,
            List<SapHetHan> sapHetHan) {
    }

    private final JdbcClient jdbc;
    private final AppProperties props;

    public DashboardController(JdbcClient jdbc, AppProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    @GetMapping("/api/dashboard")
    @Transactional(readOnly = true)
    public Dashboard dashboard(Authentication auth) {
        boolean ql = CurrentUser.from(auth).isQuanLy();
        AppProperties.Dashboard cfg = props.dashboard();
        // Tính ngày trong Java theo giờ Việt Nam, không phụ thuộc múi giờ của server DB
        LocalDate today = LocalDate.now(props.zoneId());
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        LocalDate firstOfNextMonth = firstOfMonth.plusMonths(1);

        Object[] homNay = jdbc.sql(
                        "SELECT COALESCE(SUM(TongTien - COALESCE(TienGiam, 0)), 0), COUNT(*) FROM HoaDon WHERE ThoiGian = ?")
                .param(today)
                .query((rs, i) -> new Object[]{rs.getDouble(1), rs.getInt(2)})
                .single();
        Object[] thangNay = jdbc.sql("SELECT COALESCE(SUM(TongTien - COALESCE(TienGiam, 0)), 0), COUNT(*) "
                        + "FROM HoaDon WHERE ThoiGian >= ? AND ThoiGian < ?")
                .params(firstOfMonth, firstOfNextMonth)
                .query((rs, i) -> new Object[]{rs.getDouble(1), rs.getInt(2)})
                .single();

        int tongSanPham = count("SELECT COUNT(*) FROM SanPham");
        int tongKhachHang = count("SELECT COUNT(*) FROM KhachHang");

        int soSapHetHang = jdbc.sql("SELECT COUNT(*) FROM SanPham WHERE SoLuongTon <= ?")
                .param(cfg.lowStockThreshold()).query(Integer.class).single();
        List<SapHetHang> sapHetHang = jdbc.sql("""
                        SELECT MaSanPham, TenSanPham, DonViTinh, SoLuongTon FROM SanPham
                        WHERE SoLuongTon <= ? ORDER BY SoLuongTon ASC, MaSanPham ASC LIMIT ?
                        """)
                .params(cfg.lowStockThreshold(), cfg.listLimit())
                .query((rs, i) -> new SapHetHang(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)))
                .list();

        // Lô còn hàng, hết hạn trong N ngày tới hoặc đã hết hạn chưa quá M ngày
        LocalDate from = today.minusDays(cfg.expiredLookbackDays());
        LocalDate to = today.plusDays(cfg.expiryWarningDays());
        String expiryWhere = " FROM HangHoa h JOIN SanPham s ON s.MaSanPham = h.MaSanPham "
                + "WHERE h.SoLuong > 0 AND h.HanSuDung IS NOT NULL AND h.HanSuDung BETWEEN ? AND ?";
        int soLoSapHetHan = jdbc.sql("SELECT COUNT(*)" + expiryWhere)
                .params(from, to).query(Integer.class).single();
        List<SapHetHan> sapHetHan = jdbc.sql("SELECT h.MaHang, h.MaSanPham, s.TenSanPham, h.SoLuong, h.HanSuDung"
                        + expiryWhere + " ORDER BY h.HanSuDung ASC, h.MaHang ASC LIMIT ?")
                .params(from, to, cfg.listLimit())
                .query((rs, i) -> {
                    LocalDate han = ((Date) rs.getDate(5)).toLocalDate();
                    return new SapHetHan(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), han,
                            java.time.temporal.ChronoUnit.DAYS.between(today, han));
                })
                .list();

        return new Dashboard(
                today,
                ql ? (Double) homNay[0] : null, (Integer) homNay[1],
                ql ? (Double) thangNay[0] : null, (Integer) thangNay[1],
                tongSanPham, tongKhachHang,
                cfg.lowStockThreshold(), soSapHetHang, sapHetHang,
                cfg.expiryWarningDays(), soLoSapHetHan, sapHetHan);
    }

    private int count(String sql) {
        return jdbc.sql(sql).query(Integer.class).single();
    }
}
