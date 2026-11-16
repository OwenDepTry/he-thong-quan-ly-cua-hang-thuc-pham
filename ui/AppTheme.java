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

    // ===== Bảng màu =====
    // Không để final: giá trị lấy theo chế độ sáng/tối đã lưu (xem applyPalette). Đổi chế độ cần dựng lại cửa sổ.
    public static Color PRIMARY;          // xanh lá chủ đạo
    public static Color PRIMARY_HOVER;
    public static Color PRIMARY_SOFT;     // nền nhạt (chọn dòng, badge)
    public static Color PRIMARY_TEXT;

    public static Color SIDEBAR_BG;
    public static Color SIDEBAR_HOVER;
    public static Color SIDEBAR_ACTIVE;
    public static Color SIDEBAR_TEXT;
    public static Color SIDEBAR_MUTED;
    public static Color ACCENT;           // vạch nhấn mục đang chọn

    public static Color DANGER;
    public static Color DANGER_SOFT;
    public static Color WARNING;
    public static Color WARNING_SOFT;
    public static Color INFO;

    public static Color BG_MAIN;
    public static Color SURFACE;
    public static Color SURFACE_ALT;      // dòng xen kẽ trong bảng
    public static Color BORDER;
    public static Color BORDER_STRONG;
    public static Color TEXT_DARK;        // chữ chính (sáng ở chế độ tối)
    public static Color TEXT_MUTED;
    public static Color TABLE_GRID;
    public static final Color WHITE = Color.WHITE;

    // ===== Tên cũ – giữ lại để code cũ vẫn biên dịch, trỏ về bảng màu mới =====
    public static Color PANEL_WHITE;
    public static Color GREEN_TOP;
    public static Color GREEN_TOP_DARK;
    public static Color GREEN_SIDEBAR;
    public static Color GREEN_SIDEBAR_ACTIVE;
    public static Color GREEN_SIDEBAR_TOP;
    public static Color GREEN_BORDER;
    public static Color GREEN_FORM;
    public static Color GREEN_SOFT;
    public static Color RED_LOGOUT;
    public static Color PINK_LOGOUT;
    public static Color RED_ACTION;
    public static Color BLUE_ACTION;
    public static Color CYAN_ACTION;
    public static Color ORANGE_ACTION;
    public static Color WHITE_ACTION;
    public static Color TABLE_HEADER;
    public static Color YELLOW_TEXT;

    private static final String PREF_DARK = "darkMode";
    private static boolean dark;

    static {
        boolean saved = false;
        try {
            saved = java.util.prefs.Preferences.userNodeForPackage(AppTheme.class).getBoolean(PREF_DARK, false);
        } catch (Exception ignored) {
            // không đọc được cài đặt: dùng chế độ sáng
        }
        applyPalette(saved);
    }

    public static boolean isDark() {
        return dark;
    }

    /** Đổi chế độ sáng/tối, lưu lại cho lần mở sau và cài lại Look & Feel. Cửa sổ đang mở cần được dựng lại. */
    public static void setDark(boolean value) {
        applyPalette(value);
        try {
            java.util.prefs.Preferences.userNodeForPackage(AppTheme.class).putBoolean(PREF_DARK, value);
        } catch (Exception ignored) {
            // không lưu được cũng không sao
        }
        installLookAndFeel();
    }

    private static void applyPalette(boolean useDark) {
        dark = useDark;
        if (!useDark) {
            PRIMARY = new Color(22, 163, 74);
            PRIMARY_HOVER = new Color(21, 128, 61);
            PRIMARY_SOFT = new Color(220, 252, 231);
            PRIMARY_TEXT = new Color(20, 83, 45);
            SIDEBAR_BG = new Color(20, 61, 38);
            SIDEBAR_HOVER = new Color(30, 82, 52);
            SIDEBAR_ACTIVE = new Color(39, 104, 66);
            SIDEBAR_TEXT = new Color(209, 231, 216);
            SIDEBAR_MUTED = new Color(134, 175, 148);
            ACCENT = new Color(163, 230, 53);
            DANGER = new Color(220, 38, 38);
            DANGER_SOFT = new Color(254, 226, 226);
            WARNING = new Color(217, 119, 6);
            WARNING_SOFT = new Color(254, 243, 199);
            INFO = new Color(37, 99, 235);
            BG_MAIN = new Color(243, 246, 244);
            SURFACE = Color.WHITE;
            SURFACE_ALT = new Color(249, 250, 251);
            BORDER = new Color(223, 229, 225);
            BORDER_STRONG = new Color(196, 205, 199);
            TEXT_DARK = new Color(31, 41, 55);
            TEXT_MUTED = new Color(107, 114, 128);
            TABLE_GRID = new Color(238, 241, 239);
        } else {
            PRIMARY = new Color(34, 197, 94);
            PRIMARY_HOVER = new Color(22, 163, 74);
            PRIMARY_SOFT = new Color(22, 61, 40);
            PRIMARY_TEXT = new Color(187, 247, 208);
            SIDEBAR_BG = new Color(12, 20, 16);
            SIDEBAR_HOVER = new Color(24, 38, 31);
            SIDEBAR_ACTIVE = new Color(30, 54, 41);
            SIDEBAR_TEXT = new Color(196, 214, 203);
            SIDEBAR_MUTED = new Color(116, 145, 128);
            ACCENT = new Color(163, 230, 53);
            DANGER = new Color(248, 113, 113);
            DANGER_SOFT = new Color(66, 28, 28);
            WARNING = new Color(251, 191, 36);
            WARNING_SOFT = new Color(64, 50, 16);
            INFO = new Color(96, 165, 250);
            BG_MAIN = new Color(17, 24, 20);
            SURFACE = new Color(26, 35, 30);
            SURFACE_ALT = new Color(31, 41, 36);
            BORDER = new Color(49, 63, 55);
            BORDER_STRONG = new Color(70, 87, 77);
            TEXT_DARK = new Color(229, 236, 232);
            TEXT_MUTED = new Color(148, 163, 155);
            TABLE_GRID = new Color(38, 50, 44);
        }
        PANEL_WHITE = SURFACE;
        GREEN_TOP = PRIMARY;
        GREEN_TOP_DARK = PRIMARY_HOVER;
        GREEN_SIDEBAR = SIDEBAR_BG;
        GREEN_SIDEBAR_ACTIVE = SIDEBAR_ACTIVE;
        GREEN_SIDEBAR_TOP = SIDEBAR_BG;
        GREEN_BORDER = BORDER;
        GREEN_FORM = BG_MAIN;
        GREEN_SOFT = PRIMARY_SOFT;
        RED_LOGOUT = DANGER;
        PINK_LOGOUT = DANGER_SOFT;
        RED_ACTION = DANGER;
        BLUE_ACTION = INFO;
        CYAN_ACTION = INFO;
        ORANGE_ACTION = WARNING;
        WHITE_ACTION = SURFACE_ALT;
        TABLE_HEADER = SURFACE_ALT;
        YELLOW_TEXT = ACCENT;
    }

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
    private static boolean escapeInstalled;

    private static void installEscapeClosesDialogs() {
        if (escapeInstalled) {
            return;
        }
        escapeInstalled = true;
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
            Class.forName(dark ? "com.formdev.flatlaf.FlatDarkLaf" : "com.formdev.flatlaf.FlatLightLaf")
                    .getMethod("setup").invoke(null);
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
