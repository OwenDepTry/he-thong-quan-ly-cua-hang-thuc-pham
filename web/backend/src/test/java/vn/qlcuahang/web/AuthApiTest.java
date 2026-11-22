package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthApiTest extends IntegrationTestBase {

    private org.springframework.test.web.servlet.ResultActions postLogin(String ma, String mk) throws Exception {
        return mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of("maNhanVien", ma, "matKhau", mk))));
    }

    @Test
    void quanLyDangNhapNhanTokenVaVaiTroQL() throws Exception {
        postLogin("admin", "admin")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.user.maNhanVien").value("admin"))
                .andExpect(jsonPath("$.user.role").value("QL"));
    }

    @Test
    void tokenDungDuocChoApiMe() throws Exception {
        String token = nhanVienToken();
        mvc.perform(get("/api/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maNhanVien").value("nhanvien"))
                .andExpect(jsonPath("$.role").value("NV"));
    }

    @Test
    void saiMatKhauHoacSaiMaTra401CungThongBao() throws Exception {
        postLogin("admin", "sai-mat-khau").andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sai mã nhân viên hoặc mật khẩu."));
        postLogin("khong-ton-tai", "x").andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Sai mã nhân viên hoặc mật khẩu."));
    }

    @Test
    void taiKhoanNgungHoatDongBiTuChoi() throws Exception {
        postLogin("NV05", "123").andExpect(status().isForbidden());
    }

    @Test
    void thieuThongTinTra400KemLoiTungTruong() throws Exception {
        postLogin("", "").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.maNhanVien").exists())
                .andExpect(jsonPath("$.errors.matKhau").exists());
    }

    @Test
    void khongCoTokenHoacTokenGiaTra401() throws Exception {
        mvc.perform(get("/api/san-pham")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/san-pham").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void khiKhoiDongMoiMatKhauDaDuocMaHoaBcrypt() {
        Integer conChuThuong = jdbc.sql(
                        "SELECT COUNT(*) FROM NhanVien WHERE MatKhau !~ '^\\$2[aby]\\$[0-9]{2}\\$.{53}$'")
                .query(Integer.class).single();
        assertThat(conChuThuong).isZero();
    }

    @Test
    void matKhauChuThuongCuVanDangNhapDuocVaDuocNangCap() throws Exception {
        jdbc.sql("UPDATE NhanVien SET MatKhau = 'mat-khau-cu' WHERE MaNhanVien = 'NV02'").update();

        postLogin("NV02", "mat-khau-cu").andExpect(status().isOk());

        String stored = jdbc.sql("SELECT MatKhau FROM NhanVien WHERE MaNhanVien = 'NV02'")
                .query(String.class).single();
        assertThat(stored).startsWith("$2a$10$").hasSize(60);
        postLogin("NV02", "mat-khau-cu").andExpect(status().isOk());

        // Trả lại mật khẩu mẫu (dạng hash) để không ảnh hưởng lần chạy sau
        jdbc.sql("UPDATE NhanVien SET MatKhau = crypt('123', gen_salt('bf', 10)) WHERE MaNhanVien = 'NV02'")
                .update();
    }

    @Test
    void hashDoPgcryptoTaoRa_giongAppDesktop_kiemTraDuoc() throws Exception {
        jdbc.sql("UPDATE NhanVien SET MatKhau = crypt('pgcrypto-ok', gen_salt('bf', 10)) WHERE MaNhanVien = 'NV03'")
                .update();
        postLogin("NV03", "pgcrypto-ok").andExpect(status().isOk());
        jdbc.sql("UPDATE NhanVien SET MatKhau = crypt('123', gen_salt('bf', 10)) WHERE MaNhanVien = 'NV03'")
                .update();
    }

    @Test
    void healthKhongCanDangNhap() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
