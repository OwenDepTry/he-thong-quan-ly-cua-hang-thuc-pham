package vn.qlcuahang.web.danhmuc;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.hoadon.HoaDonRepository;
import vn.qlcuahang.web.hoadon.HoaDonService;
import vn.qlcuahang.web.hoadon.KhuyenMai;

/** Danh mục phục vụ màn hình lập hóa đơn. Khách hàng: xem khachhang.KhachHangController. */
@RestController
@io.swagger.v3.oas.annotations.tags.Tag(name = "Bán hàng")
public class DanhMucController {

    private final HoaDonRepository hoaDonRepo;
    private final HoaDonService hoaDonService;

    public DanhMucController(HoaDonRepository hoaDonRepo, HoaDonService hoaDonService) {
        this.hoaDonRepo = hoaDonRepo;
        this.hoaDonService = hoaDonService;
    }

    /** Khuyến mãi đang chạy hôm nay (giờ Việt Nam). */
    @GetMapping("/api/khuyen-mai/hieu-luc")
    public List<KhuyenMai> khuyenMaiHieuLuc() {
        return hoaDonRepo.findKhuyenMaiHieuLuc(hoaDonService.today());
    }
}
