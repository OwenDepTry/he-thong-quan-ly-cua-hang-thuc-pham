package ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Thông báo nhỏ hiện ở góc dưới bên phải cửa sổ chính rồi tự mờ dần và biến mất,
 * thay cho hộp thoại "… thành công" phải bấm OK.
 *
 * <pre>
 *   Toast.success(this, "Đã thêm khách hàng KH010");
 *   Toast.success(this, "Đã xuất file hoadon.pdf", "Mở file", () -> Desktop.getDesktop().open(file));
 * </pre>
 */
public final class Toast extends JPanel {

    public enum Type { SUCCESS, INFO, ERROR }

    private static final int MARGIN = 24;
    private static final int SHOW_MS = 2600;
    private static final int SHOW_WITH_ACTION_MS = 6000;
    private static final int FADE_MS = 300;

    /** Mỗi cửa sổ chỉ giữ 1 toast; toast mới thay toast cũ. */
    private static final String CLIENT_KEY = "ui.Toast.current";

    private final Type type;
    private float alpha = 0f;
    private Timer fadeTimer;
    private Timer lifeTimer;
    private JLayeredPane host;
    private ComponentAdapter resizeListener;

    private Toast(Type type, String message, String actionLabel, ThrowingRunnable action) {
        super(new BorderLayout(12, 0));
        this.type = type;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        Color accent = type == Type.ERROR ? AppTheme.DANGER : type == Type.INFO ? AppTheme.INFO : AppTheme.ACCENT;
        String iconName = type == Type.ERROR ? "info" : type == Type.INFO ? "info" : "check";
        JLabel icon = new JLabel(AppIcons.get(iconName, 18, accent));
        add(icon, BorderLayout.WEST);

        JLabel text = new JLabel("<html><body style='width:" + Math.min(320, Math.max(160, message.length() * 7))
                + "px'>" + escape(message) + "</body></html>");
        text.setForeground(AppTheme.WHITE);
        text.setFont(AppTheme.font(Font.PLAIN, 13));
        add(text, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(10, 0));
        right.setOpaque(false);
        if (actionLabel != null && action != null) {
            JLabel btn = new JLabel(actionLabel);
            btn.setFont(AppTheme.font(Font.BOLD, 13));
            btn.setForeground(accent);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    close();
                    try {
                        action.run();
                    } catch (Exception ex) {
                        AppDialogs.error(SwingUtilities.getWindowAncestor(host), "Không mở được",
                                "Không mở được file. Bạn có thể tự mở trong thư mục đã lưu.", String.valueOf(ex));
                    }
                }
            });
            right.add(btn, BorderLayout.CENTER);
        }
        JLabel close = new JLabel("×");
        close.setFont(AppTheme.font(Font.PLAIN, 18));
        close.setForeground(new Color(255, 255, 255, 150));
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.setToolTipText("Đóng");
        close.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                close();
            }
        });
        right.add(close, BorderLayout.EAST);
        add(right, BorderLayout.EAST);

        // Rê chuột lên thì không tự đóng
        MouseAdapter hold = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (lifeTimer != null) {
                    lifeTimer.stop();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (lifeTimer != null && !getBounds().contains(SwingUtilities.convertPoint(
                        e.getComponent(), e.getPoint(), getParent()))) {
                    lifeTimer.restart();
                }
            }
        };
        addMouseListener(hold);
    }

    // ================== API ==================

    public static void success(Component parent, String message) {
        show(parent, Type.SUCCESS, message, null, null);
    }

    public static void success(Component parent, String message, String actionLabel, ThrowingRunnable action) {
        show(parent, Type.SUCCESS, message, actionLabel, action);
    }

    public static void info(Component parent, String message) {
        show(parent, Type.INFO, message, null, null);
    }

    public static void error(Component parent, String message) {
        show(parent, Type.ERROR, message, null, null);
    }

    /** Hành động của nút trên toast (cho phép ném lỗi, ví dụ Desktop.open). */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public static void show(Component parent, Type type, String message, String actionLabel, ThrowingRunnable action) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> show(parent, type, message, actionLabel, action));
            return;
        }
        JRootPane root = mainRootPane(parent);
        if (root == null) {
            return;
        }
        JLayeredPane layered = root.getLayeredPane();

        Object old = layered.getClientProperty(CLIENT_KEY);
        if (old instanceof Toast) {
            ((Toast) old).removeNow();
        }

        Toast toast = new Toast(type, message, actionLabel, action);
        toast.attach(layered, actionLabel != null ? SHOW_WITH_ACTION_MS : SHOW_MS);
        layered.putClientProperty(CLIENT_KEY, toast);
    }

    /** Luôn hiện trên cửa sổ chính, kể cả khi gọi từ trong một hộp thoại sắp đóng. */
    private static JRootPane mainRootPane(Component parent) {
        Window w = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        while (w instanceof Dialog && w.getOwner() != null) {
            w = w.getOwner();
        }
        if (w instanceof RootPaneContainer) {
            return ((RootPaneContainer) w).getRootPane();
        }
        return parent instanceof JComponent ? ((JComponent) parent).getRootPane() : null;
    }

    // ================== Hiển thị / ẩn ==================

    private void attach(JLayeredPane layered, int lifeMs) {
        this.host = layered;
        layered.add(this, JLayeredPane.POPUP_LAYER);
        reposition();
        resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reposition();
            }
        };
        layered.addComponentListener(resizeListener);

        fadeTo(1f, null);
        lifeTimer = new Timer(lifeMs, e -> close());
        lifeTimer.setRepeats(false);
        lifeTimer.start();
    }

    private void reposition() {
        if (host == null) {
            return;
        }
        Dimension d = getPreferredSize();
        setBounds(host.getWidth() - d.width - MARGIN, host.getHeight() - d.height - MARGIN, d.width, d.height);
        revalidate();
        repaint();
    }

    private void close() {
        if (lifeTimer != null) {
            lifeTimer.stop();
        }
        fadeTo(0f, this::removeNow);
    }

    private void removeNow() {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        if (lifeTimer != null) {
            lifeTimer.stop();
        }
        if (host != null) {
            host.removeComponentListener(resizeListener);
            host.remove(this);
            host.repaint(getBounds());
            if (host.getClientProperty(CLIENT_KEY) == this) {
                host.putClientProperty(CLIENT_KEY, null);
            }
            host = null;
        }
    }

    private void fadeTo(float target, Runnable done) {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        final float start = alpha;
        final long t0 = System.currentTimeMillis();
        fadeTimer = new Timer(15, e -> {
            float p = Math.min(1f, (System.currentTimeMillis() - t0) / (float) FADE_MS);
            alpha = start + (target - start) * p;
            repaint();
            if (p >= 1f) {
                ((Timer) e.getSource()).stop();
                if (done != null) {
                    done.run();
                }
            }
        });
        fadeTimer.start();
    }

    // ================== Vẽ ==================

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // bóng đổ nhẹ
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, 14, 14);
        // nền
        g2.setColor(new Color(24, 33, 28));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 12, 12);
        // vạch màu bên trái
        g2.setColor(type == Type.ERROR ? AppTheme.DANGER : type == Type.INFO ? AppTheme.INFO : AppTheme.ACCENT);
        g2.fillRoundRect(0, 8, 4, getHeight() - 19, 4, 4);
        super.paint(g2);
        g2.dispose();
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }
}
