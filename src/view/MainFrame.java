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
import java.util.LinkedHashMap;
import java.util.Map;
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
    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

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
        add(createTopContainer(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        showCard("HOME");
    }

    private boolean isAdmin() {
        return user != null && user.isAdmin();
    }

    private JPanel createTopContainer() {
        JPanel top = new JPanel(new BorderLayout());
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(createToolbar(), BorderLayout.SOUTH);
        return top;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(new Color(43, 112, 52));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JLabel lblLogo = new JLabel("GREEN MART");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(isAdmin() ? "QUẢN TRỊ VIÊN" : "NHÂN VIÊN");
        lblRole.setFont(new Font("Arial", Font.BOLD, 13));
        lblRole.setForeground(new Color(224, 255, 224));
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

        addSidebarButton(sidebar, "🏠  Trang chủ", "HOME");
        addSidebarButton(sidebar, "👤  Thông tin NV", "THONGTINNV");
        addSidebarButton(sidebar, "📦  Hàng hóa", "HANGHOA");
        addSidebarButton(sidebar, "🥬  Sản phẩm", "SANPHAM");
        addSidebarButton(sidebar, "🧑  Khách hàng", "KHACHHANG");
        addSidebarButton(sidebar, "🧾  Hóa đơn", "HOADON");
        addSidebarButton(sidebar, "📜  Lịch sử HĐ", "LICHSUHOADON");
        addSidebarButton(sidebar, "📥  Phiếu nhập", "PHIEUNHAP");
        addSidebarButton(sidebar, "🗂  Lịch sử PN", "LICHSUPHIEUNHAP");

        if (isAdmin()) {
            addSidebarButton(sidebar, "👨‍💼  Nhân viên", "NHANVIEN");
            addSidebarButton(sidebar, "🏭  Nhà cung cấp", "NHACUNGCAP");
            addSidebarButton(sidebar, "🎁  Khuyến mãi", "KHUYENMAI");
            addSidebarButton(sidebar, "📊  Thống kê", "THONGKE");
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setBackground(new Color(211, 47, 47));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String text, String cardName) {
        JButton btn = createMenuButton(text, cardName);
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(8));
        menuButtons.put(cardName, btn);
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(67, 142, 74));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> showCard(cardName));
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 72));
        header.setBackground(new Color(56, 142, 60));
        header.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG THỰC PHẨM");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        String roleText = isAdmin() ? "Quản trị viên" : "Nhân viên";
        JLabel lblHello = new JLabel("Xin chào, " + safe(user.getHoTen()) + " - " + roleText);
        lblHello.setForeground(new Color(235, 255, 235));
        lblHello.setFont(new Font("Arial", Font.PLAIN, 15));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblHello, BorderLayout.EAST);

        return header;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new GridLayout(1, isAdmin() ? 4 : 3, 10, 0));
        toolbar.setBackground(new Color(234, 243, 234));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        toolbar.setPreferredSize(new Dimension(0, 58));

        toolbar.add(createQuickButton("Sản phẩm", "SANPHAM"));
        toolbar.add(createQuickButton("Hóa đơn", "HOADON"));
        toolbar.add(createQuickButton("Phiếu nhập", "PHIEUNHAP"));

        if (isAdmin()) {
            toolbar.add(createQuickButton("Thống kê", "THONGKE"));
        }

        return toolbar;
    }

    private JButton createQuickButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(46, 125, 50));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 205, 180), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btn.addActionListener(e -> showCard(cardName));
        return btn;
    }

    private JPanel createContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 248, 245));

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
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(new Color(245, 248, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel banner = new JPanel(new BorderLayout(16, 16));
        banner.setBackground(new Color(102, 187, 106));
        banner.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        banner.setPreferredSize(new Dimension(200, 190));

        String vaiTro = isAdmin() ? "Quản trị viên" : "Nhân viên";

        JLabel lblBanner = new JLabel(
                "<html>"
                + "<div style='color:white;'>"
                + "<h1 style='margin:0 0 10px 0;'>CHÀO MỪNG ĐẾN VỚI GREEN MART</h1>"
                + "<p style='font-size:14px;'>Phần mềm hỗ trợ quản lý bán hàng, nhập hàng, khách hàng và theo dõi hoạt động cửa hàng.</p>"
                + "<p style='font-size:14px;'>Mã nhân viên: <b>" + safe(user.getMaNV()) + "</b></p>"
                + "<p style='font-size:14px;'>Họ tên: <b>" + safe(user.getHoTen()) + "</b></p>"
                + "<p style='font-size:14px;'>Vai trò: <b>" + vaiTro + "</b></p>"
                + "</div>"
                + "</html>"
        );
        lblBanner.setFont(new Font("Arial", Font.PLAIN, 15));

        JLabel lblIcon = new JLabel("🥬  🥕  🍎  🧾  📊");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        banner.add(lblBanner, BorderLayout.CENTER);
        banner.add(lblIcon, BorderLayout.EAST);

        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setBackground(new Color(245, 248, 245));

        cards.add(createHomeCard("Quản lý sản phẩm", "Theo dõi danh sách sản phẩm, giá bán, số lượng và tình trạng hàng hóa."));
        cards.add(createHomeCard("Quản lý hóa đơn", "Lập hóa đơn bán hàng, tính tổng tiền và lưu lịch sử giao dịch."));
        cards.add(createHomeCard("Quản lý phiếu nhập", "Nhập hàng từ nhà cung cấp và cập nhật số lượng tồn kho."));
        cards.add(createHomeCard("Báo cáo - thống kê", "Xem doanh thu, số lượng hóa đơn, tình hình bán hàng và hiệu quả kinh doanh."));

        panel.add(banner, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHomeCard(String title, String desc) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(46, 125, 50));

        JLabel lblDesc = new JLabel("<html><div style='font-size:13px; color:#444444; line-height:150%;'>" + desc + "</div></html>");

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);

        return card;
    }

    private void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        updateMenuState(cardName);
    }

    private void updateMenuState(String selectedCard) {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean selected = entry.getKey().equals(selectedCard);
            JButton btn = entry.getValue();

            if (selected) {
                btn.setBackground(new Color(232, 245, 233));
                btn.setForeground(new Color(27, 94, 32));
            } else {
                btn.setBackground(new Color(67, 142, 74));
                btn.setForeground(Color.WHITE);
            }
        }
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