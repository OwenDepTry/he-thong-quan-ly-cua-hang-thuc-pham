package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class SearchPanel extends JPanel {

    private final JComboBox<String> cboSort;
    private final JTextField txtKeyword;
    private final JButton btnReset;
    private final JButton btnRefresh;
    private final JLabel lblResult;
    private final ButtonGroup buttonGroup;

    public SearchPanel(String title, String... radioNames) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 6));

        cboSort = new JComboBox<>(new String[]{"none"});
        cboSort.setPreferredSize(new Dimension(68, 20));
        cboSort.setFont(AppTheme.FONT_TINY);
        add(cboSort);

        txtKeyword = new JTextField();
        txtKeyword.setPreferredSize(new Dimension(96, 20));
        txtKeyword.setFont(AppTheme.FONT_TINY);
        add(txtKeyword);

        btnReset = new JButton("Đặt lại");
        styleSmallButton(btnReset, 54);
        add(btnReset);

        btnRefresh = new JButton("Refresh");
        styleSmallButton(btnRefresh, 58);
        add(btnRefresh);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        radioPanel.setOpaque(false);

        buttonGroup = new ButtonGroup();
        for (int i = 0; i < radioNames.length; i++) {
            JRadioButton r = new JRadioButton(radioNames[i]);
            r.setOpaque(false);
            r.setForeground(Color.WHITE);
            r.setFont(new Font("Arial", Font.PLAIN, 10));
            if (i == 0) {
                r.setSelected(true);
            }
            buttonGroup.add(r);
            radioPanel.add(r);
        }
        add(radioPanel);

        lblResult = new JLabel(title);
        lblResult.setForeground(Color.WHITE);
        lblResult.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblResult);
    }

    private void styleSmallButton(JButton btn, int width) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 10));
        btn.setPreferredSize(new Dimension(width, 20));
        btn.setBackground(new Color(245, 245, 245));
    }

    public JComboBox<String> getCboSort() {
        return cboSort;
    }

    public JTextField getTxtKeyword() {
        return txtKeyword;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JLabel getLblResult() {
        return lblResult;
    }
}