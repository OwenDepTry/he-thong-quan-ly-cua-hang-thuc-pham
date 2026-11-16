package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.geom.Arc2D;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Chạy việc tốn thời gian (truy vấn CSDL) ở luồng nền để giao diện không bị đứng,
 * đồng thời phủ lớp "Đang tải…" có vòng xoay lên cửa sổ.
 *
 * <pre>
 *   AppAsync.run(this, "Đang tải thống kê…",
 *       () -> dao.load(year),          // chạy nền – KHÔNG đụng vào component Swing ở đây
 *       data -> render(data),          // chạy trên luồng giao diện khi xong
 *       err -> Toast.error(this, "Không tải được dữ liệu"));
 * </pre>
 */
public final class AppAsync {

    private AppAsync() {
    }

    public static <T> void run(Component owner, String message, Callable<T> work,
            Consumer<T> onDone, Consumer<Exception> onError) {
        BusyOverlay overlay = BusyOverlay.show(owner, message);
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return work.call();
            }

            @Override
            protected void done() {
                BusyOverlay.hide(overlay);
                try {
                    onDone.accept(get());
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    cause.printStackTrace();
                    if (onError != null) {
                        onError.accept(cause instanceof Exception ? (Exception) cause : new Exception(cause));
                    }
                }
            }
        }.execute();
    }

    /**
     * Hiện lớp phủ ngay, chạy {@code task} ở lượt vẽ kế tiếp (vẫn trên luồng giao diện).
     * Dùng cho code cũ bắt buộc chạy trên luồng giao diện (ví dụ tạo màn hình truy vấn CSDL trong constructor):
     * không tránh được việc chờ, nhưng người dùng thấy "Đang tải…" thay vì cửa sổ đơ không phản hồi.
     */
    public static void withOverlay(Component owner, String message, Runnable task) {
        BusyOverlay overlay = BusyOverlay.show(owner, message);
        if (overlay != null) {
            overlay.paintImmediately(0, 0, overlay.getWidth(), overlay.getHeight());
        }
        SwingUtilities.invokeLater(() -> {
            try {
                task.run();
            } finally {
                BusyOverlay.hide(overlay);
            }
        });
    }

    /** Lớp phủ mờ + vòng xoay, gắn vào glass pane của cửa sổ, chặn bấm chuột trong lúc tải. */
    static final class BusyOverlay extends JComponent {

        private final String message;
        private final Timer spin;
        private int angle;
        private int depth = 1;

        private BusyOverlay(String message) {
            this.message = message == null ? "Đang tải…" : message;
            setOpaque(false);
            addMouseListener(new MouseAdapter() { });   // nuốt sự kiện chuột
            addMouseMotionListener(new MouseAdapter() { });
            spin = new Timer(40, e -> {
                angle = (angle + 12) % 360;
                repaint();
            });
        }

        static BusyOverlay show(Component owner, String message) {
            JRootPane root = owner instanceof JRootPane ? (JRootPane) owner : SwingUtilities.getRootPane(owner);
            if (root == null) {
                return null;
            }
            if (root.getGlassPane() instanceof BusyOverlay && root.getGlassPane().isVisible()) {
                BusyOverlay current = (BusyOverlay) root.getGlassPane();
                current.depth++;
                return current;
            }
            BusyOverlay o = new BusyOverlay(message);
            o.putClientProperty("previousGlass", root.getGlassPane());
            root.setGlassPane(o);
            o.setVisible(true);
            o.spin.start();
            return o;
        }

        static void hide(BusyOverlay o) {
            if (o == null || --o.depth > 0) {
                return;
            }
            o.spin.stop();
            o.setVisible(false);
            JRootPane root = SwingUtilities.getRootPane(o);
            Object prev = o.getClientProperty("previousGlass");
            if (root != null && prev instanceof Component) {
                root.setGlassPane((Component) prev);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AppTheme.applyTextHints(g2);
            Color bg = AppTheme.BG_MAIN;
            g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 150));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(AppTheme.font(Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int boxW = Math.max(200, fm.stringWidth(message) + 76);
            int boxH = 54;
            int x = (getWidth() - boxW) / 2;
            int y = (getHeight() - boxH) / 2;
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(x + 1, y + 3, boxW, boxH, 14, 14);
            g2.setColor(AppTheme.SURFACE);
            g2.fillRoundRect(x, y, boxW, boxH, 14, 14);
            g2.setColor(AppTheme.BORDER);
            g2.drawRoundRect(x, y, boxW, boxH, 14, 14);

            int cx = x + 30;
            int cy = y + boxH / 2;
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(AppTheme.BORDER);
            g2.drawOval(cx - 10, cy - 10, 20, 20);
            g2.setColor(AppTheme.PRIMARY);
            g2.draw(new Arc2D.Double(cx - 10, cy - 10, 20, 20, -angle, 100, Arc2D.OPEN));

            g2.setColor(AppTheme.TEXT_DARK);
            g2.drawString(message, cx + 22, cy + fm.getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}
