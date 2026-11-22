package vn.qlcuahang.web.nhanvien;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.common.MaSinh;
import vn.qlcuahang.web.config.AppProperties;
import vn.qlcuahang.web.security.CurrentUser;

/**
 * Quản lý nhân viên, chỉ dành cho quản lý. Không trả mật khẩu; mật khẩu mới được mã hóa BCrypt.
 * Không có xóa: nhân viên nghỉ việc chuyển sang "inactive" để giữ lịch sử hóa đơn.
 */
@RestController
@PreAuthorize("hasRole('QL')")
@Tag(name = "Nhân viên", description = "Chỉ quản lý")
public class NhanVienController {

    public record NhanVien(
            String maNhanVien,
            String ho,
            String tenLot,
            String ten,
            String hoTen,
            String phai,
            LocalDate ngaySinh,
            String soDienThoai,
            String tinh,
            String diaChi,
            double luong,
            String chucVu,
            String trangThai,
            int hoaDonThangNay,
            double doanhThuThangNay) {
    }

    public record NhanVienRequest(
            @Size(max = 50) @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "Mã chỉ gồm chữ, số, - và _")
            String maNhanVien,
            @Size(max = 50) String ho,
            @Size(max = 50) String tenLot,
            @NotBlank(message = "Vui lòng nhập tên") @Size(max = 50) String ten,
            @Pattern(regexp = "^(nam|nữ|khác)?$", message = "Giới tính là nam, nữ hoặc khác") String phai,
            @Past(message = "Ngày sinh phải trước hôm nay") LocalDate ngaySinh,
            @Pattern(regexp = "^(0[0-9]{9,10})?$", message = "Số điện thoại gồm 10 hoặc 11 chữ số, bắt đầu bằng 0")
            String soDienThoai,
            @Size(max = 100) String tinh,
            @Size(max = 255) String diaChi,
            @DecimalMin(value = "0", message = "Lương không được âm") Double luong,
            @NotBlank(message = "Vui lòng chọn chức vụ") @Pattern(regexp = "^(QL|NV)$", message = "Chức vụ là QL hoặc NV")
            String chucVu,
            @Pattern(regexp = "^(active|inactive)?$", message = "Trạng thái là active hoặc inactive") String trangThai,
            /** Bắt buộc khi thêm; khi sửa bỏ trống nghĩa là giữ mật khẩu cũ. */
            @Size(min = 6, max = 72, message = "Mật khẩu từ 6 đến 72 ký tự") String matKhau) {
    }

    private static final String SELECT = """
            SELECT nv.MaNhanVien, nv.Ho, nv.TenLot, nv.Ten,
                   TRIM(CONCAT_WS(' ', nv.Ho, NULLIF(nv.TenLot, ''), nv.Ten)) AS HoTen,
                   nv.Phai, nv.NgaySinh, nv.SoDienThoai, nv.Tinh, nv.DiaChi, COALESCE(nv.Luong, 0) AS Luong,
                   nv.ChucVu, COALESCE(nv.TrangThai, 'active') AS TrangThai,
                   COALESCE(s.SoHoaDon, 0) AS SoHoaDon, COALESCE(s.DoanhThu, 0) AS DoanhThu
            FROM NhanVien nv
            LEFT JOIN (SELECT MaNhanVien, COUNT(*) AS SoHoaDon, SUM(TongTien - COALESCE(TienGiam, 0)) AS DoanhThu
                       FROM HoaDon WHERE ThoiGian >= ? AND ThoiGian < ? GROUP BY MaNhanVien) s
                   ON s.MaNhanVien = nv.MaNhanVien
            """;

    private final JdbcClient jdbc;
    private final MaSinh maSinh;
    private final PasswordEncoder encoder;
    private final AppProperties props;

    public NhanVienController(JdbcClient jdbc, MaSinh maSinh, PasswordEncoder encoder, AppProperties props) {
        this.jdbc = jdbc;
        this.maSinh = maSinh;
        this.encoder = encoder;
        this.props = props;
    }

    @GetMapping("/api/nhan-vien")
    @Operation(summary = "Danh sách nhân viên kèm số hóa đơn và doanh thu tháng này")
    public List<NhanVien> list() {
        LocalDate dau = LocalDate.now(props.zoneId()).withDayOfMonth(1);
        return jdbc.sql(SELECT + " ORDER BY (COALESCE(nv.TrangThai, 'active') = 'active') DESC, nv.ChucVu DESC, "
                        + "nv.MaNhanVien")
                .params(dau, dau.plusMonths(1))
                .query(NhanVienController::map)
                .list();
    }

    @GetMapping("/api/nhan-vien/{ma}")
    @Operation(summary = "Chi tiết nhân viên")
    public NhanVien get(@PathVariable String ma) {
        return find(ma).orElseThrow(() -> notFound(ma));
    }

    @PostMapping("/api/nhan-vien")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @Operation(summary = "Thêm nhân viên", description = "Bỏ trống mã thì hệ thống tự sinh (NV05…).")
    public NhanVien create(@Valid @RequestBody NhanVienRequest req) {
        if (req.matKhau() == null || req.matKhau().isBlank()) {
            throw ApiException.badRequest("Vui lòng đặt mật khẩu cho nhân viên mới.");
        }
        String ma = req.maNhanVien() == null || req.maNhanVien().isBlank()
                ? maSinh.next("NhanVien", "MaNhanVien", "NV", 2)
                : req.maNhanVien().trim();
        if (find(ma).isPresent()) {
            throw ApiException.conflict("Mã nhân viên " + ma + " đã tồn tại.");
        }
        jdbc.sql("""
                INSERT INTO NhanVien (MaNhanVien, Ho, TenLot, Ten, Phai, NgaySinh, SoDienThoai, Tinh, DiaChi,
                                      Luong, ChucVu, TrangThai, MatKhau)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)
                .params(ma, trim(req.ho()), trim(req.tenLot()), req.ten().trim(), trim(req.phai()), req.ngaySinh(),
                        trim(req.soDienThoai()), trim(req.tinh()), trim(req.diaChi()),
                        req.luong() == null ? 0d : req.luong(), req.chucVu(), trangThai(req.trangThai()),
                        encoder.encode(req.matKhau()))
                .update();
        return find(ma).orElseThrow();
    }

    @PutMapping("/api/nhan-vien/{ma}")
    @Transactional
    @Operation(summary = "Sửa nhân viên", description = "Có matKhau thì đặt lại mật khẩu, bỏ trống thì giữ nguyên.")
    public NhanVien update(Authentication auth, @PathVariable String ma, @Valid @RequestBody NhanVienRequest req) {
        NhanVien cu = find(ma).orElseThrow(() -> notFound(ma));
        String trangThai = trangThai(req.trangThai());
        boolean conLaQuanLy = "QL".equals(req.chucVu()) && "active".equals(trangThai);

        if (CurrentUser.from(auth).maNhanVien().equals(ma) && !conLaQuanLy) {
            throw ApiException.badRequest("Bạn không thể tự hạ chức vụ hoặc khóa tài khoản của chính mình.");
        }
        if ("QL".equals(cu.chucVu()) && "active".equals(cu.trangThai()) && !conLaQuanLy && soQuanLyHoatDong() <= 1) {
            throw ApiException.badRequest("Cửa hàng phải còn ít nhất một quản lý đang hoạt động.");
        }

        boolean doiMatKhau = req.matKhau() != null && !req.matKhau().isBlank();
        List<Object> params = new ArrayList<>(Arrays.asList(trim(req.ho()), trim(req.tenLot()), req.ten().trim(),
                trim(req.phai()), req.ngaySinh(), trim(req.soDienThoai()), trim(req.tinh()), trim(req.diaChi()),
                req.luong() == null ? cu.luong() : req.luong(), req.chucVu(), trangThai));
        if (doiMatKhau) {
            params.add(encoder.encode(req.matKhau()));
        }
        params.add(ma);
        jdbc.sql("UPDATE NhanVien SET Ho = ?, TenLot = ?, Ten = ?, Phai = ?, NgaySinh = ?, SoDienThoai = ?, "
                        + "Tinh = ?, DiaChi = ?, Luong = ?, ChucVu = ?, TrangThai = ?"
                        + (doiMatKhau ? ", MatKhau = ?" : "") + " WHERE MaNhanVien = ?")
                .params(params)
                .update();
        return find(ma).orElseThrow();
    }

    private int soQuanLyHoatDong() {
        return jdbc.sql("SELECT COUNT(*) FROM NhanVien WHERE ChucVu = 'QL' AND COALESCE(TrangThai, 'active') = 'active'")
                .query(Integer.class).single();
    }

    private Optional<NhanVien> find(String ma) {
        LocalDate dau = LocalDate.now(props.zoneId()).withDayOfMonth(1);
        return jdbc.sql(SELECT + " WHERE nv.MaNhanVien = ?")
                .params(dau, dau.plusMonths(1), ma)
                .query(NhanVienController::map)
                .optional();
    }

    private static String trim(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static String trangThai(String s) {
        return s == null || s.isBlank() ? "active" : s.trim();
    }

    private static ApiException notFound(String ma) {
        return ApiException.notFound("Không tìm thấy nhân viên " + ma + ".");
    }

    private static NhanVien map(ResultSet rs, int i) throws SQLException {
        Date ns = rs.getDate("NgaySinh");
        return new NhanVien(rs.getString("MaNhanVien"), rs.getString("Ho"), rs.getString("TenLot"),
                rs.getString("Ten"), rs.getString("HoTen"), rs.getString("Phai"),
                ns == null ? null : ns.toLocalDate(), rs.getString("SoDienThoai"), rs.getString("Tinh"),
                rs.getString("DiaChi"), rs.getDouble("Luong"), rs.getString("ChucVu"), rs.getString("TrangThai"),
                rs.getInt("SoHoaDon"), rs.getDouble("DoanhThu"));
    }
}
