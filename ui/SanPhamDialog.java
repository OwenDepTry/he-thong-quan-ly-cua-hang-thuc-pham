package ui;

import javax.swing.JTextField;

/** Hộp thoại Thêm / Sửa sản phẩm. */
public class SanPhamDialog extends FormDialog {

    private final JTextField txtMaSanPham = placeholder(new JTextField(), "VD: SP021");
    private final JTextField txtMaNhom = placeholder(new JTextField(), "VD: TH01");
    private final JTextField txtTenSanPham = placeholder(new JTextField(), "VD: Sữa tươi Vinamilk 1L");
    private final JTextField txtLoai = placeholder(new JTextField(), "VD: Đồ uống");
    private final JTextField txtDonViTinh = placeholder(new JTextField(), "VD: Hộp, Kg, Chai");
    private final JTextField txtHanSuDung = new JTextField();
    private final JTextField txtMoTa = new JTextField();
    private final JTextField txtGia = placeholder(new JTextField(), "VD: 25.000");
    private final JTextField txtSoLuongTon = placeholder(new JTextField(), "VD: 100");

    public SanPhamDialog(java.awt.Frame owner, String title) {
        super(owner, title, "Điền thông tin sản phẩm rồi bấm Lưu.", "box");

        addSection("Thông tin chung");
        addField("Mã sản phẩm", txtMaSanPham, true);
        addField("Mã nhóm", txtMaNhom, false);
        addField("Tên sản phẩm", txtTenSanPham, true, null, true);
        addField("Loại", txtLoai, false);
        addField("Đơn vị tính", txtDonViTinh, false);

        addSection("Giá và tồn kho");
        addField("Giá (đồng)", txtGia, true);
        addField("Số lượng tồn", txtSoLuongTon, true);
        addField("Hạn sử dụng", txtHanSuDung, false);
        addField("Mô tả", txtMoTa, false, null, true);

        txtSoLuongTon.setText("0");
        finish();
    }

    @Override
    protected boolean validateForm() {
        boolean ok = required(txtMaSanPham, "Mã sản phẩm");
        ok &= required(txtTenSanPham, "Tên sản phẩm");
        ok &= number(txtGia, "Giá", 0, Double.MAX_VALUE);
        ok &= integer(txtSoLuongTon, "Số lượng tồn", 0);
        return ok;
    }

    /** Giữ tên cũ cho SanPhamPanel. */
    public boolean isConfirmed() {
        return isSaved();
    }

    public void setData(Object[] row) {
        txtMaSanPham.setText(str(row[0]));
        txtMaNhom.setText(str(row[1]));
        txtTenSanPham.setText(str(row[2]));
        txtLoai.setText(str(row[3]));
        txtDonViTinh.setText(str(row[4]));
        txtHanSuDung.setText(str(row[5]));
        txtMoTa.setText(str(row[6]));
        txtGia.setText(displayNumber(row[7]));
        txtSoLuongTon.setText(displayNumber(row[8]));
    }

    public void setMaEditable(boolean editable) {
        if (!editable) {
            lockField(txtMaSanPham, "Không thể đổi mã khi sửa");
            setSubtitle("Chỉnh sửa thông tin rồi bấm Lưu.");
        }
    }

    private static String str(Object o) {
        return o == null || "null".equals(String.valueOf(o)) ? "" : String.valueOf(o);
    }

    public String getMaSanPham() {
        return text(txtMaSanPham);
    }

    public String getMaNhom() {
        return text(txtMaNhom);
    }

    public String getTenSanPham() {
        return text(txtTenSanPham);
    }

    public String getLoai() {
        return text(txtLoai);
    }

    public String getDonViTinh() {
        return text(txtDonViTinh);
    }

    public String getHanSuDung() {
        return text(txtHanSuDung);
    }

    public String getMoTa() {
        return text(txtMoTa);
    }

    /** Số thuần (VD "25000") để Double.parseDouble được. */
    public String getGia() {
        return plainNumber(txtGia);
    }

    public String getSoLuongTon() {
        return plainNumber(txtSoLuongTon);
    }
}
