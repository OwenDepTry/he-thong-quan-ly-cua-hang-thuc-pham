package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ThongKePanel extends JPanel {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    private JLabel lblTongDoanhThu;
    private JLabel lblTongHoaDon;
    private JLabel lblTongSanPhamBan;
    private JLabel lblTongKhachHang;
    private JLabel lblDoanhThuNgay;
    private JLabel lblDoanhThuThang;
    private JLabel lblDoanhThuNam;

    private JTable table7Ngay;
    private JTable tableNhanVien;
    private JTable tableKhachHang;
    private JTable tableSanPham;
    private JTable tableKhoangNgay;

    private DefaultTableModel model7Ngay;
    private DefaultTableModel modelNhanVien;
    private DefaultTableModel modelKhachHang;
    private DefaultTableModel modelSanPham;
    private DefaultTableModel modelKhoangNgay;

    private JFormattedTextField txtTuNgay;
    private JFormattedTextField txtDenNgay;
    private JLabel lblTongDoanhThuLoc;
    private JLabel lblTongHoaDonLoc;

    public ThongKePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 245));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        initUI();
        loadAllData();
    }

    private void initUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 125, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblTitle = new JLabel("THỐNG KÊ - BÁO CÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Theo dõi doanh thu, hóa đơn, khách hàng, nhân viên và sản phẩm bán chạy");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(235, 255, 235));

        JPanel text = new JPanel(new java.awt.GridLayout(2, 1));
        text.setOpaque(false);
        text.add(lblTitle);
        text.add(lblSub);

        panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setOpaque(false);

        main.add(createSummaryPanel(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));
        tabs.addTab("7 ngày gần nhất", create7NgayPanel());
        tabs.addTab("Theo nhân viên", createNhanVienPanel());
        tabs.addTab("Theo khách hàng", createKhachHangPanel());
        tabs.addTab("Sản phẩm bán chạy", createSanPhamPanel());
        tabs.addTab("Lọc theo ngày", createLocTheoNgayPanel());

        main.add(tabs, BorderLayout.CENTER);
        return main;
    }

    private JPanel createSummaryPanel() {
        JPanel wrap = new JPanel(new BorderLayout(12, 12));
        wrap.setOpaque(false);

        JPanel cardPanel = new JPanel(new java.awt.GridLayout(2, 3, 12, 12));
        cardPanel.setOpaque(false);

        lblTongDoanhThu = createValueLabel();
        lblTongHoaDon = createValueLabel();
        lblTongSanPhamBan = createValueLabel();
        lblTongKhachHang = createValueLabel();
        lblDoanhThuNgay = createValueLabel();
        lblDoanhThuThang = createValueLabel();
        lblDoanhThuNam = createValueLabel();

        cardPanel.add(createStatCard("Tổng doanh thu", lblTongDoanhThu));
        cardPanel.add(createStatCard("Tổng hóa đơn", lblTongHoaDon));
        cardPanel.add(createStatCard("Tổng sản phẩm đã bán", lblTongSanPhamBan));
        cardPanel.add(createStatCard("Tổng khách hàng", lblTongKhachHang));
        cardPanel.add(createStatCard("Doanh thu hôm nay", lblDoanhThuNgay));
        cardPanel.add(createStatCard("Doanh thu tháng nay", lblDoanhThuThang));

        JPanel cardNam = new JPanel(new BorderLayout());
        cardNam.setOpaque(false);
        cardNam.add(createStatCard("Doanh thu năm nay", lblDoanhThuNam), BorderLayout.CENTER);

        wrap.add(cardPanel, BorderLayout.CENTER);
        wrap.add(cardNam, BorderLayout.SOUTH);

        return wrap;
    }

    private JPanel create7NgayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Thống kê doanh thu 7 ngày gần nhất");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(new Color(46, 125, 50));

        model7Ngay = new DefaultTableModel(
                new String[]{"Ngày", "Số hóa đơn", "Doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table7Ngay = new JTable(model7Ngay);
        configTable(table7Ngay);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table7Ngay), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Thống kê hiệu quả bán hàng theo nhân viên");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(new Color(46, 125, 50));

        modelNhanVien = new DefaultTableModel(
                new String[]{"Mã NV", "Họ tên", "Số hóa đơn", "Tổng doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableNhanVien = new JTable(modelNhanVien);
        configTable(tableNhanVien);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Thống kê mức độ mua hàng theo khách hàng");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(new Color(46, 125, 50));

        modelKhachHang = new DefaultTableModel(
                new String[]{"Mã KH", "Họ tên", "Số lần mua", "Tổng chi tiêu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKhachHang = new JTable(modelKhachHang);
        configTable(tableKhachHang);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableKhachHang), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Top sản phẩm bán chạy");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(new Color(46, 125, 50));

        modelSanPham = new DefaultTableModel(
                new String[]{"Mã SP", "Tên sản phẩm", "Số lượng đã bán", "Doanh thu SP"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableSanPham = new JTable(modelSanPham);
        configTable(tableSanPham);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableSanPham), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLocTheoNgayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);

        JLabel lblTitle = new JLabel("Lọc doanh thu theo khoảng ngày");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(46, 125, 50));

        top.add(lblTitle);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        txtTuNgay = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDenNgay = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));

        styleDateField(txtTuNgay);
        styleDateField(txtDenNgay);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setFont(new Font("Arial", Font.BOLD, 14));
        btnLoc.setBackground(new Color(46, 125, 50));
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);

        JButton btnTaiLai = new JButton("Tải lại");
        btnTaiLai.setFont(new Font("Arial", Font.BOLD, 14));
        btnTaiLai.setBackground(new Color(97, 97, 97));
        btnTaiLai.setForeground(Color.WHITE);
        btnTaiLai.setFocusPainted(false);

        lblTongDoanhThuLoc = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongDoanhThuLoc.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongDoanhThuLoc.setForeground(new Color(27, 94, 32));

        lblTongHoaDonLoc = new JLabel("Tổng hóa đơn: 0");
        lblTongHoaDonLoc.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongHoaDonLoc.setForeground(new Color(27, 94, 32));

        filterPanel.add(new JLabel("Từ ngày:"));
        filterPanel.add(txtTuNgay);
        filterPanel.add(new JLabel("Đến ngày:"));
        filterPanel.add(txtDenNgay);
        filterPanel.add(btnLoc);
        filterPanel.add(btnTaiLai);
        filterPanel.add(lblTongDoanhThuLoc);
        filterPanel.add(lblTongHoaDonLoc);

        modelKhoangNgay = new DefaultTableModel(
                new String[]{"Ngày", "Số hóa đơn", "Doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKhoangNgay = new JTable(modelKhoangNgay);
        configTable(tableKhoangNgay);

        btnLoc.addActionListener(e -> locTheoNgay());
        btnTaiLai.addActionListener(e -> {
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            modelKhoangNgay.setRowCount(0);
            lblTongDoanhThuLoc.setText("Tổng doanh thu: 0 VNĐ");
            lblTongHoaDonLoc.setText("Tổng hóa đơn: 0");
        });

        JPanel north = new JPanel(new BorderLayout(10, 10));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(filterPanel, BorderLayout.CENTER);

        panel.add(north, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableKhoangNgay), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(200, 95));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(new Color(46, 125, 50));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("0", SwingConstants.LEFT);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setForeground(new Color(27, 94, 32));
        return lbl;
    }

    private void styleDateField(JTextField field) {
        field.setPreferredSize(new Dimension(110, 32));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 190), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void configTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(new Color(225, 235, 225));
        table.setSelectionBackground(new Color(200, 230, 201));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(232, 245, 233));
    }

    private void loadAllData() {
        loadSummary();
        loadTable7Ngay();
        loadTableNhanVien();
        loadTableKhachHang();
        loadTableSanPham();
    }

    private void loadSummary() {
        lblTongDoanhThu.setText(formatMoney(thongKeDAO.getTongDoanhThu()));
        lblTongHoaDon.setText(String.valueOf(thongKeDAO.getTongSoHoaDon()));
        lblTongSanPhamBan.setText(String.valueOf(thongKeDAO.getTongSoSanPhamDaBan()));
        lblTongKhachHang.setText(String.valueOf(thongKeDAO.getTongSoKhachHang()));
        lblDoanhThuNgay.setText(formatMoney(thongKeDAO.getDoanhThuHomNay()));
        lblDoanhThuThang.setText(formatMoney(thongKeDAO.getDoanhThuThangNay()));
        lblDoanhThuNam.setText(formatMoney(thongKeDAO.getDoanhThuNamNay()));
    }

    private void loadTable7Ngay() {
        model7Ngay.setRowCount(0);
        List<Object[]> list = thongKeDAO.thongKeDoanhThu7NgayGanNhat();
        for (Object[] row : list) {
            model7Ngay.addRow(new Object[]{
                row[0],
                row[1],
                formatMoney((int) row[2])
            });
        }
    }

    private void loadTableNhanVien() {
        modelNhanVien.setRowCount(0);
        List<Object[]> list = thongKeDAO.thongKeTheoNhanVien();
        for (Object[] row : list) {
            modelNhanVien.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((int) row[3])
            });
        }
    }

    private void loadTableKhachHang() {
        modelKhachHang.setRowCount(0);
        List<Object[]> list = thongKeDAO.thongKeTheoKhachHang();
        for (Object[] row : list) {
            modelKhachHang.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((int) row[3])
            });
        }
    }

    private void loadTableSanPham() {
        modelSanPham.setRowCount(0);
        List<Object[]> list = thongKeDAO.thongKeSanPhamBanChay();
        for (Object[] row : list) {
            modelSanPham.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                formatMoney((int) row[3])
            });
        }
    }

    private void locTheoNgay() {
        try {
            String tuNgayText = txtTuNgay.getText().trim();
            String denNgayText = txtDenNgay.getText().trim();

            if (tuNgayText.isEmpty() || denNgayText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ từ ngày và đến ngày theo dạng yyyy-MM-dd.");
                return;
            }

            Date tuNgay = Date.valueOf(tuNgayText);
            Date denNgay = Date.valueOf(denNgayText);

            if (tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(this, "Từ ngày không được lớn hơn đến ngày.");
                return;
            }

            modelKhoangNgay.setRowCount(0);

            List<Object[]> list = thongKeDAO.thongKeDoanhThuTheoKhoangNgay(tuNgay, denNgay);
            for (Object[] row : list) {
                modelKhoangNgay.addRow(new Object[]{
                    row[0],
                    row[1],
                    formatMoney((int) row[2])
                });
            }

            int tongDoanhThu = thongKeDAO.getTongDoanhThuTheoKhoangNgay(tuNgay, denNgay);
            int tongHoaDon = thongKeDAO.getTongHoaDonTheoKhoangNgay(tuNgay, denNgay);

            lblTongDoanhThuLoc.setText("Tổng doanh thu: " + formatMoney(tongDoanhThu));
            lblTongHoaDonLoc.setText("Tổng hóa đơn: " + tongHoaDon);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Vui lòng nhập theo dạng yyyy-MM-dd.");
        }
    }

    private String formatMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(money) + " VNĐ";
    }
}