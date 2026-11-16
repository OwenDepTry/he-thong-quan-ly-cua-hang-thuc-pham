package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 * Hộp thoại xem chi tiết (chỉ đọc) một dòng trong bảng: mỗi cột hiển thị thành một cặp nhãn – giá trị,
 * tiền và ngày được định dạng giống trong bảng.
 */
public class RecordDetailDialog extends JDialog {

    public RecordDetailDialog(Frame owner, String title, String iconName, JTable table, int viewRow) {
        super(owner, title, true);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.SURFACE);
        setContentPane(root);

        // ===== Đầu: icon + tiêu đề + giá trị cột thứ 2 (thường là tên) =====
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 22, 14, 22));
        JLabel icon = new JLabel(AppIcons.get(iconName, 22, AppTheme.PRIMARY), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY_SOFT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setPreferredSize(new Dimension(44, 44));
        JPanel iconWrap = new JPanel(new BorderLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(icon, BorderLayout.NORTH);
        header.add(iconWrap, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        String headline = table.getColumnCount() > 2 ? format(table, viewRow, 2) : format(table, viewRow, 0);
        JLabel lblTitle = new JLabel(headline.isEmpty() ? title : headline);
        lblTitle.setFont(AppTheme.font(Font.BOLD, 18));
        lblTitle.setForeground(AppTheme.TEXT_DARK);
        JLabel lblSub = new JLabel(table.getColumnName(0) + ": " + format(table, viewRow, 0));
        lblSub.setFont(AppTheme.font(Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.TEXT_MUTED);
        text.add(Box.createVerticalStrut(2));
        text.add(lblTitle);
        text.add(Box.createVerticalStrut(4));
        text.add(lblSub);
        header.add(text, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ===== Thân: các cặp nhãn – giá trị, 2 cột =====
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 22, 18, 22)));
        int cell = 0;
        for (int c = 0; c < table.getColumnCount(); c++) {
            JPanel item = new JPanel();
            item.setOpaque(false);
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            JLabel k = new JLabel(table.getColumnName(c).toUpperCase());
            k.setFont(AppTheme.font(Font.BOLD, 11));
            k.setForeground(AppTheme.TEXT_MUTED);
            String v = format(table, viewRow, c);
            JLabel val = new JLabel(v.isEmpty() ? "—" : v);
            val.setFont(AppTheme.font(AppTable.isMoneyColumn(table, c) ? Font.BOLD : Font.PLAIN, 14));
            val.setForeground(v.isEmpty() ? AppTheme.TEXT_MUTED : AppTheme.TEXT_DARK);
            val.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
            item.add(k);
            item.add(val);
            item.setPreferredSize(new Dimension(230, 46));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = cell % 2;
            gbc.gridy = cell / 2;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.insets = new Insets(4, 0, 6, 16);
            grid.add(item, gbc);
            cell++;
        }
        root.add(grid, BorderLayout.CENTER);

        // ===== Chân =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(AppTheme.SURFACE_ALT);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        AppButton btnClose = new AppButton("Đóng", null, AppButton.Variant.PRIMARY);
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnClose);
        footer.add(btnClose);
        root.add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private static String format(JTable table, int row, int col) {
        Object v = table.getValueAt(row, col);
        if (v == null || "null".equals(String.valueOf(v))) {
            return "";
        }
        if (AppTable.isMoneyColumn(table, col)) {
            return AppTable.money(v);
        }
        if (v instanceof Number) {
            java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("vi-VN"));
            nf.setMaximumFractionDigits(2);
            return nf.format(v);
        }
        return AppTable.display(v);
    }
}
