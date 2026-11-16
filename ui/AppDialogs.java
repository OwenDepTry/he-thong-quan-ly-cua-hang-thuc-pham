package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Hộp thoại thông báo gọn gàng: câu chính ngắn, tự xuống dòng (không tràn màn hình),
 * phần chi tiết kỹ thuật (stack trace, thông báo JDBC…) ẩn sau nút "Xem chi tiết".
 */
public final class AppDialogs {

    private static final int TEXT_WIDTH = 380;

    private AppDialogs() {
    }

    public static void error(Component parent, String title, String message, String details) {
        JOptionPane.showMessageDialog(parent, build(message, details), title, JOptionPane.ERROR_MESSAGE);
    }

    private static JPanel build(String message, String details) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("<html><body style='width:" + TEXT_WIDTH + "px'>"
                + escape(message).replace("\n", "<br>") + "</body></html>");
        lbl.setFont(AppTheme.font(Font.PLAIN, 13));
        lbl.setForeground(AppTheme.TEXT_DARK);
        panel.add(lbl, BorderLayout.NORTH);

        if (details != null && !details.isBlank()) {
            JTextArea area = new JTextArea(details.trim());
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            area.setForeground(AppTheme.TEXT_MUTED);
            area.setBackground(AppTheme.SURFACE_ALT);
            area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(TEXT_WIDTH + 20, 110));
            scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
            scroll.setVisible(false);

            JButton toggle = new AppButton("Xem chi tiết kỹ thuật", null, AppButton.Variant.GHOST);
            toggle.setFont(AppTheme.font(Font.PLAIN, 12));
            toggle.addActionListener(e -> {
                scroll.setVisible(!scroll.isVisible());
                toggle.setText(scroll.isVisible() ? "Ẩn chi tiết" : "Xem chi tiết kỹ thuật");
                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null) {
                    w.pack();
                }
            });

            JPanel toggleRow = new JPanel(new BorderLayout());
            toggleRow.setOpaque(false);
            toggleRow.add(toggle, BorderLayout.WEST);

            JPanel south = new JPanel(new BorderLayout(0, 6));
            south.setOpaque(false);
            south.add(toggleRow, BorderLayout.NORTH);
            south.add(scroll, BorderLayout.CENTER);
            panel.add(south, BorderLayout.CENTER);
        }
        return panel;
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
