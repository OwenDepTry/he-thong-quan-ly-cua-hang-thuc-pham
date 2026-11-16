package ui;

import entity.KhachHang;
import java.awt.Frame;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/** Hộp thoại Thêm / Sửa khách hàng. */
public class KhachHangDialog extends FormDialog {

    private final JTextField txtMa = placeholder(new JTextField(), "VD: KH010");
    private final JTextField txtHo = placeholder(new JTextField(), "VD: Nguyễn");
    private final JTextField txtTenLot = placeholder(new JTextField(), "VD: Văn");
    private final JTextField txtTen = placeholder(new JTextField(), "VD: An");
    private final JComboBox<Choice> cboPhai = choices("nam", "Nam", "nữ", "Nữ", "khác", "Khác");
    private final JTextField txtNgaySinh = placeholder(new JTextField(), "dd/mm/yyyy");
    private final JTextField txtSDT = placeholder(new JTextField(), "VD: 0901234567");
    private final JTextField txtTinh = placeholder(new JTextField(), "VD: TP. Hồ Chí Minh");
    private final JTextField txtNgayThamGia = placeholder(new JTextField(), "dd/mm/yyyy");
    private final JTextField txtDiem = new JTextField();
    private final JComboBox<Choice> cboTrangThai = choices("active", "Đang hoạt động", "inactive", "Ngưng hoạt động");

    public KhachHangDialog(Frame owner, String title, KhachHang kh, boolean editableId) {
        super(owner, title, editableId ? "Điền thông tin khách hàng rồi bấm Lưu."
                : "Chỉnh sửa thông tin rồi bấm Lưu.", "user");

        addSection("Thông tin cá nhân");
        addField("Mã khách hàng", txtMa, true);
        addField("Trạng thái", cboTrangThai, false);
        addField("Họ", txtHo, true);
        addField("Tên lót", txtTenLot, false);
        addField("Tên", txtTen, true);
        addField("Phái", cboPhai, false);
        addField("Ngày sinh", txtNgaySinh, true, "Ngày/tháng/năm", false);

        addSection("Liên hệ và tích điểm");
        addField("Số điện thoại", txtSDT, true);
        addField("Tỉnh / Thành phố", txtTinh, true);
        addField("Ngày tham gia", txtNgayThamGia, true, "Ngày/tháng/năm", false);
        addField("Điểm tích lũy", txtDiem, true);

        if (kh != null) {
            fillData(kh);
        } else {
            txtNgayThamGia.setText(toDisplayDate(LocalDate.now().toString()));
            txtDiem.setText("0");
        }
        if (!editableId) {
            lockField(txtMa, "Không thể đổi mã khi sửa");
        }
        finish();
    }

    private void fillData(KhachHang kh) {
        txtMa.setText(kh.getMaKhachHang());
        txtHo.setText(kh.getHo());
        txtTenLot.setText(kh.getTenLot());
        txtTen.setText(kh.getTen());
        select(cboPhai, kh.getPhai());
        txtNgaySinh.setText(toDisplayDate(kh.getNgaySinh()));
        txtSDT.setText(kh.getSoDienThoai());
        txtTinh.setText(kh.getTinh());
        txtNgayThamGia.setText(toDisplayDate(kh.getNgayThamGia()));
        txtDiem.setText(String.valueOf(kh.getDiem()));
        select(cboTrangThai, kh.getTrangThai());
    }

    @Override
    protected boolean validateForm() {
        boolean ok = required(txtMa, "Mã khách hàng");
        ok &= required(txtHo, "Họ");
        ok &= required(txtTen, "Tên");
        ok &= date(txtNgaySinh, "Ngày sinh");
        ok &= phone(txtSDT);
        ok &= required(txtTinh, "Tỉnh / Thành phố");
        ok &= date(txtNgayThamGia, "Ngày tham gia");
        ok &= integer(txtDiem, "Điểm", 0);
        return ok;
    }

    public KhachHang getKhachHang() {
        return new KhachHang(
                text(txtMa),
                text(txtHo),
                text(txtTenLot),
                text(txtTen),
                value(cboPhai),
                isoDate(txtNgaySinh),
                text(txtSDT).replaceAll("[\\s.-]", ""),
                text(txtTinh),
                isoDate(txtNgayThamGia),
                Integer.parseInt(plainNumber(txtDiem)),
                value(cboTrangThai)
        );
    }
}
