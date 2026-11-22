package config;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Mã hóa / kiểm tra mật khẩu nhân viên bằng BCrypt (dùng chung định dạng với bản web Spring Security).
 *
 * - Mật khẩu mới luôn được lưu dạng hash "$2a$10$...".
 * - Dữ liệu cũ còn lưu chữ thường vẫn đăng nhập được; NhanVienDAO sẽ tự nâng cấp lên BCrypt
 *   ngay sau lần đăng nhập thành công đó.
 */
public final class PasswordUtil {

    /** Cùng cost mặc định với BCryptPasswordEncoder của Spring Security. */
    private static final int LOG_ROUNDS = 10;

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /** true nếu chuỗi đã là hash BCrypt ($2a$ / $2b$ / $2y$). */
    public static boolean isHashed(String stored) {
        return stored != null && stored.length() == 60 && stored.matches("^\\$2[aby]\\$\\d{2}\\$.*");
    }

    /** So khớp mật khẩu người dùng nhập với giá trị trong DB (hash BCrypt hoặc chữ thường cũ). */
    public static boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        String s = stored.trim();
        if (isHashed(s)) {
            // jBCrypt chỉ nhận tiền tố $2a$; $2b$/$2y$ cho kết quả giống hệt với mật khẩu < 72 byte
            String normalized = "$2a$" + s.substring(4);
            try {
                return BCrypt.checkpw(rawPassword, normalized);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return constantTimeEquals(s, rawPassword);
    }

    /** Nếu đã là hash thì giữ nguyên, ngược lại thì mã hóa (dùng khi lưu từ form/nhập CSV). */
    public static String hashIfNeeded(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return isHashed(value) ? value : hash(value);
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] y = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(x, y);
    }
}
