package ui;

import entity.NhanVien;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NhanVienDialog extends JDialog {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtHo = new JTextField();
    private final JTextField txtTenLot = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JComboBox<String> cboPhai = new JComboBox<>(new String[]{"nam", "nữ", "khác"});
    private final JTextField txtNgaySinh = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtTinh = new JTextField();
    private final JTextField txtDiaChi = new JTextField();
    private final JTextField txtLuong = new JTextField();
    private final JComboBox<String> cboChucVu = new JComboBox<>(new String[]{"QL", "NV"});
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});
    private final JTextField txtMatKhau = new JTextField();

    private boolean saved = false;

    public NhanVienDialog(Frame owner, String title, NhanVien nv, boolean editableId) {
        super(owner, title, true);
        setLayout(new BorderLayout(8, 8));
        setSize(560, 500);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.PANEL_WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.GREEN_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 14, 10, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Mã nhân viên", txtMa);
        addRow(form, gbc, row++, "Họ", txtHo);
        addRow(form, gbc, row++, "Tên lót", txtTenLot);
        addRow(form, gbc, row++, "Tên", txtTen);
        addRow(form, gbc, row++, "Phái", cboPhai);
        addRow(form, gbc, row++, "Ngày sinh", txtNgaySinh);
        addRow(form, gbc, row++, "Số điện thoại", txtSDT);
        addRow(form, gbc, row++, "Tỉnh", txtTinh);
        addRow(form, gbc, row++, "Địa chỉ", txtDiaChi);
        addRow(form, gbc, row++, "Lương", txtLuong);
        addRow(form, gbc, row++, "Chức vụ", cboChucVu);
        addRow(form, gbc, row++, "Trạng thái", cboTrangThai);
        addRow(form, gbc, row++, "Mật khẩu", txtMatKhau);

        txtMa.setEditable(editableId);
        txtMa.setBackground(editableId ? AppTheme.WHITE : new java.awt.Color(245, 245, 245));

        if (nv != null) {
            fillData(nv);
        } else {
            txtNgaySinh.setText("2000-01-01");
            txtLuong.setText("8000000");
            txtMatKhau.setText("123");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        buttonPanel.setOpaque(false);

        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(90, 32));
        btnSave.setBackground(AppTheme.GREEN_TOP);
        btnSave.setForeground(java.awt.Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> onSave());

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        comp.setPreferredSize(new Dimension(260, 28));
        panel.add(comp, gbc);
    }

    private void fillData(NhanVien nv) {
        txtMa.setText(nv.getMaNhanVien());
        txtHo.setText(nv.getHo());
        txtTenLot.setText(nv.getTenLot());
        txtTen.setText(nv.getTen());
        cboPhai.setSelectedItem(nv.getPhai());
        txtNgaySinh.setText(nv.getNgaySinh());
        txtSDT.setText(nv.getSoDienThoai());
        txtTinh.setText(nv.getTinh());
        txtDiaChi.setText(nv.getDiaChi());
        txtLuong.setText(String.valueOf(nv.getLuong()));
        cboChucVu.setSelectedItem(nv.getChucVu());
        cboTrangThai.setSelectedItem(nv.getTrangThai());
        txtMatKhau.setText(nv.getMatKhau());
    }

    private void onSave() {
        String error = validateInput();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saved = true;
        dispose();
    }

    private String validateInput() {
        if (txtMa.getText().trim().isEmpty()) {
            return "Mã nhân viên không được để trống.";
        }
        if (txtHo.getText().trim().isEmpty()) {
            return "Họ không được để trống.";
        }
        if (txtTen.getText().trim().isEmpty()) {
            return "Tên không được để trống.";
        }
        if (!isValidDate(txtNgaySinh.getText().trim())) {
            return "Ngày sinh phải đúng định dạng yyyy-MM-dd.";
        }
        if (txtSDT.getText().trim().isEmpty()) {
            return "Số điện thoại không được để trống.";
        }
        if (!txtSDT.getText().trim().matches("\\d{8,11}")) {
            return "Số điện thoại phải gồm 8-11 chữ số.";
        }
        if (txtTinh.getText().trim().isEmpty()) {
            return "Tỉnh không được để trống.";
        }
        if (txtDiaChi.getText().trim().isEmpty()) {
            return "Địa chỉ không được để trống.";
        }
        if (txtMatKhau.getText().trim().isEmpty()) {
            return "Mật khẩu không được để trống.";
        }

        try {
            double luong = Double.parseDouble(txtLuong.getText().trim());
            if (luong < 0) {
                return "Lương không được âm.";
            }
        } catch (NumberFormatException e) {
            return "Lương phải là số.";
        }

        return null;
    }

    private boolean isValidDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public NhanVien getNhanVien() {
        return new NhanVien(
                txtMa.getText().trim(),
                txtHo.getText().trim(),
                txtTenLot.getText().trim(),
                txtTen.getText().trim(),
                cboPhai.getSelectedItem().toString(),
                txtNgaySinh.getText().trim(),
                txtSDT.getText().trim(),
                txtTinh.getText().trim(),
                txtDiaChi.getText().trim(),
                Double.parseDouble(txtLuong.getText().trim()),
                cboChucVu.getSelectedItem().toString(),
                cboTrangThai.getSelectedItem().toString(),
                txtMatKhau.getText().trim()
        );
    }
}