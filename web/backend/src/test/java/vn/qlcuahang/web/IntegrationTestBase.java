package vn.qlcuahang.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Nền cho test tích hợp: chạy cả ứng dụng với PostgreSQL thật.
 *
 * - Mặc định: Testcontainers dựng postgres:16-alpine và nạp toàn bộ sql/ ở gốc repo
 *   (schema + dữ liệu mẫu + mã hóa mật khẩu), giống hệt docker-compose.
 * - Máy không có Docker: đặt TEST_DB_URL (+ TEST_DB_USER, TEST_DB_PASSWORD) trỏ tới một database
 *   đã nạp sql/ – test sẽ ghi dữ liệu thử vào đó, đừng dùng database thật.
 *
 * Các test chỉ so sánh chênh lệch (trước/sau) nên chạy lại nhiều lần trên cùng DB vẫn đúng.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    private static PostgreSQLContainer<?> container;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper json;

    @Autowired
    protected JdbcClient jdbc;

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        String url = System.getenv("TEST_DB_URL");
        if (url != null && !url.isBlank()) {
            registry.add("spring.datasource.url", () -> url);
            registry.add("spring.datasource.username", () -> env("TEST_DB_USER", "postgres"));
            registry.add("spring.datasource.password", () -> env("TEST_DB_PASSWORD", "postgres"));
            return;
        }
        PostgreSQLContainer<?> c = startContainer();
        registry.add("spring.datasource.url", c::getJdbcUrl);
        registry.add("spring.datasource.username", c::getUsername);
        registry.add("spring.datasource.password", c::getPassword);
    }

    private static synchronized PostgreSQLContainer<?> startContainer() {
        if (container == null) {
            PostgreSQLContainer<?> c = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("qlcuahangthucpham")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withEnv("TZ", "Asia/Ho_Chi_Minh")
                    .withEnv("PGTZ", "Asia/Ho_Chi_Minh");
            for (Path sql : sqlScripts()) {
                c.withCopyFileToContainer(MountableFile.forHostPath(sql),
                        "/docker-entrypoint-initdb.d/" + sql.getFileName());
            }
            c.start();
            container = c; // dùng chung cho mọi lớp test, Ryuk tự dọn khi JVM kết thúc
        }
        return container;
    }

    /** Thư mục sql/ ở gốc repo (test chạy từ web/backend). */
    private static List<Path> sqlScripts() {
        Path dir = Path.of("../../sql").toAbsolutePath().normalize();
        try (Stream<Path> files = Files.list(dir)) {
            List<Path> list = files.filter(p -> p.toString().endsWith(".sql")).sorted().toList();
            if (list.isEmpty()) {
                throw new IllegalStateException("Không thấy file .sql trong " + dir);
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được " + dir, e);
        }
    }

    private static String env(String name, String def) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? def : v;
    }

    // ---------- Tiện ích dùng chung ----------

    protected String login(String ma, String matKhau) throws Exception {
        MvcResult res = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("maNhanVien", ma, "matKhau", matKhau))))
                .andExpect(status().isOk())
                .andReturn();
        return read(res).get("token").asText();
    }

    protected String quanLyToken() throws Exception {
        return login("admin", "admin");
    }

    protected String nhanVienToken() throws Exception {
        return login("nhanvien", "123");
    }

    protected static String bearer(String token) {
        return "Bearer " + token;
    }

    protected JsonNode read(MvcResult res) throws Exception {
        return json.readTree(res.getResponse().getContentAsByteArray());
    }

    protected String toJson(Object o) throws Exception {
        return json.writeValueAsString(o);
    }
}
