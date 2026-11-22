package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DashboardApiTest extends IntegrationTestBase {

    private JsonNode dashboard(String token) throws Exception {
        return read(mvc.perform(get("/api/dashboard").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andReturn());
    }

    @Test
    void quanLyThayDoanhThuNhanVienThiKhong() throws Exception {
        JsonNode ql = dashboard(quanLyToken());
        assertThat(ql.get("doanhThuHomNay").isNumber()).isTrue();
        assertThat(ql.get("doanhThuThangNay").isNumber()).isTrue();

        JsonNode nv = dashboard(nhanVienToken());
        assertThat(nv.get("doanhThuHomNay").isNull()).isTrue();
        assertThat(nv.get("doanhThuThangNay").isNull()).isTrue();
        assertThat(nv.get("hoaDonHomNay").isInt()).isTrue();
    }

    @Test
    void hoaDonMoiDuocCongVaoDoanhThuHomNayVaThangNay() throws Exception {
        String ma = "TD" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        jdbc.sql("INSERT INTO SanPham (MaSanPham, TenSanPham, DonViTinh, Gia, SoLuongTon) VALUES (?, 'Dầu ăn test', 'Chai', 45000, 100)")
                .param(ma).update();
        String token = quanLyToken();
        JsonNode truoc = dashboard(token);

        mvc.perform(post("/api/hoa-don").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("items", List.of(Map.of("maSanPham", ma, "soLuong", 2))))))
                .andExpect(status().isCreated());

        JsonNode sau = dashboard(token);
        assertThat(sau.get("hoaDonHomNay").asInt()).isEqualTo(truoc.get("hoaDonHomNay").asInt() + 1);
        assertThat(sau.get("doanhThuHomNay").asDouble())
                .isCloseTo(truoc.get("doanhThuHomNay").asDouble() + 90000.0, within(0.01));
        assertThat(sau.get("doanhThuThangNay").asDouble())
                .isCloseTo(truoc.get("doanhThuThangNay").asDouble() + 90000.0, within(0.01));
    }

    @Test
    void canhBaoSapHetHangVaLoSapHetHan() throws Exception {
        String sp = "TL" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        jdbc.sql("INSERT INTO SanPham (MaSanPham, TenSanPham, DonViTinh, Gia, SoLuongTon) VALUES (?, 'Sắp hết test', 'Gói', 1000, 0)")
                .param(sp).update();
        jdbc.sql("INSERT INTO HangHoa (MaHang, MaSanPham, SoLuong, NgayNhap, HanSuDung) "
                        + "VALUES (?, ?, 5, CURRENT_DATE - 1, CURRENT_DATE - 365)")
                .params("HH" + sp, sp).update();

        mvc.perform(get("/api/dashboard").header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soSapHetHang").value(org.hamcrest.Matchers.greaterThan(0)))
                // tồn = 0 là thấp nhất nên luôn nằm trong top
                .andExpect(jsonPath("$.sapHetHang[*].soLuongTon", hasItem(0)))
                .andExpect(jsonPath("$.soLoSapHetHan").value(org.hamcrest.Matchers.greaterThan(0)))
                // lô hết hạn quá 30 ngày không còn cảnh báo
                .andExpect(jsonPath("$.sapHetHan[*].maHang", org.hamcrest.Matchers.not(hasItem("HH" + sp))));
    }
}
