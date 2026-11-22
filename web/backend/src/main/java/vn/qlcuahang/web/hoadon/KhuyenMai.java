package vn.qlcuahang.web.hoadon;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Chương trình khuyến mãi và luật áp dụng – giống HoaDonDAO.lyDoKhuyenMaiKhongHopLe của app desktop:
 * đang "active", ngày hóa đơn nằm trong [NgayBatDau, NgayKetThuc], tổng tiền đạt DieuKien
 * (dạng ">=30000", ">100.000", "=500000"; "=" hiểu là ">=" như desktop).
 */
public record KhuyenMai(
        String maKhuyenMai,
        String tenKhuyenMai,
        String dieuKien,
        double phanTramGiam,
        LocalDate ngayBatDau,
        LocalDate ngayKetThuc,
        String trangThai) {

    private static final Pattern DIEU_KIEN = Pattern.compile("^\\s*(>=|>|=)?\\s*([\\d.,]+)");

    /** @return null nếu áp dụng được, ngược lại là lý do (tiếng Việt). */
    public String lyDoKhongHopLe(double tongTien, LocalDate ngayHoaDon) {
        if (!"active".equalsIgnoreCase(trangThai == null ? "" : trangThai.trim())) {
            return "Khuyến mãi " + maKhuyenMai + " đang ngừng áp dụng.";
        }
        if (ngayBatDau != null && ngayHoaDon.isBefore(ngayBatDau)) {
            return "Khuyến mãi " + maKhuyenMai + " chưa bắt đầu (từ " + ngayBatDau + ").";
        }
        if (ngayKetThuc != null && ngayHoaDon.isAfter(ngayKetThuc)) {
            return "Khuyến mãi " + maKhuyenMai + " đã hết hạn (đến " + ngayKetThuc + ").";
        }
        Double nguong = nguong();
        if (nguong != null) {
            boolean dat = isStrict() ? tongTien > nguong : tongTien >= nguong;
            if (!dat) {
                return "Hóa đơn chưa đạt điều kiện khuyến mãi " + maKhuyenMai + " (" + dieuKien.trim() + ").";
            }
        }
        return null;
    }

    /** Số tiền tối thiểu trong DieuKien, null nếu không có điều kiện đọc được. */
    public Double nguong() {
        Matcher m = dieuKien == null ? null : DIEU_KIEN.matcher(dieuKien);
        if (m == null || !m.find()) {
            return null;
        }
        String digits = m.group(2).replace(",", "").replace(".", "");
        return digits.isEmpty() ? null : Double.parseDouble(digits);
    }

    private boolean isStrict() {
        Matcher m = dieuKien == null ? null : DIEU_KIEN.matcher(dieuKien);
        return m != null && m.find() && ">".equals(m.group(1));
    }

    /** Tiền giảm làm tròn tới đồng. */
    public double tinhTienGiam(double tongTien) {
        return Math.round(tongTien * phanTramGiam / 100.0);
    }
}
