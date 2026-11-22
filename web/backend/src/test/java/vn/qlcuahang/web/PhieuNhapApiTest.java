package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PhieuNhapApiTest extends IntegrationTestBase {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

    private int tonKho(String ma) {
        return jdbc.sql("SELECT SoLuongTon FROM SanPham WHERE MaSanPham = ?").param(ma).query(Integer.class).single();
    }

    private String nhaCungCapDau() {
        return jdbc.sql("SELECT MaNhaCungCap FROM NhaCungCap WHERE COALESCE(TrangThai, 'active') = 'active' "
                + "ORDER BY MaNhaCungCap LIMIT 1").query(String.class).single();
    }

    private static Map<String, Object> dong(String ma, int sl, double gia, LocalDate han) {
        Map<String, Object> m = new HashMap<>();
        m.put("maSanPham", ma);
        m.put("soLuong", sl);
        m.put("donGia", gia);
        m.put("hanSuDung", han == null ? null : han.toString());
        return m;
    }

    @Test
    void lapPhieuNhapCongTonKhoVaTaoLoHang() throws Exception {
        String token = nhanVienToken();
        int ton5 = tonKho("SP005");
        int ton6 = tonKho("SP006");

        Map<String, Object> req = Map.of("maNhaCungCap", nhaCungCapDau(), "items", List.of(
                dong("SP005", 20, 7000, TODAY.plusDays(10)),
                dong("SP006", 15, 9000, null)));
        JsonNode res = read(mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phieuNhap.tongTien").value(20 * 7000 + 15 * 9000))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.loHangMoi.length()").value(1))
                .andExpect(jsonPath("$.loHangMoi[0].maSanPham").value("SP005"))
                .andReturn());

        assertThat(tonKho("SP005")).isEqualTo(ton5 + 20);
        assertThat(tonKho("SP006")).isEqualTo(ton6 + 15);
        String maHang = res.at("/loHangMoi/0/maHang").asText();
        assertThat(maHang).startsWith("HH");
        assertThat(jdbc.sql("SELECT SoLuong FROM HangHoa WHERE MaHang = ?").param(maHang)
                .query(Integer.class).single()).isEqualTo(20);

        String ma = res.at("/phieuNhap/maPhieuNhap").asText();
        assertThat(ma).startsWith("PN");
        mvc.perform(get("/api/phieu-nhap/" + ma).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phieuNhap.maNhanVien").value("nhanvien"));
        // Quản lý thấy phiếu của mọi người
        mvc.perform(get("/api/phieu-nhap").param("q", ma).header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].maPhieuNhap").value(ma));
    }

    @Test
    void loiNghiepVuKhongGhiGi() throws Exception {
        String token = quanLyToken();
        int ton = tonKho("SP005");
        String ncc = nhaCungCapDau();

        // Trùng sản phẩm
        mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("maNhaCungCap", ncc,
                                "items", List.of(dong("SP005", 1, 1, null), dong("SP005", 2, 1, null))))))
                .andExpect(status().isBadRequest());
        // Hạn dùng đã qua
        mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("maNhaCungCap", ncc,
                                "items", List.of(dong("SP005", 1, 1, TODAY.minusDays(1)))))))
                .andExpect(status().isBadRequest());
        // Sản phẩm không tồn tại ở dòng sau: dòng trước cũng không được ghi
        mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("maNhaCungCap", ncc,
                                "items", List.of(dong("SP005", 5, 1, null), dong("KHONGCO", 1, 1, null))))))
                .andExpect(status().isBadRequest());
        // Nhà cung cấp sai, số lượng 0
        mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("maNhaCungCap", "KHONGCO",
                                "items", List.of(dong("SP005", 1, 1, null))))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/phieu-nhap").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("maNhaCungCap", ncc,
                                "items", List.of(dong("SP005", 0, 1, null))))))
                .andExpect(status().isBadRequest());

        assertThat(tonKho("SP005")).isEqualTo(ton);
    }

    @Test
    void danhSachNhaCungCap() throws Exception {
        mvc.perform(get("/api/nha-cung-cap").header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }
}
