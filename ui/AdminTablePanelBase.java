package ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Khung chung cho các màn hình dạng bảng:
 * một "thẻ" trắng phía trên chứa thanh công cụ + tìm kiếm, một thẻ trắng phía dưới chứa bảng.
 */
public class AdminTablePanelBase extends JPanel {

    protected JTable table;
    /** Giữ lại cho tương thích – không còn hiển thị. */
    protected JPanel topPanel;
    protected JPanel centerPanel;

    public AdminTablePanelBase() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(AppTheme.SURFACE);
        centerPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
        add(centerPanel, BorderLayout.CENTER);
    }

    protected void buildTopBar(JPanel leftPanel, JPanel rightPanel) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        // Hàng trên: các nút thao tác; hàng dưới: tìm kiếm / sắp xếp
        card.add(leftPanel, BorderLayout.NORTH);
        JPanel divider = new JPanel();
        divider.setBackground(AppTheme.BORDER);
        divider.setPreferredSize(new java.awt.Dimension(1, 1));
        card.add(divider, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.NORTH);
    }

    /**
     * Phím tắt cho bảng chính:
     * - bấm tiêu đề cột để sắp xếp;
     * - bấm đúp vào dòng = nút "Sửa" (hoặc "Chi tiết" nếu không được sửa);
     * - phím Delete = nút "Xóa" (vẫn hỏi xác nhận như khi bấm nút).
     * Truyền toolbar dạng Supplier vì một số màn hình tạo toolbar sau bảng.
     */
    protected void installTableShortcuts(JTable table, java.util.function.Supplier<CrudToolbarPanel> toolbar) {
        AppTable.enableSorting(table);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2 || !javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                table.setRowSelectionInterval(row, row);
                clickFirstEnabled(toolbar.get(), "Sửa", "Chi tiết");
            }
        });

        table.getInputMap(JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0), "app-delete-row");
        table.getActionMap().put("app-delete-row", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (table.getSelectedRow() >= 0) {
                    clickFirstEnabled(toolbar.get(), "Xóa");
                }
            }
        });
    }

    private static void clickFirstEnabled(CrudToolbarPanel toolbar, String... names) {
        if (toolbar == null) {
            return;
        }
        for (String name : names) {
            javax.swing.JButton b = toolbar.getButton(name);
            if (b != null && b.isEnabled() && b.isVisible()) {
                b.doClick();
                return;
            }
        }
    }

    protected JComponent createStyledTable(JTable table) {
        AppTable.style(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setResizingAllowed(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.SURFACE);

        return AppTable.withEmptyState(scroll, table, "Không có dữ liệu");
    }
}
