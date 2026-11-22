package vn.qlcuahang.web.auth;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NhanVienRepository {

    /** Thông tin cần cho đăng nhập (bảng NhanVien có sẵn). */
    public record TaiKhoan(String maNhanVien, String hoTen, String chucVu, String trangThai, String matKhau) {
    }

    private final JdbcClient jdbc;

    public NhanVienRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<TaiKhoan> findTaiKhoan(String maNhanVien) {
        return jdbc.sql("""
                SELECT MaNhanVien,
                       TRIM(CONCAT_WS(' ', Ho, NULLIF(TenLot, ''), Ten)) AS HoTen,
                       ChucVu, TrangThai, MatKhau
                FROM NhanVien WHERE MaNhanVien = ?
                """)
                .param(maNhanVien)
                .query((rs, i) -> new TaiKhoan(
                        rs.getString("MaNhanVien"), rs.getString("HoTen"), rs.getString("ChucVu"),
                        rs.getString("TrangThai"), rs.getString("MatKhau")))
                .optional();
    }

    /** Đổi mật khẩu có điều kiện: chỉ ghi nếu mật khẩu trong DB vẫn là giá trị đã đọc. */
    public boolean replacePassword(String maNhanVien, String expectedOld, String newHash) {
        return jdbc.sql("UPDATE NhanVien SET MatKhau = ? WHERE MaNhanVien = ? AND MatKhau = ?")
                .params(newHash, maNhanVien, expectedOld)
                .update() > 0;
    }

    /** {maNhanVien, matKhau} của những tài khoản còn lưu mật khẩu chữ thường. */
    public List<String[]> findPlaintextPasswords() {
        return jdbc.sql("""
                SELECT MaNhanVien, MatKhau FROM NhanVien
                WHERE MatKhau IS NOT NULL AND MatKhau <> ''
                  AND MatKhau !~ '^\\$2[aby]\\$[0-9]{2}\\$.{53}$'
                """)
                .query((rs, i) -> new String[]{rs.getString(1), rs.getString(2)})
                .list();
    }
}
