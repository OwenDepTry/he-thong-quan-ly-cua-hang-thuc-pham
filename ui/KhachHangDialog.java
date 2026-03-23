package ui;

import entity.KhachHang;
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

public class KhachHangDialog extends JDialog {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtHo = new JTextField();
    private final JTextField txtTenLot = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JComboBox<String> cboPhai = new JComboBox<>(new String[]{"nam", "nữ", "khác"});
    private final JTextField txtNgaySinh = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtTinh = new JTextField();
    private final JTextField txtNgayThamGia = new JTextField();
    private final JTextField txtDiem = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

    private boolean saved;

    public KhachHangDialog(Frame owner, String title, KhachHang kh, boolean editableId) {
        super(owner, title, true);
        setLayout(new BorderLayout(8, 8));
        setSize(520, 430);
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
        addRow(form, gbc, row++, "Mã khách hàng", txtMa);
        addRow(form, gbc, row++, "Họ", txtHo);
        addRow(form, gbc, row++, "Tên lót", txtTenLot);
        addRow(form, gbc, row++, "Tên", txtTen);
        addRow(form, gbc, row++, "Phái", cboPhai);
        addRow(form, gbc, row++, "Ngày sinh", txtNgaySinh);
        addRow(form, gbc, row++, "Số điện thoại", txtSDT);
        addRow(form, gbc, row++, "Tỉnh", txtTinh);
        addRow(form, gbc, row++, "Ngày tham gia", txtNgayThamGia);
        addRow(form, gbc, row++, "Điểm", txtDiem);
        addRow(form, gbc, row++, "Trạng thái", cboTrangThai);

        txtMa.setEditable(editableId);
        txtMa.setBackground(editableId ? AppTheme.WHITE : new java.awt.Color(245, 245, 245));

        if (kh != null) {
            fillData(kh);
        } else {
            txtNgayThamGia.setText(LocalDate.now().toString());
            txtNgaySinh.setText("2000-01-01");
            txtDiem.setText("0");
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
        if (comp instanceof JTextField) {
            ((JTextField) comp).setPreferredSize(new Dimension(240, 28));
        } else if (comp instanceof JComboBox) {
            comp.setPreferredSize(new Dimension(240, 28));
        }
        panel.add(comp, gbc);
    }

    private void fillData(KhachHang kh) {
        txtMa.setText(kh.getMaKhachHang());
        txtHo.setText(kh.getHo());
        txtTenLot.setText(kh.getTenLot());
        txtTen.setText(kh.getTen());
        cboPhai.setSelectedItem(kh.getPhai());
        txtNgaySinh.setText(kh.getNgaySinh());
        txtSDT.setText(kh.getSoDienThoai());
        txtTinh.setText(kh.getTinh());
        txtNgayThamGia.setText(kh.getNgayThamGia());
        txtDiem.setText(String.valueOf(kh.getDiem()));
        cboTrangThai.setSelectedItem(kh.getTrangThai());
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
            return "Mã khách hàng không được để trống.";
        }
        if (txtHo.getText().trim().isEmpty()) {
            return "Họ không được để trống.";
        }
        if (txtTen.getText().trim().isEmpty()) {
            return "Tên không được để trống.";
        }
        if (txtSDT.getText().trim().isEmpty()) {
            return "Số điện thoại không được để trống.";
        }
        if (!txtSDT.getText().trim().matches("\\d{8,11}")) {
            return "Số điện thoại chỉ được chứa 8-11 chữ số.";
        }
        if (txtTinh.getText().trim().isEmpty()) {
            return "Tỉnh không được để trống.";
        }
        if (!isValidDate(txtNgaySinh.getText().trim())) {
            return "Ngày sinh phải đúng định dạng yyyy-MM-dd.";
        }
        if (!isValidDate(txtNgayThamGia.getText().trim())) {
            return "Ngày tham gia phải đúng định dạng yyyy-MM-dd.";
        }
        try {
            int diem = Integer.parseInt(txtDiem.getText().trim());
            if (diem < 0) {
                return "Điểm không được âm.";
            }
        } catch (NumberFormatException e) {
            return "Điểm phải là số nguyên.";
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

    public KhachHang getKhachHang() {
        return new KhachHang(
                txtMa.getText().trim(),
                txtHo.getText().trim(),
                txtTenLot.getText().trim(),
                txtTen.getText().trim(),
                cboPhai.getSelectedItem().toString(),
                txtNgaySinh.getText().trim(),
                txtSDT.getText().trim(),
                txtTinh.getText().trim(),
                txtNgayThamGia.getText().trim(),
                Integer.parseInt(txtDiem.getText().trim()),
                cboTrangThai.getSelectedItem().toString()
        );
    }
}