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
