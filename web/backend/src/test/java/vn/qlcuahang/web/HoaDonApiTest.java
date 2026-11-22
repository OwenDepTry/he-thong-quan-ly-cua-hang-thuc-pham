package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class HoaDonApiTest extends IntegrationTestBase {

    /** 2 sản phẩm riêng cho mỗi test: giá và tồn kho biết trước. */
    private String spA;
    private String spB;

    @BeforeEach
    void taoSanPhamThu() {
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        spA = "TA" + suffix;
        spB = "TB" + suffix;
        insertSanPham(spA, "Gạo test", 50000, 10);
        insertSanPham(spB, "Muối test", 20000, 5);
    }

    private void insertSanPham(String ma, String ten, double gia, int ton) {
        jdbc.sql("INSERT INTO SanPham (MaSanPham, TenSanPham, Loai, DonViTinh, Gia, SoLuongTon) "
                        + "VALUES (?, ?, 'Test', 'Kg', ?, ?)")
                .params(ma, ten, gia, ton).update();
    }

    private int ton(String ma) {
        return jdbc.sql("SELECT SoLuongTon FROM SanPham WHERE MaSanPham = ?").param(ma).query(Integer.class).single();
    }

    private long soHoaDon() {
        return jdbc.sql("SELECT COUNT(*) FROM HoaDon").query(Long.class).single();
    }

    private static Map<String, Object> dong(String ma, int sl) {
        return Map.of("maSanPham", ma, "soLuong", sl);
    }

    private static Map<String, Object> hoaDon(String maKH, String maKM, List<Map<String, Object>> items) {
        Map<String, Object> m = new HashMap<>();
        m.put("maKhachHang", maKH);
        m.put("maKhuyenMai", maKM);
        m.put("items", items);
        return m;
    }

    private ResultActions taoHoaDon(String token, Map<String, Object> body) throws Exception {
        return mvc.perform(post("/api/hoa-don").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(toJson(body)));
    }

    @Test
    void lapHoaDonTruTonKhoVaTinhTienTheoGiaTrongDb() throws Exception {
        String token = nhanVienToken();
        long truoc = soHoaDon();

        // spA lặp 2 dòng (2 + 1) phải được gộp thành 3
        MvcResult res = taoHoaDon(token, hoaDon("KH001", null, List.of(dong(spA, 2), dong(spB, 1), dong(spA, 1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hoaDon.maHoaDon").value(org.hamcrest.Matchers.startsWith("HD")))
                .andExpect(jsonPath("$.hoaDon.maNhanVien").value("nhanvien"))
                .andExpect(jsonPath("$.hoaDon.maKhachHang").value("KH001"))
                .andExpect(jsonPath("$.hoaDon.tongTien").value(170000.0))
                .andExpect(jsonPath("$.hoaDon.tienGiam").value(0.0))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andReturn();

        JsonNode body = read(res);
        assertThat(body.at("/hoaDon/thoiGian").asText())
                .isEqualTo(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).toString());
        assertThat(ton(spA)).isEqualTo(7);
        assertThat(ton(spB)).isEqualTo(4);
        assertThat(soHoaDon()).isEqualTo(truoc + 1);
    }

    @Test
    void apDungKhuyenMaiKhiDatDieuKien() throws Exception {
        // KM004 trong dữ liệu mẫu: >=200000, giảm 10%, đang chạy
        taoHoaDon(nhanVienToken(), hoaDon(null, "KM004", List.of(dong(spA, 4))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hoaDon.tongTien").value(200000.0))
                .andExpect(jsonPath("$.hoaDon.tienGiam").value(20000.0))
                .andExpect(jsonPath("$.hoaDon.thanhTien").value(180000.0))
                .andExpect(jsonPath("$.hoaDon.maKhuyenMai").value("KM004"));
    }

    @Test
    void khuyenMaiChuaDatDieuKienThiKhongLuuGi() throws Exception {
        long truoc = soHoaDon();
        taoHoaDon(nhanVienToken(), hoaDon(null, "KM004", List.of(dong(spA, 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("chưa đạt điều kiện")));
        assertThat(ton(spA)).isEqualTo(10);
        assertThat(soHoaDon()).isEqualTo(truoc);
    }

    @Test
    void khuyenMaiNgungHoacChuaBatDauBiTuChoi() throws Exception {
        String token = nhanVienToken();
        taoHoaDon(token, hoaDon(null, "KM006", List.of(dong(spA, 10))))   // inactive
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("ngừng áp dụng")));
        taoHoaDon(token, hoaDon(null, "KM007", List.of(dong(spA, 10))))   // bắt đầu sau 60 ngày
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("chưa bắt đầu")));
        taoHoaDon(token, hoaDon(null, "KHONGCO", List.of(dong(spA, 1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void khongDuTonKhoThiRollbackToanBo() throws Exception {
        long truoc = soHoaDon();
        taoHoaDon(nhanVienToken(), hoaDon(null, null, List.of(dong(spA, 2), dong(spB, 6))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("không đủ tồn kho")));
        assertThat(ton(spA)).isEqualTo(10);
        assertThat(ton(spB)).isEqualTo(5);
        assertThat(soHoaDon()).isEqualTo(truoc);
    }

    @Test
    void duLieuKhongHopLeTra400() throws Exception {
        String token = nhanVienToken();
        taoHoaDon(token, hoaDon(null, null, List.of())).andExpect(status().isBadRequest());
        taoHoaDon(token, hoaDon(null, null, List.of(dong(spA, 0)))).andExpect(status().isBadRequest());
        taoHoaDon(token, hoaDon("KH-KHONG-CO", null, List.of(dong(spA, 1)))).andExpect(status().isBadRequest());
        taoHoaDon(token, hoaDon(null, null, List.of(dong("SP-KHONG-CO", 1)))).andExpect(status().isBadRequest());
    }

    @Test
    void xemTruocKhongGhiDuLieu() throws Exception {
        long truoc = soHoaDon();
        mvc.perform(post("/api/hoa-don/xem-truoc").header("Authorization", bearer(nhanVienToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(hoaDon(null, "KM004", List.of(dong(spA, 5))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tongTien").value(250000.0))
                .andExpect(jsonPath("$.tienGiam").value(25000.0))
                .andExpect(jsonPath("$.phanTramGiam").value(10.0));
        assertThat(ton(spA)).isEqualTo(10);
        assertThat(soHoaDon()).isEqualTo(truoc);
    }

    @Test
    void nhanVienChiThayHoaDonCuaMinh() throws Exception {
        String nv = nhanVienToken();
        String ql = quanLyToken();
        String maCuaQuanLy = read(taoHoaDon(ql, hoaDon(null, null, List.of(dong(spA, 1))))
                .andExpect(status().isCreated()).andReturn()).at("/hoaDon/maHoaDon").asText();

        MvcResult list = mvc.perform(get("/api/hoa-don").param("size", "200").header("Authorization", bearer(nv)))
                .andExpect(status().isOk()).andReturn();
        for (JsonNode hd : read(list).get("items")) {
            assertThat(hd.get("maNhanVien").asText()).isEqualTo("nhanvien");
        }
        mvc.perform(get("/api/hoa-don/" + maCuaQuanLy).header("Authorization", bearer(nv)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/hoa-don/" + maCuaQuanLy).header("Authorization", bearer(ql)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].maSanPham").value(spA));
    }

    @Test
    void lapHoaDonDongThoiKhongTrungMaVaKhongBanQuaTonKho() throws Exception {
        String token = nhanVienToken();
        ExecutorService pool = Executors.newFixedThreadPool(6);
        try {
            List<Future<MvcResult>> futures = new ArrayList<>();
            // 12 yêu cầu mua 1 spA, nhưng chỉ còn 10
            for (int i = 0; i < 12; i++) {
                futures.add(pool.submit(() -> taoHoaDon(token, hoaDon(null, null, List.of(dong(spA, 1)))).andReturn()));
            }
            Set<String> ma = new HashSet<>();
            int ok = 0;
            int hetHang = 0;
            for (Future<MvcResult> f : futures) {
                MvcResult r = f.get();
                if (r.getResponse().getStatus() == 201) {
                    ok++;
                    ma.add(read(r).at("/hoaDon/maHoaDon").asText());
                } else if (r.getResponse().getStatus() == 409) {
                    hetHang++;
                }
            }
            assertThat(ok).isEqualTo(10);
            assertThat(hetHang).isEqualTo(2);
            assertThat(ma).hasSize(10);
            assertThat(ton(spA)).isZero();
        } finally {
            pool.shutdownNow();
        }
    }
}
