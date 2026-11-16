package ui;

import java.awt.BorderLayout;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Trang chủ: lời chào + các ô truy cập nhanh tới từng chức năng. */
public class HomePanel extends JPanel {

    private final Consumer<String> navigator;

    public HomePanel(String role) {
        this(role, "bạn", null);
    }

    public HomePanel(String role, String displayName, Consumer<String> navigator) {
        this.navigator = navigator;
        boolean manager = "QL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);

        setLayout(new BorderLayout(0, 24));
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // ===== Lời chào =====
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        String today = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN")));
        JLabel lblDate = new JLabel(Character.toUpperCase(today.charAt(0)) + today.substring(1));
        lblDate.setFont(AppTheme.font(Font.PLAIN, 13));
        lblDate.setForeground(AppTheme.TEXT_MUTED);

        JLabel lblHello = new JLabel("Xin chào, " + displayName);
        lblHello.setFont(AppTheme.font(Font.BOLD, 26));
        lblHello.setForeground(AppTheme.TEXT_DARK);

        JLabel lblSub = new JLabel(manager
                ? "Chọn một chức năng bên dưới hoặc ở menu bên trái để bắt đầu."
                : "Bắt đầu ca làm việc: lập hóa đơn, nhập hàng hoặc tra cứu khách hàng.");
        lblSub.setFont(AppTheme.font(Font.PLAIN, 14));
        lblSub.setForeground(AppTheme.TEXT_MUTED);

        header.add(lblDate);
        header.add(Box.createVerticalStrut(4));
        header.add(lblHello);
        header.add(Box.createVerticalStrut(6));
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // ===== Ô truy cập nhanh =====
        String[][] tiles = manager
                ? new String[][]{
                    {"Hóa đơn", "Lập và tra cứu hóa đơn bán hàng"},
                    {"Nhập hàng", "Tạo phiếu nhập từ nhà cung cấp"},
                    {"Sản phẩm", "Sản phẩm, loại sản phẩm, tồn kho"},
                    {"Khách hàng", "Thông tin và lịch sử mua hàng"},
                    {"Nhân viên", "Hồ sơ và tài khoản nhân viên"},
                    {"Nhà cung cấp", "Danh bạ nhà cung cấp"},
                    {"Khuyến mãi", "Chương trình giảm giá"},
                    {"Thống kê", "Doanh thu, hóa đơn, phiếu nhập"}
                }
                : new String[][]{
                    {"Hóa đơn", "Lập và tra cứu hóa đơn bán hàng"},
                    {"Nhập hàng", "Tạo phiếu nhập từ nhà cung cấp"},
                    {"Khách hàng", "Tra cứu và thêm khách hàng"},
                    {"Thông tin", "Tài khoản và mật khẩu của bạn"},
                    {"Lịch sử", "Hóa đơn, phiếu nhập bạn đã lập"}
                };

        JLabel lblQuick = new JLabel("TRUY CẬP NHANH");
        lblQuick.setFont(AppTheme.font(Font.BOLD, 12));
        lblQuick.setForeground(AppTheme.TEXT_MUTED);
        lblQuick.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel grid = new JPanel(new GridLayout(0, 4, 16, 16));
        grid.setOpaque(false);
        for (String[] t : tiles) {
            grid.add(new Tile(t[0], t[1]));
        }

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(lblQuick, BorderLayout.NORTH);
        JPanel gridWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, grid.getPreferredSize().height);
            }
        };
        gridWrap.setOpaque(false);
        gridWrap.setLayout(new BorderLayout());
        gridWrap.add(grid, BorderLayout.NORTH);
        body.add(gridWrap, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private class Tile extends JPanel {

        private boolean hover;

        Tile(String name, String desc) {
            setLayout(new BorderLayout(0, 12));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            setPreferredSize(new Dimension(200, 132));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(AppIcons.get(AppIcons.nameFor(name), 22, AppTheme.PRIMARY), SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(AppTheme.PRIMARY_SOFT);
                    g2.fillRoundRect(0, 0, 42, 42, 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            icon.setPreferredSize(new Dimension(42, 42));
            JPanel iconWrap = new JPanel(new BorderLayout());
            iconWrap.setOpaque(false);
            iconWrap.add(icon, BorderLayout.WEST);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            JLabel lblName = new JLabel(name);
            lblName.setFont(AppTheme.font(Font.BOLD, 15));
            lblName.setForeground(AppTheme.TEXT_DARK);
            JLabel lblDesc = new JLabel(desc);
            lblDesc.setFont(AppTheme.font(Font.PLAIN, 12));
            lblDesc.setForeground(AppTheme.TEXT_MUTED);
            text.add(lblName);
            text.add(Box.createVerticalStrut(3));
            text.add(lblDesc);

            add(iconWrap, BorderLayout.NORTH);
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
}
