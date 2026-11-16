package ui;

import dao.DashboardDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Trang chủ: lời chào, thẻ số liệu (doanh thu/hóa đơn hôm nay, tháng này…), cảnh báo sắp hết hàng,
 * lô hàng sắp hết hạn và các ô truy cập nhanh. Số liệu tải ở luồng nền, tự làm mới khi quay lại trang.
 */
public class HomePanel extends JPanel implements Refreshable {

    private static final NumberFormat MONEY = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Consumer<String> navigator;
    private final boolean manager;
    private final DashboardDAO dao = new DashboardDAO();

    private final StatCard card1;
    private final StatCard card2;
    private final StatCard card3;
    private final StatCard card4;
    private final ListCard lowStock;
    private final ListCard expiring;
    private boolean loadedOnce;

    public HomePanel(String role) {
        this(role, "bạn", null);
    }

    public HomePanel(String role, String displayName, Consumer<String> navigator) {
        this.navigator = navigator;
        this.manager = "QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);

        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);

        Page page = new Page();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(AppTheme.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        // ===== Lời chào =====
        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN")));
        JLabel lblDate = label(Character.toUpperCase(today.charAt(0)) + today.substring(1), Font.PLAIN, 13, AppTheme.TEXT_MUTED);
        JLabel lblHello = label("Xin chào, " + displayName, Font.BOLD, 26, AppTheme.TEXT_DARK);
        JLabel lblSub = label(manager ? "Tình hình cửa hàng hôm nay." : "Chúc bạn một ca làm việc thuận lợi.",
                Font.PLAIN, 14, AppTheme.TEXT_MUTED);
        page.add(left(lblDate));
        page.add(Box.createVerticalStrut(4));
        page.add(left(lblHello));
        page.add(Box.createVerticalStrut(4));
        page.add(left(lblSub));
        page.add(Box.createVerticalStrut(22));

        // ===== Thẻ số liệu =====
        if (manager) {
            card1 = new StatCard("receipt", "Doanh thu hôm nay");
            card2 = new StatCard("chart", "Doanh thu tháng này");
        } else {
            card1 = new StatCard("receipt", "Hóa đơn hôm nay");
            card2 = new StatCard("chart", "Hóa đơn tháng này");
        }
        card3 = new StatCard("box", "Sản phẩm đang bán");
        card4 = new StatCard("user", "Khách hàng");
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        page.add(left(cards));
        page.add(Box.createVerticalStrut(18));

        // ===== Cảnh báo =====
        lowStock = new ListCard("Sắp hết hàng", "box",
                "Tồn kho từ " + DashboardDAO.LOW_STOCK_THRESHOLD + " trở xuống",
                "Không có sản phẩm nào sắp hết hàng", manager ? "Sản phẩm" : null);
        expiring = new ListCard("Lô hàng sắp hết hạn", "history",
                "Hết hạn trong " + DashboardDAO.EXPIRY_WARNING_DAYS + " ngày tới hoặc " + DashboardDAO.EXPIRED_LOOKBACK_DAYS + " ngày qua",
                "Không có lô hàng nào sắp hết hạn", manager ? "Sản phẩm" : null);
        JPanel lists = new JPanel(new GridLayout(1, 2, 16, 0));
        lists.setOpaque(false);
        lists.add(lowStock);
        lists.add(expiring);
        page.add(left(lists));
        page.add(Box.createVerticalStrut(22));

        // ===== Truy cập nhanh =====
        page.add(left(label("TRUY CẬP NHANH", Font.BOLD, 12, AppTheme.TEXT_MUTED)));
        page.add(Box.createVerticalStrut(10));
        String[][] tiles = manager
                ? new String[][]{
                    {"Hóa đơn", "Lập và tra cứu hóa đơn"}, {"Nhập hàng", "Tạo phiếu nhập hàng"},
                    {"Sản phẩm", "Sản phẩm và tồn kho"}, {"Khách hàng", "Thông tin khách hàng"},
                    {"Nhân viên", "Hồ sơ nhân viên"}, {"Nhà cung cấp", "Danh bạ nhà cung cấp"},
                    {"Khuyến mãi", "Chương trình giảm giá"}, {"Thống kê", "Doanh thu theo năm"}}
                : new String[][]{
                    {"Hóa đơn", "Lập và tra cứu hóa đơn"}, {"Nhập hàng", "Tạo phiếu nhập hàng"},
                    {"Khách hàng", "Tra cứu khách hàng"}, {"Thông tin", "Tài khoản của bạn"},
                    {"Lịch sử", "Hóa đơn bạn đã lập"}};
        JPanel grid = new JPanel(new GridLayout(0, 4, 14, 14));
        grid.setOpaque(false);
        for (String[] t : tiles) {
            grid.add(new Tile(t[0], t[1]));
        }
        page.add(left(grid));

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        showLoading();
    }

    // ================== Tải dữ liệu ==================

    @Override
    public void addNotify() {
        super.addNotify();
        if (!loadedOnce) {
            loadedOnce = true;
            javax.swing.SwingUtilities.invokeLater(this::refresh);
        }
    }

    /** Tải lại số liệu (gọi khi người dùng quay lại Trang chủ). */
    @Override
    public void refresh() {
        AppAsync.run(this, "Đang tải số liệu…", dao::load, this::render, err -> showError());
    }

    private void showLoading() {
        for (StatCard c : new StatCard[]{card1, card2, card3, card4}) {
            c.set("…", " ", false);
        }
    }

    private void showError() {
        for (StatCard c : new StatCard[]{card1, card2, card3, card4}) {
            c.set("—", "Chưa tải được", true);
        }
        lowStock.showMessage("Không tải được dữ liệu. Kiểm tra kết nối CSDL.", true);
        expiring.showMessage("Không tải được dữ liệu. Kiểm tra kết nối CSDL.", true);
    }

    /** Hiển thị số liệu (public để có thể chụp ảnh/kiểm thử với dữ liệu mẫu). */
    public void render(DashboardDAO.Data d) {
        if (manager) {
            card1.set(money(d.doanhThuHomNay), d.hoaDonHomNay + " hóa đơn", false);
            card2.set(money(d.doanhThuThangNay), d.hoaDonThangNay + " hóa đơn", false);
        } else {
            card1.set(String.valueOf(d.hoaDonHomNay), "hóa đơn trong ngày", false);
            card2.set(String.valueOf(d.hoaDonThangNay), "hóa đơn trong tháng", false);
        }
        card3.set(MONEY.format(d.tongSanPham), d.soSapHetHang > 0
                ? d.soSapHetHang + " sản phẩm sắp hết hàng" : "Tồn kho ổn định", d.soSapHetHang > 0);
        card4.set(MONEY.format(d.tongKhachHang), "khách hàng thành viên", false);

        lowStock.setCount(d.soSapHetHang);
        lowStock.clearRows();
        for (Object[] r : d.sapHetHang) {
            int ton = ((Number) r[3]).intValue();
            String dvt = r[2] == null ? "" : " " + r[2];
            lowStock.addRow(String.valueOf(r[1]), String.valueOf(r[0]),
                    ton <= 0 ? "Hết hàng" : "Còn " + ton + dvt, ton <= 0 ? AppTheme.DANGER : AppTheme.WARNING);
        }
        lowStock.finishRows(d.soSapHetHang - d.sapHetHang.size());

        expiring.setCount(d.soLoSapHetHan);
        expiring.clearRows();
        LocalDate today = LocalDate.now();
        for (Object[] r : d.sapHetHan) {
            LocalDate hsd = r[3] instanceof java.sql.Date ? ((java.sql.Date) r[3]).toLocalDate() : null;
            String badge;
            Color color;
            if (hsd == null) {
                badge = "?";
                color = AppTheme.TEXT_MUTED;
            } else {
                long days = ChronoUnit.DAYS.between(today, hsd);
                if (days < 0) {
                    badge = "Đã hết hạn";
                    color = AppTheme.DANGER;
                } else if (days == 0) {
                    badge = "Hết hạn hôm nay";
                    color = AppTheme.DANGER;
                } else {
                    badge = "Còn " + days + " ngày";
                    color = AppTheme.WARNING;
                }
            }
            expiring.addRow(String.valueOf(r[1]),
                    r[0] + " · " + r[2] + " sản phẩm · HSD " + (hsd == null ? "?" : hsd.format(DATE)), badge, color);
        }
        expiring.finishRows(d.soLoSapHetHan - d.sapHetHan.size());
    }

    private static String money(double v) {
        return MONEY.format(Math.round(v)) + " ₫";
    }

    // ================== Thành phần ==================

    private static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.font(style, size));
        l.setForeground(color);
        return l;
    }

    private static JComponent left(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        return c;
    }

    /** Nền thẻ bo góc. */
    private static class RoundedCard extends JPanel {

        boolean hover;

        RoundedCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.SURFACE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(hover ? AppTheme.PRIMARY : AppTheme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static JLabel softIcon(String name, int box, Color fg, Color bg) {
        JLabel icon = new JLabel(AppIcons.get(name, box / 2, fg), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        Dimension d = new Dimension(box, box);
        icon.setPreferredSize(d);
        icon.setMinimumSize(d);
        icon.setMaximumSize(d);
        return icon;
    }

    /** Thẻ số liệu: icon, tiêu đề, con số lớn, dòng phụ. */
    private static class StatCard extends RoundedCard {

        private final JLabel value = label("…", Font.BOLD, 22, AppTheme.TEXT_DARK);
        private final JLabel sub = label(" ", Font.PLAIN, 12, AppTheme.TEXT_MUTED);

        StatCard(String icon, String title) {
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            JPanel iconWrap = new JPanel(new BorderLayout());
            iconWrap.setOpaque(false);
            iconWrap.add(softIcon(icon, 40, AppTheme.PRIMARY, AppTheme.PRIMARY_SOFT), BorderLayout.NORTH);
            add(iconWrap, BorderLayout.WEST);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(label(title, Font.PLAIN, 13, AppTheme.TEXT_MUTED));
            text.add(Box.createVerticalStrut(4));
            text.add(value);
            text.add(Box.createVerticalStrut(2));
            text.add(sub);
            add(text, BorderLayout.CENTER);
            setPreferredSize(new Dimension(220, 98));
        }

        void set(String v, String s, boolean warn) {
            value.setText(v);
            sub.setText(s);
            sub.setForeground(warn ? AppTheme.WARNING : AppTheme.TEXT_MUTED);
        }
    }

    /** Khung danh sách cảnh báo: tiêu đề + số lượng + "Xem tất cả", các dòng có nhãn màu bên phải. */
    private class ListCard extends RoundedCard {

        private final JLabel count = label("", Font.BOLD, 12, AppTheme.WHITE);
        private final JPanel rows = new JPanel();
        private final String emptyText;

        ListCard(String title, String icon, String subtitle, String emptyText, String linkTarget) {
            this.emptyText = emptyText;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
            setPreferredSize(new Dimension(400, 300));

            JPanel head = new JPanel(new BorderLayout(10, 0));
            head.setOpaque(false);
            head.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            head.add(softIcon(icon, 34, AppTheme.WARNING, AppTheme.WARNING_SOFT), BorderLayout.WEST);
            JPanel titles = new JPanel();
            titles.setOpaque(false);
            titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
            JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            titleRow.setOpaque(false);
            titleRow.setAlignmentX(LEFT_ALIGNMENT);
            JLabel t = label(title, Font.BOLD, 15, AppTheme.TEXT_DARK);
            titleRow.add(t);
            count.setOpaque(false);
            count.setBorder(BorderFactory.createEmptyBorder(1, 7, 1, 7));
            titleRow.add(count);
            ((FlowLayout) titleRow.getLayout()).setHgap(0);
            t.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            titles.add(titleRow);
            JLabel st = label(subtitle, Font.PLAIN, 12, AppTheme.TEXT_MUTED);
            st.setAlignmentX(LEFT_ALIGNMENT);
            titles.add(st);
            head.add(titles, BorderLayout.CENTER);
            if (linkTarget != null && navigator != null) {
                JLabel link = label("Xem tất cả", Font.BOLD, 12, AppTheme.PRIMARY);
                link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                link.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        navigator.accept(linkTarget);
                    }
                });
                head.add(link, BorderLayout.EAST);
            }
            add(head, BorderLayout.NORTH);

            rows.setOpaque(false);
            rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
            add(rows, BorderLayout.CENTER);
            showMessage("Đang tải…", false);
        }

        void setCount(int n) {
            count.setText(n > 0 ? String.valueOf(n) : "");
            repaint();
        }

        void clearRows() {
            rows.removeAll();
        }

        void addRow(String title, String sub, String badge, Color badgeColor) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(rows.getComponentCount() == 0 ? 0 : 1, 0, 0, 0, AppTheme.TABLE_GRID),
                    BorderFactory.createEmptyBorder(7, 2, 7, 2)));
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(label(title, Font.BOLD, 13, AppTheme.TEXT_DARK));
            text.add(label(sub, Font.PLAIN, 11, AppTheme.TEXT_MUTED));
            row.add(text, BorderLayout.CENTER);

            JLabel pill = new JLabel(badge) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 34));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            pill.setFont(AppTheme.font(Font.BOLD, 11));
            pill.setForeground(badgeColor);
            pill.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            JPanel pillWrap = new JPanel(new java.awt.GridBagLayout());
            pillWrap.setOpaque(false);
            pillWrap.add(pill);
            row.add(pillWrap, BorderLayout.EAST);
            rows.add(row);
        }

        void finishRows(int more) {
            if (rows.getComponentCount() == 0) {
                showMessage(emptyText, false);
                return;
            }
            if (more > 0) {
                JLabel m = label("và " + more + " mục khác…", Font.PLAIN, 12, AppTheme.TEXT_MUTED);
                m.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
                m.setAlignmentX(LEFT_ALIGNMENT);
                rows.add(m);
            }
            rows.revalidate();
            rows.repaint();
        }

        void showMessage(String text, boolean error) {
            rows.removeAll();
            JLabel l = new JLabel(text, AppIcons.get(error ? "info" : "check", 16,
                    error ? AppTheme.DANGER : AppTheme.PRIMARY), SwingConstants.LEFT);
            l.setFont(AppTheme.font(Font.PLAIN, 13));
            l.setForeground(AppTheme.TEXT_MUTED);
            l.setIconTextGap(8);
            l.setBorder(BorderFactory.createEmptyBorder(18, 2, 0, 0));
            l.setAlignmentX(LEFT_ALIGNMENT);
            rows.add(l);
            rows.revalidate();
            rows.repaint();
        }

        @Override
        protected void paintChildren(Graphics g) {
            // nền đỏ bo tròn cho số đếm
            if (!count.getText().isEmpty() && count.isShowing()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.Rectangle r = javax.swing.SwingUtilities.convertRectangle(count.getParent(), count.getBounds(), this);
                g2.setColor(AppTheme.DANGER);
                g2.fillRoundRect(r.x, r.y, r.width, r.height, r.height, r.height);
                g2.dispose();
            }
            super.paintChildren(g);
        }
    }

    /** Ô truy cập nhanh thu gọn: icon bên trái, tên + mô tả bên phải. */
    private class Tile extends RoundedCard {

        Tile(String name, String desc) {
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
            setPreferredSize(new Dimension(200, 66));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(softIcon(AppIcons.nameFor(name), 38, AppTheme.PRIMARY, AppTheme.PRIMARY_SOFT), BorderLayout.WEST);
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(Box.createVerticalGlue());
            text.add(label(name, Font.BOLD, 14, AppTheme.TEXT_DARK));
            text.add(label(desc, Font.PLAIN, 12, AppTheme.TEXT_MUTED));
            text.add(Box.createVerticalGlue());
            add(text, BorderLayout.CENTER);
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
                    if (navigator != null) {
                        navigator.accept(name);
                    }
                }
            });
        }
    }

    /** Trang cuộn dọc nhưng luôn vừa chiều ngang. */
    private static class Page extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) {
            return r.height - 40;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
