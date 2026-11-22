package vn.qlcuahang.web.nhapkho;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.hoadon.HoaDonDtos.Trang;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.NhaCungCap;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.PhieuNhapChiTiet;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.PhieuNhapTomTat;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.TaoPhieuNhapRequest;
import vn.qlcuahang.web.security.CurrentUser;

@RestController
@Tag(name = "Nhập kho", description = "Phiếu nhập hàng từ nhà cung cấp, cộng tồn kho và tạo lô có hạn dùng")
public class PhieuNhapController {

    private final PhieuNhapService service;
    private final PhieuNhapRepository repo;

    public PhieuNhapController(PhieuNhapService service, PhieuNhapRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @GetMapping("/api/nha-cung-cap")
    @Operation(summary = "Nhà cung cấp đang hợp tác")
    public List<NhaCungCap> nhaCungCap() {
        return repo.findNhaCungCap();
    }

    @GetMapping("/api/phieu-nhap")
    @Operation(summary = "Danh sách phiếu nhập (nhân viên chỉ thấy phiếu của mình)")
    public Trang<PhieuNhapTomTat> list(Authentication auth,
                                       @RequestParam(required = false) String q,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       LocalDate tuNgay,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                       LocalDate denNgay,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return service.search(CurrentUser.from(auth), q, tuNgay, denNgay, page, size);
    }

    @GetMapping("/api/phieu-nhap/{ma}")
    @Operation(summary = "Chi tiết phiếu nhập")
    public PhieuNhapChiTiet get(Authentication auth, @PathVariable String ma) {
        return service.get(CurrentUser.from(auth), ma);
    }

    @PostMapping("/api/phieu-nhap")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Lập phiếu nhập", description = "Cộng tồn kho; dòng có hạn sử dụng sẽ tạo một lô hàng mới.")
    public PhieuNhapChiTiet create(Authentication auth, @Valid @RequestBody TaoPhieuNhapRequest req) {
        return service.tao(CurrentUser.from(auth), req);
    }
}
