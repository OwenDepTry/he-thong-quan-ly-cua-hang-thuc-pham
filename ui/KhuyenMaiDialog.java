package ui;

import entity.KhuyenMai;
import java.awt.Frame;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/** Hộp thoại Thêm / Sửa khuyến mãi. */
public class KhuyenMaiDialog extends FormDialog {

    private final JTextField txtMa = placeholder(new JTextField(), "VD: KM005");
    private final JTextField txtTen = placeholder(new JTextField(), "VD: Giảm giá cuối tuần");
    private final JTextField txtDieuKien = new JTextField();
    private final JTextField txtPhanTramGiam = placeholder(new JTextField(), "0 – 100");
    private final JTextField txtNgayBatDau = placeholder(new JTextField(), "dd/mm/yyyy");
    private final JTextField txtNgayKetThuc = placeholder(new JTextField(), "dd/mm/yyyy");
    private final JComboBox<Choice> cboTrangThai = choices("active", "Đang áp dụng", "inactive", "Tạm ngưng");

    public KhuyenMaiDialog(Frame owner, String title, KhuyenMai km, boolean editableId) {
        super(owner, title, editableId ? "Tạo chương trình khuyến mãi mới."
                : "Chỉnh sửa chương trình rồi bấm Lưu.", "tag");

        addSection("Chương trình");
        addField("Mã khuyến mãi", txtMa, true);
        addField("Trạng thái", cboTrangThai, false);
        addField("Tên khuyến mãi", txtTen, true, null, true);

        addSection("Điều kiện và mức giảm");
        addField("Điều kiện áp dụng", txtDieuKien, true, "VD: >=100000 (hóa đơn từ 100.000 đ)", false);
        addField("Phần trăm giảm (%)", txtPhanTramGiam, true);
        addField("Ngày bắt đầu", txtNgayBatDau, true, "Ngày/tháng/năm", false);
        addField("Ngày kết thúc", txtNgayKetThuc, true, "Ngày/tháng/năm", false);

        if (km != null) {
            txtMa.setText(km.getMaKhuyenMai());
            txtTen.setText(km.getTenKhuyenMai());
            txtDieuKien.setText(km.getDieuKien());
            txtPhanTramGiam.setText(displayNumber(km.getPhanTramGiam()));
            txtNgayBatDau.setText(toDisplayDate(km.getNgayBatDau()));
            txtNgayKetThuc.setText(toDisplayDate(km.getNgayKetThuc()));
            select(cboTrangThai, km.getTrangThai());
        } else {
            txtPhanTramGiam.setText("10");
            txtNgayBatDau.setText(toDisplayDate(LocalDate.now().toString()));
            txtNgayKetThuc.setText(toDisplayDate(LocalDate.now().plusDays(7).toString()));
            txtDieuKien.setText(">=100000");
        }
        if (!editableId) {
            lockField(txtMa, "Không thể đổi mã khi sửa");
        }
        finish();
    }

    @Override
    protected boolean validateForm() {
        boolean ok = required(txtMa, "Mã khuyến mãi");
        ok &= required(txtTen, "Tên khuyến mãi");
        ok &= required(txtDieuKien, "Điều kiện");
        ok &= number(txtPhanTramGiam, "Phần trăm giảm", 0, 100);
        boolean datesOk = date(txtNgayBatDau, "Ngày bắt đầu");
        datesOk &= date(txtNgayKetThuc, "Ngày kết thúc");
        if (datesOk && parseDate(text(txtNgayKetThuc)).isBefore(parseDate(text(txtNgayBatDau)))) {
            datesOk = error(txtNgayKetThuc, "Ngày kết thúc phải sau ngày bắt đầu");
        }
        return ok && datesOk;
    }

    public KhuyenMai getKhuyenMai() {
        return new KhuyenMai(
                text(txtMa),
                text(txtTen),
                text(txtDieuKien),
                Double.parseDouble(plainNumber(txtPhanTramGiam)),
                isoDate(txtNgayBatDau),
                isoDate(txtNgayKetThuc),
                value(cboTrangThai)
        );
    }
}
