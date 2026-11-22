package vn.qlcuahang.web;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SanPhamApiTest extends IntegrationTestBase {

    private static Map<String, Object> sanPham(String ma, String ten, double gia, int ton) {
        Map<String, Object> m = new HashMap<>();
        m.put("maSanPham", ma);
        m.put("maNhom", "TST");
        m.put("tenSanPham", ten);
        m.put("loai", "Rau củ");
        m.put("donViTinh", "Kg");
        m.put("hanSuDung", "5");
        m.put("moTa", "Sản phẩm test");
        m.put("gia", gia);
        m.put("soLuongTon", ton);
        return m;
    }

    @Test
    void nhanVienXemDuocDanhSachVaTimKhongDau() throws Exception {
        String token = nhanVienToken();
        mvc.perform(get("/api/san-pham").param("q", "thit bo").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].maSanPham", hasItem("SP001")));
        mvc.perform(get("/api/loai-san-pham").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tenLoai", hasItem("Rau củ")));
    }

    @Test
    void nhanVienKhongDuocThemSuaXoa() throws Exception {
        String token = nhanVienToken();
        mvc.perform(post("/api/san-pham").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sanPham("TSTNV", "X", 1000, 1))))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/san-pham/SP001").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sanPham(null, "X", 1000, 1))))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/san-pham/SP001").header("Authorization", bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void quanLyThemSuaXoaSanPham() throws Exception {
        String token = quanLyToken();
        String ma = "TST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        mvc.perform(post("/api/san-pham").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sanPham(ma, "Bí đỏ test", 18000, 40))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maSanPham").value(ma))
                .andExpect(jsonPath("$.gia").value(18000.0));

        mvc.perform(post("/api/san-pham").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sanPham(ma, "Trùng", 1, 1))))
                .andExpect(status().isConflict());

        mvc.perform(put("/api/san-pham/" + ma).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sanPham(null, "Bí đỏ hữu cơ", 25000, 12))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenSanPham").value("Bí đỏ hữu cơ"))
                .andExpect(jsonPath("$.soLuongTon").value(12));

        mvc.perform(delete("/api/san-pham/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/san-pham/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void duLieuSaiTra400() throws Exception {
        String token = quanLyToken();
        Map<String, Object> bad = sanPham("TSTBAD", "", -5, -1);
        mvc.perform(post("/api/san-pham").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tenSanPham").exists())
                .andExpect(jsonPath("$.errors.gia").exists())
                .andExpect(jsonPath("$.errors.soLuongTon").exists());
    }

    @Test
    void khongXoaDuocSanPhamDaCoTrongHoaDon() throws Exception {
        mvc.perform(delete("/api/san-pham/SP001").header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isConflict());
    }
}
