package vn.qlcuahang.web.nhapkho;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public final class PhieuNhapDtos {

    private PhieuNhapDtos() {
    }

    public record NhaCungCap(String maNhaCungCap, String ten, String tenLienHe, String soDienThoai) {
    }

    public record PhieuNhapTomTat(
            String maPhieuNhap,
            String maNhaCungCap,
            String tenNhaCungCap,
            String maNhanVien,
            String tenNhanVien,
            double tongTien,
            LocalDate thoiGian,
            int soMatHang) {
    }

    public record DongNhap(String maSanPham, String tenSanPham, String donViTinh, int soLuong, double donGia,
                           double thanhTien) {
    }

    /** Lô hàng (bảng HangHoa) được tạo khi dòng nhập có hạn sử dụng. */
    public record LoHang(String maHang, String maSanPham, int soLuong, LocalDate hanSuDung) {
    }

    public record PhieuNhapChiTiet(PhieuNhapTomTat phieuNhap, List<DongNhap> items, List<LoHang> loHangMoi) {
    }

    public record DongNhapRequest(
            @NotBlank(message = "Thiếu mã sản phẩm") String maSanPham,
            @NotNull(message = "Vui lòng nhập số lượng") @Min(value = 1, message = "Số lượng phải lớn hơn 0")
            @Max(value = 1_000_000, message = "Số lượng quá lớn") Integer soLuong,
            @NotNull(message = "Vui lòng nhập giá nhập") @DecimalMin(value = "0", message = "Giá nhập không được âm")
            Double donGia,
            /** Không bắt buộc. Có thì tạo một lô hàng để Trang tổng quan cảnh báo khi sắp hết hạn. */
            LocalDate hanSuDung) {
    }

    public record TaoPhieuNhapRequest(
            @NotBlank(message = "Vui lòng chọn nhà cung cấp") String maNhaCungCap,
            @NotEmpty(message = "Phiếu nhập chưa có sản phẩm nào") @Size(max = 100, message = "Tối đa 100 dòng")
            List<@Valid DongNhapRequest> items) {
    }
}
