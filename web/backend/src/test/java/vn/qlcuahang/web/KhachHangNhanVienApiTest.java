package vn.qlcuahang.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class KhachHangNhanVienApiTest extends IntegrationTestBase {

    private static String sdtNgauNhien() {
        return "09" + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
    }

    private static Map<String, Object> khach(String ten, String sdt, String trangThai) {
        Map<String, Object> m = new HashMap<>();
        m.put("ho", "Test");
        m.put("ten", ten);
        m.put("phai", "nữ");
        m.put("soDienThoai", sdt);
        m.put("tinh", "Cần Thơ");
        m.put("trangThai", trangThai);
        return m;
    }

    @Test
    void khachHangCoThongKeVaTimKhongDau() throws Exception {
        mvc.perform(get("/api/khach-hang").param("q", "cuong").header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].maKhachHang", hasItem("KH003")))
                .andExpect(jsonPath("$[0].hoTen").exists())
                .andExpect(jsonPath("$[0].soHoaDon").exists());
    }

    @Test
    void quanLyThemSuaXoaKhachHang() throws Exception {
        String token = quanLyToken();
        String sdt = sdtNgauNhien();
        String ma = read(mvc.perform(post("/api/khach-hang").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(khach("Lan", sdt, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hoTen").value("Test Lan"))
                .andExpect(jsonPath("$.diem").value(0))
                .andExpect(jsonPath("$.trangThai").value("active"))
                .andReturn()).get("maKhachHang").asText();

        // Trùng số điện thoại
        mvc.perform(post("/api/khach-hang").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(khach("Khác", sdt, null))))
                .andExpect(status().isConflict());

        // Ngừng hoạt động → không còn trong danh sách mặc định
        mvc.perform(put("/api/khach-hang/" + ma).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(khach("Lan Anh", sdt, "inactive"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ten").value("Lan Anh"));
        mvc.perform(get("/api/khach-hang").param("q", ma).header("Authorization", bearer(token)))
                .andExpect(jsonPath("$[*].maKhachHang", not(hasItem(ma))));
        mvc.perform(get("/api/khach-hang").param("q", ma).param("tatCa", "true").header("Authorization", bearer(token)))
                .andExpect(jsonPath("$[*].maKhachHang", hasItem(ma)));

        mvc.perform(delete("/api/khach-hang/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        // Khách đã mua hàng thì không xóa được
        mvc.perform(delete("/api/khach-hang/KH003").header("Authorization", bearer(token)))
                .andExpect(status().isConflict());
    }

    @Test
    void nhanVienKhongSuaKhachHangVaKhongXemNhanVien() throws Exception {
        String token = nhanVienToken();
        mvc.perform(post("/api/khach-hang").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(khach("X", sdtNgauNhien(), null))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/nhan-vien").header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void quanLyThemNhanVienRoiNhanVienDangNhapDuoc() throws Exception {
        String token = quanLyToken();
        Map<String, Object> nv = new HashMap<>();
        nv.put("ho", "Đỗ");
        nv.put("ten", "Bình");
        nv.put("chucVu", "NV");
        nv.put("luong", 7500000);
        nv.put("matKhau", "matkhau123");
        String ma = read(mvc.perform(post("/api/nhan-vien").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(nv)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hoTen").value("Đỗ Bình"))
                .andExpect(content().string(not(org.hamcrest.Matchers.containsString("matkhau123"))))
                .andReturn()).get("maNhanVien").asText();

        login(ma, "matkhau123");

        // Khóa tài khoản thì không đăng nhập được nữa
        nv.put("trangThai", "inactive");
        nv.remove("matKhau");
        mvc.perform(put("/api/nhan-vien/" + ma).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(nv)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("inactive"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("maNhanVien", ma, "matKhau", "matkhau123"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void quanLyKhongTuKhoaChinhMinh() throws Exception {
        Map<String, Object> admin = new HashMap<>();
        admin.put("ho", "Siêu");
        admin.put("tenLot", "Quản");
        admin.put("ten", "Lý");
        admin.put("chucVu", "NV");
        mvc.perform(put("/api/nhan-vien/admin").header("Authorization", bearer(quanLyToken()))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void taiLieuApiMoCongKhai() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/phieu-nhap']").exists())
                .andExpect(jsonPath("$.paths['/api/bao-cao']").exists());
    }
}
