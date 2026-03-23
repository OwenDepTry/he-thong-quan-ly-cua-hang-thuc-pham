package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SanPhamDialog extends JDialog {

    private JTextField txtMaSanPham;
    private JTextField txtMaNhom;
    private JTextField txtTenSanPham;
    private JTextField txtLoai;
    private JTextField txtDonViTinh;
    private JTextField txtHanSuDung;
    private JTextField txtMoTa;
    private JTextField txtGia;
    private JTextField txtSoLuongTon;

    private boolean confirmed = false;

    public SanPhamDialog(java.awt.Frame owner, String title) {
        super(owner, title, true);
        initUI();
    }

    private void initUI() {
        setSize(500, 420);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(9, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtMaSanPham = new JTextField();
        txtMaNhom = new JTextField();
        txtTenSanPham = new JTextField();
        txtLoai = new JTextField();
        txtDonViTinh = new JTextField();
        txtHanSuDung = new JTextField();
        txtMoTa = new JTextField();
        txtGia = new JTextField();
        txtSoLuongTon = new JTextField();

        form.add(new JLabel("Mã sản phẩm"));
        form.add(txtMaSanPham);

        form.add(new JLabel("Mã nhóm"));
        form.add(txtMaNhom);

        form.add(new JLabel("Tên sản phẩm"));
        form.add(txtTenSanPham);

        form.add(new JLabel("Loại"));
        form.add(txtLoai);

        form.add(new JLabel("Đơn vị tính"));
        form.add(txtDonViTinh);

        form.add(new JLabel("Hạn sử dụng"));
        form.add(txtHanSuDung);

        form.add(new JLabel("Mô tả"));
        form.add(txtMoTa);

        form.add(new JLabel("Giá"));
        form.add(txtGia);

        form.add(new JLabel("Số lượng tồn"));
        form.add(txtSoLuongTon);

        JPanel actions = new JPanel();

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.setPreferredSize(new Dimension(90, 32));
        btnCancel.setPreferredSize(new Dimension(90, 32));

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());

        actions.add(btnSave);
        actions.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void handleSave() {
        if (getMaSanPham().isEmpty() || getTenSanPham().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm và Tên sản phẩm không được để trống!");
            return;
        }

        try {
            Double.parseDouble(getGia());
            Integer.parseInt(getSoLuongTon());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số và Số lượng tồn phải là số nguyên!");
            return;
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setData(Object[] row) {
        txtMaSanPham.setText(String.valueOf(row[0]));
        txtMaNhom.setText(String.valueOf(row[1]));
        txtTenSanPham.setText(String.valueOf(row[2]));
        txtLoai.setText(String.valueOf(row[3]));
        txtDonViTinh.setText(String.valueOf(row[4]));
        txtHanSuDung.setText(String.valueOf(row[5]));
        txtMoTa.setText(String.valueOf(row[6]));
        txtGia.setText(String.valueOf(row[7]));
        txtSoLuongTon.setText(String.valueOf(row[8]));
    }

    public void setMaEditable(boolean editable) {
        txtMaSanPham.setEditable(editable);
    }

    public String getMaSanPham() {
        return txtMaSanPham.getText().trim();
    }

    public String getMaNhom() {
        return txtMaNhom.getText().trim();
    }

    public String getTenSanPham() {
        return txtTenSanPham.getText().trim();
    }

    public String getLoai() {
        return txtLoai.getText().trim();
    }

    public String getDonViTinh() {
        return txtDonViTinh.getText().trim();
    }

    public String getHanSuDung() {
        return txtHanSuDung.getText().trim();
    }

    public String getMoTa() {
        return txtMoTa.getText().trim();
    }

    public String getGia() {
        return txtGia.getText().trim();
    }

    public String getSoLuongTon() {
        return txtSoLuongTon.getText().trim();
    }
}