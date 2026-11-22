package vn.qlcuahang.web.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.qlcuahang.web.auth.AuthDtos.LoginResponse;
import vn.qlcuahang.web.auth.AuthDtos.UserInfo;
import vn.qlcuahang.web.auth.NhanVienRepository.TaiKhoan;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.security.JwtService;
import vn.qlcuahang.web.security.Role;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final Pattern BCRYPT = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    private final NhanVienRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    /** Hash giả để thời gian phản hồi giống nhau khi mã nhân viên không tồn tại. */
    private final String dummyHash;

    public AuthService(NhanVienRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
        this.dummyHash = encoder.encode("khong-dung-mat-khau-nay");
    }

    public static boolean isBcrypt(String value) {
        return value != null && BCRYPT.matcher(value).matches();
    }

    public LoginResponse login(String maNhanVien, String matKhau) {
        String ma = maNhanVien == null ? "" : maNhanVien.trim();
        TaiKhoan tk = repo.findTaiKhoan(ma).orElse(null);

        if (tk == null) {
            encoder.matches(matKhau == null ? "" : matKhau, dummyHash);
            throw invalidCredentials();
        }
        String stored = tk.matKhau() == null ? "" : tk.matKhau().trim();
        if (!passwordMatches(matKhau, stored)) {
            throw invalidCredentials();
        }
        // Kiểm tra trạng thái SAU mật khẩu để không lộ tài khoản nào tồn tại
        if (!"active".equalsIgnoreCase(tk.trangThai() == null ? "" : tk.trangThai().trim())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa hoặc ngừng hoạt động.");
        }
        if (!isBcrypt(stored)) {
            upgradePassword(tk, matKhau);
        }

        Role role = Role.fromChucVu(tk.chucVu());
        JwtService.IssuedToken token = jwt.issue(tk.maNhanVien(), tk.hoTen(), role);
        return new LoginResponse(token.token(), token.expiresAt(),
                new UserInfo(tk.maNhanVien(), tk.hoTen(), role.name()));
    }

    public UserInfo me(String maNhanVien) {
        TaiKhoan tk = repo.findTaiKhoan(maNhanVien)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Tài khoản không còn tồn tại."));
        return new UserInfo(tk.maNhanVien(), tk.hoTen(), Role.fromChucVu(tk.chucVu()).name());
    }

    private boolean passwordMatches(String raw, String stored) {
        if (raw == null || stored.isEmpty()) {
            return false;
        }
        if (isBcrypt(stored)) {
            return encoder.matches(raw, stored);
        }
        // Dữ liệu cũ chưa mã hóa
        return MessageDigest.isEqual(stored.getBytes(StandardCharsets.UTF_8), raw.getBytes(StandardCharsets.UTF_8));
    }

    private void upgradePassword(TaiKhoan tk, String raw) {
        try {
            if (repo.replacePassword(tk.maNhanVien(), tk.matKhau(), encoder.encode(raw))) {
                log.info("Đã mã hóa BCrypt mật khẩu của {}", tk.maNhanVien());
            }
        } catch (RuntimeException e) {
            log.warn("Không nâng cấp được mật khẩu của {}: {}", tk.maNhanVien(), e.getMessage());
        }
    }

    private static ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Sai mã nhân viên hoặc mật khẩu.");
    }
}
