package vn.qlcuahang.web.common;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * Sinh mã kế tiếp dạng TIỀN_TỐ + số (KH031, PN2001, HH106…), giống cách app desktop đặt mã.
 * Phải gọi trong transaction: khóa advisory theo bảng để hai người thêm cùng lúc không lấy trùng mã.
 */
@Component
public class MaSinh {

    private final JdbcClient jdbc;

    public MaSinh(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * @param table  tên bảng (hằng số trong code, không lấy từ người dùng)
     * @param column cột mã
     * @param prefix tiền tố, VD "KH"
     * @param pad    số chữ số tối thiểu, đệm 0 ở trước (0 = không đệm)
     */
    public String next(String table, String column, String prefix, int pad) {
        jdbc.sql("SELECT pg_advisory_xact_lock(hashtext(?))")
                .param("qlcuahang." + table + "." + column)
                .query((rs, i) -> Boolean.TRUE).single();
        Long max = jdbc.sql("SELECT COALESCE(MAX(CAST(SUBSTRING(UPPER(TRIM(" + column + ")) FROM "
                        + (prefix.length() + 1) + ") AS BIGINT)), 0) FROM " + table
                        + " WHERE UPPER(TRIM(" + column + ")) ~ ?")
                .param("^" + prefix + "[0-9]{1,18}$")
                .query(Long.class).single();
        String so = String.valueOf(max + 1);
        return prefix + (so.length() < pad ? "0".repeat(pad - so.length()) : "") + so;
    }
}
