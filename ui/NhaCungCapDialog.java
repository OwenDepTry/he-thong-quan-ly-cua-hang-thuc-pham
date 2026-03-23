package ui;

import entity.NhaCungCap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NhaCungCapDialog extends JDialog {

    private final JTextField txtMa = new JTextField();
    private final JTextField txtTen = new JTextField();
    private final JTextField txtTenLienHe = new JTextField();
    private final JTextField txtSDT = new JTextField();
    private final JTextField txtTinh = new JTextField();
    private final JTextField txtDiaChi = new JTextField();
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"active", "inactive"});

    private boolean saved = false;

    public NhaCungCapDialog(Frame owner, String title, NhaCungCap ncc, boolean editableId) {
        super(owner, title, true);
        setLayout(new BorderLayout(8, 8));
        setSize(560, 360);
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
        addRow(form, gbc, row++, "Mã nhà cung cấp", txtMa);
        addRow(form, gbc, row++, "Tên", txtTen);
        addRow(form, gbc, row++, "Tên liên hệ", txtTenLienHe);
        addRow(form, gbc, row++, "Số điện thoại", txtSDT);
        addRow(form, gbc, row++, "Tỉnh", txtTinh);
        addRow(form, gbc, row++, "Địa chỉ", txtDiaChi);
        addRow(form, gbc, row++, "Trạng thái", cboTrangThai);

        txtMa.setEditable(editableId);
        txtMa.setBackground(editableId ? AppTheme.WHITE : new java.awt.Color(245, 245, 245));

        if (ncc != null) {
            fillData(ncc);
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

    private void fillData(NhaCungCap ncc) {
        txtMa.setText(ncc.getMaNhaCungCap());
        txtTen.setText(ncc.getTen());
        txtTenLienHe.setText(ncc.getTenLienHe());
        txtSDT.setText(ncc.getSoDienThoai());
        txtTinh.setText(ncc.getTinh());
        txtDiaChi.setText(ncc.getDiaChi());
        cboTrangThai.setSelectedItem(ncc.getTrangThai());
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
            return "Mã nhà cung cấp không được để trống.";
        }
        if (txtTen.getText().trim().isEmpty()) {
            return "Tên nhà cung cấp không được để trống.";
        }
        if (txtTenLienHe.getText().trim().isEmpty()) {
            return "Tên liên hệ không được để trống.";
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
        return null;
    }

    public boolean isSaved() {
        return saved;
    }

    public NhaCungCap getNhaCungCap() {
        return new NhaCungCap(
                txtMa.getText().trim(),
                txtTen.getText().trim(),
                txtTenLienHe.getText().trim(),
                txtSDT.getText().trim(),
                txtTinh.getText().trim(),
                txtDiaChi.getText().trim(),
                cboTrangThai.getSelectedItem().toString()
        );
    }
}