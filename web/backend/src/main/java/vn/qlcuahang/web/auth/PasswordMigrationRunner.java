package vn.qlcuahang.web.auth;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Khi khởi động: mã hóa BCrypt mọi mật khẩu nhân viên còn lưu dạng chữ thường.
 * Tắt bằng MIGRATE_PLAINTEXT_PASSWORDS=false. Tương đương sql/zzz_ma_hoa_mat_khau.sql.
 */
@Component
@ConditionalOnProperty(prefix = "app.security", name = "migrate-plaintext-passwords", havingValue = "true",
        matchIfMissing = true)
public class PasswordMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final NhanVienRepository repo;
    private final PasswordEncoder encoder;

    public PasswordMigrationRunner(NhanVienRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String[]> plain = repo.findPlaintextPasswords();
        int done = 0;
        for (String[] row : plain) {
            // Điều kiện "MatKhau = giá trị cũ" tránh ghi đè nếu desktop vừa đổi mật khẩu
            if (repo.replacePassword(row[0], row[1], encoder.encode(row[1].trim()))) {
                done++;
            }
        }
        if (done > 0) {
            log.info("Đã mã hóa BCrypt {} mật khẩu nhân viên còn lưu dạng chữ thường.", done);
        }
    }
}
