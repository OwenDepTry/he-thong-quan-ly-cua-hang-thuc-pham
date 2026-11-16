package ui;

import dao.ThongKeDAO;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;
import javax.swing.BorderFactory;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ThongKePanel extends JPanel {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JComboBox<String> cboYear;
    private JComboBox<String> cboKieuThongKe;

    private StatCardPanel cardDoanhThu;
    private StatCardPanel cardHoaDon;
    private StatCardPanel cardPhieuNhap;

    private JTabbedPane tabbedPane;

    private JTable tblSanPham;
    private JTable tblNhanVien;
    private JTable tblKhachHang;

    private DefaultTableModel modelSanPham;
    private DefaultTableModel modelNhanVien;
    private DefaultTableModel modelKhachHang;

    private SimpleAxisChartPanel chartSanPham;
    private SimpleAxisChartPanel chartNhanVien;
    private SimpleAxisChartPanel chartKhachHang;

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(AppTheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopFilters(), BorderLayout.NORTH);
        add(createCenterContent(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createTopFilters() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel summary = new JPanel(new GridLayout(1, 3, 10, 10));
        summary.setOpaque(false);

        cardDoanhThu = new StatCardPanel("Tổng doanh thu", "0");
        cardHoaDon = new StatCardPanel("Tổng hóa đơn", "0");
        cardPhieuNhap = new StatCardPanel("Tổng phiếu nhập", "0");

        summary.add(cardDoanhThu);
        summary.add(cardHoaDon);
        summary.add(cardPhieuNhap);

        JPanel filter = new JPanel(new BorderLayout());
        filter.setBackground(AppTheme.SURFACE);
        filter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        filter.setPreferredSize(new Dimension(100, 54));

        JPanel left = new JPanel();
        left.setOpaque(false);

        JLabel lblKieu = new JLabel("Kiểu thống kê");
        lblKieu.setForeground(AppTheme.TEXT_MUTED);
        lblKieu.setFont(AppTheme.font(Font.BOLD, 13));

        cboKieuThongKe = new JComboBox<>(new String[]{"Theo năm", "Theo tháng", "Theo ngày"});
        cboKieuThongKe.setPreferredSize(new Dimension(120, 28));

        left.add(lblKieu);
        left.add(cboKieuThongKe);

        JPanel right = new JPanel();
        right.setOpaque(false);

        JLabel lblYear = new JLabel("Chọn năm");
        lblYear.setForeground(AppTheme.TEXT_MUTED);
        lblYear.setFont(AppTheme.font(Font.BOLD, 13));

        int currentYear = Year.now().getValue();
        cboYear = new JComboBox<>(new String[]{
            String.valueOf(currentYear - 2),
            String.valueOf(currentYear - 1),
            String.valueOf(currentYear),
            String.valueOf(currentYear + 1)
        });
        cboYear.setSelectedItem(String.valueOf(currentYear));
        cboYear.setPreferredSize(new Dimension(90, 28));

        JButton btnThongKe = new AppButton("Thống kê", "chart", AppButton.Variant.PRIMARY);
        btnThongKe.setFocusPainted(false);
        btnThongKe.addActionListener(e -> loadData());

        right.add(lblYear);
        right.add(cboYear);
        right.add(btnThongKe);

        filter.add(left, BorderLayout.WEST);
        filter.add(right, BorderLayout.EAST);

        wrapper.add(summary, BorderLayout.NORTH);
        wrapper.add(filter, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createCenterContent() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Sản phẩm", createSanPhamTab());
        tabbedPane.addTab("Nhân viên", createNhanVienTab());
        tabbedPane.addTab("Khách hàng", createKhachHangTab());

        center.add(tabbedPane, BorderLayout.CENTER);
        return center;
    }

    private JPanel createSanPhamTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        modelSanPham = new DefaultTableModel(
                new String[]{"Mã sản phẩm", "Tên sản phẩm", "Số lượng bán", "Doanh thu", "Lợi nhuận"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSanPham = new JTable(modelSanPham);

        AppTable.style(tblSanPham);

        chartSanPham = new SimpleAxisChartPanel("Doanh số");

        panel.add(createSectionTable(tblSanPham), BorderLayout.NORTH);
        panel.add(createSectionChart(chartSanPham), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNhanVienTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        modelNhanVien = new DefaultTableModel(
                new String[]{"Mã", "Họ", "Tên", "Số lượng hóa đơn", "Tổng số tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhanVien = new JTable(modelNhanVien);

        AppTable.style(tblNhanVien);

        chartNhanVien = new SimpleAxisChartPanel("Thống kê nhân viên");

        panel.add(createSectionTable(tblNhanVien), BorderLayout.NORTH);
        panel.add(createSectionChart(chartNhanVien), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKhachHangTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        modelKhachHang = new DefaultTableModel(
                new String[]{"Mã", "Họ", "Tên", "Số lượng sản phẩm đã mua", "Tổng số tiền"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblKhachHang = new JTable(modelKhachHang);

        AppTable.style(tblKhachHang);

        chartKhachHang = new SimpleAxisChartPanel("Thống kê khách hàng");

        panel.add(createSectionTable(tblKhachHang), BorderLayout.NORTH);
        panel.add(createSectionChart(chartKhachHang), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSectionTable(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.setPreferredSize(new Dimension(100, 190));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSectionChart(JPanel chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        int year = Integer.parseInt(cboYear.getSelectedItem().toString());

        double tongDoanhThu = thongKeDAO.getTongDoanhThu(year);
        int tongHoaDon = thongKeDAO.getTongSoHoaDon(year);
        int tongPhieuNhap = thongKeDAO.getTongSoPhieuNhap(year);

        cardDoanhThu.setValue(moneyFormat.format(tongDoanhThu));
        cardHoaDon.setValue(String.valueOf(tongHoaDon));
        cardPhieuNhap.setValue(String.valueOf(tongPhieuNhap));

        loadSanPham(year);
        loadNhanVien(year);
        loadKhachHang(year);
    }

    private void loadSanPham(int year) {
        List<Object[]> list = thongKeDAO.getThongKeSanPhamTheoNam(year);
        modelSanPham.setRowCount(0);

        for (Object[] row : list) {
            modelSanPham.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                moneyFormat.format(((Number) row[3]).doubleValue()),
                moneyFormat.format(((Number) row[4]).doubleValue())
            });
        }

        int[] doanhThuTheoQuy = thongKeDAO.getDoanhThuSanPhamTheoQuy(year);
        chartSanPham.setData(
                new String[]{"Quý 1", "Quý 2", "Quý 3", "Quý 4"},
                doanhThuTheoQuy
        );
        chartSanPham.setChartTitle("Doanh số");
    }

    private void loadNhanVien(int year) {
        List<Object[]> list = thongKeDAO.getThongKeNhanVienTheoNam(year);
        modelNhanVien.setRowCount(0);

        String[] labels = new String[list.size()];
        int[] values = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Object[] row = list.get(i);
            modelNhanVien.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                row[3],
                moneyFormat.format(((Number) row[4]).doubleValue())
            });

            labels[i] = String.valueOf(row[0]);
            values[i] = (int) Math.round(((Number) row[4]).doubleValue());
        }

        chartNhanVien.setData(labels, values);
        chartNhanVien.setChartTitle("Thống kê nhân viên");
    }

    private void loadKhachHang(int year) {
        List<Object[]> list = thongKeDAO.getThongKeKhachHangTheoNam(year);
        modelKhachHang.setRowCount(0);

        String[] labels = new String[list.size()];
        int[] values = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Object[] row = list.get(i);
            modelKhachHang.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                row[3],
                moneyFormat.format(((Number) row[4]).doubleValue())
            });

            labels[i] = String.valueOf(row[0]);
            values[i] = (int) Math.round(((Number) row[4]).doubleValue());
        }

        chartKhachHang.setData(labels, values);
        chartKhachHang.setChartTitle("Thống kê khách hàng");
    }

    private static class SimpleAxisChartPanel extends JPanel {

        private String title;
        private String[] labels = new String[0];
        private int[] values = new int[0];

        public SimpleAxisChartPanel(String title) {
            this.title = title;
            setPreferredSize(new Dimension(100, 320));
            setBackground(Color.WHITE);
        }

        public void setChartTitle(String title) {
            this.title = title;
            repaint();
        }

        public void setData(String[] labels, int[] values) {
            this.labels = labels == null ? new String[0] : labels;
            this.values = values == null ? new int[0] : values;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            AppTheme.applyTextHints(g2);
            g2.setColor(AppTheme.SURFACE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int left = 64;
            int right = 24;
            int top = 52;
            int bottom = 44;
            int chartW = getWidth() - left - right;
            int chartH = getHeight() - top - bottom;

            g2.setColor(AppTheme.TEXT_DARK);
            g2.setFont(AppTheme.font(Font.BOLD, 15));
            g2.drawString(title, left, 28);

            int max = 1;
            for (int v : values) {
                max = Math.max(max, v);
            }
            // Làm tròn trục Y lên số "đẹp" chia hết cho 4 vạch (vd 1.234 → 1.600)
            double rawStep = max / 4.0;
            double magnitude = Math.pow(10, Math.floor(Math.log10(Math.max(rawStep, 1))));
            long step = Math.max(1, (long) (Math.ceil(rawStep / magnitude) * magnitude));
            max = (int) Math.min(Integer.MAX_VALUE, step * 4);
            java.text.NumberFormat nf = java.text.NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));

            // Lưới ngang + nhãn trục Y
            g2.setFont(AppTheme.font(Font.PLAIN, 11));
            for (int i = 0; i <= 4; i++) {
                int y = top + chartH - (int) (chartH * i / 4.0);
                g2.setColor(i == 0 ? AppTheme.BORDER_STRONG : AppTheme.TABLE_GRID);
                g2.drawLine(left, y, left + chartW, y);
                String lbl = nf.format(Math.round(max * i / 4.0));
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.drawString(lbl, left - 8 - g2.getFontMetrics().stringWidth(lbl), y + 4);
            }

            if (values.length == 0) {
                g2.setColor(AppTheme.TEXT_MUTED);
                g2.setFont(AppTheme.font(Font.PLAIN, 14));
                String msg = "Không có dữ liệu";
                g2.drawString(msg, left + (chartW - g2.getFontMetrics().stringWidth(msg)) / 2, top + chartH / 2);
                g2.dispose();
                return;
            }

            int n = values.length;
            int slot = chartW / n;
            int barWidth = Math.max(12, Math.min(56, (int) (slot * 0.55)));

            for (int i = 0; i < n; i++) {
                int barHeight = (int) ((values[i] * 1.0 / max) * (chartH - 18));
                int x = left + i * slot + (slot - barWidth) / 2;
                int y = top + chartH - barHeight;

                g2.setColor(AppTheme.PRIMARY);
                if (barHeight > 0) {
                    g2.fillRoundRect(x, y, barWidth, barHeight + 6, 6, 6);
                    g2.setColor(AppTheme.SURFACE);
                    g2.fillRect(x, top + chartH + 1, barWidth, 6);
                }

                g2.setFont(AppTheme.font(Font.BOLD, 11));
                g2.setColor(AppTheme.TEXT_DARK);
                String valueText = nf.format(values[i]);
                int vw = g2.getFontMetrics().stringWidth(valueText);
                g2.drawString(valueText, x + (barWidth - vw) / 2, y - 6);

                if (labels != null && i < labels.length) {
                    g2.setFont(AppTheme.font(Font.PLAIN, 12));
                    g2.setColor(AppTheme.TEXT_MUTED);
                    String lb = labels[i];
                    int lw = g2.getFontMetrics().stringWidth(lb);
                    g2.drawString(lb, x + (barWidth - lw) / 2, top + chartH + 22);
                }
            }
            g2.dispose();
        }
    }
}