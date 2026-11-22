package vn.qlcuahang.web.danhmuc;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.hoadon.HoaDonRepository;
import vn.qlcuahang.web.hoadon.HoaDonService;
import vn.qlcuahang.web.hoadon.KhuyenMai;

/** Danh mục phục vụ màn hình lập hóa đơn. */
@RestController
public class DanhMucController {

    public record KhachHangLuaChon(String maKhachHang, String hoTen, String soDienThoai) {
    }

    private final JdbcClient jdbc;
    private final HoaDonRepository hoaDonRepo;
    private final HoaDonService hoaDonService;

    public DanhMucController(JdbcClient jdbc, HoaDonRepository hoaDonRepo, HoaDonService hoaDonService) {
        this.jdbc = jdbc;
        this.hoaDonRepo = hoaDonRepo;
        this.hoaDonService = hoaDonService;
    }

    /** Khách hàng đang hoạt động; tìm theo mã, tên hoặc số điện thoại. */
    @GetMapping("/api/khach-hang")
    public List<KhachHangLuaChon> khachHang(@RequestParam(required = false) String q) {
        String k = q == null ? "" : q.trim();
        return jdbc.sql("""
                SELECT MaKhachHang,
                       TRIM(CONCAT_WS(' ', Ho, NULLIF(TenLot, ''), Ten)) AS HoTen,
                       SoDienThoai
                FROM KhachHang
                WHERE TrangThai = 'active'
                  AND (? = '' OR unaccent(MaKhachHang || ' ' || CONCAT_WS(' ', Ho, TenLot, Ten) || ' '
                                          || COALESCE(SoDienThoai, '')) ILIKE unaccent(?))
                ORDER BY MaKhachHang
                LIMIT 50
                """)
                .params(k, "%" + k + "%")
                .query((rs, i) -> new KhachHangLuaChon(rs.getString(1), rs.getString(2), rs.getString(3)))
                .list();
    }

    /** Khuyến mãi đang chạy hôm nay (giờ Việt Nam). */
    @GetMapping("/api/khuyen-mai/hieu-luc")
    public List<KhuyenMai> khuyenMaiHieuLuc() {
        return hoaDonRepo.findKhuyenMaiHieuLuc(hoaDonService.today());
    }
}
