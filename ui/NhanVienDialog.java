package ui;

import entity.NhanVien;
import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Hộp thoại Thêm / Sửa nhân viên. */
public class NhanVienDialog extends FormDialog {

    private final JTextField txtMa = placeholder(new JTextField(), "VD: NV005");
    private final JTextField txtHo = placeholder(new JTextField(), "VD: Trần");
    private final JTextField txtTenLot = placeholder(new JTextField(), "VD: Thị");
    private final JTextField txtTen = placeholder(new JTextField(), "VD: Bình");
    private final JComboBox<Choice> cboPhai = choices("nam", "Nam", "nữ", "Nữ", "khác", "Khác");
    private final JTextField txtNgaySinh = placeholder(new JTextField(), "dd/mm/yyyy");
    private final JTextField txtSDT = placeholder(new JTextField(), "VD: 0901234567");
    private final JTextField txtTinh = placeholder(new JTextField(), "VD: Quảng Ngãi");
    private final JTextField txtDiaChi = placeholder(new JTextField(), "Số nhà, đường, phường/xã…");
    private final JTextField txtLuong = placeholder(new JTextField(), "VD: 8.000.000");
    private final JComboBox<Choice> cboChucVu = choices("NV", "Nhân viên", "QL", "Quản lý");
    private final JComboBox<Choice> cboTrangThai = choices("active", "Đang làm việc", "inactive", "Đã nghỉ / khóa");
    private final JPasswordField txtMatKhau = new JPasswordField();

    public NhanVienDialog(Frame owner, String title, NhanVien nv, boolean editableId) {
        super(owner, title, editableId ? "Điền thông tin nhân viên rồi bấm Lưu."
                : "Chỉnh sửa thông tin rồi bấm Lưu.", "users");

        addSection("Thông tin cá nhân");
        addField("Mã nhân viên", txtMa, true);
        addField("Phái", cboPhai, false);
        addField("Họ", txtHo, true);
        addField("Tên lót", txtTenLot, false);
        addField("Tên", txtTen, true);
        addField("Ngày sinh", txtNgaySinh, true, "Ngày/tháng/năm", false);

        addSection("Liên hệ");
        addField("Số điện thoại", txtSDT, true);
        addField("Tỉnh / Thành phố", txtTinh, true);
        addField("Địa chỉ", txtDiaChi, true, null, true);

        addSection("Công việc và tài khoản");
        addField("Chức vụ", cboChucVu, false);
        addField("Lương (đồng)", txtLuong, true);
        addField("Trạng thái", cboTrangThai, false);
        txtMatKhau.putClientProperty("JPasswordField.showRevealButton", Boolean.TRUE);
        addField("Mật khẩu đăng nhập", txtMatKhau, true);

        if (nv != null) {
            fillData(nv);
        } else {
            txtLuong.setText("8.000.000");
            txtMatKhau.setText("123");
        }
        if (!editableId) {
            lockField(txtMa, "Không thể đổi mã khi sửa");
        }
        finish();
    }

    private void fillData(NhanVien nv) {
        txtMa.setText(nv.getMaNhanVien());
        txtHo.setText(nv.getHo());
        txtTenLot.setText(nv.getTenLot());
        txtTen.setText(nv.getTen());
        select(cboPhai, nv.getPhai());
        txtNgaySinh.setText(toDisplayDate(nv.getNgaySinh()));
        txtSDT.setText(nv.getSoDienThoai());
        txtTinh.setText(nv.getTinh());
        txtDiaChi.setText(nv.getDiaChi());
        txtLuong.setText(displayNumber(nv.getLuong()));
        select(cboChucVu, nv.getChucVu());
        select(cboTrangThai, nv.getTrangThai());
        txtMatKhau.setText(nv.getMatKhau());
    }

    @Override
    protected boolean validateForm() {
        boolean ok = required(txtMa, "Mã nhân viên");
        ok &= required(txtHo, "Họ");
        ok &= required(txtTen, "Tên");
        ok &= date(txtNgaySinh, "Ngày sinh");
        ok &= phone(txtSDT);
        ok &= required(txtTinh, "Tỉnh / Thành phố");
        ok &= required(txtDiaChi, "Địa chỉ");
        ok &= number(txtLuong, "Lương", 0, Double.MAX_VALUE);
        ok &= required(txtMatKhau, "Mật khẩu");
        return ok;
    }

    public NhanVien getNhanVien() {
        return new NhanVien(
                text(txtMa),
                text(txtHo),
                text(txtTenLot),
                text(txtTen),
                value(cboPhai),
                isoDate(txtNgaySinh),
                text(txtSDT).replaceAll("[\\s.-]", ""),
                text(txtTinh),
                text(txtDiaChi),
                Double.parseDouble(plainNumber(txtLuong)),
                value(cboChucVu),
                value(cboTrangThai),
                text(txtMatKhau)
        );
    }
}
