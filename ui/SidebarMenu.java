package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Thanh menu bên trái: nền xanh đậm, mục đang chọn có nền sáng hơn + vạch nhấn bên trái.
 */
public class SidebarMenu extends JPanel {

    private static final int WIDTH = 230;
    private static final int ITEM_HEIGHT = 44;

    private final String role;
    private final String displayName;
    private final Consumer<String> menuHandler;
    private final Map<String, MenuItem> menuItems = new LinkedHashMap<>();

    public SidebarMenu(String role, String displayName, Consumer<String> menuHandler) {
        this.role = role;
        this.displayName = displayName;
        this.menuHandler = menuHandler;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(AppTheme.SIDEBAR_BG);

        add(createHeader(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private boolean isManager() {
        return "QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.SIDEBAR_HOVER),
                BorderFactory.createEmptyBorder(18, 16, 18, 16)));

        JLabel logo = new JLabel(AppIcons.get("basket", 22, AppTheme.SIDEBAR_BG), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logo.setPreferredSize(new Dimension(40, 40));
        header.add(logo, BorderLayout.WEST);

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(displayName);
        lblName.setForeground(AppTheme.WHITE);
        lblName.setFont(AppTheme.font(Font.BOLD, 14));

        JLabel lblRole = new JLabel(isManager() ? "Quản lý" : "Nhân viên bán hàng");
        lblRole.setForeground(AppTheme.SIDEBAR_MUTED);
        lblRole.setFont(AppTheme.font(Font.PLAIN, 12));

        textWrap.add(Box.createVerticalGlue());
        textWrap.add(lblName);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(lblRole);
        textWrap.add(Box.createVerticalGlue());
        header.add(textWrap, BorderLayout.CENTER);

        return header;
    }

    private JPanel createMenuPanel() {
        String[] items = isManager()
                ? new String[]{
                    "Trang chủ", "Sản phẩm", "Nhân viên", "Khách hàng",
                    "Nhà cung cấp", "Hóa đơn", "Nhập hàng", "Khuyến mãi", "Thống kê"
                }
                : new String[]{
                    "Trang chủ", "Hóa đơn", "Nhập hàng", "Khách hàng", "Thông tin", "Lịch sử"
                };

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        JLabel section = new JLabel("MENU");
        section.setFont(AppTheme.font(Font.BOLD, 11));
        section.setForeground(AppTheme.SIDEBAR_MUTED);
        section.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 0));
        section.setAlignmentX(LEFT_ALIGNMENT);
        menuPanel.add(section);

        for (String item : items) {
            MenuItem menuItem = new MenuItem(item, false);
            menuItem.setAlignmentX(LEFT_ALIGNMENT);
            menuItems.put(item, menuItem);
            menuPanel.add(menuItem);
            menuPanel.add(Box.createVerticalStrut(2));
        }
        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.SIDEBAR_HOVER),
                BorderFactory.createEmptyBorder(10, 10, 12, 10)));
        bottom.add(new MenuItem("Đăng Xuất", true), BorderLayout.CENTER);
        return bottom;
    }

    public void setActiveButton(String text) {
        for (Map.Entry<String, MenuItem> entry : menuItems.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(text));
        }
    }

    private class MenuItem extends JPanel {

        private final boolean logout;
        private final String iconName;
        private boolean active;
        private boolean hover;

        MenuItem(String menuText, boolean logout) {
            this.logout = logout;
            this.iconName = AppIcons.nameFor(menuText);
            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_HEIGHT));
            setPreferredSize(new Dimension(WIDTH - 20, ITEM_HEIGHT));

            Color fg = logout ? new Color(252, 165, 165) : AppTheme.SIDEBAR_TEXT;
            JLabel lbl = new JLabel(logout ? "Đăng xuất" : menuText,
                    AppIcons.get(AppIcons.nameFor(menuText), 18, fg), SwingConstants.LEFT);
            lbl.setIconTextGap(12);
            lbl.setForeground(fg);
            lbl.setFont(AppTheme.font(Font.PLAIN, 14));
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
            add(lbl, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!logout) {
                        setActiveButton(menuText);
                    }
                    menuHandler.accept(menuText);
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            JLabel lbl = (JLabel) getComponent(0);
            lbl.setFont(AppTheme.font(active ? Font.BOLD : Font.PLAIN, 14));
            Color fg = active ? AppTheme.WHITE : AppTheme.SIDEBAR_TEXT;
            lbl.setForeground(fg);
            lbl.setIcon(AppIcons.get(iconName, 18, active ? AppTheme.ACCENT : fg));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(AppTheme.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(AppTheme.ACCENT);
                g2.fillRoundRect(0, 10, 4, getHeight() - 20, 4, 4);
            } else if (hover) {
                g2.setColor(logout ? new Color(127, 29, 29, 120) : AppTheme.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
