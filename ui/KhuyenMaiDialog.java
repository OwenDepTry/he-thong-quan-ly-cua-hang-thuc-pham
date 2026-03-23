package ui;

import entity.KhuyenMai;
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

public class KhuyenMaiDialog extends JDialog {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtDieuKien = new JTextField();
    private final JTextField txtPhanTramGiam = new JTextField();
    private final JTextField txtNgayBatDau = new JTextField();
    private final JTextField txtNgayKetThuc = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

    private boolean saved = false;

    public KhuyenMaiDialog(Frame owner, String title, KhuyenMai km, boolean editableId) {
        super(owner, title, true);
        setLayout(new BorderLayout(8, 8));
        setSize(560, 380);
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
        addRow(form, gbc, row++, "Mã khuyến mãi", txtMa);
        addRow(form, gbc, row++, "Tên khuyến mãi", txtTen);
        addRow(form, gbc, row++, "Điều kiện", txtDieuKien);
        addRow(form, gbc, row++, "Phần trăm giảm", txtPhanTramGiam);
        addRow(form, gbc, row++, "Ngày bắt đầu", txtNgayBatDau);
        addRow(form, gbc, row++, "Ngày kết thúc", txtNgayKetThuc);
        addRow(form, gbc, row++, "Trạng thái", cboTrangThai);

        txtMa.setEditable(editableId);
        txtMa.setBackground(editableId ? AppTheme.WHITE : new java.awt.Color(245, 245, 245));

        if (km != null) {
            fillData(km);
        } else {
            txtPhanTramGiam.setText("10");
            txtNgayBatDau.setText(LocalDate.now().toString());
            txtNgayKetThuc.setText(LocalDate.now().plusDays(7).toString());
            txtDieuKien.setText(">=100000");
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

    private void fillData(KhuyenMai km) {
        txtMa.setText(km.getMaKhuyenMai());
        txtTen.setText(km.getTenKhuyenMai());
        txtDieuKien.setText(km.getDieuKien());
        txtPhanTramGiam.setText(String.valueOf(km.getPhanTramGiam()));
        txtNgayBatDau.setText(km.getNgayBatDau());
        txtNgayKetThuc.setText(km.getNgayKetThuc());
        cboTrangThai.setSelectedItem(km.getTrangThai());
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
            return "Mã khuyến mãi không được để trống.";
        }
        if (txtTen.getText().trim().isEmpty()) {
            return "Tên khuyến mãi không được để trống.";
        }
        if (txtDieuKien.getText().trim().isEmpty()) {
            return "Điều kiện không được để trống.";
        }

        try {
            double ptg = Double.parseDouble(txtPhanTramGiam.getText().trim());
            if (ptg < 0 || ptg > 100) {
                return "Phần trăm giảm phải nằm trong khoảng 0 đến 100.";
            }
        } catch (NumberFormatException e) {
            return "Phần trăm giảm phải là số.";
        }

        if (!isValidDate(txtNgayBatDau.getText().trim())) {
            return "Ngày bắt đầu phải đúng định dạng yyyy-MM-dd.";
        }
        if (!isValidDate(txtNgayKetThuc.getText().trim())) {
            return "Ngày kết thúc phải đúng định dạng yyyy-MM-dd.";
        }

        LocalDate start = LocalDate.parse(txtNgayBatDau.getText().trim());
        LocalDate end = LocalDate.parse(txtNgayKetThuc.getText().trim());
        if (end.isBefore(start)) {
            return "Ngày kết thúc không được nhỏ hơn ngày bắt đầu.";
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

    public KhuyenMai getKhuyenMai() {
        return new KhuyenMai(
                txtMa.getText().trim(),
                txtTen.getText().trim(),
                txtDieuKien.getText().trim(),
                Double.parseDouble(txtPhanTramGiam.getText().trim()),
                txtNgayBatDau.getText().trim(),
                txtNgayKetThuc.getText().trim(),
                cboTrangThai.getSelectedItem().toString()
        );
    }
}