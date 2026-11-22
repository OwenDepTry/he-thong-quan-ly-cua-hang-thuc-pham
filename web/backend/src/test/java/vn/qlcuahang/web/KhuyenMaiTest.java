package vn.qlcuahang.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import vn.qlcuahang.web.hoadon.KhuyenMai;

/** Test đơn vị cho luật khuyến mãi (không cần database). */
class KhuyenMaiTest {

    private static final LocalDate NGAY = LocalDate.of(2026, 11, 20);

    private static KhuyenMai km(String dieuKien, String trangThai, LocalDate bd, LocalDate kt) {
        return new KhuyenMai("KM", "Test", dieuKien, 10, bd, kt, trangThai);
    }

    @Test
    void datDieuKienLonHonHoacBang() {
        KhuyenMai k = km(">=100000", "active", NGAY.minusDays(1), NGAY.plusDays(1));
        assertThat(k.lyDoKhongHopLe(100000, NGAY)).isNull();
        assertThat(k.lyDoKhongHopLe(99999, NGAY)).contains("chưa đạt điều kiện");
    }

    @Test
    void dieuKienLonHonNghiemNgatVaCoDauPhanCach() {
        KhuyenMai k = km("> 100.000", "active", null, null);
        assertThat(k.nguong()).isEqualTo(100000.0);
        assertThat(k.lyDoKhongHopLe(100000, NGAY)).isNotNull();
        assertThat(k.lyDoKhongHopLe(100001, NGAY)).isNull();
    }

    @Test
    void dauBangHieuLaLonHonHoacBangGiongDesktop() {
        assertThat(km("=500,000", "active", null, null).lyDoKhongHopLe(600000, NGAY)).isNull();
    }

    @Test
    void khongCoDieuKienThiLuonDat() {
        assertThat(km("Mọi hóa đơn", "active", null, null).lyDoKhongHopLe(1, NGAY)).isNull();
        assertThat(km(null, "active", null, null).lyDoKhongHopLe(1, NGAY)).isNull();
    }

    @Test
    void ngoaiThoiGianHoacNgungHoatDong() {
        assertThat(km(">=0", "inactive", null, null).lyDoKhongHopLe(1, NGAY)).contains("ngừng áp dụng");
        assertThat(km(">=0", "active", NGAY.plusDays(1), null).lyDoKhongHopLe(1, NGAY)).contains("chưa bắt đầu");
        assertThat(km(">=0", "active", null, NGAY.minusDays(1)).lyDoKhongHopLe(1, NGAY)).contains("hết hạn");
        // Biên: đúng ngày bắt đầu/kết thúc vẫn hợp lệ
        assertThat(km(">=0", "ACTIVE ", NGAY, NGAY).lyDoKhongHopLe(1, NGAY)).isNull();
    }

    @Test
    void tienGiamLamTronToiDong() {
        assertThat(new KhuyenMai("KM", "T", null, 15, null, null, "active").tinhTienGiam(33333)).isEqualTo(5000.0);
    }
}
