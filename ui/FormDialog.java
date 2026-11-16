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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Khung chung cho các hộp thoại Thêm/Sửa:
 * - phần đầu có icon, tiêu đề, mô tả;
 * - ô nhập xếp 2 cột, nhãn nằm trên ô, ô bắt buộc có dấu * đỏ;
 * - lỗi hiện ngay dưới ô sai (chữ đỏ + viền đỏ), tự mất khi người dùng gõ sửa;
 * - chân hộp thoại có Hủy / Lưu, Enter = Lưu, Esc = Hủy.
 *
 * Lớp con: gọi {@link #addSection}, {@link #addField}… trong constructor, rồi {@link #finish()};
 * cài đặt {@link #validateForm()} – gọi {@link #error} cho mỗi ô sai và trả về true nếu hợp lệ.
 */
public abstract class FormDialog extends JDialog {

    private static final int FIELD_HEIGHT = 34;
    private static final int COLUMN_WIDTH = 250;
    private static final DateTimeFormatter VN_DATE =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final JPanel form = new JPanel(new GridBagLayout());
    private final JLabel lblSubtitle = new JLabel();
    private final Map<JComponent, JLabel> errorLabels = new LinkedHashMap<>();
    private final Map<JComponent, Border> normalBorders = new LinkedHashMap<>();
    private int row;
    private int col;
    private boolean saved;

    protected FormDialog(Frame owner, String title, String subtitle, String iconName) {
        super(owner, title, true);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.SURFACE);
        setContentPane(root);

        root.add(buildHeader(title, subtitle, iconName), BorderLayout.NORTH);

        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(8, 22, 6, 22));
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.NORTH);
        root.add(formWrap, BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    // ================== Dựng giao diện ==================

    private JComponent buildHeader(String title, String subtitle, String iconName) {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));

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
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.font(Font.BOLD, 18));
        lblTitle.setForeground(AppTheme.TEXT_DARK);
        lblSubtitle.setText(subtitle);
        lblSubtitle.setFont(AppTheme.font(Font.PLAIN, 13));
        lblSubtitle.setForeground(AppTheme.TEXT_MUTED);
        text.add(Box.createVerticalStrut(2));
        text.add(lblTitle);
        text.add(Box.createVerticalStrut(4));
        text.add(lblSubtitle);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(AppTheme.SURFACE_ALT);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel hint = new JLabel("Enter để lưu · Esc để hủy");
        hint.setFont(AppTheme.font(Font.PLAIN, 12));
        hint.setForeground(AppTheme.TEXT_MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        AppButton btnCancel = new AppButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        AppButton btnSave = new AppButton("Lưu", "check", AppButton.Variant.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 36));
        btnSave.addActionListener(e -> onSave());
        getRootPane().setDefaultButton(btnSave);

        footer.add(hint);
        footer.add(btnCancel);
        footer.add(btnSave);
        return footer;
    }

    protected void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }

    /** Tiêu đề nhóm, chiếm cả hàng. */
    protected void addSection(String title) {
        if (col != 0) {
            row++;
            col = 0;
        }
        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setFont(AppTheme.font(Font.BOLD, 11));
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(row == 0 ? 4 : 12, 0, 6, 0),
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 6, 4, 6);
        form.add(lbl, gbc);
    }

    /** Thêm một ô vào cột kế tiếp (2 cột/hàng). */
    protected void addField(String label, JComponent field, boolean required) {
        addField(label, field, required, null, false);
    }

    /**
     * @param hint     gợi ý nhỏ dưới ô (null nếu không có)
     * @param fullRow  true để ô chiếm cả hàng (ví dụ Tên, Địa chỉ, Mô tả)
     */
    protected void addField(String label, JComponent field, boolean required, String hint, boolean fullRow) {
        if (fullRow && col != 0) {
            row++;
            col = 0;
        }

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel("<html>" + label + (required ? " <font color='#dc2626'>*</font>" : "") + "</html>");
        lbl.setFont(AppTheme.font(Font.BOLD, 13));
        lbl.setForeground(AppTheme.TEXT_DARK);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        lbl.setLabelFor(field);

        int width = fullRow ? COLUMN_WIDTH * 2 + 12 : COLUMN_WIDTH;
        Dimension size = new Dimension(width, FIELD_HEIGHT);
        field.setPreferredSize(size);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        field.setAlignmentX(LEFT_ALIGNMENT);
        if (field instanceof JTextField) {
            // giữ placeholder lớp con đã đặt (VD "dd/mm/yyyy")
            AppTheme.styleField(field, (String) field.getClientProperty("JTextField.placeholderText"));
        }
        normalBorders.put(field, field.getBorder());

        // Dòng dưới ô: gợi ý (xám) hoặc lỗi (đỏ). Luôn giữ chỗ để hộp thoại không bị nhảy khi hiện lỗi.
        JLabel below = new JLabel(hint == null ? " " : hint);
        below.setFont(AppTheme.font(Font.PLAIN, 11));
        below.setForeground(AppTheme.TEXT_MUTED);
        below.setAlignmentX(LEFT_ALIGNMENT);
        below.setBorder(BorderFactory.createEmptyBorder(3, 1, 0, 0));
        below.putClientProperty("hint", hint == null ? " " : hint);
        Dimension belowSize = new Dimension(width, 18);
        below.setPreferredSize(belowSize);
        below.setMinimumSize(belowSize);
        below.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        errorLabels.put(field, below);

        if (field instanceof JTextComponent) {
            ((JTextComponent) field).getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    clearError(field);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    clearError(field);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    clearError(field);
                }
            });
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).addActionListener(e -> clearError(field));
        }

        cell.add(lbl);
        cell.add(field);
        cell.add(below);
        cell.setPreferredSize(new Dimension(width, cell.getPreferredSize().height));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = fullRow ? 0 : col;
        gbc.gridy = row;
        gbc.gridwidth = fullRow ? 2 : 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2, 6, 4, 6);
        form.add(cell, gbc);

        if (fullRow || col == 1) {
            row++;
            col = 0;
        } else {
            col = 1;
        }
    }

    /** Gọi ở cuối constructor của lớp con. */
    protected void finish() {
        pack();
        setLocationRelativeTo(getOwner());
    }

    /** Khóa ô (ví dụ mã khi sửa): nền xám, không sửa được. */
    protected void lockField(JTextField field, String reason) {
        field.setEditable(false);
        field.setFocusable(false);
        field.setBackground(AppTheme.SURFACE_ALT);
        field.setForeground(AppTheme.TEXT_MUTED);
        JLabel below = errorLabels.get(field);
        if (below != null && reason != null) {
            below.setText(reason);
            below.putClientProperty("hint", reason);
        }
    }

    // ================== Kiểm tra dữ liệu ==================

    /** Lớp con kiểm tra từng ô, gọi error(...) cho ô sai. Trả về true nếu tất cả hợp lệ. */
    protected abstract boolean validateForm();

    private void onSave() {
        for (JComponent f : errorLabels.keySet()) {
            clearError(f);
        }
        if (validateForm()) {
            saved = true;
            dispose();
            return;
        }
        // Đưa con trỏ tới ô lỗi đầu tiên
        for (Map.Entry<JComponent, JLabel> e : errorLabels.entrySet()) {
            if (Boolean.TRUE.equals(e.getValue().getClientProperty("error"))) {
                e.getKey().requestFocusInWindow();
                break;
            }
        }
    }

    /** Đánh dấu ô sai. Luôn trả về false để viết gọn: {@code ok &= error(...)}. */
    protected boolean error(JComponent field, String message) {
        JLabel below = errorLabels.get(field);
        if (below != null) {
            below.setText(message);
            below.setForeground(AppTheme.DANGER);
            below.putClientProperty("error", Boolean.TRUE);
        }
        field.putClientProperty("JComponent.outline", "error"); // FlatLaf: viền đỏ
        if (!AppTheme.isFlatLaf()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.DANGER, 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }
        return false;
    }

    private void clearError(JComponent field) {
        JLabel below = errorLabels.get(field);
        if (below == null || !Boolean.TRUE.equals(below.getClientProperty("error"))) {
            return;
        }
        below.setText((String) below.getClientProperty("hint"));
        below.setForeground(AppTheme.TEXT_MUTED);
        below.putClientProperty("error", null);
        field.putClientProperty("JComponent.outline", null);
        if (!AppTheme.isFlatLaf()) {
            field.setBorder(normalBorders.get(field));
        }
    }

    public boolean isSaved() {
        return saved;
    }

    // ================== Tiện ích cho lớp con ==================

    protected static String text(JTextField f) {
        if (f instanceof JPasswordField) {
            return new String(((JPasswordField) f).getPassword()).trim();
        }
        return f.getText().trim();
    }

    protected boolean required(JTextField f, String name) {
        return !text(f).isEmpty() || error(f, name + " không được để trống");
    }

    protected boolean phone(JTextField f) {
        String s = text(f).replaceAll("[\\s.-]", "");
        if (s.isEmpty()) {
            return error(f, "Số điện thoại không được để trống");
        }
        return s.matches("\\d{8,11}") || error(f, "Số điện thoại phải gồm 8–11 chữ số");
    }

    /** Ô ngày: người dùng nhập dd/MM/yyyy (hoặc yyyy-MM-dd). */
    protected boolean date(JTextField f, String name) {
        if (text(f).isEmpty()) {
            return error(f, name + " không được để trống");
        }
        return parseDate(text(f)) != null || error(f, "Ngày không hợp lệ, VD: 16/11/2026");
    }

    /** Ô số (tiền, lương…): chấp nhận "8.000.000", "8,000,000", "8000000". */
    protected boolean number(JTextField f, String name, double min, double max) {
        if (text(f).isEmpty()) {
            return error(f, name + " không được để trống");
        }
        Double v = parseNumber(text(f));
        if (v == null) {
            return error(f, name + " phải là số");
        }
        if (v < min) {
            return error(f, min == 0 ? name + " không được âm" : name + " phải từ " + fmt(min) + " trở lên");
        }
        if (v > max) {
            return error(f, name + " không được lớn hơn " + fmt(max));
        }
        return true;
    }

    protected boolean integer(JTextField f, String name, int min) {
        if (text(f).isEmpty()) {
            return error(f, name + " không được để trống");
        }
        Double v = parseNumber(text(f));
        if (v == null || v != Math.floor(v)) {
            return error(f, name + " phải là số nguyên");
        }
        return v >= min || error(f, min == 0 ? name + " không được âm" : name + " phải từ " + min + " trở lên");
    }

    static LocalDate parseDate(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim().replace('-', '/').replace('.', '/');
        try {
            if (s.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                String[] p = s.split("/");
                return LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
            }
            if (s.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                String[] p = s.split("/");
                return LocalDate.parse(String.format("%02d/%02d/%s", Integer.parseInt(p[0]),
                        Integer.parseInt(p[1]), p[2]), VN_DATE);
            }
        } catch (java.time.DateTimeException | NumberFormatException e) {
            return null;
        }
        return null;
    }

    /** Ngày lưu CSDL (yyyy-MM-dd) → hiển thị dd/MM/yyyy. */
    protected static String toDisplayDate(String iso) {
        LocalDate d = parseDate(iso);
        return d == null ? (iso == null || "null".equals(iso) ? "" : iso) : d.format(VN_DATE);
    }

    /** Ô ngày → chuỗi yyyy-MM-dd để lưu CSDL (giống định dạng cũ). */
    protected static String isoDate(JTextField f) {
        LocalDate d = parseDate(text(f));
        return d == null ? text(f) : d.toString();
    }

    static Double parseNumber(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim().replace("₫", "").replace("đ", "").replace("VNĐ", "").replace(" ", "").replace(" ", "");
        if (t.matches("-?\\d{1,3}([.,]\\d{3})+")) {
            t = t.replace(".", "").replace(",", ""); // 8.000.000 / 8,000,000
        } else if (t.matches("-?\\d+,\\d+")) {
            t = t.replace(',', '.');                  // 12,5 → 12.5
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Số → chuỗi không phân cách để lưu (8000000, 12.5). */
    protected static String plainNumber(JTextField f) {
        Double v = parseNumber(text(f));
        if (v == null) {
            return text(f);
        }
        return v == Math.floor(v) && !Double.isInfinite(v) ? String.valueOf(v.longValue()) : String.valueOf(v);
    }

    /** Hiển thị số trong ô cho dễ đọc: 8000000.0 → 8.000.000 */
    protected static String displayNumber(Object value) {
        Double v = value == null ? null : parseNumber(String.valueOf(value));
        if (v == null) {
            return value == null || "null".equals(String.valueOf(value)) ? "" : String.valueOf(value);
        }
        return fmt(v);
    }

    private static String fmt(double v) {
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("vi-VN"));
        nf.setMaximumFractionDigits(2);
        return nf.format(v);
    }

    // ================== Combobox: hiển thị tiếng Việt, lưu giá trị gốc ==================

    /** Một lựa chọn: value lưu vào CSDL, label hiển thị cho người dùng. */
    public static final class Choice {

        final String value;
        final String label;

        public Choice(String value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    protected static JComboBox<Choice> choices(String... valueLabelPairs) {
        JComboBox<Choice> cbo = new JComboBox<>();
        for (int i = 0; i + 1 < valueLabelPairs.length; i += 2) {
            cbo.addItem(new Choice(valueLabelPairs[i], valueLabelPairs[i + 1]));
        }
        return cbo;
    }

    protected static void select(JComboBox<Choice> cbo, String value) {
        if (value == null) {
            return;
        }
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).value.equalsIgnoreCase(value.trim())) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
        // Giá trị lạ trong CSDL: thêm vào để không mất dữ liệu khi lưu lại
        Choice extra = new Choice(value, value);
        cbo.addItem(extra);
        cbo.setSelectedItem(extra);
    }

    protected static String value(JComboBox<Choice> cbo) {
        Object o = cbo.getSelectedItem();
        return o instanceof Choice ? ((Choice) o).value : String.valueOf(o);
    }

    /** Đặt chữ gợi ý mờ bên trong ô (hiện khi ô trống, cần FlatLaf). */
    protected static <T extends JTextField> T placeholder(T field, String text) {
        field.putClientProperty("JTextField.placeholderText", text);
        return field;
    }
}
