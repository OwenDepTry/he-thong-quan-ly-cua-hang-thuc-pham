package ui;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TaiKhoanNhanVienPanel extends JPanel {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private JTextField txtMaNV;
    private JTextField txtHo;
    private JTextField txtTenLot;
    private JTextField txtTen;
    private JTextField txtPhai;
    private JTextField txtNgaySinh;
    private JTextField txtSoDienThoai;
    private JTextField txtTinh;
    private JTextField txtDiaChi;
    private JTextField txtChucVu;
    private JTextField txtTrangThai;

    public TaiKhoanNhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Thông tin cá nhân nhân viên", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadCurrentUser();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        txtMaNV = createField(false);
        txtHo = createField(true);
        txtTenLot = createField(true);
        txtTen = createField(true);
        txtPhai = createField(true);
        txtNgaySinh = createField(true);
        txtSoDienThoai = createField(true);
        txtTinh = createField(true);
        txtDiaChi = createField(true);
        txtChucVu = createField(false);
        txtTrangThai = createField(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, gbc, 0, "Mã nhân viên", txtMaNV);
        addRow(panel, gbc, 1, "Họ", txtHo);
        addRow(panel, gbc, 2, "Tên lót", txtTenLot);
        addRow(panel, gbc, 3, "Tên", txtTen);
        addRow(panel, gbc, 4, "Phái", txtPhai);
        addRow(panel, gbc, 5, "Ngày sinh", txtNgaySinh);
        addRow(panel, gbc, 6, "Số điện thoại", txtSoDienThoai);
        addRow(panel, gbc, 7, "Tỉnh", txtTinh);
        addRow(panel, gbc, 8, "Địa chỉ", txtDiaChi);
        addRow(panel, gbc, 9, "Chức vụ", txtChucVu);
        addRow(panel, gbc, 10, "Trạng thái", txtTrangThai);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        panel.setOpaque(false);

        JButton btnSave = new JButton("Lưu thông tin");
        btnSave.setPreferredSize(new Dimension(150, 38));
        btnSave.addActionListener(e -> saveProfile());

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        btnChangePassword.setPreferredSize(new Dimension(150, 38));
        btnChangePassword.addActionListener(e -> changePassword());

        panel.add(btnSave);
        panel.add(btnChangePassword);
        return panel;
    }

    private JTextField createField(boolean editable) {
        JTextField txt = new JTextField();
        txt.setEditable(editable);
        txt.setPreferredSize(new Dimension(280, 34));
        return txt;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void loadCurrentUser() {
        NhanVien current = AppSession.getCurrentUser();
        if (current == null) return;

        NhanVien fresh = nhanVienDAO.findById(current.getMaNhanVien());
        if (fresh == null) return;

        AppSession.setCurrentUser(fresh);

        txtMaNV.setText(fresh.getMaNhanVien());
        txtHo.setText(fresh.getHo());
        txtTenLot.setText(fresh.getTenLot());
        txtTen.setText(fresh.getTen());
        txtPhai.setText(fresh.getPhai());
        txtNgaySinh.setText(fresh.getNgaySinh());
        txtSoDienThoai.setText(fresh.getSoDienThoai());
        txtTinh.setText(fresh.getTinh());
        txtDiaChi.setText(fresh.getDiaChi());
        txtChucVu.setText(fresh.getChucVu());
        txtTrangThai.setText(fresh.getTrangThai());
    }

    private void saveProfile() {
        NhanVien current = AppSession.getCurrentUser();
        if (current == null) return;

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(txtMaNV.getText().trim());
        nv.setHo(txtHo.getText().trim());
        nv.setTenLot(txtTenLot.getText().trim());
        nv.setTen(txtTen.getText().trim());
        nv.setPhai(txtPhai.getText().trim());
        nv.setNgaySinh(txtNgaySinh.getText().trim());
        nv.setSoDienThoai(txtSoDienThoai.getText().trim());
        nv.setTinh(txtTinh.getText().trim());
        nv.setDiaChi(txtDiaChi.getText().trim());

        if (nhanVienDAO.updateSelfInfo(nv)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công.");
            loadCurrentUser();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại.");
        }
    }

    private void changePassword() {
        NhanVien current = AppSession.getCurrentUser();
        if (current == null) return;

        JPasswordField txtOld = new JPasswordField();
        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mật khẩu cũ"), gbc);
        gbc.gridx = 1;
        panel.add(txtOld, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu mới"), gbc);
        gbc.gridx = 1;
        panel.add(txtNew, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Xác nhận mật khẩu"), gbc);
        gbc.gridx = 1;
        panel.add(txtConfirm, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Đổi mật khẩu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String oldPass = new String(txtOld.getPassword()).trim();
        String newPass = new String(txtNew.getPassword()).trim();
        String confirmPass = new String(txtConfirm.getPassword()).trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu mới không khớp.");
            return;
        }

        if (nhanVienDAO.changePassword(current.getMaNhanVien(), oldPass, newPass)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công.");
            loadCurrentUser();
        } else {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại. Kiểm tra lại mật khẩu cũ.");
        }
    }
}