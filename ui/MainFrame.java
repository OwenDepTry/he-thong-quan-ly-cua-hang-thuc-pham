package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    private final String role;
    private final String displayName;

    private SidebarMenu sidebarMenu;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    /** Màn hình chỉ được tạo (và truy vấn CSDL) khi người dùng mở lần đầu → đăng nhập nhanh hơn nhiều. */
    private final Map<String, Supplier<JPanel>> screenFactories = new LinkedHashMap<>();
    private final Set<String> createdScreens = new HashSet<>();
    private final Map<String, JPanel> screens = new java.util.HashMap<>();

    public MainFrame(String role, String displayName) {
        this.role = role;
        this.displayName = displayName;

        setTitle("Quản lý cửa hàng thực phẩm - " + displayName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        fitToScreen();

        initUI();
    }

    /** Chiếm ~92% vùng làm việc của màn hình (trừ taskbar), không vượt quá 1440x900. */
    private void fitToScreen() {
        Rectangle work = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int w = Math.min(1440, (int) (work.width * 0.92));
        int h = Math.min(900, (int) (work.height * 0.92));
        setSize(Math.max(w, 1100), Math.max(h, 680));
        setLocationRelativeTo(null);
        if (work.width <= 1366) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
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
            screenFactories.put("Trang chủ", () -> new HomePanel(role, displayName, this::navigate));
            screenFactories.put("Sản phẩm", SanPhamPanel::new);
            screenFactories.put("Nhân viên", NhanVienPanel::new);
            screenFactories.put("Khách hàng", KhachHangPanel::new);
            screenFactories.put("Nhà cung cấp", NhaCungCapPanel::new);
            screenFactories.put("Hóa đơn", HoaDonPanel::new);
            screenFactories.put("Nhập hàng", PhieuNhapPanel::new);
            screenFactories.put("Khuyến mãi", KhuyenMaiPanel::new);
            screenFactories.put("Thống kê", ThongKePanel::new);
        } else {
            screenFactories.put("Trang chủ", () -> new HomePanel(role, displayName, this::navigate));
            screenFactories.put("Hóa đơn", HoaDonPanel::new);
            screenFactories.put("Nhập hàng", PhieuNhapPanel::new);
            screenFactories.put("Khách hàng", KhachHangPanel::new);
            screenFactories.put("Thông tin", TaiKhoanNhanVienPanel::new);
            screenFactories.put("Lịch sử", LichSuNhanVienPanel::new);
        }

        root.add(contentPanel, BorderLayout.CENTER);

        sidebarMenu.setActiveButton("Trang chủ");
        showScreen("Trang chủ");
    }

    private void showScreen(String name) {
        if (createdScreens.contains(name)) {
            cardLayout.show(contentPanel, name);
            currentScreen = name;
            JPanel shown = screens.get(name);
            if (shown instanceof Refreshable) {
                ((Refreshable) shown).refresh();
            }
            return;
        }
        Supplier<JPanel> factory = screenFactories.get(name);
        if (factory == null) {
            return;
        }
        // Màn hình cũ truy vấn CSDL ngay trong constructor (bắt buộc chạy trên luồng giao diện),
        // nên hiện "Đang tải…" trước để cửa sổ không trông như bị treo.
        AppAsync.withOverlay(this, "Đang mở " + name + "…", () -> {
            JPanel screen = factory.get();
            screens.put(name, screen);
            contentPanel.add(screen, name);
            createdScreens.add(name);
            cardLayout.show(contentPanel, name);
            currentScreen = name;
        });
    }

    /** Điều hướng từ trong một màn hình (ví dụ ô truy cập nhanh ở Trang chủ). */
    private void navigate(String name) {
        sidebarMenu.setActiveButton(name);
        showScreen(name);
    }

    private boolean isManager() {
        return "QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private String currentScreen = "Trang chủ";

    /** Đổi sáng/tối: lưu lựa chọn, dựng lại cửa sổ ở đúng vị trí và màn hình đang xem. */
    private void toggleTheme() {
        AppTheme.setDark(!AppTheme.isDark());
        MainFrame next = new MainFrame(role, displayName);
        next.setBounds(getBounds());
        next.setExtendedState(getExtendedState());
        next.setVisible(true);
        if (!"Trang chủ".equals(currentScreen)) {
            next.navigate(currentScreen);
        }
        dispose();
    }

    private void handleMenuClick(String menuName) {
        if (menuName.startsWith("Chế độ")) {
            toggleTheme();
            return;
        }
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

        showScreen(menuName);
    }
}
