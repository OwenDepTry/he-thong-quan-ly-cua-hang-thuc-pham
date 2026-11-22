package vn.qlcuahang.web.sanpham;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.sanpham.SanPhamDtos.SanPham;

/**
 * Ảnh sản phẩm. Xem ảnh không cần đăng nhập (thẻ img không gửi được token, và ảnh hàng hóa không phải
 * dữ liệu nhạy cảm); tải lên và gỡ ảnh chỉ dành cho quản lý.
 */
@RestController
@Tag(name = "Sản phẩm")
public class HinhAnhController {

    /** Ảnh đã được trình duyệt thu nhỏ trước khi gửi nên 2 MB là dư. */
    static final int MAX_BYTES = 2 * 1024 * 1024;

    private record Anh(byte[] duLieu, String loai, String url) {
    }

    private final JdbcClient jdbc;
    private final SanPhamRepository repo;

    public HinhAnhController(JdbcClient jdbc, SanPhamRepository repo) {
        this.jdbc = jdbc;
        this.repo = repo;
    }

    @GetMapping("/api/san-pham/{ma}/hinh-anh")
    @Operation(summary = "Ảnh sản phẩm", description = "Ảnh tải lên trả về trực tiếp; ảnh ngoài thì chuyển hướng (302).")
    public ResponseEntity<byte[]> get(@PathVariable String ma) {
        Anh anh = find(ma).orElseThrow(() -> ApiException.notFound("Sản phẩm " + ma + " chưa có ảnh."));
        if (anh.duLieu() == null) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(anh.url())).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(anh.loai()))
                // Đường dẫn có ?v=<thời điểm cập nhật> nên cache lâu được
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(anh.duLieu());
    }

    @PutMapping(path = "/api/san-pham/{ma}/hinh-anh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('QL')")
    @Transactional
    @Operation(summary = "Tải ảnh sản phẩm lên", description = "JPEG, PNG hoặc WebP, tối đa 2 MB. Thay ảnh cũ nếu có.")
    public SanPham upload(@PathVariable String ma, @RequestParam("file") MultipartFile file) throws IOException {
        if (!repo.exists(ma)) {
            throw ApiException.notFound("Không tìm thấy sản phẩm " + ma + ".");
        }
        if (file.isEmpty()) {
            throw ApiException.badRequest("Chưa chọn ảnh.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw ApiException.badRequest("Ảnh lớn hơn 2 MB.");
        }
        byte[] data = file.getBytes();
        // Kiểm tra theo nội dung file, không tin phần mở rộng hay Content-Type trình duyệt gửi
        String loai = detectImageType(data);
        if (loai == null) {
            throw ApiException.badRequest("Chỉ nhận ảnh JPEG, PNG hoặc WebP.");
        }
        jdbc.sql("""
                INSERT INTO SanPhamHinhAnh (MaSanPham, DuLieu, LoaiNoiDung, Url, NguonAnh, TrangNguon, CapNhat)
                VALUES (?, ?, ?, NULL, NULL, NULL, clock_timestamp())
                ON CONFLICT (MaSanPham) DO UPDATE SET DuLieu = EXCLUDED.DuLieu, LoaiNoiDung = EXCLUDED.LoaiNoiDung,
                    Url = NULL, NguonAnh = NULL, TrangNguon = NULL, CapNhat = clock_timestamp()
                """)
                .params(ma, data, loai)
                .update();
        return repo.findById(ma).orElseThrow();
    }

    @DeleteMapping("/api/san-pham/{ma}/hinh-anh")
    @PreAuthorize("hasRole('QL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Operation(summary = "Gỡ ảnh sản phẩm")
    public void delete(@PathVariable String ma) {
        jdbc.sql("DELETE FROM SanPhamHinhAnh WHERE MaSanPham = ?").param(ma).update();
    }

    private Optional<Anh> find(String ma) {
        return jdbc.sql("SELECT DuLieu, LoaiNoiDung, Url FROM SanPhamHinhAnh WHERE MaSanPham = ?")
                .param(ma)
                .query((rs, i) -> new Anh(rs.getBytes(1), rs.getString(2), rs.getString(3)))
                .optional();
    }

    static String detectImageType(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "image/png";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "image/webp";
        }
        return null;
    }
}
