package view;

import entity.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private final User user;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(User user) {
        this.user = user;

        setTitle("Hệ thống quản lý cửa hàng thực phẩm");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
    }

    private boolean isAdmin() {
        return user != null && user.isAdmin();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(27, 94, 32));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JLabel lblLogo = new JLabel("GREEN MART");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(isAdmin() ? "QUẢN TRỊ VIÊN" : "NHÂN VIÊN");
        lblRole.setFont(new Font("Arial", Font.BOLD, 13));
        lblRole.setForeground(new Color(220, 255, 220));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel(
                "<html><center>" + safe(user.getHoTen()) + "<br>Mã NV: " + safe(user.getMaNV()) + "</center></html>"
        );
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(lblRole);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(lblUser);
        sidebar.add(Box.createVerticalStrut(22));

        sidebar.add(createMenuButton("Trang chủ", "HOME"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Thông tin NV", "THONGTINNV"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Hàng hóa", "HANGHOA"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Sản phẩm", "SANPHAM"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Khách hàng", "KHACHHANG"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Hóa đơn", "HOADON"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Lịch sử HĐ", "LICHSUHOADON"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Phiếu nhập", "PHIEUNHAP"));
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(createMenuButton("Lịch sử PN", "LICHSUPHIEUNHAP"));
        sidebar.add(Box.createVerticalStrut(8));

        if (isAdmin()) {
            sidebar.add(createMenuButton("Nhân viên", "NHANVIEN"));
            sidebar.add(Box.createVerticalStrut(8));

            sidebar.add(createMenuButton("Nhà cung cấp", "NHACUNGCAP"));
            sidebar.add(Box.createVerticalStrut(8));

            sidebar.add(createMenuButton("Khuyến mãi", "KHUYENMAI"));
            sidebar.add(Box.createVerticalStrut(8));

            sidebar.add(createMenuButton("Thống kê", "THONGKE"));
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(46, 125, 50));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(56, 142, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(46, 125, 50));
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 75));
        header.setBackground(new Color(46, 125, 50));
        header.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG THỰC PHẨM");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblHello = new JLabel("Xin chào, " + safe(user.getHoTen()));
        lblHello.setForeground(new Color(230, 255, 230));
        lblHello.setFont(new Font("Arial", Font.PLAIN, 15));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblHello, BorderLayout.EAST);

        return header;
    }

    private JPanel createContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(new ThongTinNhanVienPanel(user), "THONGTINNV");
        contentPanel.add(new HangHoaPanel(), "HANGHOA");
        contentPanel.add(new SanPhamPanel(), "SANPHAM");
        contentPanel.add(new KhachHangPanel(), "KHACHHANG");
        contentPanel.add(new HoaDonPanel(), "HOADON");
        contentPanel.add(new LichSuHoaDonPanel(), "LICHSUHOADON");
        contentPanel.add(new PhieuNhapPanel(), "PHIEUNHAP");
        contentPanel.add(new LichSuPhieuNhapPanel(), "LICHSUPHIEUNHAP");

        if (isAdmin()) {
            contentPanel.add(new NhanVienPanel(), "NHANVIEN");
            contentPanel.add(new NhaCungCapPanel(), "NHACUNGCAP");
            contentPanel.add(new KhuyenMaiPanel(), "KHUYENMAI");
            contentPanel.add(new ThongKePanel(), "THONGKE");
        }

        return contentPanel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(76, 175, 80));
        banner.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        banner.setPreferredSize(new Dimension(200, 180));

        String vaiTro = isAdmin() ? "Quản trị viên" : "Nhân viên";

        JLabel lblBanner = new JLabel(
                "<html>"
                + "<div style='color:white;'>"
                + "<h1>Chào mừng đến với GreenMart</h1>"
                + "<p style='font-size:14px;'>Quản lý bán hàng, nhập hàng và thống kê cửa hàng thực phẩm.</p>"
                + "<p style='font-size:14px;'>Tài khoản đăng nhập: <b>" + safe(user.getMaNV()) + "</b></p>"
                + "<p style='font-size:14px;'>Vai trò: <b>" + vaiTro + "</b></p>"
                + "</div>"
                + "</html>"
        );
        banner.add(lblBanner, BorderLayout.WEST);

        JLabel lblIcon = new JLabel("🥬 🥕 🧾 📊");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        banner.add(lblIcon, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(2, 2, 15, 15));
        cards.setBackground(new Color(245, 247, 250));

        cards.add(createHomeCard("Sản phẩm", "Quản lý thông tin sản phẩm và số lượng tồn kho"));
        cards.add(createHomeCard("Hóa đơn", "Lập hóa đơn bán hàng nhanh và chính xác"));
        cards.add(createHomeCard("Phiếu nhập", "Theo dõi nhập hàng và cập nhật kho"));
        cards.add(createHomeCard("Thống kê", "Xem doanh thu, tồn kho và hiệu quả bán hàng"));

        panel.add(banner, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHomeCard(String title, String desc) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(46, 125, 50));

        JLabel lblDesc = new JLabel("<html><div style='font-size:13px; color:#444444;'>" + desc + "</div></html>");

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);

        return card;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}