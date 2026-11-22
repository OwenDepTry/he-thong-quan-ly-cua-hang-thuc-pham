package vn.qlcuahang.web;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class HinhAnhApiTest extends IntegrationTestBase {

    /** Đủ để nhận diện là PNG (8 byte chữ ký + vài byte). */
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 13, 'I', 'H', 'D', 'R'};

    private String taoSanPham(String token) throws Exception {
        String ma = "IMG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> sp = new HashMap<>();
        sp.put("maSanPham", ma);
        sp.put("tenSanPham", "Sản phẩm có ảnh");
        sp.put("donViTinh", "Hộp");
        sp.put("gia", 10000);
        sp.put("soLuongTon", 5);
        mvc.perform(post("/api/san-pham").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(sp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hinhAnh").value(nullValue()));
        return ma;
    }

    @Test
    void sanPhamMauCoAnhCommonsKemNguon() throws Exception {
        mvc.perform(get("/api/san-pham/SP005").header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hinhAnh").value(startsWith("https://commons.wikimedia.org/")))
                .andExpect(jsonPath("$.nguonAnh").value(startsWith("Kander")))
                .andExpect(jsonPath("$.trangNguonAnh").value(startsWith("https://commons.wikimedia.org/wiki/File:")));
    }

    @Test
    void quanLyTaiAnhLenXemDuocKhongCanTokenRoiGo() throws Exception {
        String token = quanLyToken();
        String ma = taoSanPham(token);

        String url = read(mvc.perform(multipart(HttpMethod.PUT, "/api/san-pham/" + ma + "/hinh-anh")
                        .file(new MockMultipartFile("file", "anh.png", "image/png", PNG))
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hinhAnh").value(startsWith("/api/san-pham/" + ma + "/hinh-anh?v=")))
                .andExpect(jsonPath("$.nguonAnh").value(nullValue()))
                .andReturn()).get("hinhAnh").asText();

        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes(PNG));

        mvc.perform(delete("/api/san-pham/" + ma + "/hinh-anh").header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/san-pham/" + ma + "/hinh-anh")).andExpect(status().isNotFound());
        // Xóa sản phẩm vẫn được (ảnh xóa theo)
        mvc.perform(delete("/api/san-pham/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
    }

    @Test
    void tuChoiFileKhongPhaiAnhVaNhanVien() throws Exception {
        String token = quanLyToken();
        String ma = taoSanPham(token);
        mvc.perform(multipart(HttpMethod.PUT, "/api/san-pham/" + ma + "/hinh-anh")
                        .file(new MockMultipartFile("file", "anh.png", "image/png", "<script>".getBytes()))
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, "/api/san-pham/" + ma + "/hinh-anh")
                        .file(new MockMultipartFile("file", "anh.png", "image/png", PNG))
                        .header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/san-pham/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
    }
}
