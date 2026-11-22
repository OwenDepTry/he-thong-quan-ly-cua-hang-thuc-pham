package vn.qlcuahang.web.sanpham;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class SanPhamDtos {

    private SanPhamDtos() {
    }

    public record SanPham(
            String maSanPham,
            String maNhom,
            String tenSanPham,
            String loai,
            String donViTinh,
            /** Số ngày sử dụng (cột HanSuDung kiểu chuỗi trong schema cũ). */
            String hanSuDung,
            String moTa,
            double gia,
            int soLuongTon) {
    }

    /** Dùng cho cả thêm (cần maSanPham) và sửa (maSanPham lấy từ URL). */
    public record SanPhamRequest(
            @Size(max = 50) @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Mã chỉ gồm chữ, số, - và _")
            String maSanPham,
            @Size(max = 50) String maNhom,
            @NotBlank(message = "Vui lòng nhập tên sản phẩm") @Size(max = 100) String tenSanPham,
            @Size(max = 100) String loai,
            @NotBlank(message = "Vui lòng nhập đơn vị tính") @Size(max = 50) String donViTinh,
            @Pattern(regexp = "^\\d{0,5}$", message = "Hạn sử dụng là số ngày") String hanSuDung,
            @Size(max = 255) String moTa,
            @NotNull(message = "Vui lòng nhập giá") @DecimalMin(value = "0", message = "Giá không được âm") Double gia,
            @NotNull(message = "Vui lòng nhập tồn kho") @Min(value = 0, message = "Tồn kho không được âm") Integer soLuongTon) {
    }

    public record LoaiSanPham(String maLoai, String tenLoai) {
    }
}
