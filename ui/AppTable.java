package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.LayerUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Định dạng bảng dùng chung: dòng cao, xen kẽ màu, số căn phải, tiền tệ kiểu "125.000 ₫",
 * ngày dd/MM/yyyy, header in đậm và chữ "Không có dữ liệu" khi bảng trống.
 * Chỉ thay đổi CÁCH HIỂN THỊ – dữ liệu trong TableModel giữ nguyên nên code cũ không bị ảnh hưởng.
 */
public final class AppTable {

    private AppTable() {
    }

    /** Tên cột chứa các từ này sẽ được hiển thị dạng tiền. */
    private static final Pattern MONEY_COLUMN = Pattern.compile(
            "(?iu).*(giá|tiền|tổng|doanh thu|lợi nhuận|lương|chi phí|thanh toán).*");
    /** Cột phần trăm / số lượng không phải tiền dù có chữ "giá" (ví dụ "% giảm giá"). */
    private static final Pattern NOT_MONEY_COLUMN = Pattern.compile("(?iu).*(%|phần trăm|mã|số lượng).*");

    private static final NumberFormat MONEY = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final NumberFormat NUMBER = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        MONEY.setMaximumFractionDigits(0);
        NUMBER.setMaximumFractionDigits(2);
    }

    public static void style(JTable table) {
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(AppTheme.TABLE_GRID);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(AppTheme.PRIMARY_SOFT);
        table.setSelectionForeground(AppTheme.TEXT_DARK);
        table.setForeground(AppTheme.TEXT_DARK);
        table.setFont(AppTheme.font(Font.PLAIN, 13));
        table.setFillsViewportHeight(true);
        table.setBackground(AppTheme.SURFACE);

        CellRenderer renderer = new CellRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(Number.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
        table.setDefaultRenderer(Double.class, renderer);
        table.setDefaultRenderer(Long.class, renderer);
        table.setDefaultRenderer(BigDecimal.class, renderer);
        table.setDefaultRenderer(java.util.Date.class, renderer);

        installAutoFit(table);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setReorderingAllowed(false);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
            header.setDefaultRenderer(new HeaderRenderer(header.getDefaultRenderer()));
        }
    }

    /** Định dạng một giá trị tiền để hiển thị ngoài bảng (ô nhập, nhãn…): 52500.0 → "52.500 ₫". */
    public static String money(Object value) {
        Double n = CellRenderer.toNumber(value);
        return n == null ? (value == null ? "" : String.valueOf(value)) : MONEY.format(n) + " ₫";
    }

    /** Định dạng ngày/giờ để hiển thị: 2026-11-04 → 04/11/2026. */
    public static String display(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().format(DATE_TIME);
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().format(DATE);
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DATE_TIME);
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DATE);
        }
        String s = value.toString();
        try {
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(s).format(DATE);
            }
            if (s.matches("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}(:\\d{2}(\\.\\d+)?)?")) {
                return LocalDateTime.parse(s.replace(' ', 'T').replaceAll("\\.\\d+$", "")).format(DATE_TIME);
            }
        } catch (Exception ignored) {
            // giữ nguyên chuỗi gốc
        }
        return s;
    }

    /**
     * Tự chia độ rộng cột theo nội dung (cột tên dài được rộng hơn cột mã),
     * chạy lại mỗi khi dữ liệu thay đổi.
     */
    private static void installAutoFit(JTable table) {
        final boolean[] pending = {false};
        Runnable fit = () -> {
            pending[0] = false;
            autoFit(table);
        };
        table.getModel().addTableModelListener(e -> {
            if (!pending[0]) {
                pending[0] = true;
                javax.swing.SwingUtilities.invokeLater(fit);
            }
        });
        table.addPropertyChangeListener("model", e -> autoFit(table));
        javax.swing.SwingUtilities.invokeLater(fit);
    }

    public static void autoFit(JTable table) {
        javax.swing.table.TableColumnModel cols = table.getColumnModel();
        int rows = Math.min(table.getRowCount(), 200);
        for (int c = 0; c < cols.getColumnCount(); c++) {
            javax.swing.table.TableColumn col = cols.getColumn(c);
            TableCellRenderer hr = col.getHeaderRenderer() != null ? col.getHeaderRenderer()
                    : table.getTableHeader() == null ? null : table.getTableHeader().getDefaultRenderer();
            int w = 60;
            if (hr != null) {
                w = Math.max(w, hr.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, -1, c)
                        .getPreferredSize().width + 8);
            }
            for (int r = 0; r < rows; r++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(r, c), r, c);
                w = Math.max(w, comp.getPreferredSize().width + 8);
            }
            // Cột ngắn (mã, số, tiền, ngày) không bị co lại → không bị "…"; cột chữ dài co giãn thay
            boolean fixed = isMoneyColumn(table, c);
            if (!fixed && rows > 0) {
                Object v = table.getValueAt(0, c);
                fixed = v instanceof Number || v instanceof java.util.Date || v instanceof LocalDate
                        || v instanceof LocalDateTime || (v != null && v.toString().matches("\\d{4}-\\d{2}-\\d{2}.*"));
            }
            col.setMinWidth(fixed ? Math.min(w, 130) : 50);
            col.setPreferredWidth(Math.min(w, 360));
        }
    }

    /**
     * Bấm tiêu đề cột để sắp xếp: tăng dần → giảm dần → về thứ tự ban đầu.
     * Số/tiền/ngày so sánh theo giá trị, chữ theo bảng chữ cái tiếng Việt.
     * Chỉ dùng cho bảng mà code đọc dữ liệu qua table.getValueAt(...) (JTable tự quy đổi dòng).
     */
    public static void enableSorting(JTable table) {
        javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter =
                new javax.swing.table.TableRowSorter<javax.swing.table.TableModel>(table.getModel()) {
            @Override
            public void toggleSortOrder(int column) {
                java.util.List<? extends javax.swing.RowSorter.SortKey> keys = getSortKeys();
                if (!keys.isEmpty() && keys.get(0).getColumn() == column
                        && keys.get(0).getSortOrder() == javax.swing.SortOrder.DESCENDING) {
                    setSortKeys(null); // lần bấm thứ 3: về thứ tự gốc
                    return;
                }
                super.toggleSortOrder(column);
            }
        };
        java.util.Comparator<Object> cmp = new SmartComparator();
        for (int c = 0; c < table.getModel().getColumnCount(); c++) {
            sorter.setComparator(c, cmp);
        }
        sorter.setMaxSortKeys(1);
        table.setRowSorter(sorter);
        if (table.getTableHeader() != null) {
            table.getTableHeader().setToolTipText("Bấm tiêu đề cột để sắp xếp · Bấm đúp vào dòng để sửa · Delete để xóa");
        }
    }

    /** So sánh "thông minh": số với số, ngày với ngày, còn lại theo chữ tiếng Việt. */
    private static final class SmartComparator implements java.util.Comparator<Object> {

        private final java.text.Collator collator = java.text.Collator.getInstance(Locale.forLanguageTag("vi-VN"));

        SmartComparator() {
            collator.setStrength(java.text.Collator.SECONDARY);
        }

        @Override
        public int compare(Object a, Object b) {
            if (a == null || b == null) {
                return a == b ? 0 : (a == null ? -1 : 1);
            }
            Double na = CellRenderer.toNumber(a);
            Double nb = CellRenderer.toNumber(b);
            if (na != null && nb != null) {
                return Double.compare(na, nb);
            }
            LocalDate da = toDate(a);
            LocalDate db = toDate(b);
            if (da != null && db != null) {
                return da.compareTo(db);
            }
            return collator.compare(String.valueOf(a).trim(), String.valueOf(b).trim());
        }

        private static LocalDate toDate(Object o) {
            if (o instanceof java.sql.Date) {
                return ((java.sql.Date) o).toLocalDate();
            }
            if (o instanceof java.sql.Timestamp) {
                return ((java.sql.Timestamp) o).toLocalDateTime().toLocalDate();
            }
            if (o instanceof LocalDate) {
                return (LocalDate) o;
            }
            String s = String.valueOf(o).trim();
            try {
                if (s.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                    return LocalDate.parse(s.substring(0, 10));
                }
                if (s.matches("\\d{2}/\\d{2}/\\d{4}.*")) {
                    return LocalDate.parse(s.substring(0, 10), DATE);
                }
            } catch (Exception ignored) {
                // không phải ngày
            }
            return null;
        }
    }

    /** Bọc JScrollPane để hiển thị "Không có dữ liệu" khi bảng trống. */
    public static JComponent withEmptyState(JScrollPane scroll, JTable table, String message) {
        return new JLayer<>(scroll, new EmptyStateUI(table, message));
    }

    static boolean isMoneyColumn(JTable table, int column) {
        String name = table.getColumnName(column);
        return name != null && MONEY_COLUMN.matcher(name).matches() && !NOT_MONEY_COLUMN.matcher(name).matches();
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Object display = value;
            int align = SwingConstants.LEFT;

            if (isMoneyColumn(table, column)) {
                Double number = toNumber(value);
                if (number != null) {
                    display = MONEY.format(number) + " ₫";
                }
                align = SwingConstants.RIGHT;
            } else if (value instanceof Number) {
                display = NUMBER.format(value);
                align = SwingConstants.RIGHT;
            } else if (value instanceof java.sql.Timestamp) {
                display = ((java.sql.Timestamp) value).toLocalDateTime().format(DATE_TIME);
            } else if (value instanceof java.sql.Date) {
                display = ((java.sql.Date) value).toLocalDate().format(DATE);
            } else if (value instanceof java.util.Date) {
                display = new SimpleDateFormat("dd/MM/yyyy").format((java.util.Date) value);
            } else if (value instanceof LocalDate) {
                display = ((LocalDate) value).format(DATE);
            } else if (value instanceof LocalDateTime) {
                display = ((LocalDateTime) value).format(DATE_TIME);
            }

            JLabel c = (JLabel) super.getTableCellRendererComponent(table, display, isSelected, false, row, column);
            c.setHorizontalAlignment(align);
            c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? AppTheme.SURFACE : AppTheme.SURFACE_ALT);
                c.setForeground(AppTheme.TEXT_DARK);
            }
            return c;
        }

        static Double toNumber(Object value) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value == null) {
                return null;
            }
            String s = value.toString().trim();
            try {
                if (s.matches("-?\\d+(\\.\\d+)?")) {
                    return Double.parseDouble(s);              // "32000" hoặc "32000.0"
                }
                if (s.matches("-?\\d{1,3}([.,]\\d{3})+\\s*(đ|₫|VND|vnd)?")) {
                    return Double.parseDouble(s.replaceAll("[^\\d-]", "")); // "35,000" / "35.000 đ"
                }
                return null; // không phải số: giữ nguyên
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static final class HeaderRenderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        HeaderRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setFont(AppTheme.font(Font.BOLD, 12));
                l.setForeground(AppTheme.TEXT_MUTED);
                l.setBackground(AppTheme.SURFACE_ALT);
                l.setOpaque(true);
                boolean numeric = isMoneyColumn(table, column)
                        || (table.getRowCount() > 0 && table.getValueAt(0, column) instanceof Number);
                l.setHorizontalAlignment(numeric ? SwingConstants.RIGHT : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            }
            return c;
        }
    }

    private static final class EmptyStateUI extends LayerUI<JScrollPane> {

        private final JTable table;
        private final String message;

        EmptyStateUI(JTable table, String message) {
            this.table = table;
            this.message = message;
            table.getModel().addTableModelListener(e -> table.repaint());
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            if (table.getRowCount() > 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            AppTheme.applyTextHints(g2);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int headerH = table.getTableHeader() == null ? 0 : table.getTableHeader().getHeight();
            int cx = c.getWidth() / 2;
            int cy = headerH + (c.getHeight() - headerH) / 2;

            AppIcons.get("box", 40, AppTheme.BORDER_STRONG).paintIcon(c, g2, cx - 20, cy - 46);

            g2.setFont(AppTheme.font(Font.PLAIN, 14));
            g2.setColor(AppTheme.TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(message, cx - fm.stringWidth(message) / 2, cy + 14);
            g2.dispose();
        }
    }
}
