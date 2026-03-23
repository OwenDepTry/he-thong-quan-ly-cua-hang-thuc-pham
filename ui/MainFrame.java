package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    private final String role;
    private final String displayName;

    private SidebarMenu sidebarMenu;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(String role, String displayName) {
        this.role = role;
        this.displayName = displayName;

        setTitle("Quản lý cửa hàng thực phẩm - " + displayName);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 700));

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_MAIN);
        setContentPane(root);

        sidebarMenu = new SidebarMenu(role, displayName, this::handleMenuClick);
        root.add(sidebarMenu, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BG_MAIN);

        if (isManager()) {
            contentPanel.add(new HomePanel(role), "Trang chủ");
            contentPanel.add(new SanPhamPanel(), "Sản phẩm");
            contentPanel.add(new NhanVienPanel(), "Nhân viên");
            contentPanel.add(new KhachHangPanel(), "Khách hàng");
            contentPanel.add(new NhaCungCapPanel(), "Nhà cung cấp");
            contentPanel.add(new HoaDonPanel(), "Hóa đơn");
            contentPanel.add(new PhieuNhapPanel(), "Nhập hàng");
            contentPanel.add(new KhuyenMaiPanel(), "Khuyến mãi");
            contentPanel.add(new ThongKePanel(), "Thống kê");
        } else {
            contentPanel.add(new HomePanel(role), "Trang chủ");
            contentPanel.add(new HoaDonPanel(), "Hóa đơn");
            contentPanel.add(new PhieuNhapPanel(), "Nhập hàng");
            contentPanel.add(new KhachHangPanel(), "Khách hàng");
            contentPanel.add(new TaiKhoanNhanVienPanel(), "Thông tin");
            contentPanel.add(new LichSuNhanVienPanel(), "Lịch sử");
        }

        root.add(contentPanel, BorderLayout.CENTER);

        sidebarMenu.setActiveButton("Trang chủ");
        cardLayout.show(contentPanel, "Trang chủ");
    }

    private boolean isManager() {
        return "QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private void handleMenuClick(String menuName) {
        if ("Đăng Xuất".equals(menuName)) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn đăng xuất không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                AppSession.clear();
                new LoginFrame().setVisible(true);
                dispose();
            }
            return;
        }

        cardLayout.show(contentPanel, menuName);
    }
}