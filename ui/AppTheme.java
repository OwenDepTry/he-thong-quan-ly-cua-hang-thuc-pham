package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;

/**
 * Bảng màu, font và cấu hình Look & Feel dùng chung cho toàn bộ ứng dụng.
 * Mọi màu/font trong giao diện nên lấy từ đây thay vì viết new Color(...) trực tiếp.
 */
public class AppTheme {

    private AppTheme() {
    }

    // ===== Bảng màu chính =====
    public static final Color PRIMARY = new Color(22, 163, 74);        // xanh lá chủ đạo
    public static final Color PRIMARY_HOVER = new Color(21, 128, 61);
    public static final Color PRIMARY_SOFT = new Color(220, 252, 231); // nền nhạt (chọn dòng, badge)
    public static final Color PRIMARY_TEXT = new Color(20, 83, 45);

    public static final Color SIDEBAR_BG = new Color(20, 61, 38);
    public static final Color SIDEBAR_HOVER = new Color(30, 82, 52);
    public static final Color SIDEBAR_ACTIVE = new Color(39, 104, 66);
    public static final Color SIDEBAR_TEXT = new Color(209, 231, 216);
    public static final Color SIDEBAR_MUTED = new Color(134, 175, 148);
    public static final Color ACCENT = new Color(163, 230, 53);         // vạch nhấn mục đang chọn

    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color DANGER_SOFT = new Color(254, 226, 226);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color INFO = new Color(37, 99, 235);

    // ===== Màu trung tính =====
    public static final Color BG_MAIN = new Color(243, 246, 244);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(249, 250, 251);   // dòng xen kẽ trong bảng
    public static final Color BORDER = new Color(223, 229, 225);
    public static final Color BORDER_STRONG = new Color(196, 205, 199);
    public static final Color TEXT_DARK = new Color(31, 41, 55);
    public static final Color TEXT_MUTED = new Color(107, 114, 128);
    public static final Color WHITE = Color.WHITE;

    // ===== Tên cũ – giữ lại để code cũ vẫn biên dịch, đã trỏ về bảng màu mới =====
    public static final Color PANEL_WHITE = SURFACE;
    public static final Color GREEN_TOP = PRIMARY;
    public static final Color GREEN_TOP_DARK = PRIMARY_HOVER;
    public static final Color GREEN_SIDEBAR = SIDEBAR_BG;
    public static final Color GREEN_SIDEBAR_ACTIVE = SIDEBAR_ACTIVE;
    public static final Color GREEN_SIDEBAR_TOP = SIDEBAR_BG;
    public static final Color GREEN_BORDER = BORDER;
    public static final Color GREEN_FORM = BG_MAIN;
    public static final Color GREEN_SOFT = PRIMARY_SOFT;
    public static final Color RED_LOGOUT = DANGER;
    public static final Color PINK_LOGOUT = DANGER_SOFT;
    public static final Color RED_ACTION = DANGER;
    public static final Color BLUE_ACTION = INFO;
    public static final Color CYAN_ACTION = INFO;
    public static final Color ORANGE_ACTION = WARNING;
    public static final Color WHITE_ACTION = SURFACE_ALT;
    public static final Color TABLE_HEADER = SURFACE_ALT;
    public static final Color TABLE_GRID = new Color(238, 241, 239);
    public static final Color YELLOW_TEXT = ACCENT;

    // ===== Font =====
    /** Họ font dùng cho toàn app, chọn tự động font đẹp nhất có trên máy (đều hỗ trợ tiếng Việt). */
    public static final String FONT_FAMILY = resolveFontFamily();

    public static final Font FONT_TITLE = font(Font.BOLD, 26);
    public static final Font FONT_HEADER = font(Font.BOLD, 18);
    public static final Font FONT_SUB = font(Font.BOLD, 14);
    public static final Font FONT_NORMAL = font(Font.PLAIN, 13);
    public static final Font FONT_SMALL = font(Font.PLAIN, 12);
    public static final Font FONT_BOLD_SMALL = font(Font.BOLD, 12);
    public static final Font FONT_TINY = font(Font.PLAIN, 11);
    public static final Font FONT_BOLD = font(Font.BOLD, 14);

    public static Font font(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static Border line(Color c) {
        return BorderFactory.createLineBorder(c, 1);
    }

    public static Border empty(int t, int l, int b, int r) {
        return BorderFactory.createEmptyBorder(t, l, b, r);
    }

    /** Viền "thẻ": viền mảnh + padding bên trong. */
    public static Border card(int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    private static String resolveFontFamily() {
        // Cho phép nhúng font riêng: đặt file vào src/fonts/BeVietnamPro-Regular.ttf và -Bold.ttf
        String bundled = registerBundledFont("/fonts/BeVietnamPro-Regular.ttf", "/fonts/BeVietnamPro-Bold.ttf");
        if (bundled != null) {
            return bundled;
        }
        Set<String> installed = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidate : new String[]{"Be Vietnam Pro", "Segoe UI", "Inter", "Roboto",
            "Noto Sans", "SF Pro Text", "Helvetica Neue", "Tahoma"}) {
            if (installed.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }

    private static String registerBundledFont(String... paths) {
        String family = null;
        for (String path : paths) {
            try (InputStream in = AppTheme.class.getResourceAsStream(path)) {
                if (in == null) {
                    continue;
                }
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                if (family == null) {
                    family = f.getFamily();
                }
            } catch (Exception ignored) {
                // không có font nhúng thì dùng font hệ thống
            }
        }
        return family;
    }

    /** Nhấn Esc trong bất kỳ hộp thoại nào (Thêm/Sửa/Chi tiết…) để đóng nó, giống nút Hủy. */
    private static void installEscapeClosesDialogs() {
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            java.awt.event.KeyEvent ke = (java.awt.event.KeyEvent) event;
            if (ke.getID() != java.awt.event.KeyEvent.KEY_PRESSED
                    || ke.getKeyCode() != java.awt.event.KeyEvent.VK_ESCAPE || ke.isConsumed()) {
                return;
            }
            java.awt.Window w = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            if (w instanceof javax.swing.JDialog && !(((javax.swing.JDialog) w).getContentPane()
                    instanceof javax.swing.JOptionPane)) {
                // Esc đang dùng để đóng popup combobox thì không đóng dialog
                if (ke.getSource() instanceof javax.swing.JComboBox
                        && ((javax.swing.JComboBox<?>) ke.getSource()).isPopupVisible()) {
                    return;
                }
                ke.consume();
                w.dispatchEvent(new java.awt.event.WindowEvent(w, java.awt.event.WindowEvent.WINDOW_CLOSING));
            }
        }, java.awt.AWTEvent.KEY_EVENT_MASK);
    }

    /**
     * Áp cài đặt làm mịn chữ của hệ điều hành (ClearType trên Windows) cho phần tự vẽ chữ bằng Graphics2D,
     * để chữ vẽ tay sắc nét giống chữ của các component Swing.
     */
    public static void applyTextHints(java.awt.Graphics2D g2) {
        Object hints = java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (hints instanceof java.util.Map) {
            g2.addRenderingHints((java.util.Map<?, ?>) hints);
        } else {
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS,
                java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    public static boolean isFlatLaf() {
        return UIManager.getLookAndFeel() != null
                && UIManager.getLookAndFeel().getClass().getName().startsWith("com.formdev.flatlaf");
    }

    /**
     * Ô nhập liệu: với FlatLaf thì để FlatLaf tự vẽ (bo góc, viền focus),
     * còn không thì thêm viền mảnh + padding cho dễ nhìn.
     */
    public static void styleField(javax.swing.JComponent field, String placeholder) {
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("JTextField.showClearButton", Boolean.TRUE);
        if (!isFlatLaf()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_STRONG, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }
    }

    /**
     * Cài Look & Feel. Nếu có flatlaf-x.y.jar trong thư mục lib/ thì dùng FlatLaf (đẹp, hiện đại);
     * không có thì vẫn chạy được với giao diện mặc định. Gọi trước khi tạo bất kỳ cửa sổ nào.
     */
    public static void installLookAndFeel() {
        // Chỉ ép chế độ làm mịn chữ trên Linux. Trên Windows/macOS để Java dùng đúng cài đặt của hệ điều hành
        // (ClearType trên Windows) – ép "on" sẽ tắt ClearType và làm chữ bị mờ.
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux") && System.getProperty("awt.useSystemAAFontSettings") == null) {
            System.setProperty("awt.useSystemAAFontSettings", "lcd");
        }

        installEscapeClosesDialogs();

        boolean flat = false;
        try {
            Class.forName("com.formdev.flatlaf.FlatLightLaf").getMethod("setup").invoke(null);
            flat = true;
        } catch (Throwable ignored) {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
        }

        // Font mặc định cho mọi component
        FontUIResource base = new FontUIResource(font(Font.PLAIN, 13));
        for (Object key : UIManager.getLookAndFeelDefaults().keySet().toArray()) {
            if (key != null && key.toString().endsWith(".font")) {
                UIManager.put(key, base);
            }
        }
        UIManager.put("defaultFont", base);

        // Màu chung (cả FlatLaf lẫn L&F mặc định đều đọc)
        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("Table.selectionBackground", PRIMARY_SOFT);
        UIManager.put("Table.selectionForeground", TEXT_DARK);
        UIManager.put("Table.gridColor", TABLE_GRID);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("ToolTip.background", TEXT_DARK);
        UIManager.put("ToolTip.foreground", WHITE);

        if (flat) {
            UIManager.put("Component.accentColor", PRIMARY);
            UIManager.put("Component.focusColor", PRIMARY);
            UIManager.put("Component.focusedBorderColor", PRIMARY);
            UIManager.put("Component.borderColor", BORDER_STRONG);
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("CheckBox.arc", 4);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Button.default.background", PRIMARY);
            UIManager.put("Button.default.foreground", WHITE);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
            UIManager.put("ScrollBar.track", SURFACE);
            UIManager.put("TabbedPane.selectedBackground", SURFACE);
            UIManager.put("TabbedPane.underlineColor", PRIMARY);
            UIManager.put("TabbedPane.inactiveUnderlineColor", PRIMARY);
            UIManager.put("TabbedPane.tabHeight", 38);
            UIManager.put("TabbedPane.showTabSeparators", false);
            UIManager.put("TableHeader.background", SURFACE_ALT);
            UIManager.put("TableHeader.separatorColor", BORDER);
            UIManager.put("TableHeader.bottomSeparatorColor", BORDER);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
            UIManager.put("TitlePane.unifiedBackground", true);
            UIManager.put("OptionPane.buttonMinimumWidth", 90);
        }
    }
}
