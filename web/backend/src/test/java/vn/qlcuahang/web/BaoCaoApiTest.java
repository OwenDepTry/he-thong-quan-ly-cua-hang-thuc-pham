package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class BaoCaoApiTest extends IntegrationTestBase {

    private static final LocalDate TODAY = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

    @Test
    void baoCaoTheoNgayDuMoiNgayVaKhopVoiDatabase() throws Exception {
        LocalDate tu = TODAY.minusDays(13);
        JsonNode r = read(mvc.perform(get("/api/bao-cao").param("tuNgay", tu.toString())
                        .param("denNgay", TODAY.toString()).header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nhom").value("ngay"))
                .andExpect(jsonPath("$.theoKy.length()").value(14))
                .andExpect(jsonPath("$.theoKy[0].ky").value(tu.toString()))
                .andReturn());

        double expected = jdbc.sql("SELECT COALESCE(SUM(TongTien - COALESCE(TienGiam, 0)), 0) FROM HoaDon "
                + "WHERE ThoiGian BETWEEN ? AND ?").params(tu, TODAY).query(Double.class).single();
        assertThat(r.at("/tongHop/doanhThu").asDouble()).isEqualTo(expected, org.assertj.core.data.Offset.offset(0.5));

        double tongTheoKy = 0;
        for (JsonNode d : r.get("theoKy")) {
            tongTheoKy += d.get("doanhThu").asDouble();
        }
        assertThat(tongTheoKy).isEqualTo(expected, org.assertj.core.data.Offset.offset(0.5));
        // Dữ liệu mẫu có hóa đơn rải đều 12 tháng nên phải có sản phẩm bán chạy
        assertThat(r.get("sanPhamBanChay").size()).isGreaterThan(0);
        assertThat(r.get("theoNhanVien").size()).isGreaterThan(0);
    }

    @Test
    void khoangDaiTuDongNhomTheoThang() throws Exception {
        mvc.perform(get("/api/bao-cao").param("tuNgay", TODAY.minusMonths(11).withDayOfMonth(1).toString())
                        .param("denNgay", TODAY.toString()).header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nhom").value("thang"))
                .andExpect(jsonPath("$.theoKy.length()").value(12));
    }

    @Test
    void nhanVienKhongXemDuocVaThamSoSaiTra400() throws Exception {
        mvc.perform(get("/api/bao-cao").header("Authorization", bearer(nhanVienToken())))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/bao-cao").param("tuNgay", TODAY.toString())
                        .param("denNgay", TODAY.minusDays(1).toString()).header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/bao-cao").param("nhom", "tuan").header("Authorization", bearer(quanLyToken())))
                .andExpect(status().isBadRequest());
    }
}
