package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SidebarMenu extends JPanel {

    private final String role;
    private final String displayName;
    private final Consumer<String> menuHandler;
    private final Map<String, MenuItemPanel> menuItems = new LinkedHashMap<>();

    private static final Color SIDEBAR_BG = new Color(145, 233, 140);
    private static final Color HEADER_BG = new Color(0, 132, 60);
    private static final Color ITEM_BG = new Color(145, 233, 140);
    private static final Color ITEM_HOVER = new Color(137, 228, 132);
    private static final Color ITEM_ACTIVE = new Color(137, 228, 132);
    private static final Color ITEM_BORDER = new Color(104, 207, 99);
    private static final Color OUTER_BORDER = new Color(203, 235, 81);
    private static final Color TEXT_COLOR = new Color(41, 74, 45);
    private static final Color LOGOUT_BG = new Color(255, 102, 102);
    private static final Color LOGOUT_BORDER = new Color(224, 80, 80);

    public SidebarMenu(String role, String displayName, Consumer<String> menuHandler) {
        this.role = role;
        this.displayName = displayName;
        this.menuHandler = menuHandler;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(215, 0));
        setBackground(SIDEBAR_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OUTER_BORDER, 2),
                BorderFactory.createLineBorder(ITEM_BORDER, 1)
        ));

        initUI();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(215, 58));
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ITEM_BORDER));

        JPanel logoWrap = new JPanel(new BorderLayout());
        logoWrap.setOpaque(false);
        logoWrap.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 6));
        logoWrap.setPreferredSize(new Dimension(60, 58));

        JLabel logo = new JLabel("✥", SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(new Color(175, 255, 250));
        logo.setForeground(Color.BLACK);
        logo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        logo.setBorder(BorderFactory.createLineBorder(new Color(108, 218, 205), 1));
        logoWrap.add(logo, BorderLayout.CENTER);

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        textWrap.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 6));

        JLabel lblName = new JLabel(displayName);
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblRole = new JLabel(role);
        lblRole.setForeground(Color.WHITE);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRole.setFont(new Font("Arial", Font.BOLD, 11));

        textWrap.add(lblName);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(lblRole);

        header.add(logoWrap, BorderLayout.WEST);
        header.add(textWrap, BorderLayout.CENTER);

        return header;
    }

    private JPanel createMenuPanel() {
        String[] items = ("QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))
                ? new String[]{
                    "Trang chủ", "Sản phẩm", "Nhân viên", "Khách hàng",
                    "Nhà cung cấp", "Hóa đơn", "Nhập hàng", "Khuyến mãi", "Thống kê"
                }
                : new String[]{
                    "Trang chủ", "Hóa đơn", "Nhập hàng", "Khách hàng", "Thông tin", "Lịch sử"
                };

        JPanel menuPanel = new JPanel(new GridLayout(items.length, 1, 0, 1));
        menuPanel.setBackground(SIDEBAR_BG);

        for (String item : items) {
            MenuItemPanel menuItem = new MenuItemPanel(item);
            menuItems.put(item, menuItem);
            menuPanel.add(menuItem);
        }

        return menuPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(SIDEBAR_BG);
        bottom.setPreferredSize(new Dimension(215, 60));
        bottom.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setBackground(LOGOUT_BG);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LOGOUT_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> menuHandler.accept("Đăng Xuất"));

        bottom.add(btnLogout, BorderLayout.CENTER);
        return bottom;
    }

    public void setActiveButton(String text) {
        for (Map.Entry<String, MenuItemPanel> entry : menuItems.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(text));
        }
    }

    private class MenuItemPanel extends JPanel {

        private final String menuText;
        private final JLabel lblIcon;
        private final JLabel lblText;
        private boolean active = false;

        public MenuItemPanel(String menuText) {
            this.menuText = menuText;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(215, 58));
            setBackground(ITEM_BG);
            setBorder(BorderFactory.createLineBorder(ITEM_BORDER, 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            lblIcon = new JLabel(getIconText(menuText), SwingConstants.CENTER);
            lblIcon.setPreferredSize(new Dimension(38, 58));
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            lblIcon.setForeground(getIconColor(menuText));

            lblText = new JLabel(menuText);
            lblText.setFont(new Font("Arial", Font.BOLD, 13));
            lblText.setForeground(TEXT_COLOR);
            lblText.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));

            add(lblIcon, BorderLayout.WEST);
            add(lblText, BorderLayout.CENTER);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        setBackground(ITEM_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        setBackground(ITEM_BG);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveButton(menuText);
                    menuHandler.accept(menuText);
                }
            };

            addMouseListener(mouseHandler);
            lblIcon.addMouseListener(mouseHandler);
            lblText.addMouseListener(mouseHandler);
        }

        public void setActive(boolean active) {
            this.active = active;
            setBackground(active ? ITEM_ACTIVE : ITEM_BG);
            setBorder(BorderFactory.createLineBorder(ITEM_BORDER, active ? 2 : 1));
        }

        private String getIconText(String text) {
            switch (text) {
                case "Trang chủ":
                    return "🏠";
                case "Sản phẩm":
                    return "📦";
                case "Nhân viên":
                    return "👥";
                case "Khách hàng":
                    return "👤";
                case "Nhà cung cấp":
                    return "🤝";
                case "Hóa đơn":
                    return "🧾";
                case "Nhập hàng":
                    return "📄";
                case "Khuyến mãi":
                    return "🎁";
                case "Thống kê":
                    return "📊";
                case "Thông tin":
                    return "ℹ";
                case "Thêm khách hàng":
                    return "➕";
                case "Lịch sử":
                    return "🕘";
                default:
                    return "•";
            }
        }

        private Color getIconColor(String text) {
            switch (text) {
                case "Trang chủ":
                    return new Color(255, 102, 102);
                case "Sản phẩm":
                    return new Color(190, 140, 70);
                case "Nhân viên":
                    return new Color(60, 170, 255);
                case "Khách hàng":
                    return new Color(255, 180, 70);
                case "Nhà cung cấp":
                    return new Color(255, 190, 70);
                case "Hóa đơn":
                    return new Color(60, 140, 255);
                case "Nhập hàng":
                    return new Color(120, 140, 255);
                case "Khuyến mãi":
                    return new Color(255, 70, 120);
                case "Thống kê":
                    return new Color(80, 120, 80);
                default:
                    return TEXT_COLOR;
            }
        }
    }
}