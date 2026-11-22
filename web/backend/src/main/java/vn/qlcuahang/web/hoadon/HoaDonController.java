package vn.qlcuahang.web.hoadon;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.hoadon.HoaDonDtos.HoaDonChiTiet;
import vn.qlcuahang.web.hoadon.HoaDonDtos.HoaDonTomTat;
import vn.qlcuahang.web.hoadon.HoaDonDtos.TaoHoaDonRequest;
import vn.qlcuahang.web.hoadon.HoaDonDtos.TinhTien;
import vn.qlcuahang.web.hoadon.HoaDonDtos.Trang;
import vn.qlcuahang.web.security.CurrentUser;

@RestController
@io.swagger.v3.oas.annotations.tags.Tag(name = "Hóa đơn")
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    private final HoaDonService service;

    public HoaDonController(HoaDonService service) {
        this.service = service;
    }

    @GetMapping
    public Trang<HoaDonTomTat> list(Authentication auth,
                                    @RequestParam(required = false) String q,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                    LocalDate tuNgay,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                    LocalDate denNgay,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return service.search(CurrentUser.from(auth), q, tuNgay, denNgay, page, size);
    }

    @GetMapping("/{ma}")
    public HoaDonChiTiet get(Authentication auth, @PathVariable String ma) {
        return service.get(CurrentUser.from(auth), ma);
    }

    /** Tính tiền + kiểm tra khuyến mãi/tồn kho, không lưu. */
    @PostMapping("/xem-truoc")
    public TinhTien preview(@Valid @RequestBody TaoHoaDonRequest req) {
        return service.xemTruoc(req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HoaDonChiTiet create(Authentication auth, @Valid @RequestBody TaoHoaDonRequest req) {
        return service.tao(CurrentUser.from(auth), req);
    }
}
