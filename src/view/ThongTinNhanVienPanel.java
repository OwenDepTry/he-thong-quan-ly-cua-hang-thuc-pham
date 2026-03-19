package view;

import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ThongTinNhanVienPanel extends JPanel {

    public ThongTinNhanVienPanel(User user) {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("THÔNG TIN NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(46, 139, 87));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 12, 12));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        addRow(infoPanel, "Mã nhân viên:", user != null ? safe(user.getMaNV()) : "");
        addRow(infoPanel, "Họ tên:", user != null ? safe(user.getHoTen()) : "");
        addRow(infoPanel, "Tên đăng nhập:", user != null ? safe(user.getMaNV()) : "");
        addRow(infoPanel, "Vai trò:", getRoleText(user));
        addRow(infoPanel, "Trạng thái:", "Đang hoạt động");
        addRow(infoPanel, "Ghi chú:", "Thông tin tài khoản đang đăng nhập");

        add(infoPanel, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, String label, String value) {
        JLabel lbl1 = new JLabel(label);
        lbl1.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lbl2 = new JLabel(value);
        lbl2.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panel.add(lbl1);
        panel.add(lbl2);
    }

    private String getRoleText(User user) {
        if (user == null) return "";
        try {
            if (user.isAdmin()) return "Quản trị viên";
        } catch (Exception e) {
            // bỏ qua
        }
        return "Nhân viên";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}