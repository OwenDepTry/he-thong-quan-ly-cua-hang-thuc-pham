package ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title) {
        setLayout(new GridBagLayout());
        setBackground(AppTheme.BG_MAIN);

        JLabel lblTitle = new JLabel(title, AppIcons.get(AppIcons.nameFor(title), 40, AppTheme.PRIMARY),
                SwingConstants.CENTER);
        lblTitle.setHorizontalTextPosition(SwingConstants.CENTER);
        lblTitle.setVerticalTextPosition(SwingConstants.BOTTOM);
        lblTitle.setIconTextGap(14);
        lblTitle.setFont(AppTheme.font(Font.BOLD, 24));
        lblTitle.setForeground(AppTheme.TEXT_DARK);

        JLabel lblDesc = new JLabel("Chức năng này đang được phát triển", SwingConstants.CENTER);
        lblDesc.setFont(AppTheme.font(Font.PLAIN, 14));
        lblDesc.setForeground(AppTheme.TEXT_MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblTitle, gbc);
        gbc.gridy = 1;
        gbc.insets = new java.awt.Insets(8, 0, 0, 0);
        add(lblDesc, gbc);
    }
}
