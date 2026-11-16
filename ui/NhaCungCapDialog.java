package ui;

import entity.NhaCungCap;
import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/** Hộp thoại Thêm / Sửa nhà cung cấp. */
public class NhaCungCapDialog extends FormDialog {

    private final JTextField txtMa = placeholder(new JTextField(), "VD: NCC006");
    private final JTextField txtTen = placeholder(new JTextField(), "VD: Công ty TNHH Thực phẩm ABC");
    private final JTextField txtTenLienHe = placeholder(new JTextField(), "VD: Anh Minh");
    private final JTextField txtSDT = placeholder(new JTextField(), "VD: 0281234567");
    private final JTextField txtTinh = placeholder(new JTextField(), "VD: Đồng Nai");
    private final JTextField txtDiaChi = placeholder(new JTextField(), "Số nhà, đường, phường/xã…");
    private final JComboBox<Choice> cboTrangThai = choices("active", "Đang hợp tác", "inactive", "Ngưng hợp tác");

    public NhaCungCapDialog(Frame owner, String title, NhaCungCap ncc, boolean editableId) {
        super(owner, title, editableId ? "Điền thông tin nhà cung cấp rồi bấm Lưu."
                : "Chỉnh sửa thông tin rồi bấm Lưu.", "truck");

        addSection("Thông tin nhà cung cấp");
        addField("Mã nhà cung cấp", txtMa, true);
        addField("Trạng thái", cboTrangThai, false);
        addField("Tên nhà cung cấp", txtTen, true, null, true);

        addSection("Liên hệ");
        addField("Người liên hệ", txtTenLienHe, true);
        addField("Số điện thoại", txtSDT, true);
        addField("Tỉnh / Thành phố", txtTinh, true);
        addField("Địa chỉ", txtDiaChi, true, null, true);

        if (ncc != null) {
            txtMa.setText(ncc.getMaNhaCungCap());
            txtTen.setText(ncc.getTen());
            txtTenLienHe.setText(ncc.getTenLienHe());
            txtSDT.setText(ncc.getSoDienThoai());
            txtTinh.setText(ncc.getTinh());
            txtDiaChi.setText(ncc.getDiaChi());
            select(cboTrangThai, ncc.getTrangThai());
        }
        if (!editableId) {
            lockField(txtMa, "Không thể đổi mã khi sửa");
        }
        finish();
    }

    @Override
    protected boolean validateForm() {
        boolean ok = required(txtMa, "Mã nhà cung cấp");
        ok &= required(txtTen, "Tên nhà cung cấp");
        ok &= required(txtTenLienHe, "Người liên hệ");
        ok &= phone(txtSDT);
        ok &= required(txtTinh, "Tỉnh / Thành phố");
        ok &= required(txtDiaChi, "Địa chỉ");
        return ok;
    }

    public NhaCungCap getNhaCungCap() {
        return new NhaCungCap(
                text(txtMa),
                text(txtTen),
                text(txtTenLienHe),
                text(txtSDT).replaceAll("[\\s.-]", ""),
                text(txtTinh),
                text(txtDiaChi),
                value(cboTrangThai)
        );
    }
}
