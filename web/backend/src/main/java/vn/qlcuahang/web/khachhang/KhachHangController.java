package vn.qlcuahang.web.khachhang;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.common.MaSinh;
import vn.qlcuahang.web.config.AppProperties;

/**
 * Khách hàng thành viên. Ai đăng nhập cũng tra cứu được (để chọn khi bán hàng),
 * chỉ quản lý được thêm, sửa, xóa. Kèm số hóa đơn, tổng chi tiêu và lần mua gần nhất.
 */
@RestController
@Tag(name = "Khách hàng")
public class KhachHangController {

    public record KhachHang(
            String maKhachHang,
            String ho,
            String tenLot,
            String ten,
            String hoTen,
            String phai,
            LocalDate ngaySinh,
            String soDienThoai,
            String tinh,
            LocalDate ngayThamGia,
            int diem,
            String trangThai,
            int soHoaDon,
            double tongChiTieu,
            LocalDate lanMuaCuoi) {
    }

    public record KhachHangRequest(
            @Size(max = 50) @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Mã chỉ gồm chữ, số, - và _")
            String maKhachHang,
            @Size(max = 50) String ho,
            @Size(max = 50) String tenLot,
            @NotBlank(message = "Vui lòng nhập tên") @Size(max = 50) String ten,
            @Pattern(regexp = "^(nam|nữ|khác)?$", message = "Giới tính là nam, nữ hoặc khác") String phai,
            @Past(message = "Ngày sinh phải trước hôm nay") LocalDate ngaySinh,
            @NotBlank(message = "Vui lòng nhập số điện thoại")
            @Pattern(regexp = "^0[0-9]{9,10}$", message = "Số điện thoại gồm 10 hoặc 11 chữ số, bắt đầu bằng 0")
            String soDienThoai,
            @Size(max = 100) String tinh,
            @Pattern(regexp = "^(active|inactive)?$", message = "Trạng thái là active hoặc inactive")
            String trangThai) {
    }

    private static final String SELECT = """
            SELECT kh.MaKhachHang, kh.Ho, kh.TenLot, kh.Ten,
                   TRIM(CONCAT_WS(' ', kh.Ho, NULLIF(kh.TenLot, ''), kh.Ten)) AS HoTen,
                   kh.Phai, kh.NgaySinh, kh.SoDienThoai, kh.Tinh, kh.NgayThamGia,
                   COALESCE(kh.Diem, 0) AS Diem, COALESCE(kh.TrangThai, 'active') AS TrangThai,
                   COALESCE(s.SoHoaDon, 0) AS SoHoaDon, COALESCE(s.TongChiTieu, 0) AS TongChiTieu, s.LanMuaCuoi
            FROM KhachHang kh
            LEFT JOIN (SELECT MaKhachHang, COUNT(*) AS SoHoaDon,
                              SUM(TongTien - COALESCE(TienGiam, 0)) AS TongChiTieu, MAX(ThoiGian) AS LanMuaCuoi
                       FROM HoaDon WHERE MaKhachHang IS NOT NULL GROUP BY MaKhachHang) s
                   ON s.MaKhachHang = kh.MaKhachHang
            """;

    private final JdbcClient jdbc;
    private final MaSinh maSinh;
    private final AppProperties props;

    public KhachHangController(JdbcClient jdbc, MaSinh maSinh, AppProperties props) {
        this.jdbc = jdbc;
        this.maSinh = maSinh;
        this.props = props;
    }

    @GetMapping("/api/khach-hang")
    @Operation(summary = "Tìm khách hàng theo mã, tên hoặc số điện thoại (gõ không dấu được)",
            description = "Mặc định chỉ khách đang hoạt động, tối đa 50 dòng. tatCa=true để lấy cả khách ngừng hoạt động.")
    public List<KhachHang> list(@RequestParam(required = false) String q,
                                @RequestParam(defaultValue = "false") boolean tatCa,
                                @RequestParam(defaultValue = "50") int limit) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (!tatCa) {
            where.append(" AND COALESCE(kh.TrangThai, 'active') = 'active'");
        }
        if (q != null && !q.isBlank()) {
            where.append(" AND unaccent(kh.MaKhachHang || ' ' || CONCAT_WS(' ', kh.Ho, kh.TenLot, kh.Ten) || ' '"
                    + " || COALESCE(kh.SoDienThoai, '')) ILIKE unaccent(?)");
            params.add("%" + q.trim() + "%");
        }
        params.add(Math.min(Math.max(limit, 1), 500));
        return jdbc.sql(SELECT + where + " ORDER BY kh.MaKhachHang LIMIT ?")
                .params(params)
                .query(KhachHangController::map)
                .list();
    }

    @GetMapping("/api/khach-hang/{ma}")
    @Operation(summary = "Chi tiết khách hàng")
    public KhachHang get(@PathVariable String ma) {
        return find(ma).orElseThrow(() -> notFound(ma));
    }

    @PostMapping("/api/khach-hang")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('QL')")
    @Transactional
    @Operation(summary = "Thêm khách hàng", description = "Bỏ trống mã thì hệ thống tự sinh (KH031…).")
    public KhachHang create(@Valid @RequestBody KhachHangRequest req) {
        String ma = req.maKhachHang() == null || req.maKhachHang().isBlank()
                ? maSinh.next("KhachHang", "MaKhachHang", "KH", 3)
                : req.maKhachHang().trim();
        if (find(ma).isPresent()) {
            throw ApiException.conflict("Mã khách hàng " + ma + " đã tồn tại.");
        }
        kiemTraTrungSdt(req.soDienThoai(), null);
        jdbc.sql("""
                INSERT INTO KhachHang (MaKhachHang, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh,
                                       NgayThamGia, Diem, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?)
                """)
                .params(ma, trim(req.ho()), trim(req.tenLot()), req.ten().trim(), trim(req.phai()), req.ngaySinh(),
                        req.soDienThoai().trim(), trim(req.tinh()), LocalDate.now(props.zoneId()),
                        trangThai(req.trangThai()))
                .update();
        return find(ma).orElseThrow();
    }

    @PutMapping("/api/khach-hang/{ma}")
    @PreAuthorize("hasRole('QL')")
    @Transactional
    @Operation(summary = "Sửa khách hàng", description = "Không đổi được mã, ngày tham gia và điểm tích lũy.")
    public KhachHang update(@PathVariable String ma, @Valid @RequestBody KhachHangRequest req) {
        kiemTraTrungSdt(req.soDienThoai(), ma);
        int n = jdbc.sql("""
                UPDATE KhachHang SET Ho = ?, TenLot = ?, Ten = ?, Phai = ?, NgaySinh = ?, SoDienThoai = ?,
                                     Tinh = ?, TrangThai = ?
                WHERE MaKhachHang = ?
                """)
                .params(trim(req.ho()), trim(req.tenLot()), req.ten().trim(), trim(req.phai()), req.ngaySinh(),
                        req.soDienThoai().trim(), trim(req.tinh()), trangThai(req.trangThai()), ma)
                .update();
        if (n == 0) {
            throw notFound(ma);
        }
        return find(ma).orElseThrow();
    }

    @DeleteMapping("/api/khach-hang/{ma}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('QL')")
    @Transactional
    @Operation(summary = "Xóa khách hàng chưa từng mua hàng")
    public void delete(@PathVariable String ma) {
        KhachHang kh = find(ma).orElseThrow(() -> notFound(ma));
        if (kh.soHoaDon() > 0) {
            throw ApiException.conflict("Khách hàng " + ma + " đã có " + kh.soHoaDon()
                    + " hóa đơn nên không xóa được. Hãy chuyển sang Ngừng hoạt động.");
        }
        jdbc.sql("DELETE FROM KhachHang WHERE MaKhachHang = ?").param(ma).update();
    }

    private Optional<KhachHang> find(String ma) {
        return jdbc.sql(SELECT + " WHERE kh.MaKhachHang = ?").param(ma).query(KhachHangController::map).optional();
    }

    private void kiemTraTrungSdt(String sdt, String boQuaMa) {
        List<String> trung = jdbc.sql("SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?")
                .param(sdt.trim()).query(String.class).list();
        trung.stream().filter(m -> !m.equals(boQuaMa)).findFirst().ifPresent(m -> {
            throw ApiException.conflict("Số điện thoại này đã dùng cho khách hàng " + m + ".");
        });
    }

    private static String trim(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static String trangThai(String s) {
        return s == null || s.isBlank() ? "active" : s.trim();
    }

    private static ApiException notFound(String ma) {
        return ApiException.notFound("Không tìm thấy khách hàng " + ma + ".");
    }

    private static KhachHang map(ResultSet rs, int i) throws SQLException {
        Date ns = rs.getDate("NgaySinh");
        Date tg = rs.getDate("NgayThamGia");
        Date mc = rs.getDate("LanMuaCuoi");
        return new KhachHang(rs.getString("MaKhachHang"), rs.getString("Ho"), rs.getString("TenLot"),
                rs.getString("Ten"), rs.getString("HoTen"), rs.getString("Phai"),
                ns == null ? null : ns.toLocalDate(), rs.getString("SoDienThoai"), rs.getString("Tinh"),
                tg == null ? null : tg.toLocalDate(), rs.getInt("Diem"), rs.getString("TrangThai"),
                rs.getInt("SoHoaDon"), rs.getDouble("TongChiTieu"), mc == null ? null : mc.toLocalDate());
    }
}
