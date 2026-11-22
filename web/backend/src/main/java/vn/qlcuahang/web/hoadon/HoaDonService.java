package vn.qlcuahang.web.hoadon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.config.AppProperties;
import vn.qlcuahang.web.hoadon.HoaDonDtos.ChiTiet;
import vn.qlcuahang.web.hoadon.HoaDonDtos.DongHang;
import vn.qlcuahang.web.hoadon.HoaDonDtos.HoaDonChiTiet;
import vn.qlcuahang.web.hoadon.HoaDonDtos.HoaDonTomTat;
import vn.qlcuahang.web.hoadon.HoaDonDtos.TaoHoaDonRequest;
import vn.qlcuahang.web.hoadon.HoaDonDtos.TinhTien;
import vn.qlcuahang.web.hoadon.HoaDonDtos.Trang;
import vn.qlcuahang.web.hoadon.HoaDonRepository.SanPhamBan;
import vn.qlcuahang.web.security.CurrentUser;

@Service
public class HoaDonService {

    private final HoaDonRepository repo;
    private final AppProperties props;

    public HoaDonService(HoaDonRepository repo, AppProperties props) {
        this.repo = repo;
        this.props = props;
    }

    public LocalDate today() {
        return LocalDate.now(props.zoneId());
    }

    // ---------- Tra cứu ----------

    @Transactional(readOnly = true)
    public Trang<HoaDonTomTat> search(CurrentUser user, String q, LocalDate tuNgay, LocalDate denNgay,
                                      int page, int size) {
        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), 200);
        // Nhân viên chỉ thấy hóa đơn mình lập (giống mục "Lịch sử" của app desktop)
        String scope = user.isQuanLy() ? null : user.maNhanVien();
        List<HoaDonTomTat> items = repo.search(scope, q, tuNgay, denNgay, s, p * s);
        long total = repo.count(scope, q, tuNgay, denNgay);
        return new Trang<>(items, total, p, s);
    }

    @Transactional(readOnly = true)
    public HoaDonChiTiet get(CurrentUser user, String maHoaDon) {
        HoaDonTomTat hd = repo.findTomTat(maHoaDon)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy hóa đơn " + maHoaDon + "."));
        if (!user.isQuanLy() && !user.maNhanVien().equals(hd.maNhanVien())) {
            throw ApiException.forbidden("Bạn chỉ xem được hóa đơn do mình lập.");
        }
        return new HoaDonChiTiet(hd, repo.findChiTiet(maHoaDon));
    }

    // ---------- Tính tiền / lập hóa đơn ----------

    /** Tính tiền theo giá hiện tại, kiểm tra tồn kho và khuyến mãi; không ghi gì vào DB. */
    @Transactional(readOnly = true)
    public TinhTien xemTruoc(TaoHoaDonRequest req) {
        return tinhTien(req, false);
    }

    /**
     * Lập hóa đơn trong 1 transaction: khóa các dòng sản phẩm, kiểm tra tồn kho + khuyến mãi,
     * ghi HoaDon/ChiTietHoaDon và trừ SanPham.SoLuongTon. Lỗi ở bất kỳ bước nào → rollback toàn bộ.
     */
    @Transactional
    public HoaDonChiTiet tao(CurrentUser user, TaoHoaDonRequest req) {
        String maKH = blankToNull(req.maKhachHang());
        if (maKH != null && !repo.khachHangTonTai(maKH)) {
            throw ApiException.badRequest("Khách hàng " + maKH + " không tồn tại.");
        }

        TinhTien t = tinhTien(req, true);
        String ma = repo.nextMaHoaDon();
        repo.insertHoaDon(ma, maKH, user.maNhanVien(), t.tongTien(), t.tienGiam(), today(), t.maKhuyenMai());
        for (ChiTiet ct : t.items()) {
            repo.insertChiTiet(ma, ct.maSanPham(), ct.soLuong(), ct.donGia());
            if (!repo.truTonKho(ct.maSanPham(), ct.soLuong())) {
                // Không xảy ra vì đã khóa dòng (FOR UPDATE), giữ lại để chắc chắn
                throw ApiException.conflict("Sản phẩm " + ct.maSanPham() + " không đủ tồn kho.");
            }
        }
        return get(user, ma);
    }

    private TinhTien tinhTien(TaoHoaDonRequest req, boolean lock) {
        // Gộp các dòng trùng sản phẩm; TreeMap để luôn khóa theo cùng thứ tự (tránh deadlock)
        Map<String, Integer> soLuongTheoMa = new TreeMap<>();
        for (DongHang d : req.items()) {
            soLuongTheoMa.merge(d.maSanPham().trim(), d.soLuong(), Integer::sum);
        }

        List<ChiTiet> items = new ArrayList<>();
        double tong = 0;
        for (Map.Entry<String, Integer> e : soLuongTheoMa.entrySet()) {
            String maSP = e.getKey();
            int sl = e.getValue();
            SanPhamBan sp = repo.findSanPham(maSP, lock)
                    .orElseThrow(() -> ApiException.badRequest("Sản phẩm " + maSP + " không tồn tại."));
            if (sp.soLuongTon() < sl) {
                throw ApiException.conflict("Sản phẩm " + maSP + " (" + sp.tenSanPham() + ") không đủ tồn kho (cần "
                        + sl + ", còn " + sp.soLuongTon() + ").");
            }
            double thanhTien = sp.gia() * sl;
            tong += thanhTien;
            items.add(new ChiTiet(sp.maSanPham(), sp.tenSanPham(), sp.donViTinh(), sl, sp.gia(), thanhTien));
        }

        String maKM = blankToNull(req.maKhuyenMai());
        double tienGiam = 0;
        Double phanTram = null;
        if (maKM != null) {
            KhuyenMai km = repo.findKhuyenMai(maKM)
                    .orElseThrow(() -> ApiException.badRequest("Khuyến mãi " + maKM + " không tồn tại."));
            String lyDo = km.lyDoKhongHopLe(tong, today());
            if (lyDo != null) {
                throw ApiException.badRequest(lyDo);
            }
            tienGiam = km.tinhTienGiam(tong);
            phanTram = km.phanTramGiam();
        }
        return new TinhTien(items, tong, tienGiam, tong - tienGiam, maKM, phanTram);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
