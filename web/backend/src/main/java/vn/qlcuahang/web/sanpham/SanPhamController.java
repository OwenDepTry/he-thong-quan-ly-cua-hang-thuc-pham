package vn.qlcuahang.web.sanpham;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.sanpham.SanPhamDtos.LoaiSanPham;
import vn.qlcuahang.web.sanpham.SanPhamDtos.SanPham;
import vn.qlcuahang.web.sanpham.SanPhamDtos.SanPhamRequest;

/** Quản lý sản phẩm. Xem: mọi người dùng; thêm/sửa/xóa: chỉ quản lý (cấu hình thêm ở SecurityConfig). */
@RestController
public class SanPhamController {

    private final SanPhamRepository repo;

    public SanPhamController(SanPhamRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/san-pham")
    public List<SanPham> list(@RequestParam(required = false) String q,
                              @RequestParam(required = false) String loai) {
        return repo.search(q, loai);
    }

    @GetMapping("/api/san-pham/{ma}")
    public SanPham get(@PathVariable String ma) {
        return repo.findById(ma).orElseThrow(() -> notFound(ma));
    }

    @GetMapping("/api/loai-san-pham")
    public List<LoaiSanPham> loai() {
        return repo.findLoai();
    }

    @PostMapping("/api/san-pham")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('QL')")
    @Transactional
    public SanPham create(@Valid @RequestBody SanPhamRequest req) {
        String ma = req.maSanPham() == null ? "" : req.maSanPham().trim();
        if (ma.isEmpty()) {
            throw ApiException.badRequest("Vui lòng nhập mã sản phẩm.");
        }
        if (repo.exists(ma)) {
            throw ApiException.conflict("Mã sản phẩm " + ma + " đã tồn tại.");
        }
        repo.insert(ma, req);
        return repo.findById(ma).orElseThrow();
    }

    @PutMapping("/api/san-pham/{ma}")
    @PreAuthorize("hasRole('QL')")
    @Transactional
    public SanPham update(@PathVariable String ma, @Valid @RequestBody SanPhamRequest req) {
        if (!repo.update(ma, req)) {
            throw notFound(ma);
        }
        return repo.findById(ma).orElseThrow();
    }

    @DeleteMapping("/api/san-pham/{ma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('QL')")
    @Transactional
    public void delete(@PathVariable String ma) {
        if (!repo.exists(ma)) {
            throw notFound(ma);
        }
        if (repo.countReferences(ma) > 0) {
            throw ApiException.conflict(
                    "Không thể xóa: sản phẩm " + ma + " đã có trong hóa đơn, phiếu nhập hoặc lô hàng.");
        }
        repo.delete(ma);
    }

    private static ApiException notFound(String ma) {
        return ApiException.notFound("Không tìm thấy sản phẩm " + ma + ".");
    }
}
