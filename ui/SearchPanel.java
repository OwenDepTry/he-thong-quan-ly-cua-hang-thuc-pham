package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SearchPanel extends JPanel {

    private final JComboBox<String> cboSort;
    private final JTextField txtKeyword;
    private final JButton btnReset;
    private final JButton btnRefresh;
    private final JLabel lblResult;
    private final ButtonGroup buttonGroup;
    private final List<JRadioButton> radios;
    private final JLabel lblSort;
    private final JLabel lblKeyword;

    public SearchPanel(String title, String... radioNames) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 10));

        lblSort = new JLabel("Sắp xếp:");
        lblSort.setFont(new Font("Arial", Font.BOLD, 12));
        lblSort.setForeground(new Color(22, 58, 33));
        add(lblSort);

        cboSort = new JComboBox<>(new String[]{"none"});
        cboSort.setPreferredSize(new Dimension(100, 28));
        cboSort.setFont(new Font("Arial", Font.PLAIN, 12));
        add(cboSort);

        lblKeyword = new JLabel("Từ khóa:");
        lblKeyword.setFont(new Font("Arial", Font.BOLD, 12));
        lblKeyword.setForeground(new Color(22, 58, 33));
        add(lblKeyword);

        txtKeyword = new JTextField();
        txtKeyword.setPreferredSize(new Dimension(140, 28));
        txtKeyword.setFont(new Font("Arial", Font.PLAIN, 12));
        txtKeyword.setForeground(new Color(25, 25, 25));
        txtKeyword.setBackground(Color.WHITE);
        txtKeyword.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        add(txtKeyword);

        btnReset = new JButton("Đặt lại");
        styleSmallButton(btnReset, 88);
        add(btnReset);

        btnRefresh = new JButton("Refresh");
        styleSmallButton(btnRefresh, 88);
        add(btnRefresh);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        radioPanel.setOpaque(false);

        buttonGroup = new ButtonGroup();
        radios = new ArrayList<>();
        for (int i = 0; i < radioNames.length; i++) {
            JRadioButton r = new JRadioButton(radioNames[i]);
            r.setOpaque(false);
            r.setForeground(new Color(20, 58, 32));
            r.setFont(new Font("Arial", Font.BOLD, 12));
            if (i == 0) {
                r.setSelected(true);
            }
            buttonGroup.add(r);
            radios.add(r);
            radioPanel.add(r);
        }
        add(radioPanel);

        lblResult = new JLabel(title);
        lblResult.setHorizontalAlignment(SwingConstants.LEFT);
        lblResult.setForeground(new Color(16, 54, 29));
        lblResult.setFont(new Font("Arial", Font.BOLD, 12));
        add(lblResult);
    }

    private void styleSmallButton(JButton btn, int width) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(width, 28));
        btn.setForeground(new Color(30, 30, 30));
        btn.setBackground(new Color(246, 246, 246));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
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

    public void setSortOptions(String... options) {
        cboSort.removeAllItems();
        if (options == null || options.length == 0) {
            cboSort.addItem("Mặc định");
            return;
        }

        for (String option : options) {
            cboSort.addItem(option);
        }
        cboSort.setSelectedIndex(0);
    }

    public String getSelectedRadioText() {
        for (JRadioButton radio : radios) {
            if (radio.isSelected()) {
                return radio.getText();
            }
        }
        return radios.isEmpty() ? "" : radios.get(0).getText();
    }
}