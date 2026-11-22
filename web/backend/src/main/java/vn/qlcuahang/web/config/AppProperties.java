package vn.qlcuahang.web.config;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Cấu hình riêng của ứng dụng (khóa "app" trong application.yml). */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        ZoneId zoneId,
        Jwt jwt,
        Cors cors,
        Security security,
        Dashboard dashboard) {

    public record Jwt(String secret, Duration ttl, String issuer) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    public record Security(boolean migratePlaintextPasswords) {
    }

    public record Dashboard(int lowStockThreshold, int expiryWarningDays, int expiredLookbackDays, int listLimit) {
    }
}
