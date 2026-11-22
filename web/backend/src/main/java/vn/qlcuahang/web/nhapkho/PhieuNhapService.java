package vn.qlcuahang.web.nhapkho;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.qlcuahang.web.common.ApiException;
import vn.qlcuahang.web.common.MaSinh;
import vn.qlcuahang.web.config.AppProperties;
import vn.qlcuahang.web.hoadon.HoaDonDtos.Trang;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.DongNhapRequest;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.LoHang;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.PhieuNhapChiTiet;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.PhieuNhapTomTat;
import vn.qlcuahang.web.nhapkho.PhieuNhapDtos.TaoPhieuNhapRequest;
import vn.qlcuahang.web.security.CurrentUser;

@Service
public class PhieuNhapService {

    private final PhieuNhapRepository repo;
    private final MaSinh maSinh;
    private final AppProperties props;

    public PhieuNhapService(PhieuNhapRepository repo, MaSinh maSinh, AppProperties props) {
        this.repo = repo;
        this.maSinh = maSinh;
        this.props = props;
    }

    @Transactional(readOnly = true)
    public Trang<PhieuNhapTomTat> search(CurrentUser user, String q, LocalDate tuNgay, LocalDate denNgay,
                                         int page, int size) {
        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), 200);
        // Giống hóa đơn: nhân viên chỉ thấy phiếu nhập do mình lập
        String scope = user.isQuanLy() ? null : user.maNhanVien();
        return new Trang<>(repo.search(scope, q, tuNgay, denNgay, s, p * s),
                repo.count(scope, q, tuNgay, denNgay), p, s);
    }

    @Transactional(readOnly = true)
    public PhieuNhapChiTiet get(CurrentUser user, String ma) {
        return chiTiet(user, ma, List.of());
    }

    /**
     * Lập phiếu nhập trong 1 transaction: ghi PhieuNhap + ChiTietPhieuNhap, cộng tồn kho,
     * và tạo lô hàng (HangHoa) cho các dòng có hạn sử dụng.
     */
    @Transactional
    public PhieuNhapChiTiet tao(CurrentUser user, TaoPhieuNhapRequest req) {
        String maNCC = req.maNhaCungCap().trim();
        if (!repo.nhaCungCapHoatDong(maNCC)) {
            throw ApiException.badRequest("Nhà cung cấp " + maNCC + " không tồn tại hoặc đã ngừng hợp tác.");
        }
        LocalDate today = LocalDate.now(props.zoneId());

        // Mỗi sản phẩm chỉ một dòng (khóa chính ChiTietPhieuNhap); TreeMap để khóa theo cùng thứ tự
        Map<String, DongNhapRequest> dong = new TreeMap<>();
        for (DongNhapRequest d : req.items()) {
            String ma = d.maSanPham().trim();
            if (dong.putIfAbsent(ma, d) != null) {
                throw ApiException.badRequest("Sản phẩm " + ma + " bị nhập trùng. Hãy gộp thành một dòng.");
            }
            if (d.hanSuDung() != null && d.hanSuDung().isBefore(today)) {
                throw ApiException.badRequest("Hạn sử dụng của " + ma + " đã qua. Không thể nhập hàng hết hạn.");
            }
        }

        double tong = 0;
        for (Map.Entry<String, DongNhapRequest> e : dong.entrySet()) {
            repo.lockSanPham(e.getKey())
                    .orElseThrow(() -> ApiException.badRequest("Sản phẩm " + e.getKey() + " không tồn tại."));
            tong += e.getValue().soLuong() * e.getValue().donGia();
        }

        String ma = maSinh.next("PhieuNhap", "MaPhieuNhap", "PN", 0);
        repo.insertPhieuNhap(ma, maNCC, user.maNhanVien(), tong, today);
        List<LoHang> loMoi = new ArrayList<>();
        for (Map.Entry<String, DongNhapRequest> e : dong.entrySet()) {
            DongNhapRequest d = e.getValue();
            repo.insertChiTiet(ma, e.getKey(), d.soLuong(), d.donGia());
            repo.congTonKho(e.getKey(), d.soLuong());
            if (d.hanSuDung() != null) {
                String maHang = maSinh.next("HangHoa", "MaHang", "HH", 3);
                repo.insertLoHang(maHang, e.getKey(), d.soLuong(), today, d.hanSuDung());
                loMoi.add(new LoHang(maHang, e.getKey(), d.soLuong(), d.hanSuDung()));
            }
        }
        return chiTiet(user, ma, loMoi);
    }

    private PhieuNhapChiTiet chiTiet(CurrentUser user, String ma, List<LoHang> loMoi) {
        PhieuNhapTomTat pn = repo.findTomTat(ma)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy phiếu nhập " + ma + "."));
        if (!user.isQuanLy() && !user.maNhanVien().equals(pn.maNhanVien())) {
            throw ApiException.forbidden("Bạn chỉ xem được phiếu nhập do mình lập.");
        }
        return new PhieuNhapChiTiet(pn, repo.findChiTiet(ma), loMoi);
    }
}
