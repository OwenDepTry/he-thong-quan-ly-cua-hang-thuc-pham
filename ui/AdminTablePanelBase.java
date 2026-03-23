package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AdminTablePanelBase extends JPanel {

    protected JTable table;
    protected JPanel topPanel;
    protected JPanel centerPanel;

    public AdminTablePanelBase() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(AppTheme.GREEN_TOP);
        topPanel.setPreferredSize(new Dimension(100, 44));
        topPanel.setBorder(BorderFactory.createLineBorder(AppTheme.GREEN_BORDER, 1));
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(205, 205, 205), 1));
        add(centerPanel, BorderLayout.CENTER);
    }

    protected void buildTopBar(JPanel leftPanel, JPanel rightPanel) {
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // QUAN TRỌNG: tăng chiều cao thanh trên
        topBar.setPreferredSize(new Dimension(100, 92));

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrap.setOpaque(false);
        leftWrap.add(leftPanel);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(rightPanel, BorderLayout.CENTER);

        topBar.add(leftWrap, BorderLayout.WEST);
        topBar.add(rightWrap, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
    }

    protected JScrollPane createStyledTable(JTable table) {
        table.setRowHeight(26);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }
}