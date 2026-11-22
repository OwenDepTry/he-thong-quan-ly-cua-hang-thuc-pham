package vn.qlcuahang.web.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "Vui lòng nhập mã nhân viên") @Size(max = 50) String maNhanVien,
            @NotBlank(message = "Vui lòng nhập mật khẩu") @Size(max = 100) String matKhau) {
    }

    /** role: "QL" hoặc "NV". */
    public record UserInfo(String maNhanVien, String hoTen, String role) {
    }

    public record LoginResponse(String token, Instant expiresAt, UserInfo user) {
    }
}
