package view;

import entity.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(204, 255, 204));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblLogo = new JLabel("GREEN MART");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
        lblLogo.setForeground(new Color(0, 102, 51));
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("<html><center>" + user.getHoTen() + "<br/>(" + user.getChucVu() + ")</center></html>");
        lblUser.setAlignmentX(CENTER_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(lblUser);
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(createMenuButton("Trang chủ", "HOME"));
        sidebar.add(createMenuButton("Sản phẩm", "SANPHAM"));
        sidebar.add(createMenuButton("Nhân viên", "NHANVIEN"));
        sidebar.add(createMenuButton("Khách hàng", "KHACHHANG"));
        sidebar.add(createMenuButton("Nhà cung cấp", "NHACUNGCAP"));
        sidebar.add(createMenuButton("Hóa đơn", "HOADON"));
        sidebar.add(createMenuButton("Phiếu nhập", "PHIEUNHAP"));
        sidebar.add(createMenuButton("Khuyến mãi", "KHUYENMAI"));
        sidebar.add(createMenuButton("Thống kê", "THONGKE"));

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(255, 102, 102));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setAlignmentX(CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> logout());

        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(new Color(144, 238, 144));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 70));
        header.setBackground(new Color(34, 139, 34));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG THỰC PHẨM");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        header.add(lblTitle, BorderLayout.WEST);
        return header;
    }

    private JPanel createContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(new SanPhamPanel(), "SANPHAM");
        contentPanel.add(new NhanVienPanel(), "NHANVIEN");
        contentPanel.add(new KhachHangPanel(), "KHACHHANG");
        contentPanel.add(new NhaCungCapPanel(), "NHACUNGCAP");
        contentPanel.add(new HoaDonPanel(), "HOADON");
        contentPanel.add(new PhieuNhapPanel(), "PHIEUNHAP");
        contentPanel.add(new KhuyenMaiPanel(), "KHUYENMAI");
        contentPanel.add(new ThongKePanel(), "THONGKE");

        return contentPanel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(
                "<html><center><h1>Chào mừng đến với GreenMart</h1>"
                + "<p>Hệ thống quản lý cửa hàng thực phẩm</p>"
                + "<p>Đăng nhập thành công với tài khoản: <b>" + user.getMaNV() + "</b></p>"
                + "</center></html>",
                JLabel.CENTER
        );

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(title + " - đang triển khai", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
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
}
