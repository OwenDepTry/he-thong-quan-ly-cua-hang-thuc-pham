package vn.qlcuahang.web.hoadon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public final class HoaDonDtos {

    private HoaDonDtos() {
    }

    public record DongHang(
            @NotBlank(message = "Thiếu mã sản phẩm") @Size(max = 50) String maSanPham,
            @NotNull(message = "Thiếu số lượng") @Min(value = 1, message = "Số lượng phải từ 1")
            @Max(value = 100000, message = "Số lượng quá lớn") Integer soLuong) {
    }

    /**
     * Lập hóa đơn. Nhân viên lập = người đăng nhập, ngày = hôm nay, đơn giá = giá hiện tại trong DB
     * (không tin giá do trình duyệt gửi lên).
     */
    public record TaoHoaDonRequest(
            @Size(max = 50) String maKhachHang,
            @Size(max = 50) String maKhuyenMai,
            @NotEmpty(message = "Hóa đơn phải có ít nhất 1 sản phẩm")
            @Size(max = 100, message = "Tối đa 100 dòng hàng")
            List<@Valid @NotNull DongHang> items) {
    }

    public record ChiTiet(String maSanPham, String tenSanPham, String donViTinh, int soLuong, double donGia,
                          double thanhTien) {
    }

    /** Kết quả tính tiền (xem trước hoặc sau khi lưu). */
    public record TinhTien(List<ChiTiet> items, double tongTien, double tienGiam, double thanhTien,
                           String maKhuyenMai, Double phanTramGiam) {
    }

    public record HoaDonTomTat(String maHoaDon, String maKhachHang, String tenKhachHang, String maNhanVien,
                               String tenNhanVien, double tongTien, double tienGiam, double thanhTien,
                               LocalDate thoiGian, String maKhuyenMai) {
    }

    public record HoaDonChiTiet(HoaDonTomTat hoaDon, List<ChiTiet> items) {
    }

    public record Trang<T>(List<T> items, long total, int page, int size) {
    }
}
