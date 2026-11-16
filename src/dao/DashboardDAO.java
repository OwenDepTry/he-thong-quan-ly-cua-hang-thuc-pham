package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Số liệu cho Trang chủ. Tất cả truy vấn dùng chung 1 kết nối, gọi ở luồng nền (xem ui.AppAsync).
 */
public class DashboardDAO {

    /** Tồn kho từ mức này trở xuống được coi là "sắp hết hàng". */
    public static final int LOW_STOCK_THRESHOLD = 30;
    /** Lô hàng hết hạn trong vòng bấy nhiêu ngày tới (hoặc đã hết hạn) sẽ được cảnh báo. */
    public static final int EXPIRY_WARNING_DAYS = 7;
    /** Lô đã hết hạn quá số ngày này coi như đã xử lý, không cảnh báo nữa (vẫn xem được ở Quản lý hàng hóa). */
    public static final int EXPIRED_LOOKBACK_DAYS = 30;
    private static final int LIST_LIMIT = 5;

    public static class Data {
        public double doanhThuHomNay;
        public int hoaDonHomNay;
        public double doanhThuThangNay;
        public int hoaDonThangNay;
        public int tongSanPham;
        public int tongKhachHang;
        public int soSapHetHang;
        public int soLoSapHetHan;
        /** {maSanPham, tenSanPham, donViTinh, soLuongTon} */
        public final List<Object[]> sapHetHang = new ArrayList<>();
        /** {maHang, tenSanPham, soLuong, hanSuDung (java.sql.Date)} */
        public final List<Object[]> sapHetHan = new ArrayList<>();
    }

    public Data load() throws SQLException {
        Data d = new Data();
        try (Connection conn = DBConnection.open()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(TongTien - TienGiam), 0), COUNT(*) FROM HoaDon WHERE ThoiGian = CURRENT_DATE");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    d.doanhThuHomNay = rs.getDouble(1);
                    d.hoaDonHomNay = rs.getInt(2);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(SUM(TongTien - TienGiam), 0), COUNT(*) FROM HoaDon "
                    + "WHERE EXTRACT(YEAR FROM ThoiGian) = EXTRACT(YEAR FROM CURRENT_DATE) "
                    + "AND EXTRACT(MONTH FROM ThoiGian) = EXTRACT(MONTH FROM CURRENT_DATE)");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    d.doanhThuThangNay = rs.getDouble(1);
                    d.hoaDonThangNay = rs.getInt(2);
                }
            }
            d.tongSanPham = count(conn, "SELECT COUNT(*) FROM SanPham");
            d.tongKhachHang = count(conn, "SELECT COUNT(*) FROM KhachHang");

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM SanPham WHERE SoLuongTon <= ?")) {
                ps.setInt(1, LOW_STOCK_THRESHOLD);
                try (ResultSet rs = ps.executeQuery()) {
                    d.soSapHetHang = rs.next() ? rs.getInt(1) : 0;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT MaSanPham, TenSanPham, DonViTinh, SoLuongTon FROM SanPham "
                    + "WHERE SoLuongTon <= ? ORDER BY SoLuongTon ASC, MaSanPham ASC LIMIT " + LIST_LIMIT)) {
                ps.setInt(1, LOW_STOCK_THRESHOLD);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        d.sapHetHang.add(new Object[]{
                            rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4)
                        });
                    }
                }
            }

            String expiryWhere = " FROM HangHoa h JOIN SanPham s ON s.MaSanPham = h.MaSanPham "
                    + "WHERE h.SoLuong > 0 AND h.HanSuDung IS NOT NULL "
                    + "AND h.HanSuDung BETWEEN CURRENT_DATE - " + EXPIRED_LOOKBACK_DAYS
                    + " AND CURRENT_DATE + " + EXPIRY_WARNING_DAYS;
            d.soLoSapHetHan = count(conn, "SELECT COUNT(*)" + expiryWhere);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT h.MaHang, s.TenSanPham, h.SoLuong, h.HanSuDung" + expiryWhere
                    + " ORDER BY h.HanSuDung ASC, h.MaHang ASC LIMIT " + LIST_LIMIT);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    d.sapHetHan.add(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDate(4)
                    });
                }
            }
        }
        return d;
    }

    private static int count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
