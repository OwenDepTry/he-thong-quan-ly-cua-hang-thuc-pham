package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Thẻ số liệu: tiêu đề nhỏ màu xám, con số lớn, vạch màu chủ đạo bên trái. */
public class StatCardPanel extends JPanel {

    private final JLabel lblValue;

    public StatCardPanel(String title, String value) {
        setLayout(new BorderLayout(0, 6));
        setBackground(AppTheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                        BorderFactory.createMatteBorder(0, 4, 0, 0, AppTheme.PRIMARY)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(AppTheme.font(Font.BOLD, 12));
        lblTitle.setForeground(AppTheme.TEXT_MUTED);

        lblValue = new JLabel(value);
        lblValue.setFont(AppTheme.font(Font.BOLD, 26));
        lblValue.setForeground(AppTheme.TEXT_DARK);

        add(lblTitle, BorderLayout.NORTH);
        add(lblValue, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }
}
