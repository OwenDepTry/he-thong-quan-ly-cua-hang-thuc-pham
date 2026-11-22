package vn.qlcuahang.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * Bổ sung bảng mà bản web cần nhưng database cũ (tạo trước khi có tính năng) chưa có.
 * Giống sql/zzzz_anh_san_pham.sql nhưng không nạp ảnh mẫu; chạy lại nhiều lần không sao.
 */
@Component
@Order(0)
public class SchemaUpdater implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaUpdater.class);

    private final JdbcClient jdbc;

    public SchemaUpdater(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbc.sql("""
                CREATE TABLE IF NOT EXISTS SanPhamHinhAnh (
                    MaSanPham   VARCHAR(50) PRIMARY KEY REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
                    DuLieu      BYTEA,
                    LoaiNoiDung VARCHAR(50),
                    Url         VARCHAR(1000),
                    NguonAnh    VARCHAR(300),
                    TrangNguon  VARCHAR(1000),
                    CapNhat     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT sanphamhinhanh_co_anh CHECK (DuLieu IS NOT NULL OR Url IS NOT NULL)
                )
                """).update();
        log.debug("Đã kiểm tra bảng SanPhamHinhAnh");
    }
}
