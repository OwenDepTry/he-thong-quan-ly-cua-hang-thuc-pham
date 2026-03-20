package view;

import entity.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ThongTinNhanVienPanel extends JPanel {

    private final User user;

    public ThongTinNhanVienPanel(User user) {
        this.user = user;

        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 125, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel("THÔNG TIN NHÂN VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Thông tin tài khoản đang đăng nhập vào hệ thống");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(235, 255, 235));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(lblTitle);
        text.add(lblSub);

        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);

        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setBackground(new Color(102, 187, 106));
        topBanner.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        topBanner.setPreferredSize(new Dimension(200, 150));

        String hoTen = user != null ? safe(user.getHoTen()) : "";
        String maNV = user != null ? safe(user.getMaNV()) : "";
        String chucVu = getRoleText(user);
        String trangThai = user != null ? safe(user.getTrangThai()) : "Đang hoạt động";

        JLabel lblBannerText = new JLabel(
                "<html>"
                + "<div style='color:white;'>"
                + "<h1 style='margin:0 0 8px 0;'>HỒ SƠ NHÂN VIÊN</h1>"
                + "<p style='font-size:14px;'>Theo dõi thông tin cá nhân và quyền hạn sử dụng trong hệ thống.</p>"
                + "<p style='font-size:14px;'>Mã nhân viên: <b>" + maNV + "</b></p>"
                + "<p style='font-size:14px;'>Họ tên: <b>" + hoTen + "</b></p>"
                + "</div>"
                + "</html>"
        );
        lblBannerText.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblIcon = new JLabel("👤");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        topBanner.add(lblBannerText, BorderLayout.CENTER);
        topBanner.add(lblIcon, BorderLayout.EAST);

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 14, 14));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        addInfoRow(infoPanel, "Mã nhân viên:", maNV);
        addInfoRow(infoPanel, "Họ tên:", hoTen);
        addInfoRow(infoPanel, "Tên đăng nhập:", maNV);
        addInfoRow(infoPanel, "Chức vụ:", chucVu);
        addInfoRow(infoPanel, "Trạng thái:", trangThai);
        addInfoRow(infoPanel, "Ghi chú:", "Tài khoản đang hoạt động trong hệ thống");

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblNoteTitle = new JLabel("Mô tả quyền truy cập");
        lblNoteTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblNoteTitle.setForeground(new Color(46, 125, 50));

        JLabel lblNote = new JLabel(
                "<html><div style='font-size:14px; line-height:160%; color:#444444;'>"
                + ("Quản trị viên".equals(chucVu)
                    ? "Tài khoản này có quyền quản lý sản phẩm, nhân viên, khách hàng, nhà cung cấp, hóa đơn, phiếu nhập, khuyến mãi và thống kê."
                    : "Tài khoản này có quyền thao tác trên các chức năng nghiệp vụ được phân công như xem thông tin cá nhân, lập hóa đơn, nhập hàng và theo dõi lịch sử.")
                + "</div></html>"
        );

        notePanel.add(lblNoteTitle, BorderLayout.NORTH);
        notePanel.add(lblNote, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);
        center.add(infoPanel, BorderLayout.CENTER);
        center.add(notePanel, BorderLayout.SOUTH);

        main.add(topBanner, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        return main;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 15));
        lblValue.setOpaque(true);
        lblValue.setBackground(new Color(248, 251, 248));
        lblValue.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 220), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panel.add(lblLabel);
        panel.add(lblValue);
    }

    private String getRoleText(User user) {
        if (user == null) {
            return "";
        }
        return user.isAdmin() ? "Quản trị viên" : "Nhân viên";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}