package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Khối tìm kiếm + sắp xếp phía trên bảng.
 * Hàng 1: ô từ khóa, sắp xếp, nút Đặt lại / Làm mới.  Hàng 2: tìm theo trường nào + số kết quả.
 */
public class SearchPanel extends JPanel {

    private final JComboBox<String> cboSort;
    private final JTextField txtKeyword;
    private final JButton btnReset;
    private final JButton btnRefresh;
    private final JLabel lblResult;
    private final ButtonGroup buttonGroup;
    private final List<JRadioButton> radios;

    public SearchPanel(String title, String... radioNames) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(LEFT_ALIGNMENT);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(LEFT_ALIGNMENT);
        row2.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        txtKeyword = new JTextField();
        txtKeyword.setPreferredSize(new Dimension(260, 34));
        AppTheme.styleField(txtKeyword, "Nhập từ khóa rồi nhấn Enter…");
        txtKeyword.putClientProperty("JTextField.leadingIcon", AppIcons.get("search", 16, AppTheme.TEXT_MUTED));
        row1.add(txtKeyword);

        row1.add(mutedLabel("Sắp xếp"));
        cboSort = new JComboBox<>(new String[]{"Mặc định"});
        cboSort.setPreferredSize(new Dimension(170, 34));
        row1.add(cboSort);

        btnReset = new AppButton("Đặt lại", "reset", AppButton.Variant.GHOST);
        btnReset.setToolTipText("Xóa từ khóa và đưa sắp xếp về mặc định");
        row1.add(btnReset);

        btnRefresh = new AppButton("Làm mới", "refresh", AppButton.Variant.GHOST);
        btnRefresh.setToolTipText("Tải lại dữ liệu từ cơ sở dữ liệu");
        row1.add(btnRefresh);

        row2.add(mutedLabel("Tìm theo:"));
        buttonGroup = new ButtonGroup();
        radios = new ArrayList<>();
        for (int i = 0; i < radioNames.length; i++) {
            JRadioButton r = new JRadioButton(radioNames[i]);
            r.setOpaque(false);
            r.setForeground(AppTheme.TEXT_DARK);
            r.setFocusPainted(false);
            if (i == 0) {
                r.setSelected(true);
            }
            buttonGroup.add(r);
            radios.add(r);
            row2.add(r);
        }

        lblResult = new JLabel(title);
        lblResult.setForeground(AppTheme.TEXT_MUTED);
        lblResult.setFont(AppTheme.font(Font.PLAIN, 12));
        lblResult.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        row2.add(lblResult);

        add(row1);
        add(row2);
    }

    private static JComponent mutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setFont(AppTheme.font(Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        return lbl;
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
