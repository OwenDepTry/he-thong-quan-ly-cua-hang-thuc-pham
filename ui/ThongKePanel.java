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
        setBackground(new Color(245, 245, 245));
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
        filter.setBackground(new Color(0, 180, 50));
        filter.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        filter.setPreferredSize(new Dimension(100, 54));

        JPanel left = new JPanel();
        left.setOpaque(false);

        JLabel lblKieu = new JLabel("Kiểu thống kê");
        lblKieu.setForeground(Color.WHITE);
        lblKieu.setFont(new Font("Arial", Font.BOLD, 13));

        cboKieuThongKe = new JComboBox<>(new String[]{"Theo năm", "Theo tháng", "Theo ngày"});
        cboKieuThongKe.setPreferredSize(new Dimension(120, 28));

        left.add(lblKieu);
        left.add(cboKieuThongKe);

        JPanel right = new JPanel();
        right.setOpaque(false);

        JLabel lblYear = new JLabel("Chọn năm");
        lblYear.setForeground(Color.WHITE);
        lblYear.setFont(new Font("Arial", Font.BOLD, 13));

        int currentYear = Year.now().getValue();
        cboYear = new JComboBox<>(new String[]{
            String.valueOf(currentYear - 2),
            String.valueOf(currentYear - 1),
            String.valueOf(currentYear),
            String.valueOf(currentYear + 1)
        });
        cboYear.setSelectedItem(String.valueOf(currentYear));
        cboYear.setPreferredSize(new Dimension(90, 28));

        JButton btnThongKe = new JButton("Thống kê");
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
        tblSanPham.setRowHeight(24);

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
        tblNhanVien.setRowHeight(24);

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
        tblKhachHang.setRowHeight(24);

        chartKhachHang = new SimpleAxisChartPanel("Thống kê khách hàng");

        panel.add(createSectionTable(tblKhachHang), BorderLayout.NORTH);
        panel.add(createSectionChart(chartKhachHang), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSectionTable(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.setPreferredSize(new Dimension(100, 190));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSectionChart(JPanel chart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
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

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int left = 60;
            int right = 30;
            int top = 50;
            int bottom = 60;

            int chartW = getWidth() - left - right;
            int chartH = getHeight() - top - bottom;

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            int titleW = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleW) / 2, 28);

            g2.setColor(new Color(230, 230, 230));
            g2.fillRect(left, top, chartW, chartH);

            g2.setColor(Color.GRAY);
            g2.drawRect(left, top, chartW, chartH);

            if (values.length == 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Không có dữ liệu", left + 20, top + 30);
                return;
            }

            int max = 1;
            for (int v : values) {
                if (v > max) max = v;
            }

            int n = values.length;
            int barWidth = Math.max(20, chartW / Math.max(n * 2, 1));
            int gap = Math.max(10, (chartW - n * barWidth) / Math.max(n + 1, 1));

            g2.setColor(new Color(255, 80, 80));

            for (int i = 0; i < n; i++) {
                int barHeight = (int) ((values[i] * 1.0 / max) * (chartH - 30));
                int x = left + gap + i * (barWidth + gap);
                int y = top + chartH - barHeight;

                g2.fillRect(x, y, barWidth, barHeight);

                g2.setColor(Color.BLACK);
                String valueText = String.valueOf(values[i]);
                int vw = g2.getFontMetrics().stringWidth(valueText);
                g2.drawString(valueText, x + (barWidth - vw) / 2, y - 5);

                if (labels != null && i < labels.length) {
                    String lb = labels[i];
                    int lw = g2.getFontMetrics().stringWidth(lb);
                    g2.drawString(lb, x + (barWidth - lw) / 2, top + chartH + 20);
                }

                g2.setColor(new Color(255, 80, 80));
            }

            g2.setColor(Color.BLACK);
            g2.drawLine(left, top + chartH, left + chartW, top + chartH);
            g2.drawLine(left, top, left, top + chartH);
        }
    }
}